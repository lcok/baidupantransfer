package cn.hvious.baidupantransfer.util;


import cn.hvious.baidupantransfer.entity.CookieInfo;
import cn.hvious.baidupantransfer.entity.ShareInfo;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Auther: lc
 * @Describe:
 * @Date: create in 2021/5/12 5:47 下午
 */
public class Tools {


    public static String changeCookieBDCLND(String sourceCookie, String newBDCLND) {
        if (null == sourceCookie || null == newBDCLND || sourceCookie.equalsIgnoreCase("") || newBDCLND.equalsIgnoreCase(""))
            return null;
        String oldBDCLND = "";

        String regexBDCLND = "BDCLND=[^;]*;";
        Matcher matcher = Pattern.compile(regexBDCLND).matcher(sourceCookie);
        if (matcher.find()) {
            oldBDCLND = matcher.group();
        } else {
            System.out.println("[ Tools ] : Incorrect cookie!  Cannot find BDCLND !");
            return null;
        }
        if (oldBDCLND.equalsIgnoreCase("BDCLND=" + newBDCLND + ";")) return sourceCookie;   // same BDCLND
        // different BDCLND
        String replaceStr = "BDCLND=" + newBDCLND + ";";
        String newCookie = sourceCookie.replaceFirst(regexBDCLND, replaceStr);
        if (newCookie.equalsIgnoreCase(sourceCookie)) {
            return null;
        } else return newCookie;
    }

    public static String getRegexFirstMatchFromStr(String str, String regex) {
        if (null == str || null == regex || str.equalsIgnoreCase("") || regex.equalsIgnoreCase(""))
            return null;
        Matcher matcher = Pattern.compile(regex).matcher(str);
        if (matcher.find()) {  // find first
            return matcher.group();
        } else return null;
    }

    public static String combUrlParam(String baseUrl, Map<String, String> urlParams) {
        if (null == baseUrl || null == urlParams || urlParams.size() == 0) return baseUrl;
        StringBuilder url = new StringBuilder(baseUrl);
        for (Map.Entry<String, String> entry : urlParams.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            if (url.toString().contains("?")) {
                url.append("&").append(key).append("=").append(value);
            } else {
                url.append("?").append(key).append("=").append(value);
            }
        }
        return url.toString();
    }


    /**
     * 从字符串中找提取码
     * 会匹配 "提取码：" 和 "提取码:"
     * 冒号支持使用全角/半角
     * 冒号后可以有空格
     *
     * @param lineStr 带提取的字符串
     * @return 提取码，找不到则为null
     */
    public static String getPassCode(String lineStr) {
        if (null == lineStr || lineStr.equalsIgnoreCase("")) return null;
        String regexCodeCN = "提取码：\\s*[^\\s]*";
        String regexCodeEN = "提取码:\\s*[^\\s]*";

        String pCN = null, pEN = null;
        Matcher matcher = Pattern.compile(regexCodeCN).matcher(lineStr);
        if (matcher.find()) {
            pCN = matcher.group();
            pCN = pCN.substring(4).trim();
        }
        matcher = Pattern.compile(regexCodeEN).matcher(lineStr);
        if (matcher.find()) {
            pEN = matcher.group();
            pEN = pEN.substring(4).trim();
        }

        if (pCN != null) return pCN;
        return pEN;
    }


    /**
     * 从字符串中找出分享链接中的 短分享号（shortCode）
     * 对于一个字符串，如果内有多个分享链接和提取码，只会取第一个
     * 分享链接后面必须用空格和其他内容隔开
     * 对于从分享链接中提取的短分享号，这里采用了简单的判断方法
     * <p>
     * 简单判断方法：
     * 长度为22的，不包含"/"的 字符串
     *
     * @param lineStr 带提取的字符串
     * @return 短链接code，找不到则为null
     */
    public static String getShortCode(String lineStr) {
        if (null == lineStr || lineStr.equalsIgnoreCase("")) return null;
        String regexUrl = "[a-zA-z]+://[^\\s]*";
        Matcher matcher = Pattern.compile(regexUrl).matcher(lineStr);

        String shortCode = null;
        while (matcher.find()) {
            String url = matcher.group();
            String code = url.substring(25);   // https://pan.baidu.com/s/1xxxxxxx
            if (code.contains("/")) {
                continue;
            }
            if (code.length() == 23) {      // 认为最后一位是被意外和链接连接起来的符号
                shortCode = code.substring(0, code.length() - 1);
                break;
            }
            if (code.length() == 22) {
                shortCode = code;
                break;
            }
        }
        return shortCode;
    }


    public static boolean cookieCheck(CookieInfo cookieInfo) {
        if (null == cookieInfo) return false;
        String cookie = cookieInfo.getCookie();
        if (null == cookie || cookie.equalsIgnoreCase("")) return false;
        String regex = "BDCLND=[^;]*;";
        Matcher matcher = Pattern.compile(regex).matcher(cookie);
        if (!matcher.find()) {
            System.out.println("[ Tools ] : Incorrect cookie!  Cannot find BDCLND !");
            return false;
        }
        String regexId = "BAIDUID=[^;]*;";
        Matcher matcherId = Pattern.compile(regexId).matcher(cookie);
        if (!matcherId.find()) {
            System.out.println("[ Tools ] : Incorrect cookie!  Cannot find BAIDUID !");
            return false;
        }
        return true;
    }

    /**
     * 只检测shortCode
     *
     * @param simpleShareInfo
     * @return
     */
    public static boolean slightShareInfoCheck(ShareInfo simpleShareInfo) {
        return null != simpleShareInfo
                && null != simpleShareInfo.getShortCode();
    }

    /**
     * 只检测shortCode & passCode & bdsToken
     *
     * @param simpleShareInfo
     * @return
     */
    public static boolean simpleShareInfoCheck(ShareInfo simpleShareInfo) {
        return null != simpleShareInfo
                && null != simpleShareInfo.getShortCode()
                && null != simpleShareInfo.getPassCode()
                && null != simpleShareInfo.getBdsToken();
    }

    /**
     * 完整检测所有字段
     *
     * @param detailShareInfo
     * @return
     */
    public static boolean completeShareInfoCheck(ShareInfo detailShareInfo) {
        return null != detailShareInfo
                && null != detailShareInfo.getShortCode()
                && null != detailShareInfo.getPassCode()
                && null != detailShareInfo.getBdsToken()
                && null != detailShareInfo.getShareId()
                && null != detailShareInfo.getUk()
                && null != detailShareInfo.getFsId();
    }


    public static String getLogIdFromCookie(String cookie) {
        if (null == cookie || cookie.equalsIgnoreCase("")) return null;
        Base64.Encoder encoder = Base64.getEncoder();
        String regex = "BAIDUID=[^;]*;";
        Matcher matcher = Pattern.compile(regex).matcher(cookie);
        if (!matcher.find()) {
            System.out.println("[ Tools ] : Cannot find BAIDUID !");
            return null;
        }
        String s = matcher.group();
        String baiduId = s.substring(8, s.length() - 1);
        byte[] bytes = baiduId.getBytes(StandardCharsets.UTF_8);
        return encoder.encodeToString(bytes);   // base64 encode
    }

}
