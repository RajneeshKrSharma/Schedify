package com.unique.schedify.core.util

val emailPattern = EmailIdRegex.toRegex()

fun String.isEmailValid(): Boolean = this.run { isNotEmpty() && matches(emailPattern) }
