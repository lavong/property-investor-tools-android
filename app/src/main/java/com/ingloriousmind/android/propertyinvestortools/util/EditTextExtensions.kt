package com.ingloriousmind.android.propertyinvestortools.util

import android.widget.EditText

fun EditText.asFloat() = if (text.isNullOrBlank()) 0f else Integer.parseInt(text.toString()).toFloat()
