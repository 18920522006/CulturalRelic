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
    public static RequestFile convert(File file) throws Exception {
        RequestFile requestFile = new RequestFile();
        requestFile.setFile(file);
        requestFile.setFileName(file.getName());
        requestFile.setFileMd5(MD5FileUtil.getMD5String(file));
        requestFile.setFileType(new MimetypesFileTypeMap().getContentType(file));
        /**
         * 默认起始位置为0
         */
        requestFile.setStartPosition(0);
        return requestFile;
    }
}
