package com.github.freetsinghua.core.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.freetsinghua.core.AbstractTranslator;
import com.github.freetsinghua.core.io.ClassPathResource;
import com.github.freetsinghua.core.io.util.StringUtils;
import com.github.freetsinghua.util.LanguageUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.util.EntityUtils;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Objects;

public final class SogouTranslator extends AbstractTranslator {
    private static final String url = "http://fanyi.sogou.com/reventondc/translate";

    public SogouTranslator() {
        super(url);
    }

    @Override
    public void setFormData(String from, String to, String text) {
        requestProperties.add("from", LanguageUtils.getLanguageShort(from));
        requestProperties.add("to", LanguageUtils.getLanguageShort(to));
        requestProperties.add("client", "pc");
        requestProperties.add("fr", "browser_pc");
        requestProperties.add("text", text);
        requestProperties.add("useDetect", "on");

        // 自动检测语种
        if (Objects.equals(from, LanguageUtils.getLanguageShort("自动检测"))) {
            requestProperties.add("useDetectResult", "on");
        } else {
            requestProperties.add("useDetectResult", "off");
        }

        requestProperties.add("needQc", "1");
        requestProperties.add("uuid", token());
        requestProperties.add("oxford", "on");
        requestProperties.add("isReturnSugg", "off");
    }

    @Override
    public String query() throws Exception {
        HttpPost request = new HttpPost(StringUtils.getUrlWithQueryString(url, requestProperties));

        CloseableHttpResponse httpResponse = httpClient.execute(request);
        HttpEntity httpEntity = httpResponse.getEntity();
        String result = EntityUtils.toString(httpEntity, "UTF-8");
        EntityUtils.consume(httpEntity);
        httpResponse.close();

        return result;
    }

    @Override
    public String parses(String text) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readTree(text).path("translate").findPath("dit").toString();
    }

    private String token() {
        String result = "";
        ScriptEngine engine = new ScriptEngineManager().getEngineByName("js");
        try {

            ClassPathResource classPathResource = new ClassPathResource("tk/Sogou.js");

            FileReader reader = new FileReader(classPathResource.getFile());
            engine.eval(reader);
            if (engine instanceof Invocable) {
                Invocable invoke = (Invocable) engine;
                result = String.valueOf(invoke.invokeFunction("token"));
            }
        } catch (ScriptException | NoSuchMethodException | IOException e) {
            e.printStackTrace();
        }
        return result;
    }
}
