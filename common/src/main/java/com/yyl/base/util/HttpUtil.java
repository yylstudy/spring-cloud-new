package com.yyl.base.util;

import com.fasterxml.jackson.databind.JavaType;
import com.yyl.base.common.CommonConstant;
import com.yyl.base.common.ConfigConstant;
import com.yyl.base.context.ApplicationContextHolder;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.client.RequestCallback;
import org.springframework.web.client.ResponseExtractor;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;

import java.lang.reflect.Field;
import java.net.URI;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * @author yang.yonglian
 * @version 1.0.0
 * @Description http工具类 底层采用httpClient连接池，spring也提供了默认的SimpleClientHttpRequestFactory，但是功能较少，也未提供池化的功能
 *              这里使用HttpComponentsClientHttpRequestFactory替换
 * @createTime 2022/3/22 14:16
 */
@Slf4j
public class HttpUtil {
    /**
     * 全局RestTemplate
     */
    private static volatile RestTemplate restTemplate;
    /**
     * url构建工厂，RestTemplate默认会将整个url进行encode，这个在执行get请求url传参存在特殊参数时会有bug，如&
     * 这个创建DefaultUriBuilderFactory并且设置为不编码，仅供
     * doGet(String url, Map<String,String> header,Object data,Class<T> responseClass) 这个方法内部已将参数url编码
     */
    private static volatile DefaultUriBuilderFactory uriFactory;
    /**
     * clazz->属性名->属性对象映射集合
     */
    private static final Map<Class,Map<String, Field>> fieldsMaps = new ConcurrentHashMap<>();

    public static RestTemplate getRestTemplate(){
        if(restTemplate==null){
            synchronized (HttpUtil.class){
                if(restTemplate==null){
                    ClientHttpRequestFactory factory = clientHttpRequestFactory();
                    RestTemplate rest = new RestTemplate(factory);
                    DefaultUriBuilderFactory defaultUriBuilderFactory = new DefaultUriBuilderFactory();
                    defaultUriBuilderFactory.setEncodingMode(DefaultUriBuilderFactory.EncodingMode.NONE);
                    uriFactory = defaultUriBuilderFactory;
                    rest.setErrorHandler(new CustomResponseErrorHandler());
                    restTemplate = rest;
                }
            }
        }
        return restTemplate;
    }


    /**
     * 发送get请求
     * @param url http地址
     * @param responseClass 返回值类型
     * @param <T>
     * @return
     */
    public static <T> T doGetForObject(String url,Class<T> responseClass){
        return getRestTemplate().getForObject(url,responseClass);
    }

    /**
     * 发送get请求
     * @param url http地址
     * @param header http header键值对
     * @param responseClass 返回值类型
     * @param <T>
     * @return
     */
    public static <T> T doGetForObject(String url, Map<String,String> header,Class<T> responseClass){
        HttpHeaders httpHeaders = getHeader(header);
        HttpEntity<Map<Object,Object>> requestEntity = new HttpEntity(httpHeaders);
        return getRestTemplate().exchange(url, HttpMethod.GET, requestEntity, responseClass).getBody();
    }
    /**
     * 发送get请求
     * @param url http地址
     * @param headersConsumer httpHeaders处理器
     * @param responseClass 返回值类型
     * @param <T>
     * @return
     */
    public static <T> T doGetForObject(String url, Consumer<HttpHeaders> headersConsumer, Class<T> responseClass){
        HttpHeaders httpHeaders = new HttpHeaders();
        headersConsumer.accept(httpHeaders);
        HttpEntity<Map<Object,Object>> requestEntity = new HttpEntity(httpHeaders);
        return getRestTemplate().exchange(url, HttpMethod.GET, requestEntity, responseClass).getBody();
    }


    /**
     * 发送get请求
     * @param url http地址
     * @param header header键值对
     * @param data 数据 暂不支持集合、数组
     * @param responseType
     * @param <T>
     * @return
     */
    public static <T> T doGetForObject(String url, Map<String,String> header,Object data,Class<T> responseType){
        HttpHeaders httpHeaders = getHeader(header);
        if(data!=null){
            if(url.indexOf("?")==-1){
                url+="?";
            }
            if(data instanceof String){
                url+=data;
            }else{
                String paramUrlStr = objectToUrlStr(data);
                url+=paramUrlStr;
            }
        }
        log.debug("doGet url:{}",url);
        HttpEntity<Map<Object,Object>> requestEntity = new HttpEntity(httpHeaders);
        RequestCallback requestCallback = getRestTemplate().httpEntityCallback(requestEntity, responseType);
        ResponseExtractor<ResponseEntity<T>> responseExtractor = getRestTemplate().responseEntityExtractor(responseType);
        URI expanded = uriFactory.expand(url);
        ResponseEntity<T> result = getRestTemplate().execute(expanded, HttpMethod.GET, requestCallback, responseExtractor);
        return nonNull(result).getBody();
    }

    /**
     * 发送post请求
     * @param url http地址
     * @param param 参数对象
     * @param responseClass 返回值类型
     * @param <T>
     * @return
     */
    public static <T> T doPostForObject(String url,Object param,Class<T> responseClass){
        return getRestTemplate().postForObject(url,param,responseClass);
    }
    /**
     * 发送post请求
     * @param url http地址
     * @param param 参数对象
     * @param responseClass 返回值类型
     * @param <T>
     * @return
     */
    public static <T> ResponseEntity<T> doPostForEntity(String url,Object param,Class<T> responseClass){
        return getRestTemplate().postForEntity(url,param,responseClass);
    }

    /**
     * 发送post请求
     * @param url http地址
     * @param param 参数对象
     * @param headersConsumer http header
     * @param responseClass 返回值类型
     * @param <T>
     * @return
     */
    public static <T> T doPostForObject(String url,Object param,Consumer<HttpHeaders> headersConsumer,Class<T> responseClass){
        HttpHeaders httpHeaders = new HttpHeaders();
        headersConsumer.accept(httpHeaders);
        HttpEntity<Object> requestEntity = new HttpEntity(param,httpHeaders);
        return getRestTemplate().postForObject(url,  requestEntity, responseClass);
    }
    /**
     * 发送post请求
     * @param url http地址
     * @param param 参数对象
     * @param headersConsumer http header
     * @param responseClass 返回值类型
     * @param <T>
     * @return
     */
    public static <T> ResponseEntity<T> doPostForEntity(String url,Object param,Consumer<HttpHeaders> headersConsumer,Class<T> responseClass){
        HttpHeaders httpHeaders = new HttpHeaders();
        headersConsumer.accept(httpHeaders);
        HttpEntity<Object> requestEntity = new HttpEntity(param,httpHeaders);
        return getRestTemplate().postForEntity(url,  requestEntity, responseClass);
    }

    /**
     * 发送post请求
     * @param url http地址
     * @param param 参数对象
     * @param header http header
     * @param responseClass 返回值类型
     * @param <T>
     * @return
     */
    public static <T> T doPostForObject(String url,Object param,Map<String,String> header,Class<T> responseClass){
        HttpHeaders httpHeaders = getHeader(header);
        HttpEntity<Object> requestEntity = new HttpEntity(param,httpHeaders);
        return getRestTemplate().postForObject(url,  requestEntity, responseClass);
    }
    /**
     * 发送post请求
     * @param url http地址
     * @param param 参数对象
     * @param header http header
     * @param responseClass 返回值类型
     * @param <T>
     * @return
     */
    public static <T> ResponseEntity<T> doPostForEntity(String url,Object param,Map<String,String> header,Class<T> responseClass){
        HttpHeaders httpHeaders = getHeader(header);
        HttpEntity<Object> requestEntity = new HttpEntity(param,httpHeaders);
        return getRestTemplate().postForEntity(url,  requestEntity, responseClass);
    }

    /**
     * 发送http请求
     * @param url http地址
     * @param httpMethod http方法类型
     * @param param 参数对象
     * @param header http header
     * @param responseClass 返回值类型
     * @param <T>
     * @return
     */
    public static <T> T doExchange(String url,HttpMethod httpMethod,Object param,Map<String,String> header,Class<T> responseClass){
        HttpHeaders httpHeaders = getHeader(header);
        HttpEntity<Object> requestEntity = new HttpEntity(param,httpHeaders);
        return getRestTemplate().exchange(url, httpMethod, requestEntity, responseClass).getBody();
    }

    /**
     * 发送http请求  例如转化结果为Result<User>，传入parametrized为Result.class，parameterClass为User.class即可
     * @param url http地址
     * @param httpMethod http方法类型
     * @param param 参数对象
     * @param header http header
     * @param parametrized 返回值类型
     * @param parameterClass 泛型上的参数类型
     * @param <T>
     * @return
     */
    public static <T> T doExchange(String url,HttpMethod httpMethod,Object param,Map<String,String> header,Class<T> parametrized,Class parameterClass){
        HttpHeaders httpHeaders = getHeader(header);
        HttpEntity<Object> requestEntity = new HttpEntity(param,httpHeaders);
        JavaType javaType = JsonUtil.getMapper().getTypeFactory().constructParametricType(parametrized, parameterClass);
        ParameterizedTypeReference<T> parameterizedTypeReference = ParameterizedTypeReference.forType(javaType);
        return getRestTemplate().exchange(url, httpMethod, requestEntity, parameterizedTypeReference).getBody();
    }
    /**
     * 发送http请求
     * @param url http地址
     * @param httpMethod http方法类型
     * @param param 参数对象
     * @param header http header
     * @param parameterizedTypeReference 参数化类型引用，转化结果为Result<List<Map>>，需传入 new ParameterizedTypeReference<Result<List<Map>>>(){} 即可
     * @param <T>
     * @return
     */
    public static <T> T doExchange(String url,HttpMethod httpMethod,Object param,Map<String,String> header,ParameterizedTypeReference<T> parameterizedTypeReference){
        HttpHeaders httpHeaders = getHeader(header);
        HttpEntity<Object> requestEntity = new HttpEntity(param,httpHeaders);
        return getRestTemplate().exchange(url, httpMethod, requestEntity, parameterizedTypeReference).getBody();
    }


    private static HttpHeaders getHeader(Map<String,String> header){
        if(CollectionUtils.isEmpty(header)){
            return null;
        }
        HttpHeaders headers = new HttpHeaders();
        header.forEach(headers::add);
        return headers;
    }

    private static String objectToUrlStr(Object data){
        if(data==null){
            return "";
        }
        if(data instanceof Map){
            String str = ((Map<Object,Object>) data).entrySet().stream().map(entry->{
                Object value = entry.getValue();
                Object key = entry.getKey();
                String valueStr = objToString(value);
                if(valueStr==null){
                    return null;
                }
                return encode(String.valueOf(key))+"="+encode(valueStr);
            }).filter(Objects::nonNull).collect(Collectors.joining("&"));
            return str;
        }
        Collection<Field> fields = fieldsMaps.computeIfAbsent(data.getClass(), clazz->{
            Map<String,Field> fieldMap = new HashMap<>();
            ReflectionUtils.doWithFields(data.getClass(),field -> {
                fieldMap.putIfAbsent(field.getName(),field);
            });
            return fieldMap;
        }).values();
        String str = fields.stream().map(field->{
            ReflectionUtils.makeAccessible(field);
            Object value = ReflectionUtils.getField(field,data);
            String valueStr = objToString(value);
            if(valueStr==null){
                return null;
            }
            return encode(field.getName())+"="+encode(valueStr);
        }).filter(Objects::nonNull).collect(Collectors.joining("&"));
        return str;
    }

    private static String encode(String str){
        try{
            return URLEncoder.encode(str,"utf-8");
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    private static String objToString(Object value){
        if(value==null){
            return null;
        }
        String valueStr;
        if(value instanceof Date){
            SimpleDateFormat sdf = new SimpleDateFormat(CommonConstant.STANDARD_FORMAT);
            valueStr = sdf.format(value);
        }else if(value instanceof LocalDate){
            valueStr = ((LocalDate) value).format(DateTimeFormatter.ofPattern(CommonConstant.STANDARD_FORMAT));
        }else if(value instanceof LocalDateTime){
            valueStr = ((LocalDateTime) value).format(DateTimeFormatter.ofPattern(CommonConstant.STANDARD_FORMAT));
        }else{
            valueStr = String.valueOf(value);
        }
        return valueStr;
    }

    public static ClientHttpRequestFactory clientHttpRequestFactory() {
        Environment environment = ApplicationContextHolder.getEnvironment();
        int readTimeout = 30000;
        int connectTimeout = 5000;
        int connectRequestTimeout = 5000;
        int maxTotal = 500;
        int maxPerRoute = 200;
        if(environment!=null){
            readTimeout = environment.getProperty(ConfigConstant.REST_READ_TIMEOUT,Integer.class,30000);
            connectTimeout = environment.getProperty(ConfigConstant.REST_CONNECT_TIMEOUT,Integer.class,5000);
            connectRequestTimeout = environment.getProperty(ConfigConstant.REST_CONNECT_REQUEST_TIMEOUT,Integer.class,5000);
            maxTotal = environment.getProperty(ConfigConstant.REST_MAX_TOTAL,Integer.class,500);
            maxPerRoute = environment.getProperty(ConfigConstant.REST_MAX_PER_ROUTE,Integer.class,200);
        }
        log.debug("HttpUtil readTimeout:{}",readTimeout);
        log.debug("HttpUtil connectTimeout:{}",connectTimeout);
        log.debug("HttpUtil connectRequestTimeout:{}",connectRequestTimeout);
        log.debug("HttpUtil maxTotal:{}",maxTotal);
        log.debug("HttpUtil maxPerRoute:{}",maxPerRoute);
        HttpComponentsClientHttpRequestFactory hcchrf = new HttpComponentsClientHttpRequestFactory();
        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
        connectionManager.setMaxTotal(maxTotal);
        connectionManager.setDefaultMaxPerRoute(maxPerRoute);
        RequestConfig.Builder rcb = RequestConfig.custom();
        rcb.setConnectTimeout(connectTimeout);
        rcb.setSocketTimeout(readTimeout);
        rcb.setConnectionRequestTimeout(connectRequestTimeout);
        hcchrf.setHttpClient(
                HttpClientBuilder.create()
                        .setDefaultRequestConfig(rcb.build())
                        .setConnectionManager(connectionManager)
                        .build());

        return hcchrf;
    }


    private static <T> T nonNull(@Nullable T result) {
        Assert.state(result != null, "No result");
        return result;
    }


}
