package com.young.weexbase.model

import java.lang.Exception

class NoBodyEntity

fun VideoInfo.getPlayUrl(): String {
    return this?.data?.playInfoList?.get(0)?.playURL ?: ""
}

data class VideoInfo(
        val `data`: Data?,
        val message: String?,
        val status: Int?
)

data class Data(
        val playInfoList: List<PlayInfo>?,
        val requestId: String?,
        val videoBase: VideoBase?
)

data class PlayInfo(
        val bitrate: String?,
        val complexity: Any?,
        val creationTime: String?,
        val definition: String?,
        val duration: String?,
        val encrypt: String?,
        val encryptType: Any?,
        val format: String?,
        val fps: String?,
        val height: Double,
        val jobId: String?,
        val modificationTime: String?,
        val narrowBandType: String?,
        val plaintext: Any?,
        val playURL: String,
        val preprocessStatus: String?,
        val rand: Any?,
        val size: Int?,
        val specification: String?,
        val status: String?,
        val streamType: String?,
        val watermarkId: Any?,
        val width: Double?
)

data class VideoBase(
        val coverURL: String,
        val creationTime: String,
        val duration: String,
        val mediaType: String,
        val outputType: String,
        val status: String,
        val thumbnailList: List<Any>,
        val title: String,
        val transcodeMode: String,
        val videoId: String
)

data class ErrorResult(
        var errorCode: String = "-1",
        var errorMsg: String = "网络异常"

) : Exception()


data class OptionResult(
        val `data`: OptionData,
        val message: String,
        val status: Int
)

class OptionData


data class UpFileResult(
        //@SerializedName(value = "data", alternate = ["data"])
        val data: FileResultData,
        val message: String,
        val status: Int
)

data class FileResultData(
        val file_id: String,
        val file_name: String,
        val file_url: String,
        val filemd5: Any,
        val filesize: String,
        val filetype: String
)