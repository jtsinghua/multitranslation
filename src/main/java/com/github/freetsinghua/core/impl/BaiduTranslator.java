package com.github.freetsinghua.core.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.freetsinghua.core.AbstractTranslator;
import com.github.freetsinghua.core.io.ClassPathResource;
import com.github.freetsinghua.core.io.util.StringUtils;
import com.github.freetsinghua.util.CollectionUtils;
import com.github.freetsinghua.util.LanguageUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.util.EntityUtils;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public final class BaiduTranslator extends AbstractTranslator {
    private static final String url = "https://fanyi.baidu.com/v2transapi";

    public BaiduTranslator() {
        super(url);
    }

    @Override
    public void setFormData(String from, String to, String text) {
        requestProperties.add("from", LanguageUtils.getLanguageShort(from));
        requestProperties.add("to", LanguageUtils.getLanguageShort(to));
        requestProperties.add("query", text);
        requestProperties.add("transtype", "translang");
        requestProperties.add("simple_means_flag", "3");
        requestProperties.add("sign", token(text, "320305.131321201"));
        requestProperties.add("token", "bfcf0d72a412c0efc3fed45b98d9acdf");
    }

    /**
     * 网络查询
     *
     * @return 返回查询结果
     * @throws Exception 如果出现错误
     */
    @Override
    public String query() throws Exception {
        HttpPost request = new HttpPost(url);

        request.setEntity(
                new UrlEncodedFormEntity(CollectionUtils.map2list(requestProperties), "UTF-8"));
        request.setHeader(
                "Cookie",
                "BAIDUID=15181292E1C73B915445F0A5AB2C3A1B:FG=1; BIDUPSID=15181292E1C73B915445F0A5AB2C3A1B; PSTM=1532674894; REALTIME_TRANS_SWITCH=1; FANYI_WORD_SWITCH=1; HISTORY_SWITCH=1; SOUND_SPD_SWITCH=1; SOUND_PREFER_SWITCH=1; BDORZ=B490B5EBF6F3CD402E515D22BCDA1598; DOUBLE_LANG_SWITCH=1; from_lang_often=%5B%7B%22value%22%3A%22en%22%2C%22text%22%3A%22%u82F1%u8BED%22%7D%2C%7B%22value%22%3A%22zh%22%2C%22text%22%3A%22%u4E2D%u6587%22%7D%5D; locale=zh; H_PS_PSSID=1450_21126_22159; PSINO=5; Hm_lvt_64ecd82404c51e03dc91cb9e8c025574=1536807962,1536832635,1536832785,1536887993; Hm_lpvt_64ecd82404c51e03dc91cb9e8c025574=1536887993; to_lang_often=%5B%7B%22value%22%3A%22th%22%2C%22text%22%3A%22%u6CF0%u8BED%22%7D%2C%7B%22value%22%3A%22zh%22%2C%22text%22%3A%22%u4E2D%u6587%22%7D%2C%7B%22value%22%3A%22en%22%2C%22text%22%3A%22%u82F1%u8BED%22%7D%5D");
        request.setHeader(
                "User-Agent",
                "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/68.0.3440.106 Safari/537.36");

        request.setHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");

        CloseableHttpResponse response = httpClient.execute(request);
        HttpEntity entity = response.getEntity();

        String result = EntityUtils.toString(entity, "UTF-8");

        EntityUtils.consume(entity);
        response.getEntity().getContent().close();
        response.close();

        return result;
    }

    /**
     * 解析返回结果，获取翻译结果
     *
     * @param text 返回的字符串，一般是json字符串
     * @return 返回翻译结果
     * @throws IOException 若是读取错误
     * @throws IllegalStateException 若是返回的字符串为空
     */
    @Override
    public String parses(String text) throws IOException, IllegalStateException {
        System.out.println(text);

        if (StringUtils.isEmpty(text)) {
            throw new IllegalStateException("the result is empty!!");
        }

        ObjectMapper mapper = new ObjectMapper();
        return mapper.readTree(text).path("trans_result").findPath("dst").toString();
    }

    /**
     * 获取token字段值
     *
     * @param text 要翻译的文本
     * @param gtk 固定字符串
     * @return 返回token
     */
    private String token(String text, String gtk) {
        String result = "";
        ScriptEngine engine = new ScriptEngineManager().getEngineByName("js");
        try {

            ClassPathResource classPathResource = new ClassPathResource("tk/Baidu.js");
            File file = classPathResource.getFile();

            FileReader reader = new FileReader(file);
            engine.eval(reader);
            if (engine instanceof Invocable) {
                Invocable invoke = (Invocable) engine;
                result = String.valueOf(invoke.invokeFunction("token", text, gtk));
            }
        } catch (ScriptException | NoSuchMethodException | IOException e) {
            e.printStackTrace();
        }

        System.out.println("token = " + result);

        return result;
    }
}
