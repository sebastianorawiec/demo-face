package com.azure.face.demo.model.Azure;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class AzureError {

    @JsonProperty("code")
    String code;
    @JsonProperty("message")
    String message;
}
