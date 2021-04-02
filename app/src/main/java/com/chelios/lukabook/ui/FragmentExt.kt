package com.chelios.lukabook.ui

import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar

//extension function - reducing boilerplate code for showing snackbars in fragments
fun Fragment.snackbar(text: String) {
    Snackbar.make(
        requireView(),   //this comes from Fragment
        text,
        Snackbar.LENGTH_LONG
    ).show()
}