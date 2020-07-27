package com.azure.face.demo.model.Azure;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Candidate {

    @JsonProperty("personId")
    String personId;
}
