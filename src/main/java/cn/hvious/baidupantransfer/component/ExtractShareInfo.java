package cn.hvious.baidupantransfer.component;

import cn.hvious.baidupantransfer.entity.CookieInfo;
import cn.hvious.baidupantransfer.entity.ShareInfo;
import cn.hvious.baidupantransfer.util.Tools;
import okhttp3.*;

import java.io.IOException;

/**
 * @Auther: lc
 * @Describe:
 * @Date: create in 2021/5/12 5:59 下午
 */
public class ExtractShareInfo {


    private static final String SHARE_BASE_URL = "https://pan.baidu.com/s/1";
    private static final String UK_REGEX = "\"share_uk\":\"[^\"]*\"";
    private static final String SHARE_ID_REGEX = "\"shareid\":[^\"]*,";
    private static final String FS_ID_REGEX = "\"fs_id\":[^\"]*,";


    public static ShareInfo extractShareDetailInfo(ShareInfo simpleShareInfo, CookieInfo cookieInfo) throws IOException {
        if (!Tools.cookieCheck(cookieInfo)) {
            System.out.println("[ GetShare ] => " + "incorrect cookie!");
            return null;
        }
        if (!Tools.simpleShareInfoCheck(simpleShareInfo)) {
            System.out.println("[ GetShare ] => " + "incorrect shareInfo!");
            return null;
        }
        String shortCode = simpleShareInfo.getShortCode();


        String url = SHARE_BASE_URL + shortCode, resultHtml = null;
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .addHeader("Host", "pan.baidu.com")
                .addHeader("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/90.0.4430.93 Safari/537.36")
                .addHeader("Sec-Fetch-Dest", "document")
                .addHeader("Sec-Fetch-Mode", "navigate")
                .addHeader("Sec-Fetch-Site", "same-origin")
                .addHeader("sec-ch-ua", "\" Not A;Brand\";v=\"99\", \"Chromium\";v=\"90\", \"Google Chrome\";v=\"90\"")
                .addHeader("sec-ch-ua-mobile", "?0")
                .addHeader("Sec-Fetch-User", "?1")
                .addHeader("Upgrade-Insecure-Requests", "1")
                .addHeader("Pragma", "no-cache")
                .addHeader("Referer", "https://pan.baidu.com/share/init?surl=" + shortCode)
                .addHeader("Cookie", cookieInfo.getCookie())
                .url(url)
                .build();

        Response response = client.newCall(request).execute();  // sync
        if (response.code() != 200) {
            System.out.println("[ GetShare ] => HTTP Code: " + response.code());
        }
        if (response.isSuccessful()) {
            resultHtml = response.body().string();
        } else {
            System.out.println("[ GetShare ] => " + "cannot get document !");
            return null;
        }

        String uk_ = Tools.getRegexFirstMatchFromStr(resultHtml, UK_REGEX);
        String shareId_ = Tools.getRegexFirstMatchFromStr(resultHtml, SHARE_ID_REGEX);
        String fsId_ = Tools.getRegexFirstMatchFromStr(resultHtml, FS_ID_REGEX);

        if (uk_ == null || shareId_ == null || fsId_ == null) {
            System.out.println("[ GetShare ] => " + "cannot find share Info !");
            return null;
        }

        String uk = uk_.substring(12, uk_.length() - 1);
        String shareId = shareId_.substring(10, shareId_.length() - 1);
        String fsId = fsId_.substring(8, fsId_.length() - 1);

        simpleShareInfo.setShareId(shareId);
        simpleShareInfo.setFsId(fsId);
        simpleShareInfo.setUk(uk);
        return simpleShareInfo;

    }
}
