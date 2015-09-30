package com.liurenyou.im;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.SendMessageToWX;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.mm.sdk.openapi.WXImageObject;
import com.tencent.mm.sdk.openapi.WXMediaMessage;
import com.tencent.mm.sdk.openapi.WXTextObject;
import com.tencent.mm.sdk.openapi.WXWebpageObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;

/**
 * Created by keyf on 2015/9/18.
 */
public class WeixinShareManager {

    private static final int THUMB_SIZE = 150;
    /**
     * 文字
     */
    public static final int WEIXIN_SHARE_WAY_TEXT = 1;
    /**
     * 图片
     */
    public static final int WEIXIN_SHARE_WAY_PIC = 2;
    /**
     * 链接
     */
    public static final int WEIXIN_SHARE_WAY_WEBPAGE = 3;
    /**
     * 会话
     */
    public static final int WEIXIN_SHARE_TYPE_TALK = SendMessageToWX.Req.WXSceneSession;
    /**
     * 朋友圈
     */
    public static final int WEIXIN_SHARE_TYPE_FRENDS = SendMessageToWX.Req.WXSceneTimeline;
    private static WeixinShareManager instance;
    private static String weixinAppId;
    private IWXAPI wxApi;
    private Context context;

    private WeixinShareManager(Context context) {
        this.context = context;
        weixinAppId = Constants.APP_ID;

        if (weixinAppId != null) {
            initWeixinShare(context);
        }
    }


    public static WeixinShareManager getInstance(Context context) {
        if (instance == null) {
            instance = new WeixinShareManager(context);
        }
        return instance;
    }

    private void initWeixinShare(Context context) {
        wxApi = WXAPIFactory.createWXAPI(context, weixinAppId, true);
        wxApi.registerApp(weixinAppId);
    }


    public void shareByWeixin(ShareContent shareContent, int shareType) {
        switch (shareContent.getShareWay()) {
            case WEIXIN_SHARE_WAY_TEXT:
                shareText(shareType, shareContent);
                break;
            case WEIXIN_SHARE_WAY_PIC:
                sharePicture(shareType, shareContent);
                break;
            case WEIXIN_SHARE_WAY_WEBPAGE:
                shareWebPage(shareType, shareContent);
                break;
        }
    }

    private abstract class ShareContent {
        protected abstract int getShareWay();

        protected abstract String getContent();

        protected abstract String getTitle();

        protected abstract String getURL();

        protected abstract int getPicResource();

    }


    public class ShareContentText extends ShareContent {
        private String content;

        public ShareContentText(String content) {
            this.content = content;
        }

        @Override
        protected String getContent() {
            return content;
        }

        @Override
        protected String getTitle() {
            return null;
        }

        @Override
        protected String getURL() {
            return null;
        }

        @Override
        protected int getPicResource() {
            return -1;
        }

        @Override
        protected int getShareWay() {
            return WEIXIN_SHARE_WAY_TEXT;
        }

    }


    public class ShareContentPic extends ShareContent {
        private int picResource;

        private String url;

        private String content;

        public ShareContentPic(int picResource) {
            this.picResource = picResource;
        }

        public ShareContentPic(String picResource) {
            this.url = picResource;
        }

        public ShareContentPic(String content, boolean flag) {
            this.content = content;
        }

        @Override
        protected String getContent() {
            return this.content;
        }

        @Override
        protected String getTitle() {
            return null;
        }

        @Override
        protected String getURL() {
            return this.url;
        }

        @Override
        protected int getPicResource() {
            return picResource;
        }

        @Override
        protected int getShareWay() {
            return WEIXIN_SHARE_WAY_PIC;
        }
    }


    public class ShareContentWebpage extends ShareContent {
        private String title;
        private String content;
        private String url;
        private int picResource;

        public ShareContentWebpage(String title, String content,
                                   String url, int picResource) {
            this.title = title;
            this.content = content;
            this.url = url;
            this.picResource = picResource;
        }

        @Override
        protected String getContent() {
            return content;
        }

        @Override
        protected String getTitle() {
            return title;
        }

        @Override
        protected String getURL() {
            return url;
        }

        @Override
        protected int getPicResource() {
            return picResource;
        }

        @Override
        protected int getShareWay() {
            return WEIXIN_SHARE_WAY_WEBPAGE;
        }

    }


    private void shareText(int shareType, ShareContent shareContent) {
        String text = shareContent.getContent();

        WXTextObject textObj = new WXTextObject();
        textObj.text = text;

        WXMediaMessage msg = new WXMediaMessage();
        msg.mediaObject = textObj;
        msg.description = text;

        SendMessageToWX.Req req = new SendMessageToWX.Req();

        req.transaction = buildTransaction("textshare");
        req.message = msg;

        req.scene = shareType;
        wxApi.sendReq(req);
    }

    private void sharePicture(int shareType, ShareContent shareContent) {
        if (shareContent.getURL() != null) {
            sharePicture1(shareType, shareContent);
            return;
        }

        if (shareContent.getContent() != null) {
            sharePicture2(shareType, shareContent);
            return;
        }

        Bitmap bmp = BitmapFactory.decodeResource(context.getResources(), shareContent.getPicResource());
        WXImageObject imgObj = new WXImageObject(bmp);

        WXMediaMessage msg = new WXMediaMessage();
        msg.mediaObject = imgObj;

        Bitmap thumbBmp = Bitmap.createScaledBitmap(bmp, THUMB_SIZE, THUMB_SIZE, true);
        bmp.recycle();
        msg.thumbData = WeixinShareUtil.bmpToByteArray(thumbBmp, true);

        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = buildTransaction("imgshareappdata");
        req.message = msg;
        req.scene = shareType;
        wxApi.sendReq(req);
    }

    private void sharePicture2(int shareType, ShareContent shareContent) {
        byte[] buf = Base64.decode(shareContent.getContent(), Base64.DEFAULT);
        Bitmap bmp = BitmapFactory.decodeByteArray(buf, 0,buf.length);
        //Bitmap bmp = BitmapFactory.decodeResource(context.getResources(), shareContent.getPicResource());
        WXImageObject imgObj = new WXImageObject(bmp);

        WXMediaMessage msg = new WXMediaMessage();
        msg.mediaObject = imgObj;

        Bitmap thumbBmp = Bitmap.createScaledBitmap(bmp, 120,120, true);//THUMB_SIZE, THUMB_SIZE, true);
        bmp.recycle();
        msg.thumbData = WeixinShareUtil.bmpToByteArray(thumbBmp, true);

        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = buildTransaction("imgshareappdata");
        req.message = msg;
        req.scene = shareType;
        wxApi.sendReq(req);
    }


    private void sharePicture1(final int shareType, final ShareContent shareContent) {
        final String url = shareContent.getURL();
        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg1) {

                Bitmap bmp = (Bitmap) msg1.obj;
                WXImageObject imgObj;
                WXMediaMessage msg = new WXMediaMessage();
                String transTag = "img";
                imgObj = new WXImageObject(bmp);
                imgObj.imageUrl = url;

                msg.mediaObject = imgObj;

                Bitmap thumbBmp = Bitmap.createScaledBitmap(bmp, 120, 120, true);//THUMB_SIZE, THUMB_SIZE, true);
                bmp.recycle();
                msg.thumbData = WeixinShareUtil.bmpToByteArray(thumbBmp, true);

                SendMessageToWX.Req req = new SendMessageToWX.Req();
                req.transaction = buildTransaction(transTag);


                req.message = msg;
                req.scene = shareType;
                wxApi.sendReq(req);

            }
        };

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Bitmap bmp = BitmapFactory.decodeStream(new URL(url).openStream());
                    Message msg = Message.obtain();
                    msg.obj = bmp;
                    handler.sendMessage(msg);
                } catch (Exception e) {
                    Log.e("6renyou", e.toString());
                }
            }
        }).start();

    }


    private void shareWebPage(int shareType, ShareContent shareContent) {
        WXWebpageObject webpage = new WXWebpageObject();
        webpage.webpageUrl = shareContent.getURL();
        WXMediaMessage msg = new WXMediaMessage(webpage);
        msg.title = shareContent.getTitle();
        msg.description = shareContent.getContent();

        Bitmap thumb = BitmapFactory.decodeResource(context.getResources(), shareContent.getPicResource());
        if (thumb == null) {
            Toast.makeText(context, "图片不能为空", Toast.LENGTH_SHORT).show();
        } else {
            msg.thumbData = WeixinShareUtil.bmpToByteArray(thumb, true);
        }

        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = buildTransaction("webpage");
        req.message = msg;
        req.scene = shareType;
        wxApi.sendReq(req);
    }

    private String buildTransaction(final String type) {
        return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
    }
}