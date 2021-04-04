package com.chelios.lukabook.data.entities

import com.chelios.lukabook.other.Constants.DEFAULT_PROFILE_PICTURE_URL
import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.IgnoreExtraProperties

@IgnoreExtraProperties  //properties with @Exclude annotation are not saved in Firestore
data class User (
    val uid: String = "",
    val username: String = "",
    val profilePictureUrl: String = DEFAULT_PROFILE_PICTURE_URL,
    val description: String = "",
    var follows: List<String> = listOf(), //list of uid-s of users who are followed by this user
    @get:Exclude
    var isFollowing: Boolean = false    //later, for easier posts for this user
)