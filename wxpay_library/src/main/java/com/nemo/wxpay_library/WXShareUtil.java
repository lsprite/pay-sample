package com.nemo.wxpay_library;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;

import com.nemo.wxpay_library.view.ShareDialog;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.modelmsg.WXWebpageObject;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

public class WXShareUtil {

    public static void doShare(final Context context, final String webpageUrl, final String title, final String description, final int drawableID) {
        ShareDialog shareDialog = new ShareDialog(context);
        shareDialog.show();
        shareDialog.setOnClickListener(new ShareDialog.OnClickListener() {
            @Override
            public void OnClick(View v, int position) {
                share(context, webpageUrl, title, description, drawableID, position);
//                switch (position) {
//                    case 0:
//                        share(context, webpageUrl, title, description, 0);
//                        break;
//                    case 1:
//                        share(context, webpageUrl, title, description, 1);
//                    case 2:
//                        share(context, webpageUrl, title, description, 2);
//                        break;
//                    default:
//                        break;
//                }
            }
        });
    }

    /**
     * @param context
     * @param webpageUrl  网页地址
     * @param title       标题
     * @param description 描述
     * @param drawableID  图片id 一定要drawable，mipmap不行
     * @param flag        0:分享给好友 1：分享朋友圈
     */
    private static void share(Context context, String webpageUrl, String title, String description, int drawableID, int flag) {
        IWXAPI iwxapi = WXAPIFactory.createWXAPI(context, Constant.APP_ID, true);
        WXWebpageObject webpage = new WXWebpageObject();
        webpage.webpageUrl = webpageUrl;
        WXMediaMessage msg = new WXMediaMessage(webpage);
        msg.title = title;
        msg.description = description;
        //这里替换一张自己工程里的图片资源
        Bitmap thumb = BitmapFactory.decodeResource(context.getResources(), drawableID);
        msg.setThumbImage(thumb);
        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = String.valueOf(System.currentTimeMillis());
        req.message = msg;
        switch (flag) {
            case 0:
                req.scene = SendMessageToWX.Req.WXSceneSession;
                break;
            case 1:
                req.scene = SendMessageToWX.Req.WXSceneTimeline;
                break;
            case 2:
                req.scene = SendMessageToWX.Req.WXSceneFavorite;
                break;
            default:
                break;
        }
        iwxapi.sendReq(req);
    }

//    private static String buildTransaction(String str) {
//        return (str == null) ? String.valueOf(System.currentTimeMillis()) : str + System.currentTimeMillis();
//    }
}
