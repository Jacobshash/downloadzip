package com.loltoulan.downloadzip.config;

import lombok.Data;

import java.io.InputStream;


@Data
public class FileInfo {
    private InputStream fileInputStream;

    private String suffix;

    private String fileName;
    
    private boolean isDirectory;
}
