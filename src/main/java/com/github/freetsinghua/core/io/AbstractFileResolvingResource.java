package com.github.freetsinghua.core.io;

import com.github.freetsinghua.core.io.util.ResourceUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

/** @ClassName @Description @Author z.tsinghua @Date 2018/9/13 */
public abstract class AbstractFileResolvingResource extends AbstractResource {

    @Override
    public boolean exists() {
        try {
            URL url = getURL();

            if (ResourceUtils.isFileURL(url)) {
                return getFile().exists();
            } else {
                URLConnection connection = url.openConnection();
                customizeConnection(connection);

                HttpURLConnection httpConn =
                        connection instanceof HttpURLConnection
                                ? (HttpURLConnection) connection
                                : null;
                if (null != httpConn) {
                    int responseCode = httpConn.getResponseCode();
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        return true;
                    } else if (responseCode == HttpURLConnection.HTTP_NOT_FOUND) {
                        return false;
                    }
                }

                if (connection.getContentLength() > 0) {
                    return true;
                }

                if (httpConn != null) {
                    httpConn.disconnect();
                    return false;
                } else {
                    InputStream inputStream = getInputStream();
                    inputStream.close();
                    return true;
                }
            }

        } catch (IOException e) {
            return false;
        }
    }

    @Override
    public boolean isReadable() {
        try {
            URL url = getURL();

            if (ResourceUtils.isFileURL(url)) {
                return getFile().canRead() && !getFile().isDirectory();
            } else {
                return true;
            }

        } catch (IOException e) {
            return false;
        }
    }

    @Override
    public boolean isFile() {
        try {
            URL url = getURL();

            return ResourceUtils.URL_PROTOCOL_FILE.equals(url.getProtocol());

        } catch (IOException e) {
            return false;
        }
    }

    @Override
    public File getFile() throws IOException {
        URL url = getURL();

        return ResourceUtils.getFile(url, getDescription());
    }

    private void customizeConnection(URLConnection con) throws IOException {
        ResourceUtils.useCachesIfNecessary(con);
        if (con instanceof HttpURLConnection) {
            customizeConnection((HttpURLConnection) con);
        }
    }
}
