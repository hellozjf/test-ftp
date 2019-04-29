package com.hellozjf.test.testftp;

import com.google.googlejavaformat.java.Formatter;
import com.google.googlejavaformat.java.FormatterException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.*;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Jingfeng Zhou
 */
@Service
@Slf4j
public class SystemClipboardMonitor implements ClipboardOwner {

    @Autowired
    private TranslateService translateService;

    private Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();

    public SystemClipboardMonitor(){
        init();
    }

    public void init() {
        //如果剪贴板中有文本，则将它的ClipboardOwner设为自己
        if (clipboard.isDataFlavorAvailable(DataFlavor.stringFlavor)){
            clipboard.setContents(clipboard.getContents(null), this);
        }
    }

    /************
     * 测试代码 *
     * **********
     */
    public static void main(String[] args) {
        SystemClipboardMonitor temp = new SystemClipboardMonitor();
        new JFrame().setVisible(true);
    }

    /**********************************************
     * 如果剪贴板的内容改变，则系统自动调用此方法 *
     **********************************************
     */
    @Override
    public void lostOwnership(Clipboard clipboard, Transferable contents) {

        // 首先需要判断有没有开启翻译
        CheckboxMenuItem enableTranslate = (CheckboxMenuItem) SystemTray.getSystemTray().getTrayIcons()[0].getPopupMenu().getItem(0);
        log.debug("state = {}", enableTranslate.getState());

        String text = null;
        if (enableTranslate.getState()) {
            // 如果不暂停一下，经常会抛出IllegalStateException
            // 猜测是操作系统正在使用系统剪切板，故暂时无法访问
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            // 取出文本并进行一次文本处理
            if (clipboard.isDataFlavorAvailable(DataFlavor.stringFlavor)){
                try {
                    text = (String)clipboard.getData(DataFlavor.stringFlavor);
                } catch (UnsupportedFlavorException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            log.debug("text = {}", text);

            // 判断是否要格式化代码，如果每一行都是数字开头，说明就是代码
            StringBuilder code = new StringBuilder();
            Pattern pattern = Pattern.compile("^\\s*\\d+\\s*(.*)\\s*$");
            String[] lines = text.split("\n");
            boolean match = true;
            for (String line : lines) {
                Matcher matcher = pattern.matcher(line);
                if (matcher.find()) {
                    code.append(matcher.group(1));
                    code.append("\n");
                } else {
                    match = false;
                    break;
                }
            }
            // 说明是格式化代码，我已经得到需要格式化的代码了，现在美化它
            if (match) {
                try {
                    text = new Formatter().formatSource(code.toString());
                } catch (FormatterException e) {
                    log.error("e = {}", e);
                    text = code.toString();
                }
            }

            // 说明不是要格式化代码，那就执行翻译操作
            if (! match) {
                text = text.replaceAll("[\\r\\n]", " ")
                        .replaceAll("’", "'")
                        .replaceAll("”", "\"")
                        .replaceAll("“", "\"")
                        .replaceAll("—", "-");
                if (! ChineseUtil.isChinese(text)) {
                    log.debug("text = {}", text);
                    text = translateService.translate(text);
                    log.debug("translate = {}", text);
                }
            }

            // 存入剪贴板，并注册自己为所有者
            // 用以监控下一次剪贴板内容变化
            StringSelection tmp = new StringSelection(text);
            clipboard.setContents(tmp, this);
        }
    }
}
