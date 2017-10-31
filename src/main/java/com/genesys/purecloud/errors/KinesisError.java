package com.genesys.purecloud.errors;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.genesys.purecloud.response.PutRecordResponse;

public class KinesisError extends Exception {
    private static final ObjectMapper mapper = new ObjectMapper();

    @JsonProperty("ErrorMessage")
    private String errorMessage;

    @JsonProperty("ErrorCode")
    private String errorCode;

    public String body() throws JsonProcessingException {

        PutRecordResponse response = new PutRecordResponse();

        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        return mapper.writeValueAsString(this);
    }

    protected KinesisError(String errorMessage,String errorCode){

        this.errorMessage = errorMessage;
        this.errorCode = errorCode;
    }
}
