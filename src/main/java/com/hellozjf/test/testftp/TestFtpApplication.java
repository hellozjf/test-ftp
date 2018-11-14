package com.hellozjf.test.testftp;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPClientConfig;
import org.apache.commons.net.ftp.FTPReply;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
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
        SpringApplication.run(TestFtpApplication.class, args);
    }

    @Bean
    public CommandLineRunner commandLineRunner(FileWatcher fileWatcher, CustomConfig customConfig) {
        return args -> {

            fileWatcher.setPath(customConfig.getPath());
            fileWatcher.handleEvents();

        };
    }
}
