package com.chelios.lukabook.repositories

import android.net.Uri
import com.chelios.lukabook.data.entities.Post
import com.chelios.lukabook.data.entities.User
import com.chelios.lukabook.other.Resource

interface MainRepository {

    suspend fun createPost(imageUri: Uri, text: String): Resource<Any>  //"Any" because we just need to be notified if post was successfully posted or not (without any return data)

    suspend fun getUsers(uids: List<String>): Resource<List<User>>

    suspend fun getUser(uid: String): Resource<User>

    //get posts from people who are followed
    suspend fun getPostsForFollows(): Resource<List<Post>>

    suspend fun toggleLikeForPost(post: Post): Resource<Boolean>

    suspend fun deletePost(post: Post): Resource<Post>

    suspend fun getPostsForProfile(uid: String): Resource<List<Post>>

    suspend fun toggleFollowForUser(uid: String): Resource<Boolean>

    suspend fun searchUser(query: String): Resource<List<User>>
}