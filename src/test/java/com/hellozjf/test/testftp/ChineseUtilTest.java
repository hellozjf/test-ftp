package com.hellozjf.test.testftp;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;

/**
 * @author Jingfeng Zhou
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class ChineseUtilTest {

    @Test
    public void isChinese() {
        String s = "hello world";
        log.debug("{}", ChineseUtil.isChinese(s));
        s = "hello 周靖峰";
        log.debug("{}", ChineseUtil.isChinese(s));
    }
}