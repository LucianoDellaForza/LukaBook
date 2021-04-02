package com.chelios.lukabook.other

import java.lang.Exception

//function for reducing boilerplate code in repositories since every fun there will have same try/catch block
inline fun <T> safeCall(action: () -> Resource<T>): Resource<T> {
    return try {
        action()
    } catch (e: Exception) {
        Resource.Error(e.message ?: "An unknown error occurred")
    }
}