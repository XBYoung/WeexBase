package com.young.weexbase.ex

import android.app.Activity
import android.text.TextUtils
import com.young.weexbase.ui.activity.WeexBaseActivity.Companion.DATA
import com.google.gson.Gson

fun Activity.parseData():Map<String,Any?>{
    var data = intent.getStringExtra(DATA)
    return if (data == null || data.isEmpty() || TextUtils.equals(data, "null")) emptyMap() else Gson().fromJson(data, Map::class.java)  as Map<String, Any?>
}