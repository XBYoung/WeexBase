package com.young.weexbase.weex.adapter

import android.text.TextUtils
import android.widget.ImageView
import com.young.weexbase.BuildConfig
import com.young.weexbase.constants.DOMAIN_NAME
import com.young.weexbase.weex.util.KeyStore
import com.young.weexbase.weex.util.cache.FileCache
import com.young.weexbase.weex.util.cache.ImageFileCache
import org.apache.weex.WXSDKManager
import org.apache.weex.adapter.IWXImgLoaderAdapter
import org.apache.weex.common.WXImageStrategy
import org.apache.weex.dom.WXImageQuality
import org.xutils.common.util.LogUtil

/**
 * Created by Aikexing on 2016/8/4.
 * 图片文件加载
 */
class WeexImageAdapter : IWXImgLoaderAdapter {
    companion object{
        private const val KEY_SUB =  "resource/images/"
    }
    override fun setImage(url: String, view: ImageView, quality: WXImageQuality, strategy: WXImageStrategy) { //实现你自己的图片下载。
        if (view != null) {
            WXSDKManager.getInstance().postOnUiThread({
                var imageUrl: String? = url
                if (imageUrl == null || TextUtils.isEmpty(imageUrl)) {
                    if (strategy?.placeHolder != null && strategy.placeHolder.isNotEmpty()) {
                        imageUrl = strategy.placeHolder
                        strategy.placeHolder = null
                    }
                }
                if (null == imageUrl) {
                    imageUrl = ""
                }
                val base_page_url = KeyStore.getInstance(view.context).get(DOMAIN_NAME, BuildConfig.BASE_PAGE_URL)
                imageUrl = if (FileCache.getInstance().isNetFile(imageUrl) && imageUrl.startsWith(base_page_url) && imageUrl.indexOf(KEY_SUB) != -1) { //在线页面项目资源图片
                    base_page_url + "/" + imageUrl.substring(imageUrl.indexOf(KEY_SUB), imageUrl.length)
                } else if (!FileCache.getInstance().isNetFile(imageUrl) && imageUrl.indexOf(KEY_SUB) != -1) { //离线页面项目资源图片
                    base_page_url + "/" + imageUrl.substring(imageUrl.indexOf(KEY_SUB), imageUrl.length)
                } else if (imageUrl.startsWith("file://")) { //本地图片
                    imageUrl.replace("file:///".toRegex(), "/").replace("file://".toRegex(), "/")
                } else { //其他图片
                    imageUrl
                }
                var placeHolder: String? = null
                if (strategy?.placeHolder != null && strategy.placeHolder.isNotEmpty()) {
                    placeHolder = strategy.placeHolder
                    placeHolder = if (FileCache.getInstance().isNetFile(placeHolder) && placeHolder.startsWith(base_page_url) && placeHolder.indexOf(KEY_SUB) != -1) { //在线页面项目资源图片
                        base_page_url + "/" + placeHolder.substring(placeHolder.indexOf(KEY_SUB), placeHolder.length)
                    } else if (!FileCache.getInstance().isNetFile(placeHolder) && placeHolder.indexOf(KEY_SUB) != -1) { //离线页面项目资源图片
                        base_page_url + "/" + placeHolder.substring(placeHolder.indexOf(KEY_SUB), placeHolder.length)
                    } else if (placeHolder.startsWith("file://")) { //本地图片
                        placeHolder.replace("file:///".toRegex(), "/").replace("file://".toRegex(), "/")
                    } else { //其他图片
                        placeHolder
                    }
                }
                if (placeHolder != null) {
                    ImageFileCache.getInstance().loadImageToImageViewMultiplexWeex(view, placeHolder, imageUrl, strategy)
                } else if (imageUrl != null) {
                    LogUtil.d("imageUrl = $imageUrl")
                    ImageFileCache.getInstance().loadImageToImageViewMultiplexWeex(view, imageUrl, placeHolder, strategy)
                }
            }, 0)
        }
    }


}