package com.genesys.purecloud;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import com.genesys.purecloud.errors.KinesisError;
import com.genesys.purecloud.errors.ResourceNotFoundException;
import com.genesys.purecloud.request.DescribeStreamRequest;
import com.genesys.purecloud.request.PutRecordRequest;
import com.genesys.purecloud.request.PutRecordsRequest;

import com.genesys.purecloud.response.DescribeStreamResponse;
import org.apache.commons.io.IOUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Servlet extends HttpServlet {
    private static final ObjectMapper mapper = new ObjectMapper();

    private final static String KINESIS_TARGET = "Kinesis_20131202.";

    private final Map<String, KinesisStream> streams = new HashMap<>();

    public static <T> T parseFromRequest(HttpServletRequest request, Class<T> contentClass) throws IOException{


        if(request.getHeader("Content-Type").indexOf("json")> -1) {
            //application/x-amz-json-1.1
            String bodyJson = IOUtils.toString(request.getInputStream());
            return mapper.readValue(bodyJson, contentClass);
        }else if(request.getHeader("Content-Type").indexOf("cbor")> -1 ){
            //application/x-amz-cbor-1.1

            //TODO: figure out CBOR encoding (and need to encode response)
//          final BufferedReader reader = request.getReader();
//          byte[] json = IOUtils.toByteArray(reader);
//          CBORFactory f = new CBORFactory();
//          ObjectMapper mapper = new ObjectMapper(f);
//          mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
//
//          return mapper.readValue(json, contentClass);
        }

        return null;

    }

    public static <T> String toJson(T object) throws JsonProcessingException{
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        return mapper.writeValueAsString(object);

    }

    public void addStream(String streamName) {
        KinesisStream stream = new KinesisStream();
        streams.put(streamName, stream);
    }

    @Override
    protected  void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String uri = request.getRequestURI().substring(1);
        String[] fragments = uri.split("/");


        String streamName = fragments[0];

        if(streams.containsKey(streamName)){
            mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
            List<PutRecordRequest> records = streams.get(streamName).getRequests();
            PrintWriter writer = response.getWriter();
            response.getWriter().write(mapper.writeValueAsString(records));
            response.getWriter().flush();
            response.getWriter().close();
        }else{
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        streams.forEach((key, stream) -> stream.clear());
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String uri = request.getRequestURI().substring(1);
        String[] fragments = uri.split("/");
        String streamName = fragments[0];

        if(streams.containsKey(streamName) && fragments.length > 2){
            String operation = fragments[1];
            Double errorRate = Double.parseDouble(fragments[2]);
            if(operation.equalsIgnoreCase("errorrate")){
                streams.get(streamName).setErrorRate(errorRate);
            }else if(operation.equalsIgnoreCase("ratelimiterrorrate")){
                streams.get(streamName).setRateLimitErrorRate(errorRate);
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        final String amzTarget = extractAmzTarget(request);
        String responseBody = "";

        try {

            if (amzTarget.equals("PutRecords")) {
                responseBody = handlePutRecords(request);

            } else if (amzTarget.equals("PutRecord")) {
                responseBody = handlePutRecord(request);

            } else if (amzTarget.equals("DescribeStream")) {
                responseBody = handleDescribeStream(request);

            } else {

                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            }
        }
        catch(KinesisError e){
            responseBody = e.body();
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            System.out.print(e);
        }
        catch(UnrecognizedPropertyException upe){
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            System.out.print(upe);
        }catch (Exception ex){
            System.out.print(ex);
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);

        }

        response.getWriter().write(responseBody);
        response.getWriter().flush();
        response.getWriter().close();

    }

    private KinesisStream getStream(String streamName) throws ResourceNotFoundException{
        KinesisStream stream = streams.get(streamName);

        if(stream == null){
            throw new ResourceNotFoundException();
        }

        return stream;
    }

    private String handlePutRecords(HttpServletRequest request) throws IOException, ResourceNotFoundException{
        PutRecordsRequest records = parseFromRequest(request, PutRecordsRequest.class);
        KinesisStream stream = getStream(records.getStreamName());
        return toJson(stream.handleRecordsRequest(records));
    }

    private String handlePutRecord(HttpServletRequest request) throws IOException, ResourceNotFoundException{
        PutRecordRequest records =  parseFromRequest(request, PutRecordRequest.class);

        KinesisStream stream = getStream(records.getStreamName());

        return toJson(stream.handleRecordRequest(records));
    }

    private String handleDescribeStream(HttpServletRequest request) throws IOException, ResourceNotFoundException{
        DescribeStreamRequest describeRequest =  parseFromRequest(request, DescribeStreamRequest.class);

        DescribeStreamResponse response = new DescribeStreamResponse(describeRequest.getStreamName());

        return toJson(response);
    }


    private String extractAmzTarget(HttpServletRequest request) {
        final String amzTarget = request.getHeader("X-Amz-Target");
        if (amzTarget == null || !amzTarget.startsWith(KINESIS_TARGET)) {
            return "Unknown";
        }
        return amzTarget.substring(KINESIS_TARGET.length());
    }

}
