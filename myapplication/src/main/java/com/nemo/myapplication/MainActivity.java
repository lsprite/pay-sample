package com.nemo.myapplication;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.alipay.sdk.pay.demo.util.PayUtil;
import com.ctrlsoft.pt.ncypt.R;
import com.nemo.wxpay_library.WXPayUtil;
import com.nemo.wxpay_library.WXShareUtil;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void myClick(View view) {
        switch (view.getId()) {
            case R.id.alipay:
                PayUtil payUtil = new PayUtil(new PayUtil.CallBack() {
                    @Override
                    public void onFailure(Exception e) {

                    }

                    @Override
                    public void onError(String errorMsg) {

                    }

                    @Override
                    public void alipaySucceed() {

                    }

                    @Override
                    public void alipayError(String resultInfo, String resultStatus) {

                    }
                }, this);
                payUtil.doPay("1", "0.01");
                break;
            case R.id.wxpay:
                WXPayUtil wxPayUtil = new WXPayUtil(new WXPayUtil.CallBack() {
                    @Override
                    public void onFailure(Exception e) {

                    }

                    @Override
                    public void onError(String errorMsg) {

                    }
                }, this);
                wxPayUtil.doPay("1", "0.01");
                break;
            case R.id.dialog:
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("弹出警告框");
                builder.setMessage("确定删除吗？");
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        finish();
                    }
                });
                builder.setCancelable(false);
                builder.show();
                break;
            case R.id.share:
                //drawableID一定要drawable，mipmap不行
                WXShareUtil.doShare(MainActivity.this, "http://www.baidu.com", "标题", "描述", R.drawable.ic_launcher);
                break;
            default:
                break;
        }

    }
}
