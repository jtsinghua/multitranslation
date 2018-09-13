package com.github.freetsinghua.core.io.util;

import org.jetbrains.annotations.Nullable;

/** @ClassName @Description @Author z.tsinghua @Date 2018/9/13 */
public class ClassUtils {

    /** The package separator character: '.' */
    private static final char PACKAGE_SEPARATOR = '.';
    /** The path separator character: '/' */
    private static final char PATH_SEPARATOR = '/';

    public static String classPackageAsResourcePath(@Nullable Class<?> clazz) {
        if (clazz == null) {
            return "";
        }
        String className = clazz.getName();
        int packageEndIndex = className.lastIndexOf(PACKAGE_SEPARATOR);
        if (packageEndIndex == -1) {
            return "";
        }
        String packageName = className.substring(0, packageEndIndex);
        return packageName.replace(PACKAGE_SEPARATOR, PATH_SEPARATOR);
    }
}
