package com.ctrlsoft.pt.ncypt.wxapi;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;

import com.ctrlsoft.pt.ncypt.R;
import com.nemo.wxpay_library.Constant;
import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

public class WXPayEntryActivity extends Activity implements IWXAPIEventHandler {

    private static final String TAG = "WXPayEntryActivity";

    private IWXAPI api;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pay_result);

        api = WXAPIFactory.createWXAPI(this, Constant.APP_ID);

        api.handleIntent(getIntent(), this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        api.handleIntent(intent, this);
    }

    @Override
    public void onReq(BaseReq req) {
    }

    @Override
    public void onResp(BaseResp resp) {
//        Log.d(TAG, "onPayFinish, type = " + resp.getType() + ",errCode = \" + resp.errCode");
        if (resp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {
            if (String.valueOf(resp.errCode).equals("0")) {
                Intent intent = new Intent(getPackageName() + ".WxPay.OK");
                sendBroadcast(intent);
                finish();
            } else {
                try {
                    AlertDialog.Builder builder = new AlertDialog.Builder(WXPayEntryActivity.this);
                    builder.setTitle("提示");
                    if (String.valueOf(resp.errCode).equals("-2")) {
                        builder.setMessage("您已取消支付");
                    } else if (String.valueOf(resp.errCode).equals("-1")) {
                        builder.setMessage("因微信支付还未申请成功，暂时不能使用，请选择其它支付方式");
                    } else {
                        builder.setMessage("其他异常,请联系客服");
                    }
                    builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            finish();
                        }
                    });
                    builder.setCancelable(false);
                    builder.show();
                } catch (Exception e) {
                    // TODO: handle exception
                }
            }

        }
    }
}