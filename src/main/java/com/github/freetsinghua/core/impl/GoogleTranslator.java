package com.github.freetsinghua.core.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.freetsinghua.core.AbstractTranslator;
import com.github.freetsinghua.core.io.ClassPathResource;
import com.github.freetsinghua.core.io.util.StringUtils;
import com.github.freetsinghua.util.LanguageUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.util.EntityUtils;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

public final class GoogleTranslator extends AbstractTranslator {
    private static final String url = "https://translate.google.cn/translate_a/single";

    private static Integer cookieCnt = 1;

    private static final String COOKIE_BASE =
            "_ga=GA1.3.233903037.1531810972; "
                    + "NID=138=H3VSTKzsssKDJ8sheq_6-mNhWtYNksI2A3aMckdiKxJktvsviyNOrvxseIjK0eJfdQ9HL68evaquXDEsIfVG7LTs77-MslbKk2DzGrm3UKDBQNrgYHjwuw-5p5G3da98; "
                    + "1P_JAR=";

    public GoogleTranslator() {
        super(url);
    }

    @Override
    public void setFormData(String from, String to, String text) {
        requestProperties.add("client", "t");
        requestProperties.add("sl", LanguageUtils.getLanguageShort(from));
        requestProperties.add("tl", LanguageUtils.getLanguageShort(to));
        requestProperties.add("hl", "zh-CN");
        requestProperties.add("dt", "at");
        requestProperties.add("dt", "bd");
        requestProperties.add("dt", "ex");
        requestProperties.add("dt", "ld");
        requestProperties.add("dt", "md");
        requestProperties.add("dt", "qca");
        requestProperties.add("dt", "rw");
        requestProperties.add("dt", "rm");
        requestProperties.add("dt", "ss");
        requestProperties.add("dt", "t");
        requestProperties.add("ie", "UTF-8");
        requestProperties.add("oe", "UTF-8");
        requestProperties.add("source", "btn");
        requestProperties.add("ssel", "0");
        requestProperties.add("tsel", "0");
        requestProperties.add("kc", "0");
        requestProperties.add("tk", token(text));
        requestProperties.add("q", text);
    }

    @Override
    public String query() throws Exception {
        URIBuilder uri = new URIBuilder(url);
        for (String key : requestProperties.keySet()) {
            List<String> values = requestProperties.getValues(key);
            for (String value : values) {
                uri.addParameter(key, value);
            }
        }

        System.out.println(uri.toString());

        HttpUriRequest request = new HttpGet(uri.toString());
        request.setHeader("authority", "translate.google.cn");
        request.setHeader("accept-encoding", "gzip, deflate, br");

        String cookie = COOKIE_BASE + StringUtils.getFormatDateString() + "-" + cookieCnt;

        request.setHeader("cookie", cookie);
        request.setHeader(
                "user-agent",
                "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/68.0.3440.106 Safari/537.36");

        request.setHeader("referer", "https://translate.google.cn/");

        CloseableHttpResponse response = httpClient.execute(request);
        HttpEntity entity = response.getEntity();

        String result = EntityUtils.toString(entity, "utf-8");

        EntityUtils.consume(entity);
        response.getEntity().getContent().close();
        response.close();

        return result;
    }

    @Override
    public String parses(String text) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readTree(text).get(0).get(0).get(0).toString();
    }

    private String token(String text) {
        String tk = "";
        ScriptEngine engine = new ScriptEngineManager().getEngineByName("js");
        try {

            ClassPathResource classPathResource = new ClassPathResource("tk/Google.js");

            FileReader reader = new FileReader(classPathResource.getFile());
            engine.eval(reader);

            if (engine instanceof Invocable) {
                Invocable invoke = (Invocable) engine;
                tk = String.valueOf(invoke.invokeFunction("token", text));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return tk;
    }
}
