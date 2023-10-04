package com.zero.triptalk.place.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PlaceRequest {

    private String name;

    private String region;

    private String si;

    private String gun;

    private String gu;

    private String address;

    private double latitude;

    private double longitude;

    public Place toEntity(){
        return Place.builder()
                .name(name)
                .region(region)
                .si(si)
                .gun(gun)
                .gu(gu)
                .address(address)
                .latitude(latitude)
                .longitude(longitude)
                .build();
    }

}
