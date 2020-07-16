package com.young.weexbase.weex.util.cache;

import android.text.TextUtils;
import com.young.weexbase.BuildConfig;
import com.young.weexbase.weex.util.ThreadPool;
import org.apache.http.util.EncodingUtils;
import org.xutils.common.util.LogUtil;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

public class FileCache {

    /**
     * 数据内部存储，程序数据目录
     */
    protected static String INTERNAL_FILES_PATH = "/data/data/" + BuildConfig.APPLICATION_ID + "/files";
    /**
     * 文件缓存对象
     */
    protected static FileCache fileCache = null;
    /**
     * 网络图片下载地址队列
     */
    protected Set<String> downUrls;

    /**
     * 获取单列对象
     *
     * @return
     */
    public static FileCache getInstance() {
        if (fileCache == null) {
            fileCache = new FileCache();
        }
        return fileCache;
    }

    /**
     * 创建单列对象
     */
    protected FileCache() {
        // 缓存目录
        new File(INTERNAL_FILES_PATH).mkdirs();
        // 网络图片下载地址队列
        downUrls = new HashSet<String>();
    }

    /**
     * 文件缓存
     * @param url 地址（本地或网络）
     * @param type 类型 0，普通文件缓存，1 文本文件缓存并读取，2 文本文件直接读取不缓存，3 文本文件直接读取不缓存(仅读第一行)
     * @param callBack （回调地址）
     */
    public void downFileForUrl(String url, final int type, final FileCacheCallBack callBack) {
        LogUtil.i(url);
        boolean isNetFile = isNetFile(url);
        String pathFlag = null;
        if (isNetFile) {
            if(type==2||type==3){
                //不使用缓存
                ThreadPool.executorMethodLastInFirstOut(new ThreadPool.ThreadPoolMethodCallBack() {
                    @Override
                    public void callBack(String methodName, Object object) {
                        if (TextUtils.equals(methodName, "readFileForUrl")) {
                            if (object != null && object instanceof String) {
                                String content = (String) object;
                                if (callBack != null) {
                                    callBack.callBackContent(content);
                                }
                            }
                        }
                    }
                }, this, "readFileForUrl", url,type==3);
                return;
            }
            //网络文件
            final String path = getFileCachePath(url);
            if (!isExists(path)) {
                //下载
                ThreadPool.executorMethodLastInFirstOut(new ThreadPool.ThreadPoolMethodCallBack() {
                    @Override
                    public void callBack(String methodName, Object object) {
                        if (TextUtils.equals(methodName, "downFileForUrl")) {
                            if (object != null && object instanceof Boolean) {
                                boolean isSuccess = (boolean) object;
                                if (callBack != null) {
                                    callBack.callBack(path, isSuccess ? FileCacheCallBack.STATE_DOWN_SUCCESS : FileCacheCallBack.STATE_DOWN_ERROR);
                                    //读取文件内容
                                    if(isSuccess && type != 0){
                                        ThreadPool.executorMethodLastInFirstOut(new ThreadPool.ThreadPoolMethodCallBack() {
                                            @Override
                                            public void callBack(String methodName, Object object) {
                                                if (TextUtils.equals(methodName, "readFile")) {
                                                    if (object != null && object instanceof String) {
                                                        String content = (String) object;
                                                        if (callBack != null) {
                                                            callBack.callBackContent(content);
                                                        }
                                                    }
                                                }
                                            }
                                        }, FileCache.this, "readFile", path);
                                    }
                                }
                            }
                        }
                    }
                }, this, "downFileForUrl", url, path);
            } else {
                pathFlag = path;
                //已经缓存
                callBack.callBack(path, FileCacheCallBack.STATE_NET_CACHE);
            }
        } else {
            //非网络文件
            if (callBack != null) {
                if (!TextUtils.isEmpty(url)) {
                    String path = url;
                    if (isExists(path)) {
                        pathFlag = path;
                        callBack.callBack(url, FileCacheCallBack.STATE_LOCAL_FILE);
                    } else {
                        callBack.callBack(url, FileCacheCallBack.STATE_UNKNOWN_URL);
                    }
                } else {
                    callBack.callBack(url, FileCacheCallBack.STATE_IS_EMPTY);
                }
            }
        }
        //获取文件内容
        if(pathFlag!=null && type != 0){
            ThreadPool.executorMethodLastInFirstOut(new ThreadPool.ThreadPoolMethodCallBack() {
                @Override
                public void callBack(String methodName, Object object) {
                    if (TextUtils.equals(methodName, "readFile")) {
                        if (object != null && object instanceof String) {
                            String content = (String) object;
                            if (callBack != null) {
                                callBack.callBackContent(content);
                            }
                        }
                    }
                }
            }, this, "readFile", pathFlag);
        }
    }

    /**
     * 下载网络文件到指定目录
     *
     * @param url
     * @param path
     */
    public static boolean downFileForUrl(String url, String path) {
        boolean success = true;
        InputStream is = null;
        FileOutputStream fos = null;
        try {
            HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
            conn.setConnectTimeout(1000 * 20);
            conn.connect();
            if (conn.getResponseCode() == 200) {
                is = conn.getInputStream();
                File file = new File(path);
                if (!file.getParentFile().exists()) {
                    file.getParentFile().mkdirs();
                }
                fos = new FileOutputStream(file);
                byte[] buf = new byte[1024];
                int readLen = 0;
                while ((readLen = is.read(buf)) > 0) {
                    fos.write(buf, 0, readLen);
                }
                fos.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
            success = false;
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                }
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                }
            }
            //如果下载不成功或者下载过程中失败 ，则删除错误的文件
            if (!success) {
                deleteFile(new File(path));
            }
            return success;
        }
    }

    /**
     * 判断是否网络文件
     *
     * @param url
     * @return
     */
    public boolean isNetFile(String url) {
        if (!TextUtils.isEmpty(url)) {
            return url.indexOf("http://") != -1 || url.indexOf("https://") != -1;
        }
        return false;
    }


    /**
     * 获取本地缓存文件逻辑路径
     *
     * @param url
     * @return
     */
    protected String getFileCachePath(String url) {
        if (!TextUtils.isEmpty(url)) {
            //网络地址
            String path = url.replace("https://", "").replace("http://", "");
            //返回逻辑路径
            return new File(INTERNAL_FILES_PATH, path).getAbsolutePath();
        } else {
            return null;
        }
    }

    /**
     * 判断本地缓存文件是否存在
     *
     * @param path
     * @return
     */
    protected boolean isExists(String path) {
        if (!TextUtils.isEmpty(path)) {
            return new File(path).exists();
        }
        return false;
    }

    /**
     * 改地址是否有缓存文件
     * @param url
     * @return
     */
    public boolean isHadFileCache(String url){
        return isExists(getFileCachePath(url));
    }


    /**
     * 读取文件内容
     * @param path
     * @return
     */
    public String readFile(String path){
        FileInputStream fileInputStream = null;
        try {
            fileInputStream = new FileInputStream(new File(path));
            byte [] buffer = new byte[fileInputStream.available()];
            fileInputStream.read(buffer);
            return EncodingUtils.getString(buffer, "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        } finally {
            if(fileInputStream!=null){
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 读取网络文件 不缓存
     * @param url
     */
    public String readFileForUrl(String url, boolean one) {
        HttpURLConnection conn = null;
        InputStream is = null;
        InputStreamReader inputStreamReader = null;
        BufferedReader bufferReader = null;
        try {
            conn = (HttpURLConnection) new URL(url).openConnection();
            conn.setConnectTimeout(1000 * 20);
            conn.connect();
            if (conn.getResponseCode() == 200) {
                is = conn.getInputStream();
                inputStreamReader = new InputStreamReader(is);
                bufferReader = new BufferedReader(inputStreamReader);
                if(one){
                    return bufferReader.readLine();
                }else{
                    StringBuffer stringBuffer = new StringBuffer();
                    String buf = null;
                    while((buf = bufferReader.readLine())!=null){
                        stringBuffer.append(buf+"\n");
                    }
                    return stringBuffer.toString();
                }
            }
            return "";
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        } finally {
            if (bufferReader != null) {
                try {
                    bufferReader.close();
                } catch (IOException e) {
                }
            }
            if (inputStreamReader != null) {
                try {
                    inputStreamReader.close();
                } catch (IOException e) {
                }
            }
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                }
            }
            if(conn!=null){
                try {
                    conn.disconnect();
                } catch (Exception e) {
                }
            }
        }
    }

    /**
     * 删除指定缓存
     * @param url
     */
    public void deleteCacheFileForUrl(String url){
        //网络文件逻辑缓存地址
        String path = getFileCachePath(url);
        if(isExists(path)){
            deleteFile(new File(path));
        }
    }


    /**
     * 清除缓存
     *
     * @Title deleteCacheFile
     */
    public static void deleteCacheFile() {
        //缓存目录
        File file = new File(INTERNAL_FILES_PATH);
        //删除目录及其下面的文件
        deleteFile(file);
        // 重建缓存目录
        file.mkdirs();
    }

    /**
     * 删除文件和文件夹子文件
     *
     * @param file
     * @Title deleteFile
     */
    public static void deleteFile(File file) {
        if (file.exists()) {
            if (file.isFile()) {
                file.delete();
                return;
            }
            if (file.isDirectory()) {
                File[] childFile = file.listFiles();
                if (childFile == null || childFile.length == 0) {
                    file.delete();
                    return;
                }
                for (File f : childFile) {
                    deleteFile(f);
                }
                file.delete();
            }
        }
    }

}
