package com.young.weexbase.ex

fun Map<String, Any?>?.getStrValue(key: String, defaultValue: String = ""): String {
    this?.let {
        return it[key]?.let {
            it.toString()
        } ?: defaultValue
    }
    return defaultValue
}