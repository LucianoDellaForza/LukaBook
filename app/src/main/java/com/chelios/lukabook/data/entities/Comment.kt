package com.chelios.lukabook.data.entities

import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.IgnoreExtraProperties

@IgnoreExtraProperties
data class Comment (
        val commentId: String = "",
        val postId: String = "", //post id that comment belongs to
        val uid: String = "",    //user id who made the comment
        @get:Exclude var username: String = "",
        @get:Exclude var profilePictureUrl: String = "",
        val comment: String = "",
        val date: Long = System.currentTimeMillis()
)