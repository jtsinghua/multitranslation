package com.github.freetsinghua.core.io;

import com.github.freetsinghua.core.io.util.ClassUtils;
import com.github.freetsinghua.core.io.util.ObjectUtils;
import com.github.freetsinghua.core.io.util.StringUtils;
import org.jetbrains.annotations.Nullable;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/** @ClassName @Description @Author z.tsinghua @Date 2018/9/13 */
public class ClassPathResource extends AbstractFileResolvingResource {

    private final String path;

    @Nullable private ClassLoader classLoader;

    @Nullable private Class<?> clazz;

    public ClassPathResource(String path) {
        this(path, (ClassLoader) null);
    }

    private ClassPathResource(String path, ClassLoader classLoader) {
        checkNull(path);

        String pathToUse = StringUtils.cleanPath(path);

        if (pathToUse.startsWith("/")) {
            pathToUse = pathToUse.substring(1);
        }

        this.path = pathToUse;
        this.classLoader = classLoader != null ? classLoader : getDefaultClassLoader();
    }

    private ClassLoader getDefaultClassLoader() {

        ClassLoader cl = null;

        try {
            cl = Thread.currentThread().getContextClassLoader();
        } catch (Exception e) {
            //swallow exception
        }

        if (null == cl) {
            cl = ClassPathResource.class.getClassLoader();

            if (null == cl) {
                try {
                    cl = ClassLoader.getSystemClassLoader();
                } catch (Throwable throwable) {
                    //swallow exception
                }
            }
        }

        return cl;
    }

    private ClassPathResource(String path, @Nullable Class<?> clazz) {
        checkNull(path);

        this.path = StringUtils.cleanPath(path);
        this.clazz = clazz;
    }

    public final String getPath() {
        return this.path;
    }

    @Nullable
    public final ClassLoader getClassLoader() {
        return this.classLoader;
    }

    @Override
    public String getDescription() {
        StringBuilder builder = new StringBuilder("class path resource[");

        String pathToUse = this.path;

        if ((null != this.clazz) && !"/".startsWith(pathToUse)) {
            builder.append(ClassUtils.classPackageAsResourcePath(this.clazz));
            builder.append("/");
        }

        if ("/".startsWith(pathToUse)) {
            pathToUse = pathToUse.substring(1);
        }

        builder.append(pathToUse);
        builder.append("]");

        return builder.toString();
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }

        if (!(other instanceof ClassPathResource)) {
            return false;
        }

        ClassPathResource otherRs = (ClassPathResource) other;

        return this.path.equals(otherRs.path)
                && ObjectUtils.nullSafeEquals(this.classLoader, otherRs.classLoader)
                && ObjectUtils.nullSafeEquals(this.clazz, otherRs.clazz);
    }

    @Override
    public int hashCode() {
        return this.path.hashCode();
    }

    @Override
    public InputStream getInputStream() throws IOException {
        InputStream is;

        if (null != this.clazz) {
            is = this.clazz.getResourceAsStream(this.path);
        } else if (null != this.classLoader) {
            is = this.classLoader.getResourceAsStream(this.path);
        } else {
            is = ClassLoader.getSystemResourceAsStream(this.path);
        }

        if (null == is) {
            throw new FileNotFoundException(
                    getDescription() + " cannot be opened because it does not exist");
        }

        return is;
    }

    @Override
    public URL getURL() throws IOException {
        URL url = resolveURL();
        if (null == url) {
            throw new FileNotFoundException(
                    getDescription() + " cannot be resolve to URL because it does not exist");
        }

        return url;
    }

    @Override
    public Resource createRelative(String relativePath) throws IOException {
        String pathToUse = StringUtils.applyRelativePath(this.path, relativePath);

        return this.clazz != null
                ? new ClassPathResource(pathToUse, this.clazz)
                : new ClassPathResource(pathToUse, classLoader);
    }

    @Nullable
    @Override
    public String getFilename() {
        return StringUtils.getFilename(this.path);
    }

    @Override
    public boolean exists() {
        return resolveURL() != null;
    }

    private URL resolveURL() {
        if (null != this.clazz) {
            return this.clazz.getResource(this.path);
        } else if (null != this.classLoader) {
            return this.classLoader.getResource(this.path);
        } else {
            return ClassLoader.getSystemResource(this.path);
        }
    }

    private void checkNull(String path) {
        if (null == path) {
            throw new IllegalStateException("Path must not be null");
        }
    }
}
