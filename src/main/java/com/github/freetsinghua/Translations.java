package com.github.freetsinghua;

import com.github.freetsinghua.core.AbstractHttpAttribute;

/** @ClassName @Description @Author z.tsinghua @Date 2018/9/13 */
public class Translations {

    private AbstractHttpAttribute abstractHttpAttribute;

    public Translations(AbstractHttpAttribute abstractHttpAttribute) {
        this.abstractHttpAttribute = abstractHttpAttribute;
    }

    public String traslate(String from, String to, String text) {
        return this.abstractHttpAttribute.run(from, to, text);
    }
}
