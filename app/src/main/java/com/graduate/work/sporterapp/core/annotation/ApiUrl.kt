package com.graduate.work.sporterapp.core.annotation

@Target(AnnotationTarget.CLASS)
annotation class ApiUrl(
    val url: String,
)