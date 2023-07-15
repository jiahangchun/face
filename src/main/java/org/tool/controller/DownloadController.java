package org.tool.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;
import org.tool.Digest;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Administrator
 */
@ShellComponent
@Slf4j
public class DownloadController {

    @Autowired
    private Digest digest;


    @ShellMethod(value = "从智能魔方盒中下载人脸图片", key = {"face"}, group = "智能分析盒")
    public void add(@ShellOption(value = "ip", help = "智能盒子ip地址，需要再同一网段", defaultValue = "192.168.90.210") String ipAddress,
                    @ShellOption(value = "pwd", help = "魔方盒子密码", defaultValue = "moredian@123") String pwd,
                    @ShellOption(defaultValue = Digest.PIC_FILE_PATH, help = "图片下载地址") String picDownloadFilePath) {
        log.info("开始从魔方盒中获取人脸信息 智能盒子ip：{} 魔方盒子密码:{} 图片下载地址:{}",
                ipAddress,
                pwd,
                picDownloadFilePath);
        digest.process(ipAddress,
                pwd,
                picDownloadFilePath);
    }


}
