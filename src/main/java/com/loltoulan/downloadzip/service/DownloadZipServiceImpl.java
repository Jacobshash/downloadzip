package com.loltoulan.downloadzip.service;

import com.loltoulan.downloadzip.utils.CompressUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

@Service
@Slf4j
@RequiredArgsConstructor
public class DownloadZipServiceImpl implements DownloadZipService {

    @Override
    public void downloadZip(HttpServletResponse response) throws IOException {
        String zipFileName = "myFiles";
        String sourceFilePath = "D:\\giteeblog\\loltoulanblog";
        CompressUtil.httpDownloadCompressFile(sourceFilePath, response, null);
    }
}
