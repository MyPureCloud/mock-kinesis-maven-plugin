package com.genesys.purecloud;

import com.genesys.purecloud.request.PutRecordRequest;
import com.genesys.purecloud.request.PutRecordsRequest;
import com.genesys.purecloud.response.PutRecordResponse;
import com.genesys.purecloud.response.PutRecordsResponse;

import java.util.*;

public class KinesisStream {

    List<PutRecordRequest> requests = new ArrayList<>();
    double errorRate = 0.0;
    double rateLimitErrorRate = 0.0;


    public PutRecordResponse handleRecordRequest(PutRecordRequest request){
        Double errorValue = new Random().nextDouble();

        if(errorValue < errorRate){
            return PutRecordResponse.buildWithRandomError();
        }else if(errorValue < rateLimitErrorRate){
            return PutRecordResponse.buildWithRateLimitError();
        }else{
            requests.add(request);
            return PutRecordResponse.build();
        }

    }

    public PutRecordsResponse handleRecordsRequest(PutRecordsRequest request){

        PutRecordsResponse response = new PutRecordsResponse();

        ArrayList<PutRecordResponse> responseRecords = new ArrayList<>();

        for(PutRecordRequest record : request.getRecords()){
            PutRecordResponse resp = handleRecordRequest(record);

            if(resp.getErrorCode() != null){
                response.setFailedRecordCount(response.getFailedRecordCount() + 1);
            }

            responseRecords.add(resp);
        }

        response.setRecords(responseRecords);

        return response;

    }

    public void clear(){
        requests.clear();
    }

    public List<PutRecordRequest> getRequests(){
        return requests;
    }

    public void setErrorRate(double errorRate){
        this.errorRate = errorRate;
    }

    public void setRateLimitErrorRate(double errorRate){
        this.rateLimitErrorRate = errorRate;
    }
}
