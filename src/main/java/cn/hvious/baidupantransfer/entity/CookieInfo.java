package cn.hvious.baidupantransfer.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @Auther: lc
 * @Describe:
 * @Date: create in 2021/5/15 4:58 下午
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class CookieInfo {
    private String cookie;
    private String logId;
}
