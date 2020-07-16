package com.young.weexbase.model.apiAbout

import com.young.weexbase.model.*
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*


interface OutTokenApi

interface WithTokenApi {
    @GET("/fireapi/vod/GetPlayInfo/{videoId}")
    fun getVideoInfo(@Path("videoId") videoId: String): Call<VideoInfo>

    @GET("/fireapi/vod/addVidSvideoFavorite/{allsvduid}")
    fun saveVideo(@Path("allsvduid") videoId: String): Call<OptionResult>

    @GET("/fireapi/vod/removeVidSvideoFavorite/{allsvduid}")
    fun saveCancelVideo(@Path("allsvduid") videoId: String): Call<OptionResult>

    @GET("/fireapi/vod/addVidSvideoparise/{allsvduid}")
    fun likeVideo(@Path("allsvduid") videoId: String): Call<OptionResult>

    @GET("/fireapi/vod/delVidSvideoparise/{allsvduid}")
    fun likeCancelVideo(@Path("allsvduid") videoId: String): Call<OptionResult>

    //长度不超过128个字符或汉字  //必传，必须带扩展名，且扩展名不区分大小写
    @POST("/fireapi/vod/getUploadAuthAndAddress")
    fun getUploadAuthAndAddress(@Body param: Map<String, String>): Call<UpVideoPreResult>

    @POST("/fireapi/vod/refreshUploadAuthAndAddress/{videoId}")
    fun freshUploadFileInfo(@Path("videoId") videoId: String): Call<FreshUploadFileInfoResult>

    @GET("/fireapi/vod/updateVdhandlestat/{videoId}")
    fun uploadFileComplete(@Path("videoId") videoId: String): Call<UpLoadFileCompleteResult>


    @Multipart
    @POST("/fireapi/oss/upload/image")
    fun uploadImg(@Part part: MultipartBody.Part?): Call<UpFileResult>

    @Multipart
    @POST("/fireapi/oss/upload/image")
    fun uploadImgs(@Part parts: List<MultipartBody.Part>): Call<UpFileResult>

    @Streaming //添加这个注解用来下载大文件
    @GET
    fun downloadFileUrl(@Url fileUrl: String): Call<ResponseBody>
}
