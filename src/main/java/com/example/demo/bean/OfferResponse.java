package com.example.demo.bean;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class OfferResponse {
    private String id;
    private List<Offer> offers;
}
