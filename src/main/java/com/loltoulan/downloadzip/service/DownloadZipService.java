package com.loltoulan.downloadzip.service;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

public interface DownloadZipService {
    void downloadZip(HttpServletResponse response) throws IOException;

    void downloadEncryptedZip(HttpServletResponse response) throws IOException;

}
