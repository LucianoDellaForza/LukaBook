package com.chelios.lukabook.repositories

import android.net.Uri
import com.chelios.lukabook.other.Resource

interface MainRepository {

    suspend fun createPost(imageUri: Uri, text: String): Resource<Any>  //"Any" because we just need to be notified if post was successfully posted or not (without any return data)
}