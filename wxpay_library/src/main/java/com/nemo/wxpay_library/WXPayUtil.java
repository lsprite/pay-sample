package com.nemo.wxpay_library;

import android.content.Context;
import android.util.Log;

import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import net.sourceforge.simcpux.MD5;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class WXPayUtil {
    private IWXAPI iwxapi;
    private Context context;
    public CallBack callBack;
    private String url = "http://139.129.99.113/dlgczyWXPayService//WXPayInfo.aspx";

    public WXPayUtil(CallBack callBack, Context context) {
        super();
        this.context = context;
        this.callBack = callBack;
        iwxapi = WXAPIFactory.createWXAPI(context, Constant.APP_ID, true);
        iwxapi.registerApp(Constant.APP_ID);
    }

    public void doPay(String userID, String price) {
        getWxParams(userID, price);
    }

    //获取订单号后获取alipay需要的参数
    private void getWxParams(String userID, String price) {
        OkHttpClient client = new OkHttpClient();
        //创建表单请求参数
        FormBody.Builder builder = new FormBody.Builder();
        builder.add("type", "WXPay_UnifiedOrder");
        builder.add("userID", getURLEncoder(AESUtils.EncryptAsDoNet(userID)));
        builder.add("price", getURLEncoder(AESUtils.EncryptAsDoNet(price)));
        builder.add("clienttype", getURLEncoder(AESUtils.EncryptAsDoNet("0")));
        builder.add("total_fee", getURLEncoder(AESUtils.EncryptAsDoNet(StringUtil.toFen(price))));
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
                    JSONObject contentJson = new JSONObject(value);
                    if (code.equals("10000")) {
                        PayReq req = new PayReq();
                        req.appId = Constant.APP_ID;
                        req.partnerId = Constant.MCH_ID;
                        req.prepayId = contentJson.getString("prepay_id");
                        req.packageValue = "Sign=WXPay";
                        req.nonceStr = genNonceStr();
                        req.timeStamp = String.valueOf(genTimeStamp());
                        List<NameValuePair> signParams = new LinkedList<NameValuePair>();
                        signParams.add(new BasicNameValuePair("appid", req.appId));
                        signParams.add(new BasicNameValuePair("noncestr", req.nonceStr));
                        signParams.add(new BasicNameValuePair("package", req.packageValue));
                        signParams.add(new BasicNameValuePair("partnerid", req.partnerId));
                        signParams.add(new BasicNameValuePair("prepayid", req.prepayId));
                        signParams.add(new BasicNameValuePair("timestamp", req.timeStamp));
                        req.sign = genAppSign(signParams);
                        wxpay(req);
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

    // 跳转到微信支付(真的支付)
    public void wxpay(PayReq req) {
        iwxapi.registerApp(Constant.APP_ID);
        iwxapi.sendReq(req);
    }

    // 微信支付的一些方法
    private String genNonceStr() {
        Random random = new Random();
        return MD5.getMessageDigest(String.valueOf(random.nextInt(10000)).getBytes());
    }

    private long genTimeStamp() {
        return System.currentTimeMillis() / 1000;
    }

    private String genAppSign(List<NameValuePair> params) {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < params.size(); i++) {
            sb.append(params.get(i).getName());
            sb.append('=');
            sb.append(params.get(i).getValue());
            sb.append('&');
        }
        sb.append("key=");
        sb.append(Constant.API_KEY);

        String appSign = MD5.getMessageDigest(sb.toString().getBytes()).toUpperCase();
        Log.e("orion", appSign);
        return appSign;
    }

    //接口失败Failure
    //接口返回错误error
    public interface CallBack {
        void onFailure(Exception e);

        void onError(String errorMsg);

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
