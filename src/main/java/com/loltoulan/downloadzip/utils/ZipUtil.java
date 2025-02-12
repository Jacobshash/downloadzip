package com.loltoulan.downloadzip.utils;

import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.model.enums.CompressionLevel;
import net.lingala.zip4j.model.enums.EncryptionMethod;

import lombok.extern.slf4j.Slf4j;

import java.io.File;

@Slf4j
public class ZipUtil {

    /**
     * 生成加密的ZIP文件
     *
     * @param sourceFilePath 要压缩的文件或目录路径
     * @param zipFilePath    生成的ZIP文件路径
     * @param password       ZIP文件密码
     */
    public static void createEncryptedZip(String sourceFilePath, String zipFilePath, String password) {
        try {
            File fileToAdd = new File(sourceFilePath);
            try (ZipFile zipFile = new ZipFile(zipFilePath)) {
                // 设置ZIP参数
                ZipParameters zipParameters = new ZipParameters();
                zipParameters.setCompressionLevel(CompressionLevel.NORMAL);
                zipParameters.setEncryptFiles(true);
                zipParameters.setEncryptionMethod(EncryptionMethod.AES); // 使用AES加密

                // 设置密码
                zipFile.setPassword(password.toCharArray());

                // 添加文件或目录到ZIP文件中
                if (fileToAdd.isDirectory()) {
                    zipFile.addFolder(fileToAdd, zipParameters);
                } else {
                    zipFile.addFile(fileToAdd, zipParameters);
                }
            }

            System.out.println("加密ZIP文件生成成功: " + zipFilePath);
        } catch (Exception e) {
            log.error("加密ZIP文件生成失败: {}", e.getMessage());
        }
    }
}
