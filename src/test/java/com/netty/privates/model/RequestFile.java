package com.netty.privates.model;

import java.io.File;
import java.io.Serializable;

/**
 * @author wangchen
 * @date 2018/3/28 9:41
 */
public class RequestFile implements Serializable {
    /**
     * 文件
     */
    private File file;
    /**
     * 文件名
     */
    private String fileName;
    /**
     * 开始位置
     */
    private long startPosition;
    /**
     * 结尾位置
     */
    private long endPosition;
    /**
     * 内容字节数组
     */
    private byte[] content;
    /**
     * 文件Md5值
     */
    private String fileMd5;
    /**
     * 片段的MD5值
     */
    private String fileSectionMd5;
    /**
     * 片段数组长度
     */
    private long fileSectionSize;
    /**
     * 文件类型
     */
    private String fileType;
    /**
     * 文件总长
     */
    private long fileSize;

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public long getStartPosition() {
        return startPosition;
    }

    public void setStartPosition(long startPosition) {
        this.startPosition = startPosition;
    }

    public long getEndPosition() {
        return endPosition;
    }

    public void setEndPosition(long endPosition) {
        this.endPosition = endPosition;
    }

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }

    public String getFileMd5() {
        return fileMd5;
    }

    public void setFileMd5(String fileMd5) {
        this.fileMd5 = fileMd5;
    }

    public String getFileSectionMd5() {
        return fileSectionMd5;
    }

    public void setFileSectionMd5(String fileSectionMd5) {
        this.fileSectionMd5 = fileSectionMd5;
    }

    public long getFileSectionSize() {
        return fileSectionSize;
    }

    public void setFileSectionSize(long fileSectionSize) {
        this.fileSectionSize = fileSectionSize;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }
}
