package com.hellozjf.test.testftp;

import lombok.extern.slf4j.Slf4j;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;

/**
 * @author hellozjf
 */
@Slf4j
public class ClipBoardUtil {

    public static void main(String[] args) {
        setSysClipboardText("复制的内容");
    }

    /**
     * 将字符串复制到剪切板。
     */
    public static void setSysClipboardText(String writeMe) {
        Clipboard clip = Toolkit.getDefaultToolkit().getSystemClipboard();
        Transferable tText = new StringSelection(writeMe);
        clip.setContents(tText, null);
        log.debug("clip.setContents {}", writeMe);
    }

}
