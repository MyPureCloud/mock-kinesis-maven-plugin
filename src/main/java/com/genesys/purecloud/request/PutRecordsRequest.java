package com.genesys.purecloud.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.genesys.purecloud.response.PutRecordResponse;

import java.io.IOException;
import java.util.List;
public class PutRecordsRequest {

    @JsonProperty("StreamName")
    private String streamName;

    @JsonProperty("Records")
    private List<PutRecordRequest> records;

    public String getStreamName() {
        return streamName;
    }
    public void setStreamName(String streamName){ this.streamName = streamName;}

    public List<PutRecordRequest> getRecords() {
        return records;
    }
    public void setRecords(List<PutRecordRequest> records){ this.records = records;}
}
