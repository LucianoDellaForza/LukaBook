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
}