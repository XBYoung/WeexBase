package com.young.weexbase.ex

import com.young.weexbase.model.FireMan

fun FireMan.from(): String {
    if (this.comeFrom?.endsWith("总队") == true) {
        return this.comeFrom!!.replace("总队", "")
    }
    return this.comeFrom?:"未知"
}

