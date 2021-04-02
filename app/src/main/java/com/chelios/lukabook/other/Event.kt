package com.chelios.lukabook.other

import androidx.lifecycle.Observer

//for: in error case, after rotating device: snackbars not to show twice
class Event<out T>(private val content: T) {

    //for consuming an event
    var hasBeenHandled = false
        private set

    fun getContentIfNotHandled(): T? {
        return if(!hasBeenHandled) {
            hasBeenHandled = true
            content
        } else null
    }

    //if event is already handled or just want to get the content without consuming it
    fun peekContent() = content
}

class EventObserver<T>(
    private inline val onError: ((String) -> Unit)? = null,
    private inline val onLoading: (() -> Unit)? = null,
    private inline val onSuccess: (T) -> Unit
) : Observer<Event<Resource<T>>> {
    override fun onChanged(t: Event<Resource<T>>?) {
        when(val content = t?.peekContent()) {
            is Resource.Success -> {
                content.data?.let(onSuccess)
            }
            is Resource.Error -> {
                t.getContentIfNotHandled()?.let {
                    onError?.let { error ->
                        error(it.message!!)
                    }
                }
            }
            is Resource.Loading -> {
                onLoading?.let { loading ->
                    loading()
                }
            }
        }
    }
}