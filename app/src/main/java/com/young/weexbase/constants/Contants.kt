package com.young.weexbase.constants

import com.young.weexbase.BuildConfig
import com.young.weexbase.weex.application.WeexApplication
import java.io.File

val JS_CACHE_DIR by lazy { WeexApplication.application?.cacheDir?.absolutePath + File.separator + "weexCahce" }

var FROM_SD = !BuildConfig.DEBUG

val MEMACCNO = "memaccno"

const val TAG_LAUNCH_FIRST = "tag_launch"
