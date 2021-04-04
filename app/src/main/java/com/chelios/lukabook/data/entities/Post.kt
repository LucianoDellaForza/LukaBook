package com.chelios.lukabook.data.entities

import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.IgnoreExtraProperties

@IgnoreExtraProperties
data class Post (
        val id: String = "",
        val authorUid: String = "", //user id who made post,
        @get:Exclude var authorUsername: String = "",
        @get:Exclude var authorProfilePictureUrl: String = "",
        val text: String = "",  //text of a post
        val imageUrl: String = "", //image url of a post
        val date: Long = 0L, //date of post in milliseconds (for sorting posts chronologically)
        @get:Exclude var isLiked: Boolean = false,   //if currently logged in user liked this post
        @get:Exclude var isLiking: Boolean = false,  //when user likes a post, network request is made and during that delay user can not like same post again (disable like image button during that)
        val likedBy: List<String> = listOf() //list of user uid-s who liked the post
)