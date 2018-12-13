package com.hellozjf.test.testftp;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPClientConfig;
import org.apache.commons.net.ftp.FTPReply;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Bean;

import javax.swing.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;

@SpringBootApplication
@Slf4j
public class TestFtpApplication {

    public static void main(String[] args) {
        // springboot默认不开启图形界面，因此也无法使用剪切板，通过设置headless(false)开启图形界面
        new SpringApplicationBuilder(TestFtpApplication.class)
                .headless(false)
                .run(args);
    }

    @Bean
    public CommandLineRunner commandLineRunner(FileWatcher fileWatcher, CustomConfig customConfig) {
        return args -> {

            fileWatcher.setPath(customConfig.getPath());
            fileWatcher.handleEvents();

        };
    }
}
