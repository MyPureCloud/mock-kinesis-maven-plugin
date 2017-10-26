package com.genesys.purecloud;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.genesys.purecloud.request.PutRecordRequest;
import com.genesys.purecloud.request.PutRecordsRequest;
import com.genesys.purecloud.response.PutRecordResponse;
import com.genesys.purecloud.response.PutRecordsResponse;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class ServletTest extends Mockito {

    private static final String STREAM_NAME = "test-stream-name";
    private static final ObjectMapper mapper = new ObjectMapper();

    private ServletInputStream getMockStream(String body) throws IOException{
        ServletInputStream mockServletInputStream = mock(ServletInputStream.class);

        when(mockServletInputStream.read(Matchers.<byte[]>any(), anyInt(), anyInt())).thenAnswer(new Answer<Integer>() {
            @Override
            public Integer answer(InvocationOnMock invocationOnMock) throws Throwable {
                Object[] args = invocationOnMock.getArguments();
                byte[] output = (byte[]) args[0];
                int offset = (int) args[1];
                int length = (int) args[2];
                return new ByteArrayInputStream(body.getBytes()).read(output, offset, length);
            }
        });

       // when(mockServletInputStream.

        return mockServletInputStream;
    }

    private PutRecordsRequest getPutRecordsRequest(){
        PutRecordsRequest recordRequest = new PutRecordsRequest();
        List<PutRecordRequest> records = new ArrayList<>();

        PutRecordRequest record = new PutRecordRequest();
        record.setData("DATA");
        record.setPartitionKey("FOO");
        records.add(record);

        recordRequest.setRecords(records);
        recordRequest.setStreamName(STREAM_NAME);

        return recordRequest;
    }

    @Test
    public void testServletPutRecords() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        when(request.getRequestURI()).thenReturn("/");
        when(request.getHeader("Content-Type")).thenReturn("application/x-amz-json-1.1");
        when(request.getHeader("X-Amz-Target")).thenReturn("Kinesis_20131202.PutRecords");

        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(writer);

        when(request.getInputStream()).thenReturn(new WrappedServletInputStream(Servlet.toJson(getPutRecordsRequest())));

        Servlet target = new Servlet();
        target.addStream(STREAM_NAME);

        target.doPost(request, response);

        writer.flush(); // it may not have been flushed yet..
        writer.close();
        String out = stringWriter.toString();

        PutRecordsResponse recodResponse = mapper.readValue(out, PutRecordsResponse.class);

        assertEquals(0, recodResponse.getFailedRecordCount());

        PutRecordResponse firstResponse = recodResponse.getRecords().get(0);

        assertNotNull(firstResponse.getSequenceNumber());
        assertNotNull(firstResponse.getShardId());
        assertEquals(null, firstResponse.getErrorCode());
        assertEquals(null, firstResponse.getErrorMessage());

    }

    @Test
    public void testServletPutRecordsWithError() throws Exception {

        Servlet target = new Servlet();
        target.addStream(STREAM_NAME);


        HttpServletRequest errorRateRequest = mock(HttpServletRequest.class);
        HttpServletResponse errorRateResponse = mock(HttpServletResponse.class);

        when(errorRateRequest.getRequestURI()).thenReturn("/" + STREAM_NAME + "/ratelimiterrorrate/1.0");

        target.doPut(errorRateRequest, errorRateResponse);



        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        when(request.getRequestURI()).thenReturn("/");
        when(request.getHeader("Content-Type")).thenReturn("application/x-amz-json-1.1");
        when(request.getHeader("X-Amz-Target")).thenReturn("Kinesis_20131202.PutRecords");

        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(writer);

        when(request.getInputStream()).thenReturn(new WrappedServletInputStream(Servlet.toJson(getPutRecordsRequest())));



        target.doPost(request, response);

        writer.flush(); // it may not have been flushed yet..
        writer.close();
        String out = stringWriter.toString();

        PutRecordsResponse recodResponse = mapper.readValue(out, PutRecordsResponse.class);

        assertEquals(1, recodResponse.getFailedRecordCount());

        PutRecordResponse firstResponse = recodResponse.getRecords().get(0);

        assertNotNull(firstResponse.getErrorCode());
        assertNotNull(firstResponse.getErrorMessage());
        assertEquals(null, firstResponse.getSequenceNumber());
        assertEquals(null, firstResponse.getShardId());

    }

}
