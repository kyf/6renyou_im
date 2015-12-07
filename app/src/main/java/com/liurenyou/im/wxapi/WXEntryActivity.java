package com.liurenyou.im.wxapi;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.liurenyou.im.Constants;
import com.liurenyou.im.MainActivity;
import com.liurenyou.im.R;
import com.tencent.mm.sdk.openapi.BaseReq;
import com.tencent.mm.sdk.openapi.BaseResp;
import com.tencent.mm.sdk.openapi.ConstantsAPI;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.SendAuth;
import com.tencent.mm.sdk.openapi.ShowMessageFromWX;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.mm.sdk.openapi.WXAppExtendObject;
import com.tencent.mm.sdk.openapi.WXMediaMessage;

import java.util.logging.LogManager;

public class WXEntryActivity extends Activity implements IWXAPIEventHandler {

    private static final int TIMELINE_SUPPORTED_VERSION = 0x21020001;

    private Button gotoBtn, regBtn, launchBtn, checkBtn;

    private IWXAPI api;

    private Context mContext = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.entry);
        mContext = this;
        api = WXAPIFactory.createWXAPI(this, Constants.APP_ID, false);

        api.handleIntent(getIntent(), this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        setIntent(intent);
        api.handleIntent(intent, this);
    }

    @Override
    public void onReq(BaseReq req) {}

    @Override
    public void onResp(BaseResp resp) {
        if(resp instanceof SendAuth.Resp){
            SendAuth.Resp saresp = (SendAuth.Resp) resp;
            Toast.makeText(this, saresp.state + "," + saresp.userName + "," + saresp.token + "," + saresp.resultUrl, Toast.LENGTH_LONG).show();
            Log.e("6renyou", saresp.state + "," + saresp.userName + "," + saresp.token + "," + saresp.resultUrl);
            finish();
            return;
        }


        int result = 0;

        switch (resp.errCode) {
            case BaseResp.ErrCode.ERR_OK:
                result = R.string.errcode_success;
                break;
            case BaseResp.ErrCode.ERR_USER_CANCEL:
                result = R.string.errcode_cancel;
                break;
            case BaseResp.ErrCode.ERR_AUTH_DENIED:
                result = R.string.errcode_deny;
                break;
            default:
                result = R.string.errcode_unknown;
                break;
        }

        Toast.makeText(this, resp.errStr, Toast.LENGTH_LONG).show();
        finish();
    }


}
