package cn.hvious.baidupantransfer.component;

import cn.hvious.baidupantransfer.entity.ShareInfo;
import cn.hvious.baidupantransfer.util.Tools;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * @Auther: lc
 * @Describe:
 * @Date: create in 2021/5/12 6:02 下午
 */
public class ExtractLinkInfo {

    public static ShareInfo getShareLinkInfoFromOneLine(String lineStr) {
        String passCode = Tools.getPassCode(lineStr);
        String shortCode = Tools.getShortCode(lineStr);
        if (passCode == null || shortCode == null) {
            System.out.println("[ GetSharedInfo ] => " + " Incorrect Line,Cannot find <ShortCode> or <PassCode> ");
            return null;
        }
        return new ShareInfo(shortCode, passCode);
    }


    public static List<ShareInfo> getShareInfosFromFile(String filePath) {
        Path path = Paths.get(filePath);
        boolean isExist = Files.exists(path, LinkOption.NOFOLLOW_LINKS);
        if (!isExist) {
            System.out.println("[ GetSharedInfo ] file not exist!");
            return null;
        }
        List<ShareInfo> rtn = new ArrayList<>();
        String lineStr, passCode, shortCode;

        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(filePath));
            while ((lineStr = reader.readLine()) != null) {
                passCode = Tools.getPassCode(lineStr);
                shortCode = Tools.getShortCode(lineStr);
                if (null != passCode && null != shortCode) {
                    System.out.println("[ ShortCode and PassCode ] : " + shortCode + "     " + passCode);
                    rtn.add(new ShareInfo(shortCode, passCode));
                }
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException ignored) {
                }
            }
        }
        return rtn;
    }


}
