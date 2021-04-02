package com.chelios.lukabook.data.entities

import com.chelios.lukabook.other.Constants.DEFAULT_PROFILE_PICTURE_URL
import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.IgnoreExtraProperties

@IgnoreExtraProperties
data class User (
    val uid: String = "",
    val username: String = "",
    val profilePictureUrl: String = DEFAULT_PROFILE_PICTURE_URL,
    val description: String = "",
    var follows: List<String> = listOf(), //list of uid-s of users who are followed by this user
    @Exclude
    var isFollowing: Boolean = false    //later, for easier posts for this user
)