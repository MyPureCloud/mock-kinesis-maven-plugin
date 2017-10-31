package com.genesys.purecloud;

import com.genesys.purecloud.request.PutRecordRequest;
import com.genesys.purecloud.request.PutRecordsRequest;
import com.genesys.purecloud.response.PutRecordResponse;
import com.genesys.purecloud.response.PutRecordsResponse;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class KinesisStreamTest {
    @Test
    public void handleRecordsResponse() throws IOException {
        PutRecordsRequest request = new PutRecordsRequest();
        List<PutRecordRequest> records = new ArrayList<>();

        PutRecordRequest record = new PutRecordRequest();
        record.setData("DATA");
        record.setPartitionKey("FOO");
        records.add(record);

        request.setRecords(records);


        KinesisStream target = new KinesisStream();
        PutRecordsResponse response = target.handleRecordsRequest(request);

        assertEquals(0, response.getFailedRecordCount());

        PutRecordResponse firstResponse = response.getRecords().get(0);

        assertNotNull(firstResponse.getSequenceNumber());
        assertNotNull(firstResponse.getShardId());
        assertEquals(null, firstResponse.getErrorCode());
        assertEquals(null, firstResponse.getErrorMessage());

    }

    @Test
    public void handleRecordsResponseErrors() throws IOException {
        PutRecordsRequest request = new PutRecordsRequest();
        List<PutRecordRequest> records = new ArrayList<>();

        PutRecordRequest record = new PutRecordRequest();
        record.setData("DATA");
        record.setPartitionKey("FOO");
        records.add(record);

        request.setRecords(records);


        KinesisStream target = new KinesisStream();
        target.setErrorRate(1);
        PutRecordsResponse response = target.handleRecordsRequest(request);

        assertEquals(1, response.getFailedRecordCount());

        PutRecordResponse firstResponse = response.getRecords().get(0);

        assertNotNull(firstResponse.getErrorCode());
        assertNotNull(firstResponse.getErrorMessage());
        assertEquals(null, firstResponse.getSequenceNumber());
        assertEquals(null, firstResponse.getShardId());

    }
}