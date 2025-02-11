package com.loltoulan.downloadzip.utils;

import com.loltoulan.downloadzip.config.FileInfo;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.*;
import java.util.zip.*;

/**
 * @description: 压缩工具类
 */
@SuppressWarnings("JavadocDeclaration")
@Slf4j
public class CompressUtil {


    /**
     * 将文件打包到zip并创建文件
     *
     * @param sourceFilePath
     * @param zipFilePath
     * @throws IOException
     */
    public static void createLocalCompressFile(String sourceFilePath, String zipFilePath) throws IOException {
        createLocalCompressFile(sourceFilePath, zipFilePath, null);
    }

    /**
     * 将文件打包到zip并创建文件
     *
     * @param sourceFilePath
     * @param zipFilePath
     * @param zipName
     * @throws IOException
     */
    public static void createLocalCompressFile(String sourceFilePath, String zipFilePath, String zipName) throws IOException {
        File sourceFile = new File(sourceFilePath);
        if (!sourceFile.exists()) {
            throw new RuntimeException(sourceFilePath + "不存在！");
        }
        if(StringUtils.isBlank(zipName)){
            zipName = sourceFile.getName();
        }
        File zipFile = createNewFile(zipFilePath + File.separator + zipName + ".zip");
        try (FileOutputStream fileOutputStream = new FileOutputStream(zipFile)) {
            compressFile(sourceFile, fileOutputStream);
        }
    }

    /**
     * 获取压缩文件流
     *
     * @param sourceFilePath
     * @return ByteArrayOutputStream
     * @throws IOException
     */
    public static OutputStream compressFile(String sourceFilePath, OutputStream outputStream) throws IOException {
        File sourceFile = new File(sourceFilePath);
        if (!sourceFile.exists()) {
            throw new RuntimeException(sourceFilePath + "不存在！");
        }
        return compressFile(sourceFile, outputStream);
    }

    /**
     * 获取压缩文件流
     *
     * @param sourceFile
     * @return ByteArrayOutputStream
     * @throws IOException
     */
    private static OutputStream compressFile(File sourceFile, OutputStream outputStream) throws IOException {
        try (CheckedOutputStream checkedOutputStream = new CheckedOutputStream(outputStream, new CRC32());
             ZipOutputStream zipOutputStream = new ZipOutputStream(checkedOutputStream)) {
            doCompressFile(sourceFile, zipOutputStream, StringUtils.EMPTY);
            return outputStream;
        }
    }

    /**
     * 处理目录下的文件
     *
     * @param sourceFile
     * @param zipOutputStream
     * @param zipFilePath
     * @throws IOException
     */
    private static void doCompressFile(File sourceFile, ZipOutputStream zipOutputStream, String zipFilePath) throws IOException {
        // 如果文件是隐藏的，不进行压缩
        if (sourceFile.isHidden()) {
            return;
        }
        if (sourceFile.isDirectory()) {// 如果是文件夹
            handDirectory(sourceFile, zipOutputStream, zipFilePath);
        } else {// 如果是文件就添加到压缩包中
            try (FileInputStream fileInputStream = new FileInputStream(sourceFile)) {
                // String fileName = zipFilePath + File.separator + sourceFile.getName();
                String fileName = zipFilePath + sourceFile.getName();
                addCompressFile(fileInputStream, fileName, zipOutputStream);
                // String fileName = zipFilePath.replace("\\", "/") + "/" + sourceFile.getName();
                // addCompressFile(fileInputStream, fileName, zipOutputStream);
            }
        }
    }

    /**
     * 处理文件夹
     *
     * @param dir         文件夹
     * @param zipOut      压缩包输出流
     * @param zipFilePath 压缩包中的文件夹路径
     */
    private static void handDirectory(File dir, ZipOutputStream zipOut, String zipFilePath) throws IOException {
        File[] files = dir.listFiles();
        if (ArrayUtils.isEmpty(files)) {
            ZipEntry zipEntry = new ZipEntry(zipFilePath + dir.getName() + File.separator);
            zipOut.putNextEntry(zipEntry);
            zipOut.closeEntry();
            return;
        }
        for (File file : Objects.requireNonNull(files)) {
            doCompressFile(file, zipOut, zipFilePath + dir.getName() + File.separator);
        }
    }

    /**
     * 获取压缩文件流
     *
     * @param documentList 需要压缩的文件集合
     * @return ByteArrayOutputStream
     */
    public static OutputStream compressFile(List<FileInfo> documentList, OutputStream outputStream) {
        Map<String, List<FileInfo>> documentMap = new HashMap<>();
        documentMap.put("", documentList);
        return compressFile(documentMap, outputStream);
    }

    /**
     * 将文件打包到zip
     *
     * @param documentMap 需要下载的附件集合 map的key对应zip里的文件夹名
     * @return ByteArrayOutputStream
     */
    public static OutputStream compressFile(Map<String, List<FileInfo>> documentMap, OutputStream outputStream) {
        CheckedOutputStream checkedOutputStream = new CheckedOutputStream(outputStream, new CRC32());
        ZipOutputStream zipOutputStream = new ZipOutputStream(checkedOutputStream);
        try {
            for (Map.Entry<String, List<FileInfo>> documentListEntry : documentMap.entrySet()) {
                String dirName = documentMap.size() > 1 ? documentListEntry.getKey() : "";
                Map<String, Integer> fileNameToLen = new HashMap<>();// 记录单个合同号文件夹下每个文件名称出现的次数（对重复文件名重命名）
                for (FileInfo document : documentListEntry.getValue()) {
                    try {
                        // 防止单个文件夹下文件名重复 对重复的文件进行重命名
                        String documentName = document.getFileName();
                        if (fileNameToLen.get(documentName) == null) {
                            fileNameToLen.put(documentName, 1);
                        } else {
                            int fileLen = fileNameToLen.get(documentName) + 1;
                            fileNameToLen.put(documentName, fileLen);
                            documentName = documentName + "(" + fileLen + ")";
                        }
                        String fileName = documentName + "." + document.getSuffix();
                        if (StringUtils.isNotBlank(dirName)) {
                            fileName = dirName + File.separator + fileName;
                        }
                        addCompressFile(document.getFileInputStream(), fileName, zipOutputStream);
                    } catch (Exception e) {
                        log.info("filesToZip exception :", e);
                    }
                }
            }
        } catch (Exception e) {
            log.error("filesToZip exception:{}", e.getMessage(), e);
        }
        return outputStream;
    }

    /**
     * 将单个文件写入文件压缩包
     *
     * @param inputStream     文件输入流
     * @param fileName        文件在压缩包中的相对全路径
     * @param zipOutputStream 压缩包输出流
     */
    private static void addCompressFile(InputStream inputStream, String fileName, ZipOutputStream zipOutputStream) {
        try (BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream)) {
            ZipEntry zipEntry = new ZipEntry(fileName);
            zipOutputStream.putNextEntry(zipEntry);
            byte[] bytes = new byte[1024];
            int length;
            while ((length = bufferedInputStream.read(bytes)) >= 0) {
                zipOutputStream.write(bytes, 0, length);
                zipOutputStream.flush();
            }
            zipOutputStream.closeEntry();
            // System.out.println("map size, value is " + RamUsageEstimator.sizeOf(zipOutputStream));
        } catch (Exception e) {
            log.info("addFileToZip exception:", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * 通过网络请求下载zip
     *
     * @param sourceFilePath       需要压缩的文件路径
     * @param response            HttpServletResponse
     * @param zipName            压缩包名称
     * @throws IOException
     */
    public static void httpDownloadCompressFile(String sourceFilePath, HttpServletResponse response, String zipName) throws IOException {
        File sourceFile = new File(sourceFilePath);
        if (!sourceFile.exists()) {
            throw new RuntimeException(sourceFilePath + "不存在！");
        }
        if(StringUtils.isBlank(zipName)){
            zipName = sourceFile.getName();
        }
        response.setContentType("application/octet-stream");
        response.setCharacterEncoding("utf-8");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + zipName + ".zip\"");
        try (ServletOutputStream servletOutputStream = response.getOutputStream()){
            CompressUtil.compressFile(sourceFile, servletOutputStream);
            servletOutputStream.flush();
        }
    }

    public static void httpDownloadCompressFileOld(String sourceFilePath, HttpServletResponse response, String zipName) throws IOException {
        try (ServletOutputStream servletOutputStream = response.getOutputStream()){
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            byte[] zipBytes = byteArrayOutputStream.toByteArray();
            response.setContentType("application/download");
            response.setHeader("Content-Disposition", "attachment; filename=\"" + zipName + ".zip\"");
            response.setContentLength(zipBytes.length);
            servletOutputStream.write(zipBytes);
            servletOutputStream.flush();
        }
    }

    /**
     * 通过网络请求下载zip
     *
     * @param sourceFilePath       需要压缩的文件路径
     * @param response            HttpServletResponse
     * @throws IOException
     */
    public static void httpDownloadCompressFile(String sourceFilePath, HttpServletResponse response) throws IOException {
        httpDownloadCompressFile(sourceFilePath, response, null);
    }


    /**
     * 检查文件名是否已经存在，如果存在，就在文件名后面加上“(1)”，如果文件名“(1)”也存在，则改为“(2)”，以此类推。如果文件名不存在，就直接创建一个新文件。
     *
     * @param filename 文件名
     * @return File
     */
    public static File createNewFile(String filename) {
        File file = new File(filename);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                log.error("文件创建失败", e);
            }
        } else {
            String base = filename.substring(0, filename.lastIndexOf("."));
            String ext = filename.substring(filename.lastIndexOf("."));
            int i = 1;
            while (true) {
                String newFilename = base + "(" + i + ")" + ext;
                file = new File(newFilename);
                if (!file.exists()) {
                    try {
                        file.createNewFile();
                    } catch (IOException e) {
                        log.error("文件创建失败", e);
                    }
                    break;
                }
                i++;
            }
        }
        return file;
    }
}
