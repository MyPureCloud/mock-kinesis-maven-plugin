package com.genesys.purecloud.request;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DescribeStreamRequest {
    @JsonProperty("ExclusiveStartShardId")
    private String exclusiveStartShardId;

    @JsonProperty("Limit")
    private int limit;

    @JsonProperty("StreamName")
    private String streamName;

    public String getExclusiveStartShardId() {
        return exclusiveStartShardId;
    }

    public void setExclusiveStartShardId(String exclusiveStartShardId) {
        this.exclusiveStartShardId = exclusiveStartShardId;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public String getStreamName() {
        return streamName;
    }

    public void setStreamName(String streamName) {
        this.streamName = streamName;
    }
}
