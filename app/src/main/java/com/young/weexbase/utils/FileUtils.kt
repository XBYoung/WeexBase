package com.young.weexbase.utils

import android.content.Context
import com.young.weexbase.constants.JS_CACHE_DIR
import org.xutils.common.util.LogUtil
import java.io.*


object FileUtils {

    fun copyAssetsToSd(context: Context, assetDir: String, dir: String = JS_CACHE_DIR) {
        //取消闭包函数计数
        /*   fun cacuFileCount(): (Int) -> Int {
               var count = 0
               return {
                   count += it
                   count
               }
           }

           fun fileIndex(): () -> Int {
               var index = 0
               return {
                   index += 1
                   index
               }
           }*/

        copyAssets(context, assetDir, JS_CACHE_DIR)

    }


    /**
     * 复制文件到SD
     */
    private fun copyAssets(context: Context, assetDir: String, dir: String) {
        LogUtil.d("assets $assetDir   $dir")
        val files: Array<String> = try { // 获得Assets一共有几多文件
            context.assets.list(assetDir)
        } catch (e1: IOException) {
            e1.printStackTrace()
            return
        }
        LogUtil.d("assets  dirSize = ${files.size}")
        val mWorkingPath = File(dir)
        // 如果文件路径不存在
        if (!mWorkingPath.exists()) { // 创建文件夹
            if (!mWorkingPath.mkdirs()) { // 文件夹创建不成功时调用
            }
        }
        for (i in files.indices) {
            try { // 获得每个文件的名字
                val fileName = files[i]
                // 根据路径判断是文件夹还是文件
                if (!fileName.contains(".")) {
                    if (assetDir.isEmpty()) {
                        copyAssets(context, fileName, "$dir/$fileName/")
                    } else {
                        copyAssets(context, "$assetDir/$fileName", dir + "/"
                                + fileName + "/")
                    }
                    continue
                }
                val outFile = File(mWorkingPath, fileName)
                if (outFile.exists()){
                    outFile.delete()
                }
                var inputStream = if (assetDir.isNotEmpty()) context.assets.open("$assetDir/$fileName") else context.assets.open(fileName)
                val out: OutputStream = FileOutputStream(outFile)
                // Transfer bytes from in to out
                val buf = ByteArray(1024)
                var len: Int = 0
                while (inputStream.read(buf).also { len = it } > 0) {
                    out.write(buf, 0, len)
                }
                inputStream.close()
                out.close()
                LogUtil.d("assets  end ... copyAssets")
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }

    }

    fun jsCacheIsExist():Boolean{
        val jsCacheFile = File(JS_CACHE_DIR)
        if (jsCacheFile.exists() || !jsCacheFile.listFiles().isNullOrEmpty()){
            return true
        }else{
            if (jsCacheFile.exists() && jsCacheFile.listFiles().isNullOrEmpty()){
                jsCacheFile.delete()
            }
        }
        return false
    }

    /**
     * 删除js文件缓存
     */
    fun clearJsCache() {
        with(File(JS_CACHE_DIR)) {
            if (this.exists()) {
                this.delete()
            }
        }
    }
}