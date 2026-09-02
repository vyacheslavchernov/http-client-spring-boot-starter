# МИГРИРОВАНО В [https://github.com/vyacheslavchernov/vych-spring-toolkit](https://github.com/vyacheslavchernov/vych-spring-toolkit)

# Http-клиент для использования в spring-boot приложениях
##  Maven
```xml
<repositories>
        <repository>
            <id>github</id>
            <url>https://maven.pkg.github.com/vyacheslavchernov/http-client-spring-boot-starter</url>
        </repository>
</repositories>
```

Актуальную версию [см. здесь](https://github.com/vyacheslavchernov?tab=packages&repo_name=http-client-spring-boot-starter) 
```xml
<dependency>
    <groupId>ru.vych</groupId>
    <artifactId>http-client-spring-boot-starter</artifactId>
    <version>0.0.3-RELEASE</version>
</dependency>
```

## Конфигурация клиента
Все доступные параметры конфигурации можно посмотреть в [HttpClientConfig.class](src/main/java/ru/vych/http/config/HttpClientConfig.java)
```java
@Configuration
public class ExampleHttpClientConfiguration {
public static final String CLIENT_NAME = "ExampleHttpClient";
    @Bean(name = CLIENT_NAME)
    public HttpClient client(HttpClientBuilder builder) {
        HttpClientConfig config = new HttpClientConfig()
                .setRoot(TEST_SERVER_URI)
                .setTimeout(15000)
                .setStoreCookies(true);
        return builder.build(config);
    }
}
```

## Использование клиента
Объявление http-клиента
```java
@Autowired
@Qualifier(ExampleHttpClientConfiguration.CLIENT_NAME)
protected HttpClient httpClient;
```

Простой GET запрос
```java
var rq = Request.builder()
        .setUrl(ENDPOINT_PATH)
        .setMethod(HttpMethod.GET)
        .setResponseClass(String.class)
        .build();

var rs = httpClient.execute(rq);
```

GET запрос с query параметром и ответом в виде объекта
```java
var rq = Request.builder()
        .setUrl(TEST_CONTROLLER_PATH + GET_QUERY_ENDPOINT)
        .setMethod(HttpMethod.GET)
        .addQueryParam("name", "value")
        .setResponseClass(ResponseDto.class)
        .build();

var rs = httpClient.execute(rq);

var body = rs.<ResponseDto>getCastedBody();
```
