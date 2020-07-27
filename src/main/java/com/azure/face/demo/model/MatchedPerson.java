package com.azure.face.demo.model;

import lombok.Data;

import java.io.File;

@Data
public class MatchedPerson {


    String personName = "";
    String photo;
    String photoFullUrl;

    public String getPhoto(){

       return  "detect" + File.separator + photo.replace(".jpg",".thumb.jpg");
    }


}


