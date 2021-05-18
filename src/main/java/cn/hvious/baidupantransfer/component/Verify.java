package cn.hvious.baidupantransfer.component;

import cn.hvious.baidupantransfer.entity.CookieInfo;
import cn.hvious.baidupantransfer.entity.ShareInfo;
import cn.hvious.baidupantransfer.util.Tools;
import com.alibaba.fastjson.JSONObject;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @Auther: lc
 * @Describe:
 * @Date: create in 2021/5/12 5:57 下午
 */
public class Verify {

    private static final String VERIFY_BASE_URL = "https://pan.baidu.com/share/verify";


    /**
     * @param cookieInfo
     * @param simpleShareInfo
     * @return 返回新的cookieInfo
     * @throws IOException
     */
    public static CookieInfo verify(ShareInfo simpleShareInfo, CookieInfo cookieInfo) throws IOException {
        // shrotCode  passCode  cookie
        if (!Tools.cookieCheck(cookieInfo)) {
            System.out.println("[ Verify ] => " + "incorrect cookie!");
            return null;
        }
        if (!Tools.simpleShareInfoCheck(simpleShareInfo)) {
            System.out.println("[ Verify ] => " + "incorrect shareInfo!");
            return null;
        }

        String shortCode = simpleShareInfo.getShortCode();
        Map<String, String> urlParams = new HashMap<>();
        urlParams.put("surl", shortCode);
        urlParams.put("t", System.currentTimeMillis() + "");
        urlParams.put("channel", "chunlei");
        urlParams.put("web", "1");
        urlParams.put("app_id", "250528");
        urlParams.put("bdstoken", simpleShareInfo.getBdsToken());
        urlParams.put("logid", cookieInfo.getLogId());
        urlParams.put("clienttype", "0");

        String url = Tools.combUrlParam(VERIFY_BASE_URL, urlParams);

        FormBody formBody = new FormBody.Builder()
                .add("pwd", simpleShareInfo.getPassCode())
                .add("vcode", "")
                .add("vcode_str", "")
                .build();

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .addHeader("Host", "pan.baidu.com")
                .addHeader("Origin", "https://pan.baidu.com")
                .addHeader("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/90.0.4430.93 Safari/537.36")
                .addHeader("Sec-Fetch-Dest", "empty")
                .addHeader("Sec-Fetch-Mode", "cors")
                .addHeader("Sec-Fetch-Site", "same-origin")
                .addHeader("sec-ch-ua", "\" Not A;Brand\";v=\"99\", \"Chromium\";v=\"90\", \"Google Chrome\";v=\"90\"")
                .addHeader("sec-ch-ua-mobile", "?0")
                .addHeader("Referer", "https://pan.baidu.com/share/init?surl=" + shortCode)
                .addHeader("Cookie", cookieInfo.getCookie())
                .url(url)
                .post(formBody)
                .build();
        Response response = client.newCall(request).execute();  // sync
        if (response.code() != 200) {
            System.out.println("[ Verify ] => HTTP Code: " + response.code());
        }
        if (response.isSuccessful()) {
            String result = response.body().string();
            JSONObject json = JSONObject.parseObject(result);
            System.out.println("[ Verify ] =>  " + json.toJSONString());
            if (json.getInteger("errno") == 105) {
                System.out.println("[ Verify ] => " + "ShortCode Error！ ===>  " + shortCode);
            }
            if (json.getInteger("errno") != 0) {
                System.out.println("[ Verify ] => " + "verify FAILED!");
                return null;
            }

            String newCookie = Tools.changeCookieBDCLND(cookieInfo.getCookie(), json.getString("randsk"));
            if (null == newCookie) {
                System.out.println("[ Verify ] => " + "change cookie FAILED!");
            }
            cookieInfo.setCookie(newCookie);
            return cookieInfo;
        }
        return null;
    }


}
