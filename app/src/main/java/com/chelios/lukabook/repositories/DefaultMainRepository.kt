package com.chelios.lukabook.repositories

import android.net.Uri
import com.chelios.lukabook.data.entities.Comment
import com.chelios.lukabook.data.entities.Post
import com.chelios.lukabook.data.entities.ProfileUpdate
import com.chelios.lukabook.data.entities.User
import com.chelios.lukabook.other.Constants.DEFAULT_PROFILE_PICTURE_URL
import com.chelios.lukabook.other.Resource
import com.chelios.lukabook.other.safeCall
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import dagger.hilt.android.scopes.ActivityScoped
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.lang.IllegalStateException
import java.util.*

@ActivityScoped     //same instance in all fragments in activity
class DefaultMainRepository : MainRepository {

    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()
    private val storage = Firebase.storage

    //references to collections in firestore
    private val users = firestore.collection("users")
    private val posts = firestore.collection("posts")
    private val comments = firestore.collection("comments")

    override suspend fun createPost(imageUri: Uri, text: String) = withContext(Dispatchers.IO) {
        safeCall {
            val uid = auth.uid!!
            val postId = UUID.randomUUID().toString()
            //upload image to storage and get its reference
            val imageUploadResult = storage.getReference(postId).putFile(imageUri).await()
            val imageUrl = imageUploadResult?.metadata?.reference?.downloadUrl?.await().toString()
            //create post and upload it to firestore
            val post = Post (
                    id = postId,
                    authorUid = uid,
                    text = text,
                    imageUrl = imageUrl,
                    date = System.currentTimeMillis()
            )
            posts.document(postId).set(post).await()
            Resource.Success(Any())
        }
    }

    override suspend fun getPostsForProfile(uid: String) = withContext(Dispatchers.IO) {
        safeCall {
            val profilePosts = posts.whereEqualTo("authorUid", uid)
                    .orderBy("date", Query.Direction.DESCENDING)
                    .get()
                    .await()
                    .toObjects(Post::class.java)
                    .onEach { post ->
                        val user = getUser(post.authorUid).data!!
                        post.authorProfilePictureUrl = user.profilePictureUrl
                        post.authorUsername = user.username
                        post.isLiked = uid in post.likedBy
                    }
            Resource.Success(profilePosts)
        }
    }

    override suspend fun searchUser(query: String)= withContext(Dispatchers.IO) {
        safeCall {
            val userResults = users.whereGreaterThanOrEqualTo("username", query.toUpperCase(Locale.ROOT))
                    .get().await().toObjects(User::class.java)
            Resource.Success(userResults)
        }
    }

    override suspend fun toggleFollowForUser(uid: String) = withContext(Dispatchers.IO) {
        safeCall {
            var isFollowing = false
            firestore.runTransaction { transaction ->
                val currentUid = auth.uid!!
                val currentUser = transaction.get(users.document(currentUid)).toObject(User::class.java)!!
                isFollowing = uid in currentUser.follows
                val newFollows = if(isFollowing) currentUser.follows - uid else currentUser.follows + uid
                transaction.update(users.document(currentUid), "follows", newFollows)
            }.await()
            Resource.Success(!isFollowing)
        }
    }

    override suspend fun toggleLikeForPost(post: Post) = withContext(Dispatchers.IO) {
        safeCall {
            var isLiked = false
            firestore.runTransaction {transaction ->
                val uid = FirebaseAuth.getInstance().uid!!
                val postResult = transaction.get(posts.document(post.id))
                val currentLikes = postResult.toObject(Post::class.java)?.likedBy ?: listOf()
                transaction.update(
                        posts.document(post.id),
                        "likedBy",
                        if(uid in currentLikes) currentLikes - uid else {
                            isLiked = true
                            currentLikes + uid  //+ -> adds uid to currentLikes list and return that list, - -> removes uid from list and returns list
                        }
                )
            }.await()
            Resource.Success(isLiked)
        }
    }

    override suspend fun getPostsForFollows() = withContext(Dispatchers.IO) {
        safeCall {
            val uid = FirebaseAuth.getInstance().uid!!
            val follows = getUser(uid).data!!.follows
            val allPosts = posts.whereIn("authorUid", follows)
                    .orderBy("date", Query.Direction.DESCENDING)
                    .get()
                    .await()
                    .toObjects(Post::class.java)
                    .onEach {post ->
                        val user = getUser(post.authorUid).data!!
                        post.authorProfilePictureUrl = user.profilePictureUrl
                        post.authorUsername = user.username
                        post.isLiked = uid in post.likedBy
                    }
            Resource.Success(allPosts)
        }
    }

    override suspend fun deletePost(post: Post) = withContext(Dispatchers.IO) {
        safeCall {
            posts.document(post.id).delete().await()
            //delete post image from storage
            storage.getReferenceFromUrl(post.imageUrl).delete().await()
            Resource.Success(post)
        }
    }

    override suspend fun createComment(commentText: String, postId: String) = withContext(Dispatchers.IO) {
        safeCall {
            val uid = auth.uid!!
            val commentId = UUID.randomUUID().toString()
            val user = getUser(uid).data!!
            val comment = Comment(
                    commentId,
                    postId,
                    uid,
                    user.username,
                    user.profilePictureUrl,
                    commentText
            )
            comments.document(commentId).set(comment).await()
            Resource.Success(comment)
        }
    }

    override suspend fun updateProfilePicture(uid: String, imageUri: Uri) = withContext(Dispatchers.IO) {
        //safeCall
        val storageRef = storage.getReference(uid)
        val user = getUser(uid).data!!
        //if profile pic already exists (and its not default), delete it and only then upload new one
        if(user.profilePictureUrl != DEFAULT_PROFILE_PICTURE_URL) {
            storage.getReferenceFromUrl(user.profilePictureUrl).delete().await()
        }
        storageRef.putFile(imageUri).await().metadata?.reference?.downloadUrl?.await()
    }

    override suspend fun updateProfile(profileUpdate: ProfileUpdate) = withContext(Dispatchers.IO) {
        safeCall {
            val imageUrl = profileUpdate.profilePictureUri?.let{ uri ->
                updateProfilePicture(profileUpdate.uidToUpdate, uri).toString()
            }
            val map = mutableMapOf(
                    "username" to profileUpdate.username,
                    "description" to profileUpdate.description
            )
            imageUrl?.let { url ->
                map["profilePictureUrl"] = url
            }
            users.document(profileUpdate.uidToUpdate).update(map.toMap()).await()
            Resource.Success(Any())
        }
    }

    override suspend fun deleteComment(comment: Comment) = withContext(Dispatchers.IO) {
        safeCall {
            comments.document(comment.commentId).delete().await()
            Resource.Success(comment)
        }
    }

    override suspend fun getCommentsForPost(postId: String) = withContext(Dispatchers.IO) {
        safeCall {
            val commentsForPost = comments
                    .whereEqualTo("postId", postId)
                    .orderBy("date", Query.Direction.DESCENDING)
                    .get()
                    .await()
                    .toObjects(Comment::class.java)
                    .onEach { comment ->
                        val user = getUser(comment.uid!!).data!!
                        comment.username = user.username
                        comment.profilePictureUrl = user.profilePictureUrl
                    }
            Resource.Success(commentsForPost)
        }
    }

    override suspend fun getUsers(uids: List<String>): Resource<List<User>> = withContext(Dispatchers.IO) {
        safeCall {
            val usersList = users.whereIn("uid", uids).orderBy("username").get().await()
                    .toObjects(User::class.java)
            Resource.Success(usersList)
        }
    }

    override suspend fun getUser(uid: String): Resource<User> = withContext(Dispatchers.IO) {
        safeCall {
            val user = users.document(uid).get().await().toObject(User::class.java)
                    ?: throw IllegalStateException()
            val currentUid = FirebaseAuth.getInstance().uid!!
            val currentUser = users.document(currentUid).get().await().toObject(User::class.java)
                    ?: throw IllegalStateException()
            user.isFollowing = uid in currentUser.follows
            Resource.Success(user)
        }
    }
}