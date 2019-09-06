package ru.skillbranch.devintensive.extensions

import android.content.res.Resources

val Int.dp
    get() = (this * Resources.getSystem().displayMetrics.density + 0.5f).toInt()