package cn.hvious.baidupantransfer.component;

import cn.hvious.baidupantransfer.entity.CookieInfo;
import cn.hvious.baidupantransfer.entity.ShareInfo;
import cn.hvious.baidupantransfer.util.Tools;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;

/**
 * @Auther: lc
 * @Describe:
 * @Date: create in 2021/5/15 5:24 下午
 */
public class ExtractToken {

    private static final String INIT_BASE_URL = "https://pan.baidu.com/share/init?surl=";
    private static final String BDSTOKEN_REGEX = "\"bdstoken\":\"[^\"]*\"";


    public static String getBdsToken(ShareInfo simpleShareInfo, CookieInfo cookieInfo) throws IOException {
        if (!Tools.cookieCheck(cookieInfo)) {
            System.out.println("[ GetToken ] => " + "incorrect cookie!");
            return null;
        }
        if (!Tools.slightShareInfoCheck(simpleShareInfo)) {
            System.out.println("[ GetToken ] => " + "incorrect shareInfo!");
            return null;
        }

        String url = INIT_BASE_URL + simpleShareInfo.getShortCode(), resultHtml = null;
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .addHeader("Host", "pan.baidu.com")
                .addHeader("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/90.0.4430.93 Safari/537.36")
                .addHeader("Sec-Fetch-Dest", "document")
                .addHeader("Sec-Fetch-Mode", "navigate")
                .addHeader("Sec-Fetch-Site", "none")
                .addHeader("sec-ch-ua", "\" Not A;Brand\";v=\"99\", \"Chromium\";v=\"90\", \"Google Chrome\";v=\"90\"")
                .addHeader("sec-ch-ua-mobile", "?0")
                .addHeader("Sec-Fetch-User", "?1")
                .addHeader("Upgrade-Insecure-Requests", "1")
                .addHeader("Cookie", cookieInfo.getCookie())
                .url(url)
                .build();

        Response response = client.newCall(request).execute();  // sync
        if (response.code() != 200) {
            System.out.println("[ GetToken ] => HTTP Code: " + response.code());
        }
        if (response.isSuccessful()) {
            resultHtml = response.body().string();
        } else {
            System.out.println("[ GetToken ] => " + "cannot get document !");
            return null;
        }

        String stoken_ = Tools.getRegexFirstMatchFromStr(resultHtml, BDSTOKEN_REGEX);
        if (stoken_ == null) {
            System.out.println("[ GetToken ] => " + "cannot find BDSTOKEN !");
            return null;
        }
        return stoken_.substring(12, stoken_.length() - 1);
    }
}
