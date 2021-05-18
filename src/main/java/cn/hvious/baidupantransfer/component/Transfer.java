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
 * @Date: create in 2021/5/12 5:59 下午
 */
public class Transfer {

    private static final String TRANSFER_BASE_URL = "https://pan.baidu.com/share/transfer";


    public static boolean transferFile(ShareInfo detailShareInfo, CookieInfo cookieInfo, String panPath) throws IOException {
        if (!Tools.cookieCheck(cookieInfo)) {
            System.out.println("[ Transfer ] => " + "incorrect cookie!");
            return false;
        }
        if (!Tools.completeShareInfoCheck(detailShareInfo)) {
            System.out.println("[ Transfer ] => " + "incorrect shareInfo!");
            return false;
        }
        Map<String, String> urlParams = new HashMap<>();
        urlParams.put("shareid", detailShareInfo.getShareId());
        urlParams.put("from", detailShareInfo.getUk());
        urlParams.put("channel", "chunlei");
        urlParams.put("web", "1");
        urlParams.put("app_id", "250528");
        urlParams.put("bdstoken", "5d13b6819a7a65e739444e45b8b19ce4");
        urlParams.put("logid", cookieInfo.getLogId());   // coolie里面 BAIDUID 的base64编码
        urlParams.put("clienttype", "0");

        String url = Tools.combUrlParam(TRANSFER_BASE_URL, urlParams);

        String fsids = "[" + detailShareInfo.getFsId() + "]";
        FormBody formBody = new FormBody.Builder()
                .add("fsidlist", fsids)
                .add("path", panPath)
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
                .addHeader("Upgrade-Insecure-Requests", "1")
                .addHeader("Pragma", "no-cache")
                .addHeader("Referer", "https://pan.baidu.com/s/1" + detailShareInfo.getShortCode())
                .addHeader("Cookie", cookieInfo.getCookie())
                .url(url)
                .post(formBody)
                .build();
        Response response = client.newCall(request).execute();  // sync
        if (response.code() != 200) {
            System.out.println("[ Transfer ] => HTTP Code: " + response.code());
        }
        if (response.isSuccessful()) {
            String result = response.body().string();
            JSONObject json = JSONObject.parseObject(result);
            System.out.println("[ Transfer ] =>  " + json.toJSONString());
            Integer errno = json.getInteger("errno");
            if (errno == 12) System.out.println("[ Transfer ] => Already Exist!");
            return errno == 0;
        }
        return false;
    }
}
