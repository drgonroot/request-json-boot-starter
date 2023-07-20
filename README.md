
# request-json-boot-starter: 可多层级提取request body json指定参数值

[![maven central](https://img.shields.io/maven-central/v/com.btye102/request-json-boot-starter.svg?label=Maven%20Central)](https://github.com/drgonroot/request-json-boot-starter)   [![License](https://img.shields.io/:license-MulanPSL2-blue.svg)](http://license.coscl.org.cn/MulanPSL2/index.html)
[![jdk](https://img.shields.io/badge/JDK-8+-green.svg)](https://www.oracle.com/java/technologies/javase/javase-jdk8-downloads.html)

-------

## 📚简介

支持语言: java8+    
使用springboot: springboot2   
使用json工具: jackson

对接前端上传各种json的参数:   
* 使用Spring框架，需要使用RequestBody.class解析参数，需要编写对应实体类，同时解析粒度是类级别。
* 对接本框架，可以像RequestParam.class注解一样去获取对应参数值，同时还支持多层级的获取参数值的。解析注解：RequestJson.class  RequestJsonParam.class。

## 🚀使用指南
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
### 🌓HTTP请求解析参数使用指南
http发送Post请求，content-type: application/json。
通过RequestJson.class RequestJsonParam.class 提取json中参数值

```java
/**
 * request body json:
 * {
 *   "other1": "hello world",
 *   "other2": {
 *      "other3":"hello world"
 *   },
 *   "list": ["1", "2", "3"],
 *   "set":[1, 2, 3]
 * }
 */
@RestController
public class TestController {
    @RequestMapping(value = "/", method = RequestMethod.POST)
    public void test(@RequestJsonParam String other,
                     @RequestJsonParam(value = "other1") String other1,
                     @RequestJsonParam(value = "other2", required = false) Object other2,
                     // 多层级参数解析
                     @RequestJsonParam(value = "other2.other3", required = false, defaultValue = "other") String other3,
                     // 解析基础类型
                     @RequestJsonParam long long1,
                     @RequestJsonParam boolean bool,
                     @RequestJsonParam Integer inta,
                     @RequestJsonParam String str,
                     @RequestJsonParam Object object,
                     // request body解析
                     @RequestJson JsonNode jsonNode,
                     // 解析集合类型
                     @RequestJsonParam Set<Object> set,
                     @RequestJsonParam List<Object> list,
                     @RequestJsonParam Map<String, Object> testMap) {

    }
}
```

## 📦安装

### 🍊Maven
在项目的pom.xml的dependencies中加入以下内容:

```xml
<dependency>
  <groupId>com.btye102</groupId>
  <artifactId>request-json-boot-starter</artifactId>
  <version>1.0.1</version>
</dependency>
```