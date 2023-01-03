package com.yyl.base.util;

import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseErrorHandler;

import java.io.IOException;

/**
 * @author yang.yonglian
 * @version 1.0.0
 * @Description 自定义RestTemplate Response错误处理器，默认是DefaultResponseErrorHandler，当响应状态码为4xx和5xx时抛出异常
 *              我们想要的是获取异常的响应体信息和状态码，所以自定义，此代码也是参考DefaultResponseErrorHandler
 * @createTime 2022/12/12 10:08
 */

public class CustomResponseErrorHandler implements ResponseErrorHandler {
    @Override
    public boolean hasError(ClientHttpResponse response) throws IOException {
        int rawStatusCode = response.getRawStatusCode();
        HttpStatus statusCode = HttpStatus.resolve(rawStatusCode);
        return (statusCode != null ? statusCode.isError(): hasError(rawStatusCode));
    }
    protected boolean hasError(int unknownStatusCode) {
        HttpStatus.Series series = HttpStatus.Series.resolve(unknownStatusCode);
        return (series == HttpStatus.Series.CLIENT_ERROR || series == HttpStatus.Series.SERVER_ERROR);
    }

    @Override
    public void handleError(ClientHttpResponse response) throws IOException {

    }
}
