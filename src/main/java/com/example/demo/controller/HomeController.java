package com.example.demo.controller;

import com.example.demo.bean.OfferResponse;
import com.example.demo.bean.SimpleResponse;
import com.example.demo.bean.SomeRequest;
import com.example.demo.helper.ABCCaller;
import com.example.demo.helper.DEFCaller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {

    @Autowired
    private ABCCaller abcCaller;

    @Autowired
    private DEFCaller defCaller;

    @GetMapping("/abc")
    public OfferResponse getABC(){
        return abcCaller.getResponse("apple");
    }

    @GetMapping("/def")
    public SimpleResponse getDEF(){
        SomeRequest request = new SomeRequest();
        request.setLocale("us");
        return defCaller.getResponse(request);
    }
}