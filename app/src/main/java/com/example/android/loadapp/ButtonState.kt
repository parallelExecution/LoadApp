package com.example.android.loadapp


sealed class ButtonState {
    object Unclicked : ButtonState()
    object Loading : ButtonState()
    object Completed : ButtonState()
}