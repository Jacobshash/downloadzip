package com.loltoulan.downloadzip.service;

import com.loltoulan.downloadzip.utils.CompressUtil;
import com.loltoulan.downloadzip.utils.ZipUtil;

import cn.hutool.core.io.FileUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

@Service
@Slf4j
@RequiredArgsConstructor
public class DownloadZipServiceImpl implements DownloadZipService {

    @Override
    public void downloadZip(HttpServletResponse response) throws IOException {
        String zipFileName = "myFiles";
        String sourceFilePath = "D:\\giteeblog\\loltoulanblog";
        CompressUtil.httpDownloadCompressFile(sourceFilePath, response, zipFileName);
    }

    @Override
    public void downloadEncryptedZip(HttpServletResponse response) throws IOException {
        getResourceResponseEntity(response);
    }

    private static void getResourceResponseEntity(HttpServletResponse response) throws IOException {
        // 要压缩的文件或目录路径
        String sourceFilePath = "E:\\User\\lolto\\Desktop\\sql";
        // 生成的ZIP文件路径
        String zipFilePath = "E:\\User\\lolto\\Desktop\\sql-123.zip";
        // ZIP文件密码
        String password = "123456";

        // 生成加密ZIP文件
        ZipUtil.createEncryptedZip(sourceFilePath, zipFilePath, password);

        // 创建文件资源
        File file = new File(zipFilePath);
        String encodeFileName = URLEncoder.encode(file.getName(), StandardCharsets.UTF_8);
        response.setContentType("application/octet-stream");
        response.setCharacterEncoding("utf-8");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + encodeFileName + ".zip\"");

        try (ServletOutputStream servletOutputStream = response.getOutputStream()) {
            try (FileInputStream fileInputStream = new FileInputStream(file)) {
                fileInputStream.transferTo(servletOutputStream);
            }
            servletOutputStream.flush();
        }
        FileUtil.del(zipFilePath);
    }
}
