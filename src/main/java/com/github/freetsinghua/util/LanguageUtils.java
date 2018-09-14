package com.github.freetsinghua.util;

import com.github.freetsinghua.core.io.ClassPathResource;
import org.apache.commons.io.FileUtils;
import org.dom4j.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/** @ClassName @Description @Author z.tsinghua @Date 2018/9/12 */
public final class LanguageUtils {

    private static final Map<String, String> SHORT_LANGUAGE_MAP = new HashMap<String, String>();

    public static void init(String path) {
        String content = null;
        try {
            ClassPathResource resource = new ClassPathResource("config/" + path + ".xml");
            content = FileUtils.readFileToString(resource.getFile(), "utf-8");

        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        Document document = null;
        try {
            document = DocumentHelper.parseText(content);
        } catch (DocumentException e) {
            e.printStackTrace();
            return;
        }

        Element rootElement = document.getRootElement();

        Iterator iterator = rootElement.elementIterator();

        while (iterator.hasNext()) {
            Element next = (Element) iterator.next();

            Attribute attribute = next.attribute("value");
            String value = attribute.getValue();

            String language = next.getText();

            SHORT_LANGUAGE_MAP.put(language, value);
        }
    }

    /**
     * 获取语言简称
     *
     * @param language 语言名称，如简体中文
     * @return 语言简称,如zh-CN
     */
    public static String getLanguageShort(String language) {
        return SHORT_LANGUAGE_MAP.get(language);
    }
}
