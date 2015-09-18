package com.liurenyou.im;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.Toast;

import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.SendMessageToWX;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.mm.sdk.openapi.WXImageObject;
import com.tencent.mm.sdk.openapi.WXMediaMessage;
import com.tencent.mm.sdk.openapi.WXTextObject;
import com.tencent.mm.sdk.openapi.WXWebpageObject;

/**
 * Created by keyf on 2015/9/18.
 */
public class WeixinShareManager{

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

    private WeixinShareManager(Context context){
        this.context = context;
        weixinAppId = Constants.APP_ID;

        if(weixinAppId != null){
            initWeixinShare(context);
        }
    }


    public static WeixinShareManager getInstance(Context context){
        if(instance == null){
            instance = new WeixinShareManager(context);
        }
        return instance;
    }

    private void initWeixinShare(Context context){
        wxApi = WXAPIFactory.createWXAPI(context, weixinAppId, true);
        wxApi.registerApp(weixinAppId);
    }


    public void shareByWeixin(ShareContent shareContent, int shareType){
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

    private abstract class ShareContent{
        protected abstract int getShareWay();
        protected abstract String getContent();
        protected abstract String getTitle();
        protected abstract String getURL();
        protected abstract int getPicResource();

    }


    public class ShareContentText extends ShareContent{
        private String content;

        public ShareContentText(String content){
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


    public class ShareContentPic extends ShareContent{
        private int picResource;
        public ShareContentPic(int picResource){
            this.picResource = picResource;
        }

        @Override
        protected String getContent() {
            return null;
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
            return picResource;
        }

        @Override
        protected int getShareWay() {
            return WEIXIN_SHARE_WAY_PIC;
        }
    }


    public class ShareContentWebpage extends ShareContent{
        private String title;
        private String content;
        private String url;
        private int picResource;
        public ShareContentWebpage(String title, String content,
                                   String url, int picResource){
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


    private void shareWebPage(int shareType, ShareContent shareContent) {
        WXWebpageObject webpage = new WXWebpageObject();
        webpage.webpageUrl = shareContent.getURL();
        WXMediaMessage msg = new WXMediaMessage(webpage);
        msg.title = shareContent.getTitle();
        msg.description = shareContent.getContent();

        Bitmap thumb = BitmapFactory.decodeResource(context.getResources(), shareContent.getPicResource());
        if(thumb == null){
            Toast.makeText(context, "图片不能为空", Toast.LENGTH_SHORT).show();
        }else{
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