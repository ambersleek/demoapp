package com.example.demo.helper;

import com.example.demo.bean.OfferResponse;
import com.example.demo.misc.MyMock;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;

@Component
public class ABCCaller {

    private RestOperations operations = new RestTemplate();

    @MyMock(name = "ABC")
    public OfferResponse getResponse(String name){
        String url = "https://demoaspect.free.beeceptor.com/abc";
        return operations.getForEntity(url, OfferResponse.class).getBody();
    }
}
