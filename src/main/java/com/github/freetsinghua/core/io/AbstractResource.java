package com.github.freetsinghua.core.io;

import com.github.freetsinghua.core.io.util.NestedIOException;
import com.github.freetsinghua.core.io.util.ResourceUtils;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

/** @ClassName @Description @Author z.tsinghua @Date 2018/9/13 */
public abstract class AbstractResource implements Resource {
    @Override
    public boolean exists() {

        try {
            return getFile().exists();
        } catch (IOException e) {

            try {
                InputStream inputStream = getInputStream();
                inputStream.close();

                return true;
            } catch (IOException e1) {
                return false;
            }
        }
    }

    @Override
    public boolean isReadable() {
        return true;
    }

    @Override
    public boolean isOpen() {
        return false;
    }

    @Override
    public boolean isFile() {
        return false;
    }

    @Override
    public URL getURL() throws IOException {
        throw new FileNotFoundException(getDescription() + " cannot be resolved to URL");
    }

    @Override
    public URI getURI() throws IOException {

        URL url = getURL();

        try {
            return ResourceUtils.toURI(url);
        } catch (URISyntaxException e) {
            throw new NestedIOException("Invalid URI [" + url + "]", e);
        }
    }

    @Override
    public File getFile() throws IOException {
        throw new FileNotFoundException(
                getDescription() + " cannot be resolve to absolute file path");
    }

    @Override
    public long contentLength() throws IOException {

        try (InputStream inputStream = getInputStream()) {

            long size = 0;
            byte[] bytes = new byte[255];
            int read;

            while ((read = inputStream.read(bytes)) != -1) {
                size += read;
            }

            return size;
        }
    }

    @Override
    public long lastModified() throws IOException {
        long lastModified = getFileForLastModifiedCheck().lastModified();

        if (0L == lastModified) {
            throw new FileNotFoundException(
                    getDescription()
                            + " cannot resolved in the file system for resolving its last-modified timestamp");
        }

        return lastModified;
    }

    private File getFileForLastModifiedCheck() throws IOException {
        return getFile();
    }

    @Override
    public Resource createRelative(String relativePath) throws IOException {
        throw new FileNotFoundException("cannot create relative resource for " + getDescription());
    }

    @Nullable
    @Override
    public String getFilename() {
        return null;
    }

    @Override
    public boolean equals(Object other) {
        return this == other
                || (other instanceof Resource
                        && ((Resource) other).getDescription().equals(getDescription()));
    }

    @Override
    public int hashCode() {
        return getDescription().hashCode();
    }

    @Override
    public String toString() {
        return getDescription();
    }
}
