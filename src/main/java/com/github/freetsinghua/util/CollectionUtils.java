package com.github.freetsinghua.util;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.*;

/** @ClassName @Description @Author z.tsinghua @Date 2018/9/13 */
public class CollectionUtils {
    public static List<? extends NameValuePair> map2list(MultiValueMap<String, String> paramsMap) {

        List<NameValuePair> listParams = new ArrayList<NameValuePair>();
        Set<String> keys = paramsMap.keySet();

        for (String key : keys) {
            List<String> values = paramsMap.getValues(key);

            if (values.size() != 0) {
                for (String value : values) {
                    listParams.add(new BasicNameValuePair(key, value));
                }
            }
        }

        return listParams;
    }
}
