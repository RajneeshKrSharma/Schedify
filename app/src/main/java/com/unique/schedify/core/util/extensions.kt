package com.unique.schedify.core.util

import com.unique.schedify.auth.login.presentation.emailPattern

fun String.isEmailValid(): Boolean = this.run { isNotEmpty() && matches(emailPattern) }
