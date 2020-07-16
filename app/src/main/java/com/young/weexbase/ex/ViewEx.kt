package com.young.weexbase.ex

import android.view.View

fun View.click(click:(View)->Unit){
    this.setOnClickListener {
        click(it)
    }
}