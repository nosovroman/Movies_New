package com.example.pravki.extensions

import android.os.Build
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlin.String

class String {
}

fun String?.formatDate(): String {
    return if (this != null && this != "") {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val x = LocalDate.parse(this)
            x.format(DateTimeFormatter.ofPattern("dd MMM yyyy"))
        } else {
            this
        }
    } else {
        "-"
    }
}