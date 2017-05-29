package com.nmote.jwti

fun String?.trimToNull() = this?.trim().let { if (it.isNullOrEmpty()) null else it }

