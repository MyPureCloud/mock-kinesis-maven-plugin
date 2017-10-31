package com.genesys.purecloud.response;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

public class PutRecordResponse {
    private static final AtomicInteger atomicSequenceNumber = new AtomicInteger();
    private static final String[] errors = new String[]{
            "InvalidArgumentException",
            "KMSAccessDeniedException",
            "KMSDisabledException",
            "KMSInvalidStateException",
            "KMSNotFoundException",
            "KMSOptInRequired"
    };

    private static final String[] rateerrors = new String[]{
            "KMSThrottlingException",
            "ProvisionedThroughputExceededException"
    };

    @JsonProperty("SequenceNumber")
    private String sequenceNumber;

    @JsonProperty("ShardId")
    private String shardId;

    @JsonProperty("ErrorMessage")
    private String errorMessage;

    @JsonProperty("ErrorCode")
    private String errorCode;

    public String getSequenceNumber() {
        return sequenceNumber;
    }

    public String getShardId() {
        return shardId;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public static PutRecordResponse buildWithRandomError(){
        PutRecordResponse response = new PutRecordResponse();
        response.errorCode = getError();
        response.errorMessage = "GENERIC ERROR MESSAGE";
        return response;
    }

    public static PutRecordResponse buildWithRateLimitError(){
        PutRecordResponse response = new PutRecordResponse();
        response.errorCode = getRateLimitError();
        response.errorMessage = "GENERIC ERROR MESSAGE";
        return response;
    }

    public static PutRecordResponse build(){
        PutRecordResponse response = new PutRecordResponse();
        response.sequenceNumber = String.valueOf(atomicSequenceNumber.addAndGet(1));

        response.shardId = "shardId-000000000001";

        return response;

    }

    private static String getError(){
        Random random = new Random();
        return errors[random.nextInt(errors.length)];
    }

    private static String getRateLimitError(){
        Random random = new Random();
        return rateerrors[random.nextInt(rateerrors.length)];
    }
}