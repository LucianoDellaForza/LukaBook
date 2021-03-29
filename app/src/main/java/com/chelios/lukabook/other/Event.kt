package com.chelios.lukabook.other

//for: in error case, after rotating device: snackbars not to show twice
class Event<out T>(private val content: T) {

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