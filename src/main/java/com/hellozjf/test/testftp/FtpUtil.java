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
    public boolean uploadUUID(String path, String filename) {
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
            if (!ftp.changeWorkingDirectory("/vdb1/uploads/uuid/")) {
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

            log.info("{} -> uuid/{}{}", filename, uuid, suffix);

            ftp.logout();
        } catch (IOException e) {
            error = true;
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
