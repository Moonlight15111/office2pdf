package org.moonlight.office2pdf.util;

import org.apache.commons.lang3.StringUtils;
import org.moonlight.office2pdf.common.Const;

import java.io.File;
import java.io.IOException;

/**
 * file util
 * @author Moonlight
 * @date 2021/4/10 11:18
 */
public class FileUtil {

    /**
     * 获取文件后缀名
     * @param path 文件路径
     * @return 文件后缀名
     */
    public static String getFileSuffix(String path) {
        int lastIndex;
        if (StringUtils.isNotBlank(path) && (lastIndex = path.lastIndexOf(Const.DOT)) > -1) {
            return path.substring(lastIndex + 1);
        }
        return null;
    }

    public static boolean createFile(File file) throws IOException {
        if (!file.exists()) {
            boolean mkdirs = true;
            if (!file.getParentFile().exists()) {
                mkdirs = file.getParentFile().mkdirs();
            }
            if (!mkdirs) {
                return false;
            }
            return file.createNewFile();
        }
        return true;
    }

    public static void delFile(String waterMarkImgPath) {
        if (StringUtils.isBlank(waterMarkImgPath)) {
            return;
        }
        File file = new File(waterMarkImgPath);
        if (!file.exists()) {
            return;
        }
        file.delete();
    }
}
