package com.mio.plugin.renderer

import android.app.Activity
import android.os.Bundle
import android.widget.TextView

class MainActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(TextView(this).apply {
            text = "PanVK driver plugin (Mesa 27.0.0-devel, r10)"
        })
    }
}