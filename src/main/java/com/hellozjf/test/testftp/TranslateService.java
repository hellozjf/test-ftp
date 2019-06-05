package com.hellozjf.test.testftp;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

/**
 * 调用百度翻译api，实现翻译
 * @author hellozjf
 */
@Service
@Slf4j
public class TranslateService {

    @Autowired
    private BaiduConfig baiduConfig;

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * 将q翻译成中文
     * @param q
     * @return
     */
    public String translate(String q) {
        int retryCount = 3;
        for (int i = 0; i < retryCount; i++) {
            try {
                HttpClient httpClient = HttpClient.newHttpClient();
                String urlEncodeQ = null;
                try {
                    urlEncodeQ = URLEncoder.encode(q, "utf-8");
                } catch (UnsupportedEncodingException e) {
                    log.error("e = {}", e);
                }
                String md5 = null;
                try {
                    md5 = DigestUtils.md5DigestAsHex((baiduConfig.getAppid() + q + baiduConfig.getSalt() + baiduConfig.getKey()).getBytes("utf-8")).toLowerCase();
                } catch (UnsupportedEncodingException e) {
                    log.error("e = {}", e);
                    return q;
                }
                String form = String.format("q=%s&from=%s&to=%s&appid=%s&salt=%s&sign=%s",
                        urlEncodeQ, "en", "zh", baiduConfig.getAppid(), baiduConfig.getSalt(), md5);
                log.debug("form = {}", form);
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create("https://fanyi-api.baidu.com/api/trans/vip/translate"))
                        .header("Content-Type", "application/x-www-form-urlencoded")
                        .POST(HttpRequest.BodyPublishers.ofString(form))
                        .build();
                HttpResponse<String> response = null;
                try {
                    response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                } catch (Exception e) {
                    log.error("e = {}", e);
                    return q;
                }
                String body = response.body();
                JsonNode node = null;
                try {
                    log.debug("body = {}", body);
                    node = objectMapper.readTree(body);
                    log.debug("node = {}", node);
                } catch (IOException e) {
                    log.error("e = {}", e);
                    return q;
                }
                ArrayNode arrayNode = (ArrayNode) node.get("trans_result");
                if (arrayNode == null) {
                    return q;
                }
                String dst = arrayNode.get(0).get("dst").asText();
                log.debug("dst = {}", dst);
                return dst;
            } catch (Exception e) {
                log.error("e = {}", e);
            }
        }
        return "翻译服务请求超时";
    }
}
