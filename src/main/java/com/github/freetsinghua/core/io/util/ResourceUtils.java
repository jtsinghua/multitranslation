package com.github.freetsinghua.core.io.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;

/** @ClassName @Description @Author z.tsinghua @Date 2018/9/13 */
public class ResourceUtils {

    /** URL protocol for a file in the file system: "file" */
    public static final String URL_PROTOCOL_FILE = "file";

    public static URI toURI(URL url) throws URISyntaxException {
        return toURI(url.toString());
    }

    /**
     * Create a URI instance for the given location String, replacing spaces with "%20" URI encoding
     * first.
     *
     * @param location the location String to convert into a URI instance
     * @return the URI instance
     * @throws URISyntaxException if the location wasn't a valid URI
     */
    public static URI toURI(String location) throws URISyntaxException {
        return new URI(StringUtils.replace(location, " ", "%20"));
    }

    /**
     * Determine whether the given URL points to a resource in the file system, i.e. has protocol
     * "file", "vfsfile" or "vfs".
     *
     * @param url the URL to check
     * @return whether the URL has been identified as a file system URL
     */
    public static boolean isFileURL(URL url) {
        String protocol = url.getProtocol();
        return URL_PROTOCOL_FILE.equals(protocol);
    }

    public static void useCachesIfNecessary(URLConnection con) {
        con.setUseCaches(con.getClass().getSimpleName().startsWith("JNLP"));
    }

    public static File getFile(URL url, String description) throws FileNotFoundException {

        checkNull(url, "resource URL must not be null");

        if (!URL_PROTOCOL_FILE.equals(url.getProtocol())) {
            throw new FileNotFoundException(
                    description
                            + " cannot be resolved to absolute file path because it does not reside in the file system: "
                            + url);
        }

        try {
            return new File(toURI(url).getSchemeSpecificPart());
        } catch (URISyntaxException e) {
            return new File(url.getFile());
        }
    }

    private static void checkNull(URL url, String s) {
        if (null == url) {
            throw new IllegalStateException(s);
        }
    }
}
