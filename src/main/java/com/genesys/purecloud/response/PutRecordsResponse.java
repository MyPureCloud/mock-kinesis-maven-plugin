package com.genesys.purecloud.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class PutRecordsResponse {

    @JsonProperty("FailedRecordCount")
    private int failedRecordCount;

    @JsonProperty("Records")
    private List<PutRecordResponse> records;

    public int getFailedRecordCount(){
        return failedRecordCount;
    }
    public void setFailedRecordCount(int value){ failedRecordCount = value;}

    public List<PutRecordResponse> getRecords(){
        return records;
    }

    public void setRecords(List<PutRecordResponse> records){ this.records = records;}

}
