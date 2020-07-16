package com.young.weexbase.model

fun Any?.toStringNotNull():String{
    if (this is String?){
        if (this.isNullOrEmpty()){
            return ""
        }
    }
    return this.toString()
}