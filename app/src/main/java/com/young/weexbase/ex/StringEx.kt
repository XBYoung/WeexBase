package com.young.weexbase.ex

fun String?.protocolExist() = this?.startsWith("http://") == true || this?.startsWith("https://") == true


fun String?.removeProtocol(): String {
    return when {
        this?.startsWith("http://") == true -> {
            this.replace("http://", "")
        }
        this?.startsWith("https://") == true -> {
            this.replace("https://", "")
        }
        else -> {
            return this ?: ""
        }
    }

}

fun String?.isHttps() = this?.startsWith("https://") == true

fun String?.appendProtocol(isHttps: Boolean): String {
    return if (isHttps) {
        "https://$this"
    } else {
        "http://$this"
    }
}

