package com.chelios.lukabook.ui

import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar

//extension function for easier snackbar showing in fragments (reduces boilerplate code for showing snackbars)
fun Fragment.snackbar(text: String) {
    Snackbar.make(
        requireView(),   //this comes from Fragment
        text,
        Snackbar.LENGTH_LONG
    ).show()
}