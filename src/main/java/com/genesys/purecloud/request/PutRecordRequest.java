package com.genesys.purecloud.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;


import java.io.IOException;

public class PutRecordRequest {
    @JsonProperty("StreamName")
    private String streamName;

    @JsonProperty("Data")
    private String data;

    @JsonProperty("PartitionKey")
    private String partitionKey;

    public String getStreamName() {
        return streamName;
    }
    public void setStreamName(String streamName){ this.streamName = streamName;}

    public String getData() {
        return data;
    }
    public void setData(String data){ this.data = data; }

    public String getPartitionKey() {
        return partitionKey;
    }
    public void setPartitionKey(String partitionKey){ this.partitionKey = partitionKey;}

}
