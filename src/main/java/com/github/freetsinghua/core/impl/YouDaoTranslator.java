package com.github.freetsinghua.core.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.freetsinghua.core.AbstractTranslator;
import com.github.freetsinghua.core.io.util.StringUtils;
import com.github.freetsinghua.util.LanguageUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

/** @ClassName @Description @Author z.tsinghua @Date 2018/9/14 */
public class YouDaoTranslator extends AbstractTranslator {
    private static final String URL =
            "http://fanyi.youdao.com/translate_o?smartresult=dict&smartresult=rule";

    public YouDaoTranslator() {
        super(URL);
        LanguageUtils.init("youdao");
    }

    @Override
    public void setProperty(String from, String to, String text)
            throws UnsupportedEncodingException {
        String slat = String.valueOf(System.currentTimeMillis() + (long) (Math.random() * 10 + 1));
        String sign = StringUtils.md5("fanyideskweb" + text + slat + "ebSeFb%=XZ%T[KZ)c(sy!");

        String fromLanguage = LanguageUtils.getLanguageShort(from);
        if (StringUtils.isEmpty(fromLanguage)) {
            throw new IllegalStateException("不支持的语言: [" + from + "]");
        }

        String toLanguage = LanguageUtils.getLanguageShort(to);
        if (StringUtils.isEmpty(toLanguage)) {
            throw new IllegalStateException("不支持的语言：[" + to + "]");
        }

        requestProperties.add("i", text);
        requestProperties.add("from", fromLanguage);
        requestProperties.add("to", toLanguage);
        requestProperties.add("smartresult", "dict");
        requestProperties.add("client", "fanyideskweb");
        requestProperties.add("salt", slat);
        requestProperties.add("sign", sign);
        requestProperties.add("doctype", "json");
        requestProperties.add("version", "2.1");
        requestProperties.add("keyfrom", "fanyi.web");
        requestProperties.add("action", "FY_BY_CLICKBUTTION");
        requestProperties.add("typoResult", "false");
    }

    @Override
    public String parses(String text) throws IOException {

        if (StringUtils.isEmpty(text)){
            throw new IllegalStateException("result is empty.");
        }

        ObjectMapper mapper = new ObjectMapper();
        return  mapper.readTree(text).path("translateResult").findPath("tgt").toString();
    }

    @Override
    public String query() throws Exception {
        HttpPost request = new HttpPost(StringUtils.getUrlWithQueryString(URL, requestProperties));

        request.setHeader("Cookie","OUTFOX_SEARCH_USER_ID=1799185238@10.169.0.83;");
        request.setHeader("Referer","http://fanyi.youdao.com/");
        request.setHeader("User-Agent","Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/65.0.3325.181 Safari/537.36");

        CloseableHttpResponse httpResponse = httpClient.execute(request);
        HttpEntity httpEntity = httpResponse.getEntity();
        String result = EntityUtils.toString(httpEntity, "UTF-8");
        EntityUtils.consume(httpEntity);
        httpResponse.close();

        return result;
    }
}
