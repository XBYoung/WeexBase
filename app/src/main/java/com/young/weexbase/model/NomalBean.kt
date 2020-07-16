package com.young.weexbase.model

import org.xutils.common.util.LogUtil

fun BaseResult.isSuccess(): Boolean {
    return this.status == 1
}

open class BaseResult(open val status: Int, open val message: String)

data class OrgVideo(
        val allsvduid: String = "allsvduid",
        val auditdate: String = "auditdate",
        val commentnum: String = "commentnum",
        val favoritenum: String = "favoritenum",
        val isfavorite: String = "isfavorite",
        val memaccno: String = "memaccno",
        val memname: String = "memname",
        val parisenum: String = "parisenum",
        val playnum: String = "playnum",
        val recomvdid: String = "recomvdid",
        val sortby: String = "sortby",
        val svideodesc: String = "svideodesc",
        val svideopic: String = "svideopic",
        val svideotitle: String = "svideotitle",
        val svideourl: String = "svideourl",
        val isparise: String = "isparise",
        val headimg: String = "headimg",
        val orgshotname: String = "orgshotname"
)

fun Map<String, Any?>.convertToFireVideo(): FireVideo {
    return OrgVideo().let {
        LogUtil.d("this[it.auditdate]  ${this[it.auditdate]}")

        FireVideo(this[it.allsvduid].toStringNotNull(),
                this[it.svideotitle].toStringNotNull(),
                this[it.svideopic].toStringNotNull(),
                this[it.svideourl].toStringNotNull(),
                this[it.isparise] == 0,
                this[it.isfavorite] == 0,
                this[it.commentnum] as? Int ?: 0,
                this[it.parisenum] as? Int ?: 0,
                this[it.favoritenum] as? Int ?: 0,

                this[it.auditdate]?.let {
                    val dateStr = it.toString()
                    if (dateStr.contains(".")) {
                        dateStr.substringBefore(".").toLong()
                    } else {
                        dateStr.toLong()
                    }
                } ?: 0L,
                FireMan("", this[it.memname].toStringNotNull(), this[it.orgshotname].toStringNotNull(), this[it.headimg].toStringNotNull(), this[it.memaccno].toStringNotNull())
        )
    }
}


fun List<Map<String, Any?>>.convertoFireVideoList(): MutableList<FireVideo> {
    val fireVideoList = mutableListOf<FireVideo>()
    this.forEach {
        fireVideoList.add(it.convertToFireVideo())
    }
    return fireVideoList
}


fun FireVideo.initPlayUrl(videoInfo: VideoInfo) {
    videoInfo.data?.playInfoList?.get(0)?.playURL?.let { url ->
        this.playUrl = url
    }
}

data class FireVideo(
        val id: String,
        var name: String,
        var coverUrl: String,
        var playUrl: String,
        var isLiked: Boolean,
        var isSaved: Boolean,
        var commentCount: Int,
        var pariseCount: Int,
        var favoritCount: Int,
        var upDate: Long,
        var author: FireMan
)

data class FireMan(
        val id: String,
        var name: String,
        var comeFrom: String?,
        var profile: String?,
        val account: String?
)


fun FireVideo.copyBy(otherVideo: FireVideo) {
    if (this.playUrl == otherVideo.playUrl && this != otherVideo) {
        this.name = otherVideo.name
        this.coverUrl = otherVideo.coverUrl
        this.isLiked = otherVideo.isLiked
        this.isSaved = otherVideo.isSaved
        this.commentCount = otherVideo.commentCount
        this.upDate = otherVideo.upDate
        this.author = otherVideo.author
    }

}


data class UpVideoPreResult(
        val `data`: PreAndFreshData,
        val message: String,
        val status: Int
)

data class PreAndFreshData(
        val requestId: String,
        val uploadAddress: String,
        val uploadAuth: String,
        val videoId: String
)


data class FreshUploadFileInfoResult(
        val `data`: PreAndFreshData,
        val message: String,
        val status: Int
)

data class UpLoadFileCompleteResult(
        val `data`: UpLoadFileCompleteData,
        override val message: String,
        override val status: Int
):BaseResult(status,message)

class UpLoadFileCompleteData()


data class ApiHeader(
        val mobiletype: String,
        val mobilemodel: String,
        val mobileotherinfo: String,
        val mobileos: String,
        val appversion: String
)

data class UpLoadBean(val apkPath: String, val force: Boolean)

data class PickVideoBean(val type: Int, val maxTime: Long, val maxSize: Long, val width: Int, val height: Int)