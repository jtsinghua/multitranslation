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
import java.io.FileReader;
import java.io.IOException;
import java.util.Objects;

public final class SogouTranslator extends AbstractTranslator {
    private static final String url = "http://fanyi.sogou.com/reventondc/translate";

    public SogouTranslator() {
        super(url);
        LanguageUtils.init("sogou");
    }

    @Override
    public void setProperty(String from, String to, String text) {

        String fromLanguage = LanguageUtils.getLanguageShort(from);
        if (StringUtils.isEmpty(fromLanguage)) {
            throw new IllegalStateException("不支持的语言: [" + from + "]");
        }

        String toLanguage = LanguageUtils.getLanguageShort(to);
        if (StringUtils.isEmpty(toLanguage)) {
            throw new IllegalStateException("不支持的语言：[" + to + "]");
        }

        requestProperties.add("from", fromLanguage);
        requestProperties.add("to", toLanguage);
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

        request.setHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        request.setHeader(
                "Cookie",
                "IPLOC=CN5301; SUID=6C96DDDE5218910A000000005B7BB18C; SUV=1534833035732077; sct=6; ld=zkllllllll2bWEpGlllllVmv4UwlllllTHLQblllllGlllllxZlll5@@@@@@@@@@; LSTMV=219%2C188; LCLKINT=2208; ABTEST=0|1536890877|v17; SELECTION_SWITCH=1; HISTORY_SWITCH=1; MTRAN_ABTEST=0; SNUID=2EB6D6D40A0F7EC141E838000BC2222F");
        request.setHeader(
                "User-Agent",
                "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/68.0.3440.106 Safari/537.36");

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
