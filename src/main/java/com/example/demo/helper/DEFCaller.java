package com.example.demo.helper;

import com.example.demo.bean.SimpleResponse;
import com.example.demo.bean.SomeRequest;
import com.example.demo.misc.MyMock;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;

@Component
public class DEFCaller {

    private RestOperations operations = new RestTemplate();

    @MyMock(name = "DEF")
    public SimpleResponse getResponse(SomeRequest request){
        String url = "https://demoaspect.free.beeceptor.com/def/" + request.getLocale();
        return operations.getForEntity(url, SimpleResponse.class).getBody();
    }
}
