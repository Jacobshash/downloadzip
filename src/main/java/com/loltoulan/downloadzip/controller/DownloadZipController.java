package com.loltoulan.downloadzip.controller;

import com.loltoulan.downloadzip.service.DownloadZipService;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

@RestController
@Slf4j
@RequiredArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
public class DownloadZipController {
    private final DownloadZipService downloadZipService;

    @RequestMapping("/download/zip/no-password")
    public void downloadZip(){
        // downloadZipService.downloadZip();
    }

    @GetMapping(value = "/download/zip/testFileToZip")
    public void testFileToZip(HttpServletResponse response) throws IOException {
        downloadZipService.downloadZip(response);
    }
}
