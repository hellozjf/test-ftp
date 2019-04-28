package com.hellozjf.test.testftp;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.springframework.core.io.ClassPathResource;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
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
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

    /**
     * 开启ftp上传服务
     * @param fileWatcher
     * @param customConfig
     */
    private void startFtp(FileWatcher fileWatcher,
                          CustomConfig customConfig,
                          TranslateService translateService,
                          SystemClipboardMonitor systemClipboardMonitor) throws IOException {

        fileWatcher.setPath(customConfig.getPath());
        fileWatcher.handleEvents();


        // 判断是否支持系统托盘
        if (SystemTray.isSupported()) {
            // 获取图片所在的URL
            ClassPathResource classPathResource = new ClassPathResource("app.png");
            URL url = classPathResource.getURL();
            log.debug("url = {}", url.getPath());
            // 实例化图像对象
            ImageIcon icon = new ImageIcon(url);
            // 获得Image对象
            Image image = icon.getImage();
            PopupMenu popupMenu = new PopupMenu();
            // CheckBoxMenuItem，是否要复制开启翻译功能
            CheckboxMenuItem enableTranslate = new CheckboxMenuItem("enable translate");
            enableTranslate.addItemListener(e -> {
                log.debug("e = {}", e);
                systemClipboardMonitor.init();
            });
            MenuItem exit = new MenuItem("exit");
            exit.addActionListener(e -> {
                log.debug("e = {}", e);
                System.exit(0);
            });
            popupMenu.add(enableTranslate);
            popupMenu.addSeparator();
            popupMenu.add(exit);
            // 创建托盘图标
            TrayIcon trayIcon = new TrayIcon(image, "test-ftp持续为您服务", popupMenu);
            // 获得系统托盘对象
            SystemTray systemTray = SystemTray.getSystemTray();
            try {
                // 为系统托盘加托盘图标
                systemTray.add(trayIcon);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            JOptionPane.showMessageDialog(null, "not support");
        }
    }


    @Bean
    public CommandLineRunner commandLineRunner(FileWatcher fileWatcher,
                                               CustomConfig customConfig,
                                               TranslateService translateService,
                                               SystemClipboardMonitor systemClipboardMonitor) {
        return args -> {
            startFtp(fileWatcher, customConfig, translateService, systemClipboardMonitor);
        };
    }
}
