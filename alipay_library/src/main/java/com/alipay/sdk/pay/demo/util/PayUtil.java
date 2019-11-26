package com.alipay.sdk.pay.demo.util;

import android.app.Activity;
import android.text.TextUtils;
import android.util.Log;

import com.alipay.sdk.app.PayTask;
import com.alipay.sdk.pay.demo.AESUtils;
import com.alipay.sdk.pay.demo.PayResult;

import org.json.JSONObject;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class PayUtil {
    public CallBack callBack;
    public Activity activity;
    private String url = "http://139.129.99.113/dlgczyZFBPayService/ZFBOrderPayInfo.aspx";

    public PayUtil(CallBack callBack, Activity activity) {
        super();
        this.callBack = callBack;
        this.activity = activity;
    }

    public void doPay(String userID, String price) {
        getAlipayParams(userID, price);
    }


    //获取订单号后获取alipay需要的参数
    private void getAlipayParams(String userID, String price) {
        OkHttpClient client = new OkHttpClient();
        //创建表单请求参数
        FormBody.Builder builder = new FormBody.Builder();
        builder.add("getkind", "ZFBPay_SignInfo");
        builder.add("userID", getURLEncoder(AESUtils.EncryptAsDoNet(userID)));
        builder.add("price", getURLEncoder(AESUtils.EncryptAsDoNet(price)));
        builder.add("clienttype", getURLEncoder(AESUtils.EncryptAsDoNet("0")));
        FormBody formBody = builder.build();
        Request request = new Request.Builder()
                .url(url)
                .post(formBody)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callBack.onFailure(e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    String json = response.body().string();
                    Log.e("nemo", "---" + url);
                    Log.e("nemo", json);
                    JSONObject dataJson = new JSONObject(json);
                    String code = dataJson.getString("code");
                    String message = dataJson.getString("message");
                    String value = dataJson.getString("value");
                    if (code.equals("10000")) {
                        alipay(value);
                    } else {
                        callBack.onError(message);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    callBack.onFailure(e);
                }

            }
        });
    }


    // 跳转到支付宝支付(真的支付)
    private void alipay(final String params) {
        final Runnable payRunnable = new Runnable() {

            @Override
            public void run() {
                PayTask alipay = new PayTask(activity);
                Map<String, String> result = alipay.payV2(params, true);
                PayResult payResult = new PayResult(result);
                /**
                 * 对于支付结果，请商户依赖服务端的异步通知结果。同步通知结果，仅作为支付结束的通知。
                 */
                String resultInfo = payResult.getResult();// 同步返回需要验证的信息
                String resultStatus = payResult.getResultStatus();
                // 判断resultStatus 为9000则代表支付成功
                if (TextUtils.equals(resultStatus, "9000")) {
                    // 该笔订单是否真实支付成功，需要依赖服务端的异步通知。
                    callBack.alipaySucceed();
                } else {
                    // 该笔订单真实的支付结果，需要依赖服务端的异步通知。
                    callBack.alipayError(resultInfo, resultStatus);
//                    if (TextUtils.equals(resultStatus, "8000")) {
////                        System.out.println("支付结果确认中");
//                    } else {
////                        System.out.println("支付失败");
//                        // 其他值就可以判断为支付失败，包括用户主动取消支付，或者系统返回的错误
//                    }
                }
            }
        };

        // 必须异步调用
        Thread payThread = new Thread(payRunnable);
        payThread.start();

    }

    //接口失败Failure
    //接口返回错误error
    public interface CallBack {
        void onFailure(Exception e);

        void onError(String errorMsg);

        //支付成功
        void alipaySucceed();

        //支付失败
        //resultStatus
        //8000:支付结果确认中
        //其他:支付失败
        void alipayError(String resultInfo,
                         String resultStatus);
    }

    private static String getURLEncoder(String value) {
        try {
            if (value == null || "".equals(value)) {
                return "";
            } else {
                return URLEncoder.encode(value, "utf-8");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

}