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

    @JsonProperty("ExplicitHashKey")
    private String explicitHashKey;

    @JsonProperty("SequenceNumberForOrdering")
    private String sequenceNumberForOrdering;

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

    public String getExplicitHashKey() {
        return explicitHashKey;
    }
    public void setExplicitHashKey(String explicitHashKey){ this.explicitHashKey = explicitHashKey;}

    public String getSequenceNumberForOrdering() {
        return sequenceNumberForOrdering;
    }
    public void setSequenceNumberForOrdering(String sequenceNumberForOrdering){ this.sequenceNumberForOrdering = sequenceNumberForOrdering;}

}
