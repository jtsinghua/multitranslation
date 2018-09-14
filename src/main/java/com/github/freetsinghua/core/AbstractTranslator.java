package com.github.freetsinghua.core;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

/** @ClassName @Description @Author z.tsinghua @Date 2018/9/13 */
public abstract class AbstractTranslator extends AbstractHttpAttribute {
    public AbstractTranslator(String url) {
        super(url);
    }

    public abstract void setProperty(String from, String to, String text)
            throws UnsupportedEncodingException;

    public abstract String parses(String text) throws IOException;

    @Override
    public String run(String from, String to, String text) {
        String result = "";
        try {
            setProperty(from, to, text);
            result = parses(query());
        } catch (Exception e) {
            e.printStackTrace();
        }
        close();

        return result;
    }
}
