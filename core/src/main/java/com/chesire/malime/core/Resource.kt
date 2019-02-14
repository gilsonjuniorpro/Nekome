package com.chesire.malime.core

sealed class Resource<T> {
    class Success<T>(val data: T) : Resource<T>()
    class Error<T>(val msg: String) : Resource<T>()
}
