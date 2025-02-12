package com.loltoulan.downloadzip;

import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.model.enums.CompressionLevel;
import net.lingala.zip4j.model.enums.EncryptionMethod;

import org.junit.jupiter.api.Test;
// import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.io.IOException;

// @SpringBootTest
class DownloadzipApplicationTests {

    @Test
    void contextLoads() {
    }

    @Test
    public void testEncryptedZipExample() {
        // 要压缩的文件或目录
        File fileToAdd = new File("E:\\User\\lolto\\Desktop\\美团技术");

        // 生成的ZIP文件路径
        String zipFilePath = "E:\\User\\lolto\\Desktop\\美团技术.zip";

        // 创建ZipFile对象
        ZipFile zipFile = new ZipFile(zipFilePath);

        // 设置ZIP参数
        ZipParameters zipParameters = new ZipParameters();
        zipParameters.setCompressionLevel(CompressionLevel.NORMAL);
        zipParameters.setEncryptFiles(true);
        zipParameters.setEncryptionMethod(EncryptionMethod.ZIP_STANDARD); // 或者使用 EncryptionMethod.AES

        // 设置密码
        char[] password = "123456".toCharArray();
        zipFile.setPassword(password);

        try {
            // 添加文件或目录到ZIP文件中
            if (fileToAdd.isDirectory()) {
                zipFile.addFolder(fileToAdd, zipParameters);
            } else {
                zipFile.addFile(fileToAdd, zipParameters);
            }

            System.out.println("加密ZIP文件生成成功: " + zipFilePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
