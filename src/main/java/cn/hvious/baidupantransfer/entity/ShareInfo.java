package cn.hvious.baidupantransfer.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @Auther: lc
 * @Describe:
 * @Date: create in 2021/5/13 2:24 下午
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ShareInfo {
    private String shortCode;
    private String passCode;

    private String shareId;
    private String uk;
    private String fsId;
    private String bdsToken;


    public ShareInfo(String shortCode, String passCode) {
        this.shortCode = shortCode;
        this.passCode = passCode;
    }

}