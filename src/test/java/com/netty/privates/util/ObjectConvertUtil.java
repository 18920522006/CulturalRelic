package com.netty.privates.util;

import com.netty.privates.model.RequestFile;
import org.apache.tools.ant.util.FileUtils;

import javax.activation.MimetypesFileTypeMap;
import java.io.*;

/**
 * @author wangchen
 * @date 2018/3/28 14:45
 */
public class ObjectConvertUtil {
    public static RequestFile[] convert(File[] files) throws Exception {
        if (files != null && files.length > 0) {
            RequestFile[] requestFiles = new RequestFile[files.length];
            for (int i = 0; i < files.length; i++) {
                File file = files[0];
                RequestFile requestFile = new RequestFile();
                requestFile.setFile(file);
                requestFile.setFileName(file.getName());
                requestFile.setFileMd5(MD5FileUtil.getMD5String(file));
                requestFile.setFileType(new MimetypesFileTypeMap().getContentType(file));
                requestFile.setStartPosition(0);
                requestFiles[i] = requestFile;
            }
            return requestFiles;
        } else {
            return new RequestFile[0];
        }
    }
}
