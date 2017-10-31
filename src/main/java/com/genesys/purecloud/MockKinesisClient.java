package com.genesys.purecloud;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.genesys.purecloud.request.PutRecordRequest;
import okhttp3.*;

import java.io.IOException;
import java.util.List;

public class MockKinesisClient {

    private static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");

    private OkHttpClient client = new OkHttpClient();
    private int port;
    private ObjectMapper mapper = new ObjectMapper();

    public MockKinesisClient(int port){
        this.port = port;
    }

    /**
     * Clears all records from a stream
     */
    public void clearStreams() throws IOException{
        Request request = new Request.Builder()
                .url(String.format("http://localhost:%s/", this.port))
                .delete()
                .build();
        client.newCall(request).execute();
    }

    /**
     * Gets a list of records in a stream
     *
     * @param streamName The name of the stream
     */
    public List<PutRecordRequest> getRecordsForStream(String streamName) throws IOException{
        RequestBody body = RequestBody.create(JSON, "{}");
        Request request = new Request.Builder()
                .url(String.format("http://localhost:%s/%s", this.port, streamName))
                .get()
                .build();
        Response response = client.newCall(request).execute();

        List<PutRecordRequest> records = mapper.readValue(response.body().string(), new TypeReference<List<PutRecordRequest>>(){});

        return records;

    }

    /**
     * Sets the error rate for a stream
     *
     * @param streamName The name of the stream
     * @param errorRate value between 0 (no errors) and 1 (all errors) for the chance that an error will be returned
     */
    public void setErrorRate(String streamName, double errorRate) throws IOException{
        RequestBody body = RequestBody.create(JSON, "{}");
        Request request = new Request.Builder()
                .url(String.format("http://localhost:%s/%s/errorrate/%s", this.port, streamName, errorRate))
                .put(body)
                .build();
        client.newCall(request).execute();

    }

    /**
     * Sets the rate limit error rate for a stream
     *
     * @param streamName The name of the stream
     * @param errorRate value between 0 (no errors) and 1 (all errors) for the chance that an error will be returned
     */
    public void setRateLimitErrorRate(String streamName, double errorRate)throws IOException{
        RequestBody body = RequestBody.create(JSON, "{}");
        Request request = new Request.Builder()
                .url(String.format("http://localhost:%s/%s/ratelimiterrorrate/%s", this.port, streamName, errorRate))
                .put(body)
                .build();
        client.newCall(request).execute();

    }
}
