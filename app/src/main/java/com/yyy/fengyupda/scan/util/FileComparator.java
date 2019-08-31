package com.yyy.fengyupda.scan.util;
import java.io.File;
import java.util.Comparator;

public class FileComparator implements Comparator<File> {
    @Override
    public int compare(File file1, File file2) {
        if (file1.lastModified() < file2.lastModified()) {
            return 1;// 最后修改的文件在前
        } else {
            return -1;
        }
    }
}
