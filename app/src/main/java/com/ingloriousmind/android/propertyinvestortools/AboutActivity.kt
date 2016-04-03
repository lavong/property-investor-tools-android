package com.ingloriousmind.android.propertyinvestortools

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.util.Linkify
import android.view.View
import org.jetbrains.anko.*

class AboutActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        relativeLayout {
            padding = dip(16)
            val imageView = imageView(R.mipmap.ic_launcher) {
                id = View.generateViewId()
                onClick { browse("http://www.ingloriousmind.com") }
            }.lparams {
                centerInParent()
            }
            textView(R.string.about_description) {
                textAlignment = View.TEXT_ALIGNMENT_CENTER
                autoLinkMask = Linkify.WEB_URLS
            }.lparams {
                below(imageView)
            }
        }
    }

}
