package com.hellozjf.test.testftp;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author Jingfeng Zhou
 */
@Data
@Component
@ConfigurationProperties("baidu")
public class BaiduConfig {
    private String appid;
    private String key;
    private String salt;
}
