package com.hellozjf.test.testftp;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;

/**
 * @author hellozjf
 */
@Slf4j
@Component
public class FileWatcher {

    private Executor executor = Executors.newFixedThreadPool(10);
    private WatchService watcher;
    private Path path;

    @Autowired
    private FtpUtil ftpUtil;

    @Autowired
    private CustomConfig customConfig;

    /**
     * 设置文件夹的字符串地址
     *
     * @param path
     */
    public void setPath(String path) {
        setPath(Paths.get(path));
    }

    /**
     * 只能设置一次Path
     *
     * @param path
     */
    private void setPath(Path path) {
        if (this.path != null) {
            log.error("path already set!");
            return;
        }
        try {
            watcher = FileSystems.getDefault().newWatchService();
            path.register(watcher, ENTRY_CREATE);
        } catch (IOException e) {
            log.error("register failed! e = {}", e);
            return;
        }
        this.path = path;
    }

    public void handleEvents() {

        if (path == null) {
            log.error("path does not set!");
            return;
        }

        executor.execute(() -> {
            try {
                // start to process the data files
                while (true) {

                    // start to handle the file change event
                    WatchKey key = watcher.take();

                    for (WatchEvent<?> event : key.pollEvents()) {
                        // get event type
                        WatchEvent.Kind<?> kind = event.kind();

                        // get file name
                        WatchEvent<Path> pathWatchEvent = (WatchEvent<Path>) event;
                        Path fileName = pathWatchEvent.context();

                        if (kind == ENTRY_CREATE) {

                            // 说明点1
                            // create a new thread to monitor the new file
                            executor.execute(() -> {
                                File file = new File(path.toFile().getAbsolutePath() + "/" + fileName);
                                boolean exist;
                                long size = 0;
                                long lastModified = 0;
                                int sameCount = 0;
                                while (exist = file.exists()) {
                                    // if the 'size' and 'lastModified' attribute keep same for 3 times,
                                    // then we think the file was transferred successfully
                                    if (size == file.length() && lastModified == file.lastModified()) {
                                        if (++sameCount >= customConfig.getWaitCount()) {
                                            break;
                                        }
                                    } else {
                                        size = file.length();
                                        lastModified = file.lastModified();
                                    }
                                    try {
                                        Thread.sleep(customConfig.getWaitTime());
                                    } catch (InterruptedException e) {
                                        return;
                                    }
                                }
                                // if the new file was cancelled or deleted
                                if (!exist) {
                                    return;
                                } else {
                                    // update database ...
                                    log.debug("add file {} to ftp", fileName);
                                    // 将文件发送给FTP
                                    ftpUtil.uploadUUID(path.toFile().getAbsolutePath(), fileName.toFile().getName());
                                }
                            });
                        }
                    }

                    // IMPORTANT: the key must be reset after processed
                    if (!key.reset()) {
                        return;
                    }
                }
            } catch (InterruptedException e) {
                log.error("程序异常被中断");
            }
        });

    }
}
