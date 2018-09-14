## MultiTrans

### 支持多种翻译工具的调用

   - 谷歌翻译
   - 百度翻译
   - 必应翻译
   - 搜狗翻译
   - 有道翻译

### 若是需要集成其他翻译工具，可以通过继承AbstractTranslator抽象类来扩展

### 各翻译工具所支持的语言，在资源路径下config下面的xml文件中配置，通过LanguageUtils来解析

###Example
  <code>
    Translations translations = new Translations(new YouDaoTranslator());
    
    String result = translations.traslate("中文", "英语", "我恨你");
    
    System.out.println(result);
  </code>

 