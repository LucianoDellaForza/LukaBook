package com.chelios.lukabook.data.entities

import android.net.Uri

data class ProfileUpdate (
        val uidToUpdate: String = "",
        val username: String = "",
        val description: String = "",
        val profilePictureUri: Uri? = null
)