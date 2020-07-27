package com.azure.face.demo.model.Azure;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class APIResponse {

    @JsonProperty("error")
    private AzureError error;

    @JsonProperty("personId")
    private String personId;

    @JsonProperty("persistedFaceId")
    private String faceId;

    @JsonProperty("status")
    private String status;

    @JsonProperty("faceId")
    String detectedFaceId;

    @JsonProperty("candidates")
    List<Candidate> candidates;

}
