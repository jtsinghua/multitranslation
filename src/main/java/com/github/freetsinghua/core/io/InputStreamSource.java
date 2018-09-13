package com.github.freetsinghua.core.io;

import java.io.IOException;
import java.io.InputStream;

/**
 * @InterfaceName InputStreamSource
 * @Description Simple interface for objects that are sources for an {@link InputStream}.
 * @Author z.tsinghua
 * @Date 2018/9/13
 */
public interface InputStreamSource {

    /**
     * Return an {@link InputStream} for the content of an underlying resource.
     * @return the input stream for the underlying resource (must not be {@code null})
     * @throws IOException if the content stream could not be opened
     * @throws java.io.FileNotFoundException if the file is not found
     */
    InputStream getInputStream() throws IOException;

}
