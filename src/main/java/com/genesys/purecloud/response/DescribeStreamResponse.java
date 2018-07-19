package com.genesys.purecloud.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DescribeStreamResponse {
    @JsonProperty("KeyId")
    private String keyId;

    @JsonProperty("EncryptionType")
    private String encryptionType;

    @JsonProperty("StreamStatus")
    private String streamStatus;

    @JsonProperty("StreamName")
    private String streamName;

    @JsonProperty("Shards")
    private Shard[] shards;

    @JsonProperty("StreamARN")
    private String streamARN;

    @JsonProperty("StreamCreationTimestamp")
    private int streamCreationTimestamp;

    @JsonProperty("RetentionPeriodHours")
    private int retentionPeriodHours;


    @JsonProperty("EnhancedMonitoring")
    private EnhancedMonitoring enhancedMonitoring;

    public DescribeStreamResponse(String name){
        this.encryptionType = "NONE";
        this.streamStatus ="ACTIVE";

        this.shards = new Shard[]{new Shard()};

        this.streamName = name;
        this.streamARN = name;
        this.streamCreationTimestamp = 1511891888;
        this.retentionPeriodHours = 24;

        enhancedMonitoring = new EnhancedMonitoring();


    }


    public class Shard {
        @JsonProperty("HashKeyRange")
        private HashKeyRange hashKeyRange;

        @JsonProperty("ShardId")
        private String shardId;

        @JsonProperty("ParentShardId")
        private String parentShardId;

        @JsonProperty("AdjacentParentShardId")
        private String adjacentParentShardId;


        @JsonProperty("SequenceNumberRange")
        private SequenceNumberRange sequenceNumberRange;


        public Shard(){
            hashKeyRange = new HashKeyRange();
            shardId = "shardId-000000000005";
            parentShardId = "shardId-000000000000";
            adjacentParentShardId = "shardId-000000000003";
            sequenceNumberRange = new SequenceNumberRange();

        }

    }

    public class SequenceNumberRange{
        @JsonProperty("StartingSequenceNumber")
        private String startingSequenceNumber;

        public SequenceNumberRange(){
            startingSequenceNumber = "49582093722028364078007656726295729525841911387684601954";
        }
    }

    public class EnhancedMonitoring{
        @JsonProperty("ShardLevelMetrics")
        private String[] shardLevelMetrics;

        public EnhancedMonitoring(){
            shardLevelMetrics = new String[0];
        }
    }

    public class HashKeyRange{

        @JsonProperty("EndingHashKey")
        private String endingHashKey;

        @JsonProperty("StartingHashKey")
        private String startingHashKey;


        public HashKeyRange(){
            this.endingHashKey = "170141183460469231731687303715884105727";
            this.startingHashKey = "0";
        }

        public String getEndingHashKey() {
            return endingHashKey;
        }

        public void setEndingHashKey(String endingHashKey) {
            this.endingHashKey = endingHashKey;
        }

        public String getStartingHashKey() {
            return startingHashKey;
        }

        public void setStartingHashKey(String startingHashKey) {
            this.startingHashKey = startingHashKey;
        }
    }
}
