package cn.hvious.baidupantransfer;


import cn.hvious.baidupantransfer.component.*;
import cn.hvious.baidupantransfer.entity.CookieInfo;
import cn.hvious.baidupantransfer.entity.ShareInfo;
import cn.hvious.baidupantransfer.util.Tools;

import java.io.IOException;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

/**
 * @Auther: lc
 * @Describe:
 * @Date: create in 2021/5/12 11:57 上午
 */
public class Main {

    public static String cookie = null;
    public static String panPath = null;


    public static void main(String[] args) throws IOException, InterruptedException {

        System.out.println("==================Hvious Baidu Pan Batch Transfer==================\n"
                + "Author  : hvious\n"
                + "Github  : https://github.com/hvious/baidupantransfer\n"
                + "License : MIT");
        System.out.println("==================Hvious Baidu Pan Batch Transfer==================");

        cookie = scanner("百度网盘Cookie");
        panPath = scanner("百度网盘存储路径(/开头，根目录为/)");
        String filePath = scanner("存放分享链接的本地文件路径");

        System.out.println("\n==================Baidu Pan Batch Transfer Start==================");

        List<ShareInfo> shareInfosFromFile = ExtractLinkInfo.getShareInfosFromFile(filePath);
        if (shareInfosFromFile == null || shareInfosFromFile.size() == 0) {
            System.out.println("========> this is no link in file!");
            return;
        }

        CookieInfo cookieInfo = combineCookie(cookie);

        String bdsToken = ExtractToken.getBdsToken(shareInfosFromFile.get(0), cookieInfo);
        if (null == bdsToken) {
            System.out.println("========> cannot get bdstoken !");
            return;
        }


        batchTransfer(insertBdsToken(shareInfosFromFile, bdsToken), cookieInfo);
    }

    private static String scanner(String tip) {
        Scanner scanner = new Scanner(System.in);
        StringBuilder sb = new StringBuilder();
        sb.append("请输入 " + tip + " : ");
        System.out.println(sb.toString());
        if (scanner.hasNext()) {
            String ipt = scanner.next();
            if (null != ipt && !ipt.equalsIgnoreCase("")) {
                return ipt;
            }
        }
        throw new RuntimeException("非法的 " + tip + ". ");
    }


    public static List<ShareInfo> insertBdsToken(List<ShareInfo> source, String bdsToken) {
        for (ShareInfo info : source) {
            info.setBdsToken(bdsToken);
        }
        return source;
    }


    public static CookieInfo combineCookie(String cookie) {
        if (null == cookie || cookie.equalsIgnoreCase("")) return null;
        CookieInfo cookieInfo = new CookieInfo();
        cookieInfo.setCookie(cookie);
        if (!Tools.cookieCheck(cookieInfo)) {
            System.out.println("[ combineCookie ] => " + "incorrect cookie!");
            return null;
        }

        cookieInfo.setLogId(Tools.getLogIdFromCookie(cookie));
        return cookieInfo;
    }


    public static void batchTransfer(List<ShareInfo> infosOnlyShortPass, CookieInfo cookieInfo) throws InterruptedException, IOException {
        if (null == infosOnlyShortPass || infosOnlyShortPass.size() == 0) return;
        System.out.println("\n\n========>  Start Transfer  \n");
        Random random = new Random();
        int success = 0;

        for (ShareInfo shareInfo : infosOnlyShortPass) {
            int randemMill = random.nextInt(2) * 1000;
            System.out.println("\nsleep time(ms): " + randemMill);
            Thread.sleep(randemMill);

            System.out.println("========>   [ " + shareInfo.getShortCode() + " ] [ " + shareInfo.getPassCode() + " ]");

            CookieInfo newCookieInfo = Verify.verify(shareInfo, cookieInfo);
            if (null == newCookieInfo) {
                System.out.println("========> Verify failed");
                continue;
            }
            cookieInfo = newCookieInfo;
            cookie = cookieInfo.getCookie();

            ShareInfo newShareInfo = ExtractShareInfo.extractShareDetailInfo(shareInfo, cookieInfo);
            if (null == newShareInfo) {
                System.out.println("========> GetDetailShareInfo failed");
                continue;
            }
            shareInfo = newShareInfo;
            System.out.println("========>  " + shareInfo.toString());

            boolean isSucc = Transfer.transferFile(shareInfo, cookieInfo, panPath);
            if (isSucc) {
                success++;
                System.out.println("========> Transfer success");
            } else
                System.out.println("========> Transfer failed");
        }

        System.out.println("\n\n========>  Now Cookie :\n" + cookie);
        System.out.println("\n==================Baidu Pan Batch Transfer End==================");
        System.out.println("\n========>  DONE :  [ " + success + "/" + infosOnlyShortPass.size() + " ]");
    }


}
