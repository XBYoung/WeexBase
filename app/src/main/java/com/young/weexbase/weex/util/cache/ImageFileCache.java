package com.young.weexbase.weex.util.cache;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;

import androidx.collection.LruCache;

import android.text.TextUtils;
import android.widget.ImageView;

import com.young.weexbase.weex.util.ThreadPool;
import com.young.weexbase.weex.view.WeexImageView;
import com.bumptech.glide.Glide;

import org.apache.weex.common.WXImageStrategy;
import org.xutils.common.util.LogUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

public class ImageFileCache {

    /**
     * 图片大小 ，原始大小
     */
    public static final int IMAGE_SIZE_NONE = 0;
    /**
     * 图片大小 ，自适应
     */
    public static final int IMAGE_SIZE_AUTO = 1;
    /**
     * 图片大小，指定大小
     */
    public static final int IMAGE_SIZE_SET = 2;

    /**
     * 图片文件缓存对象
     */
    protected static ImageFileCache imageFileCache = null;

    /**
     * 图片缓存容器
     */
    private LruCache<String, Bitmap> mMemoryCache;

    /**
     * 获取单列对象
     *
     * @return
     */
    public static ImageFileCache getInstance() {

        if (imageFileCache == null) {
            imageFileCache = new ImageFileCache();
        }
        return imageFileCache;
    }


    protected ImageFileCache() {
        // 获取系统分配给本应用的内存
        int maxMemory = (int) Runtime.getRuntime().maxMemory();
        // 给LruCache分配1/5 的内存
        int mCacheSize = maxMemory / 5;
        // 构建LruCache缓存
        mMemoryCache = new LruCache<String, Bitmap>(mCacheSize) {
            // 必须重写此方法，来测量Bitmap的大小
            @Override
            protected int sizeOf(String key, Bitmap value) {
                return value.getRowBytes() * value.getHeight();
            }

        };
        LogUtil.d("ImageLoader 缓存内存占用：" + mCacheSize / 1024 / 1024
                + "  M ");
    }

    public void clearCache() {
        if (mMemoryCache != null) {
            if (mMemoryCache.size() > 0) {
                mMemoryCache.evictAll();
            }
            // mMemoryCache = null;
        }
    }

    public void loadImageToImageViewMultiplexWeex(final ImageView imageView, final String oneUrl, final String twotUrl, final WXImageStrategy strategy) {
        if (imageView != null && oneUrl != null) {
            imageView.setTag(oneUrl);
        } else {
            return;
        }
        loadImageToImageView(imageView, oneUrl, IMAGE_SIZE_AUTO, 0, (bitmap, path, state, isGif) -> {

            if (state == ImageFileCacheCallBack.STATE_SUCCESS) {
                if (isGif) {
                    imageView.setTag(null);
                    Glide.with(imageView.getContext()).asGif().load(path).into(imageView);
                } else {
                    if (bitmap != null) {
                        Object tag = imageView.getTag();

                        if (tag != null && tag.equals(oneUrl)) {
                            imageView.setImageBitmap(bitmap);
                            ThreadPool.executeDelay(100, (methodName, object) -> {
                                if (strategy != null) {
                                    strategy.getImageListener().onImageFinish(oneUrl, imageView, true, null);
                                }
                            });
                        }
                    } else {
                        ThreadPool.executorMethodLastInFirstOut((methodName, object) -> {
                            if (TextUtils.equals(methodName, "getBitmapForFile")) {
                                //回调返回
                                if (object != null && object instanceof Bitmap) {
                                    Bitmap bitmap1 = (Bitmap) object;
                                    // 记录到内存缓存
                                    mMemoryCache.put(oneUrl, bitmap1);
                                    imageView.setImageBitmap(bitmap1);
                                    if (strategy != null) {
                                        strategy.getImageListener().onImageFinish(oneUrl, imageView, true, null);
                                    }
                                }
                            }
                        }, ImageFileCache.this, "getBitmapForFile", imageView.getContext(), path, IMAGE_SIZE_AUTO, 0);

                    }
                }
            }
            if (strategy != null) {
                strategy.getImageListener().onImageFinish(oneUrl, imageView, false, null);
            }

            //第二张图
            if (!TextUtils.isEmpty(twotUrl)) {
                loadImageToImageViewMultiplexWeex(imageView, twotUrl, null, strategy);
            }
        });
    }

    public void loadImageToImageViewMultiplex(final ImageView imageView, final String url, final String errorUrl) {
        if (imageView != null && url != null) {
            imageView.setTag(url);
        } else {
            return;
        }
        loadImageToImageView(imageView, url, IMAGE_SIZE_AUTO, 0, new ImageFileCacheCallBack() {
            @Override
            public void callBack(Bitmap bitmap, String path, int state, boolean isGif) {
                if (bitmap != null) {
                    Object tag = imageView.getTag();
                    if (tag != null && tag.equals(url)) {
                        imageView.setImageBitmap(bitmap);
                    }
                } else {
                    //错误图处理
                    if (state == ImageFileCacheCallBack.STATE_ERROR) {
                        if (!TextUtils.isEmpty(errorUrl)) {
                            loadImageToImageViewMultiplex(imageView, errorUrl, null);
                        }
                    }
                    //GIF
                    if (state == ImageFileCacheCallBack.STATE_SUCCESS) {
                        try {
                            if (imageView instanceof GifImageView) {
                                ((WeexImageView) imageView).setImageDrawable(new GifDrawable(path), true);
                            } else {
                                //非GIF，获取本地常规图片
                                ThreadPool.executorMethodLastInFirstOut(new ThreadPool.ThreadPoolMethodCallBack() {
                                    @Override
                                    public void callBack(String methodName, Object object) {
                                        if (TextUtils.equals(methodName, "getBitmapForFile")) {
                                            //回调返回
                                            if (object != null && object instanceof Bitmap) {
                                                Bitmap bitmap = (Bitmap) object;
                                                // 记录到内存缓存
                                                mMemoryCache.put(url, bitmap);
                                                imageView.setImageBitmap(bitmap);
                                            }
                                        }
                                    }
                                }, ImageFileCache.this, "getBitmapForFile", imageView.getContext(), path, IMAGE_SIZE_AUTO, 0);

                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });
    }

    /**
     * 读取拖到ImageView 多路复用
     *
     * @param imageView
     * @param url
     */
    public void loadImageToImageViewMultiplex(final ImageView imageView, final String url) {
        if (imageView != null && url != null) {
            imageView.setTag(url);
        } else {
            return;
        }
        loadImageToImageView(imageView, url, IMAGE_SIZE_AUTO, 0, new ImageFileCacheCallBack() {
            @Override
            public void callBack(Bitmap bitmap, String path, int state, boolean isGif) {
                if (bitmap != null) {
                    Object tag = imageView.getTag();
                    if (tag != null && tag.equals(url)) {
                        imageView.setImageBitmap(bitmap);
                    }
                }
            }
        });
    }


    /**
     * 读取图片到ImageView,简单调用
     *
     * @param imageView
     * @param url
     */
    public void loadImageToImageView(final ImageView imageView, final String url) {
        loadImageToImageView(imageView, url, IMAGE_SIZE_AUTO, 0, new ImageFileCacheCallBack() {
            @Override
            public void callBack(Bitmap bitmap, String path, int state, boolean isGif) {
                if (state == ImageFileCacheCallBack.STATE_SUCCESS) {
                    if (bitmap != null) {
                        imageView.setImageBitmap(bitmap);
                    } else if (path != null) {
                        if (imageView instanceof WeexImageView) {
                            try {
                                ((WeexImageView) imageView).setImageDrawable(new GifDrawable(path), true);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        });
    }

    /**
     * 读取图片到ImageView
     *
     * @param imageView
     * @param url
     * @param imageSize
     * @param bmpWidth
     * @param callBack
     */
    public void loadImageToImageView(ImageView imageView, final String url, final int imageSize, int bmpWidth, final ImageFileCacheCallBack callBack) {
        if (imageView == null) {
            if (callBack != null) {
                callBack.callBack(null, null, ImageFileCacheCallBack.STATE_ERROR, isGifUrl(url));
            }
            return;
        }
        if (imageSize == IMAGE_SIZE_AUTO) {
            int width = imageView.getWidth();
            if (width > 0) {
                bmpWidth = width;
            } else {
                bmpWidth = 0;
            }
        }
        loadImage(imageView.getContext(), url, imageSize, bmpWidth, callBack);
    }

    /**
     * 读取图片
     *
     * @param context
     * @param url
     * @param imageSize
     * @param bmpWidth
     * @param callBack
     */
    protected void loadImage(final Context context, final String url, final int imageSize, final int bmpWidth, final ImageFileCacheCallBack callBack) {
        if (callBack == null) {
            return;
        }
        //内存缓存检查
        if (!TextUtils.isEmpty(url)) {
            Bitmap bitmap = mMemoryCache.get(url);
            if (bitmap != null && !bitmap.isRecycled()) {
                callBack.callBack(bitmap, null, ImageFileCacheCallBack.STATE_SUCCESS, isGifUrl(url));
                return;
            }
            callBack.callBack(null, null, ImageFileCacheCallBack.STATE_WAITTING, isGifUrl(url));
        } else {
            callBack.callBack(null, null, ImageFileCacheCallBack.STATE_ERROR, isGifUrl(url));
            return;
        }
        FileCache.getInstance().downFileForUrl(url, 0, new FileCacheCallBack() {
            @Override
            public void callBack(final String path, int state) {
                if (state == FileCacheCallBack.STATE_DOWN_ERROR || state == FileCacheCallBack.STATE_UNKNOWN_URL || state == FileCacheCallBack.STATE_IS_EMPTY || TextUtils.isEmpty(path)) {
                    callBack.callBack(null, path, ImageFileCacheCallBack.STATE_ERROR, isGifUrl(url));
                } else {
                    if (isGifUrl(url)) {
                        callBack.callBack(null, path, ImageFileCacheCallBack.STATE_SUCCESS, true);
                    } else {
                        //非GIF，获取本地常规图片
                        ThreadPool.executorMethodLastInFirstOut((methodName, object) -> {
                            if (TextUtils.equals(methodName, "getBitmapForFile")) {
                                //回调返回
                                if (object != null && object instanceof Bitmap) {
                                    Bitmap bitmap = (Bitmap) object;
                                    // 记录到内存缓存
                                    mMemoryCache.put(url, bitmap);
                                    //
                                    callBack.callBack(bitmap, path, ImageFileCacheCallBack.STATE_SUCCESS, isGifUrl(url));
                                } else {
                                    //
                                    callBack.callBack(null, path, ImageFileCacheCallBack.STATE_ERROR, isGifUrl(url));
                                }
                            }
                        }, ImageFileCache.this, "getBitmapForFile", context, path, imageSize, bmpWidth);
                    }
                }
            }

            @Override
            public void callBackContent(String content) {

            }
        });
    }

    /**
     * 读取本地图片
     *
     * @param context
     * @param path
     * @param imageSize
     * @param bmpWidth
     * @return
     */
    protected Bitmap getBitmapForFile(Context context, String path,
                                      int imageSize, int bmpWidth) {
        // 检查本地文件
        File file = new File(path);
        // 如果本地文件不存在
        if (file.exists()) {
            // 如果本地文件存在，直接读取文件返回Bitmap
            Bitmap bitmap = getBitmapForFile(context, file, imageSize,
                    bmpWidth);
            // 如果文件存在，但是读取图片失败，则删除该图片（错误文件）
            if (bitmap == null) {
                file.delete();
            }
            return bitmap;
        }
        return null;
    }

    /**
     * GIF图片判断
     *
     * @param url
     * @return
     */
    public boolean isGifUrl(String url) {
        if (null == url) {
            return false;
        }
        return url.endsWith(".gif");
    }

    /**
     * GIF图片判断
     *
     * @param file
     * @return
     */
    public boolean isGifFile(File file) {
        try {
            FileInputStream inputStream = new FileInputStream(file);
            int[] flags = new int[5];
            flags[0] = inputStream.read();
            flags[1] = inputStream.read();
            flags[2] = inputStream.read();
            flags[3] = inputStream.read();
            inputStream.skip(inputStream.available() - 1);
            flags[4] = inputStream.read();
            inputStream.close();
            return flags[0] == 71 && flags[1] == 73 && flags[2] == 70 && flags[3] == 56 && flags[4] == 0x3B;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 读取图片文件
     *
     * @param context
     * @param file
     * @param imageSize
     * @param bmpWidth
     * @return
     */
    public static Bitmap getBitmapForFile(Context context, File file, int imageSize, int bmpWidth) {
        // 屏幕宽度
        int widthPixels = context.getResources().getDisplayMetrics().widthPixels;
        // 图片读取配置
        BitmapFactory.Options options = new BitmapFactory.Options();
        // 颜色方式
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        // 缩放比例
        int scal = 1;
        // 图片宽度高度
        int outWidth = 0;
        int outHeight = 0;
        // 非原始比例
        if (imageSize == IMAGE_SIZE_AUTO) {
            // 读取图片信息
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(file.getAbsolutePath(), options);
            outWidth = options.outWidth;
            outHeight = options.outHeight;
            if (outWidth > widthPixels) {
                if (bmpWidth > 0) {
                    scal = outWidth / bmpWidth;
                } else {
                    scal = outWidth / widthPixels;
                }
                // 零值处理
                if (scal <= 0) {
                    scal = 1;
                }
            } else {
                scal = 1;
            }
            //缩放度
            options.inSampleSize = scal;
        }
        // 读取缩放图片
        options.inJustDecodeBounds = false;
        Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath(),
                options);
        // 指定大小,并且指定大小大于0，小于等于屏幕宽度
        if (imageSize == IMAGE_SIZE_SET && bmpWidth > 0
                && bmpWidth <= widthPixels) {
            // 如果指定大小与原图相同
            if (bmpWidth == outWidth) {
                return bitmap;
            }
            // 创建指定大小新图
            Bitmap appoint = Bitmap.createBitmap(bmpWidth, (int) (1.0f
                    * bmpWidth * outHeight / outWidth), bitmap.getConfig());
            // 绘制新图
            new Canvas(appoint).drawBitmap(bitmap, null,
                    new Rect(0, 0, appoint.getWidth(), appoint.getHeight()),
                    null);
            // 释放原图
            bitmap.recycle();
            return appoint;
        } else {
            return bitmap;
        }
    }
}
