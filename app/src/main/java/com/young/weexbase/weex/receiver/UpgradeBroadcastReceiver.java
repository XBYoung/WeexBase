package com.young.weexbase.weex.receiver;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;
import com.young.weexbase.weex.util.DeviceInfoUtil;
import com.young.weexbase.weex.util.KeyStore;
import org.xutils.common.util.LogUtil;
import java.io.File;

/**
 * Created by Aikexing on 2017/3/20.
 */
public class UpgradeBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        long downloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
        String upgrade_downloadId = KeyStore.getInstance(context).get("upgrade_downloadId", "");
        if (TextUtils.equals(upgrade_downloadId, downloadId + "")) {
            System.out.println(intent.toString());
            DownloadManager manager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
            DownloadManager.Query query = new DownloadManager.Query();
            query.setFilterById(downloadId);
            Cursor cursor = manager.query(query);
            if (!cursor.moveToFirst()) {
                cursor.close();
                return;
            }
            // 下载ID
            long id = cursor.getLong(cursor.getColumnIndex(DownloadManager.COLUMN_ID));
            // 下载请求的状态
            int status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS));
            // 下载文件在本地保存的路径（Android 7.0 以后 COLUMN_LOCAL_FILENAME 字段被弃用, 需要用 COLUMN_LOCAL_URI 字段来获取本地文件路径的 Uri）
            String localFilename = "";
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                //Android 7.0  以下
                localFilename = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_FILENAME));
            } else {
                //Android 7.0 及其以上
                String localFilenameUri = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));
                if (localFilenameUri != null) {
                    File mFile = new File(Uri.parse(localFilenameUri).getPath());
                    localFilename = mFile.getAbsolutePath();
                }
            }
            if (status == DownloadManager.STATUS_SUCCESSFUL) {
                LogUtil.d("版本升级  " + "下载成功,文件路径: " + localFilename);
                DeviceInfoUtil.installApk(context, new File(localFilename));
            }
            if (status == DownloadManager.STATUS_FAILED) {
            }
        }
    }
}
