package com.young.weexbase.weex.receiver;

import android.content.Context;
import android.content.Intent;

import com.alibaba.sdk.android.push.MessageReceiver;
import com.alibaba.sdk.android.push.notification.CPushMessage;
import com.google.gson.Gson;
import com.young.weexbase.ui.activity.WeexActivity;
import com.young.weexbase.weex.module.AppModuleLogic;

import org.xutils.common.util.LogUtil;

import java.util.Map;

/**
 *
 */
public class FireAdminMessageReceiver extends MessageReceiver {

    // LOG_TAG
    public static final String REC_TAG = "FireAdmin_Message_Receiver";

    @Override
    public void onNotification(Context context, String title, String summary, Map<String, String> extraMap) {
        LogUtil.e(REC_TAG + " 接收到通知, 标题: " + title + ", 内容: " + summary + ", 参数: " + extraMap);
    }

    @Override
    public void onMessage(Context context, CPushMessage cPushMessage) {
        LogUtil.e(REC_TAG + "接收到消息, 消息ID: " + cPushMessage.getMessageId() + ", 标题: " + cPushMessage.getTitle() + ", 内容:" + cPushMessage.getContent());
    }

    @Override
    public void onNotificationOpened(Context context, String title, String summary, String extraMap) {
        LogUtil.e(REC_TAG + "通知被打开, 标题: " + title + ", 内容: " + summary + ", 参数:" + extraMap);
        startPage(context, extraMap);
    }

    @Override
    protected void onNotificationClickedWithNoAction(Context context, String title, String summary, String extraMap) {
        LogUtil.e(REC_TAG + "无跳转逻辑通知打开, 标题: " + title + ", 内容: " + summary + ", 参数:" + extraMap);
        startPage(context, extraMap);
    }

    @Override
    protected void onNotificationReceivedInApp(Context context, String title, String summary, Map<String, String> extraMap, int openType, String openActivity, String openUrl) {
        LogUtil.e(REC_TAG + "应用内到接收到通知, 标题: " + title + ", 内容: " + summary + ", 参数:" + extraMap + ", openType:" + openType + ", openActivity:" + openActivity + ", openUrl:" + openUrl);
    }

    @Override
    protected void onNotificationRemoved(Context context, String messageId) {
        LogUtil.e(REC_TAG + "通知删除");
    }

    /**
     * 下方是处理打开逻辑
     */

    /**
     * 启动指定页面
     *
     * @param context
     */
    public static void startPage(Context context, String param) {
        try {
            //获取param参数
            param = AppModuleLogic.getMapValue(new Gson().fromJson(param, Map.class), "param", "{}");
        } catch (Exception e) {
            param = "{}";
        }
        Intent intent = new Intent(context, WeexActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(WeexActivity.DATA, "{\"noticeAction\":" + param + "}");
        context.startActivity(intent);
    }
}