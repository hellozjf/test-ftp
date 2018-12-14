package com.hellozjf.test.testftp;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPClientConfig;
import org.apache.commons.net.ftp.FTPReply;
import org.springframework.stereotype.Component;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.TemporalField;
import java.util.UUID;

/**
 * @author hellozjf
 */
@Component
@Slf4j
public class FtpUtil {

    /**
     * 将文件上传到uuid目录，并更换文件名
     * @param path
     * @param filename
     * @return
     */
    public boolean uploadUUID(String urlPrefix, String path, String filename) {
        FTPClient ftp = new FTPClient();
        FTPClientConfig config = new FTPClientConfig();
        ftp.configure(config);

        boolean error = false;
        try {
            String server = "aliyun.hellozjf.com";

            log.debug("setControlEncoding");
            ftp.setControlEncoding("UTF-8");

            log.debug("Connected to {}.", server);
            ftp.connect(server);
            if (!FTPReply.isPositiveCompletion(ftp.getReplyCode())) {
                ftp.disconnect();
                System.err.println("FTP server refused connection.");
                System.exit(1);
            }

            log.debug("login");
            if (!ftp.login("hellozjf", "Zjf@1234")) {
                log.error("login failed!");
                System.exit(1);
            }

            log.debug("changeWorkingDirectory");
            if (!ftp.changeWorkingDirectory("/uploads/uuid/")) {
                log.error("changeWorkingDirectory failed!");
                System.exit(1);
            }

            log.debug("transfer file");
            InputStream in = new FileInputStream(path + "/" + filename);
            String suffix = filename.substring(filename.lastIndexOf("."));
            String uuid = UUID.randomUUID().toString().replace("-", "");
            if (!ftp.setFileType(FTP.BINARY_FILE_TYPE)) {
                log.error("setFileType failed!");
                System.exit(1);
            }
            if (!ftp.storeFile(uuid + suffix, in)) {
                log.error("storeFile failed!");
                System.exit(1);
            }
            in.close();

            String clipboardText = "uuid/" + uuid + suffix;
            log.info("{} -> {}", filename, clipboardText);
            ClipBoardUtil.setSysClipboardText(urlPrefix + clipboardText);

            ftp.logout();
        } catch (IOException e) {
            error = true;
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (ftp.isConnected()) {
                try {
                    ftp.disconnect();
                } catch (IOException ioe) {
                    // do nothing
                }
            }
            return !error;
        }
    }

    /**
     * 将文件上传到对应日期目录，保持文件名
     * @param urlPrefix
     * @param path
     * @param filename
     * @return
     */
    public boolean uploadDateFolder(String urlPrefix, String path, String filename) {
        FTPClient ftp = new FTPClient();
        FTPClientConfig config = new FTPClientConfig();
        ftp.configure(config);

        boolean error = false;
        try {
            String server = "aliyun.hellozjf.com";

            log.debug("setControlEncoding");
            ftp.setControlEncoding("UTF-8");

            log.debug("Connected to {}.", server);
            ftp.connect(server);
            if (!FTPReply.isPositiveCompletion(ftp.getReplyCode())) {
                ftp.disconnect();
                System.err.println("FTP server refused connection.");
                System.exit(1);
            }

            log.debug("login");
            if (!ftp.login("hellozjf", "Zjf@1234")) {
                log.error("login failed!");
                System.exit(1);
            }

            // 获取今天的年月日
            LocalDateTime localDateTime = LocalDateTime.now();
            String year = String.valueOf(localDateTime.getYear());
            String month = String.valueOf(localDateTime.getMonth().getValue());
            String day = String.valueOf(localDateTime.getDayOfMonth());

            // 跳转到对应的/年/月/日目录
            log.debug("changeWorkingDirectory");
            if (!ftp.changeWorkingDirectory("/uploads")) {
                log.error("changeWorkingDirectory failed!");
                System.exit(1);
            }
            if (!ftp.changeWorkingDirectory(year)) {
                log.info("创建文件夹 /uploads/{}", year);
                boolean bMakeResult = ftp.makeDirectory(year);
                log.debug("makeDirectory {}", bMakeResult);
                if (! ftp.changeWorkingDirectory(year)) {
                    log.error("changeWorkingDirectory failed!");
                    System.exit(1);
                }
            }
            if (!ftp.changeWorkingDirectory(month)) {
                log.info("创建文件夹 /uploads/{}/{}", year, month);
                boolean bMakeResult = ftp.makeDirectory(month);
                log.debug("makeDirectory {}", bMakeResult);
                if (! ftp.changeWorkingDirectory(month)) {
                    log.error("changeWorkingDirectory failed!");
                    System.exit(1);
                }
            }
            if (!ftp.changeWorkingDirectory(day)) {
                log.info("创建文件夹 /uploads/{}/{}/{}", year, month, day);
                boolean bMakeResult = ftp.makeDirectory(day);
                log.debug("makeDirectory {}", bMakeResult);
                if (! ftp.changeWorkingDirectory(day)) {
                    log.error("changeWorkingDirectory failed!");
                    System.exit(1);
                }
            }
            log.debug("ftp.workingDirectory = {}", ftp.printWorkingDirectory());

            log.debug("transfer file");
            InputStream in = new FileInputStream(path + "/" + filename);
            if (!ftp.setFileType(FTP.BINARY_FILE_TYPE)) {
                log.error("setFileType failed!");
                System.exit(1);
            }
            // 如果有重名的文件，那对不起直接覆盖了
            if (!ftp.storeFile(filename, in)) {
                log.error("storeFile failed!");
                System.exit(1);
            }
            in.close();

            String clipboardText = year + "/" + month + "/" + day + "/" + filename;
            log.info("{} -> {}", filename, clipboardText);
            ClipBoardUtil.setSysClipboardText(urlPrefix + clipboardText);

            ftp.logout();
        } catch (IOException e) {
            error = true;
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (ftp.isConnected()) {
                try {
                    ftp.disconnect();
                } catch (IOException ioe) {
                    // do nothing
                }
            }
            return !error;
        }
    }
}
