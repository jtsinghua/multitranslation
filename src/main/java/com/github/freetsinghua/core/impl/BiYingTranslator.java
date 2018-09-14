package com.github.freetsinghua.core.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.freetsinghua.core.AbstractTranslator;
import com.github.freetsinghua.core.io.util.StringUtils;
import com.github.freetsinghua.util.CollectionUtils;
import com.github.freetsinghua.util.LanguageUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

/** @ClassName @Description @Author z.tsinghua @Date 2018/9/14 */
public class BiYingTranslator extends AbstractTranslator {

    private static final String URL =
            "https://cn.bing.com/ttranslate?&category=&IG=4B3141D414F74881AB792BF3CAFFB170&IID=translator.5036.3";

    public BiYingTranslator() {
        super(URL);
        LanguageUtils.init("biying");
    }

    @Override
    public void setProperty(String from, String to, String text)
            throws UnsupportedEncodingException {

        String fromLanguage = LanguageUtils.getLanguageShort(from);
        if (StringUtils.isEmpty(fromLanguage)) {
            throw new IllegalStateException("不支持的语言：[" + from + "]");
        }

        String toLanguage = LanguageUtils.getLanguageShort(to);
        if (StringUtils.isEmpty(toLanguage)) {
            throw new IllegalStateException("不支持的语言：[" + to + "]");
        }

        requestProperties.add("text", text);
        requestProperties.add("from", fromLanguage);
        requestProperties.add("to", toLanguage);
    }

    @Override
    public String parses(String text) throws IOException {
        if (StringUtils.isEmpty(text)) {
            throw new IllegalStateException("the result is empty!!");
        }

        ObjectMapper mapper = new ObjectMapper();
        return mapper.readTree(text).path("translationResponse").toString();
    }

    @Override
    public String query() throws Exception {

        HttpPost post = new HttpPost(URL);
        post.setEntity(
                new UrlEncodedFormEntity(CollectionUtils.map2list(requestProperties), "UTF-8"));

        post.setHeader("content-type", "application/x-www-form-urlencoded");
        post.setHeader(
                "cookie",
                "MUID=2B4C3DC0131D60B32C2B31F1171D63DA; SRCHD=AF=NOFORM; SRCHUID=V=2&GUID=32C12280A2924F32A96C6F3B4F5354F7&dmnchg=1; SRCHUSR=DOB=20180719; MUIDB=2B4C3DC0131D60B32C2B31F1171D63DA; ANON=A=7BE2861A8B9155D93E714823FFFFFFFF&E=157a&W=1; NAP=V=1.9&E=1520&C=tDWFqIc3QoAONHdy9R6FKSW5OADpustvIrlFg5h-KtjzfIDgeC1NQQ&W=1; TOptOut=1; _EDGE_S=mkt=zh-cn&SID=0FC4DB90545A6E08235AD7FA55746FDA; _SS=SID=0FC4DB90545A6E08235AD7FA55746FDA&HV=1536892998; SRCHHPGUSR=WTS=63672489796; MSCC=1; btstkn=EpzHSqIrZ%252Bdphu%252FZnUpIJVVh3FZZmUFx1Tj02wAn6EKlrqYLbBVW9uHs2bJhADkW");
        post.setHeader(
                "user-agent",
                "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/68.0.3440.106 Safari/537.36");

        CloseableHttpResponse response = httpClient.execute(post);
        HttpEntity entity = response.getEntity();

        String result = EntityUtils.toString(entity, "utf-8");

        EntityUtils.consume(entity);
        response.getEntity().getContent().close();
        response.close();

        return result;
    }
}
