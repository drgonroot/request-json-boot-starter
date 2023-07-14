## 📚简介

支持语言: java8    
使用springboot: springboot2
使用json工具: jackson
## 使用指南
EnableRequestJson.class 注解放在应用上

```java
import org.springframework.boot.SpringApplication;

@EnableRequestJson
@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class);
    }
}
```
http发送Post请求，content-type: application/json。
通过RequestJson.class RequestJsonParam.class 提取json中参数值

```json
{
  "other1": "hello world",
  "other2": {
    "other3":"hello world"
  },
  "list": ["1", "2", "3"],
  "set":[1, 2, 3]
}
```

```java
@RestController
public class TestController {
    @RequestMapping(value = "/", method = RequestMethod.POST)
    public void test(@RequestJsonParam String other,
                     @RequestJsonParam(value = "other1") String other1,
                     @RequestJsonParam(value = "other2", required = false) Object other2,
                     @RequestJsonParam(value = "other2.other3", required = false, defaultValue = "other") String other3,
                     @RequestJsonParam long long1,
                     @RequestJsonParam boolean bool,
                     @RequestJsonParam Integer inta,
                     @RequestJsonParam String str,
                     @RequestJsonParam Object object,
                     @RequestJson JsonNode jsonNode,
                     @RequestJsonParam Set<Object> set,
                     @RequestJsonParam List<Object> list,
                     @RequestJsonParam Map<String, Object> testMap) {

    }
}
```