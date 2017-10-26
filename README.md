# Mock-kinesis

Mock kinesis is a library intended to run with the maven-failsafe-plugin to act as a fake kinesis endpoint for your integration tests.

## Installation 

Maven:
```xml
<dependency>
    <groupId>com.genesys.purecloud</groupId>
    <artifactId>mock-kinesis-maven-plugin</artifactId>
    <version>${mock.kinesis.version}</version>
</dependency>
 
```

and then under <build><plugins>

```xml
<plugin>
    <groupId>com.genesys.purecloud</groupId>
    <artifactId>mock-kinesis-maven-plugin</artifactId>
    <version>${mock.kinesis.version}</version>
    <executions>
        <execution>
            <goals>
                <goal>start</goal>
            </goals>
            <configuration>
                <port>${it.kinesis.port}</port>
                <streamname>kinesis-mock</streamname>
            </configuration>
        </execution>
    </executions>
</plugin>
```

This starts mock kinesis listening on port 8899 and then adds a new stream called 'kinesis-mock'.

## Usage

In your application's code 

```java
System.setProperty("com.amazonaws.sdk.disableCbor", "true");
clientBuilder.setEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration("http://localhost:8899", "us-east-1"));
```

The **MockKinesisClient** class will wrap http calls to the server that you can use to change error rates, clear streams or get messages that were sent to the stream.  An example test case would look like this:

```java
@Test
public void testSmallBatchOfRequests() throws JSONException, IOException, ExecutionException, InterruptedException {
    MockKinesisClient client = new MockKinesisClient(8899);
    client.clearStreams();  //clear all records
    client.setRateLimitErrorRate(streamName, 0); //set error rate to 0

    functionThatTriggersMessagesSentToKinesis();

    Thread.sleep(waitForEventsToFlushInMs);// wait for all records to be sent
    List<PutRecordRequest> records = client.getRecordsForStream(streamName); // get records from stream

    assertEquals(1, records.size());

}

```

## Error Responses
There are two configuration options for mock kinesis to return error codes: MockKinesisClient.setRateLimitErrorRate and MockKinesisClient.setErrorRate

setRateLimitErrorRate will return rate limiting error codes  "KMSThrottlingException", "ProvisionedThroughputExceededException" where setErrorRate will return a random error code from "InvalidArgumentException","KMSAccessDeniedException","KMSDisabledException","KMSInvalidStateException","KMSNotFoundException","KMSOptInRequired"


## A Note on CBOR Content Type
Kinesis uses the (CBOR)[http://cbor.io/] content type by default.  Mock kinesis doesn't yet support decoding that content type so when setting up your client, you first have to set the system property to turn it off.

```java 
System.setProperty("com.amazonaws.sdk.disableCbor", "true");
```

Messages with CBOR enter the server with **Content-type** set to **application/x-amz-cbor-1.1**
 normal json messages will have the content type **application/x-amz-json-1.1**



