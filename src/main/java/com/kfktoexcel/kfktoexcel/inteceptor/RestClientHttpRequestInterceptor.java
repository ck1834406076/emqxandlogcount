package com.kfktoexcel.kfktoexcel.inteceptor;

import com.kfktoexcel.kfktoexcel.controller.EmqxPerfController;
import lombok.extern.log4j.Log4j;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
public class RestClientHttpRequestInterceptor implements ClientHttpRequestInterceptor {
    private static final Logger logger = LoggerFactory.getLogger(RestClientHttpRequestInterceptor.class);
    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        HttpHeaders headers = request.getHeaders();
        //headers.add("Cookie","SESSIONID=b8dd5bd9-9fb7-48cb-a86b-e079cb554fb8");
        logger.info("request size:{} kb",(Integer.valueOf(headers.get("Content-Length").get(0)))/1024);
        return execution.execute(request,body);
    }
}
