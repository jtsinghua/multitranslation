package com.github.freetsinghua.core;

import java.io.IOException;

/**
 * @ClassName
 * @Description
 * @Author z.tsinghua
 * @Date 2018/9/13
 */
public abstract class AbstractTranslator extends AbstractHttpAttribute {
    public AbstractTranslator(String url) {
        super(url);
    }

    public abstract void setFormData(String from, String to, String text);

    public abstract String parses(String text) throws IOException;

    @Override
    public String run(String from, String to, String text) {
        String result = "";
        setFormData(from, to, text);
        try {
            result = parses(query());
        } catch (Exception e) {
            e.printStackTrace();
        }
        close();

        return result;
    }
}
