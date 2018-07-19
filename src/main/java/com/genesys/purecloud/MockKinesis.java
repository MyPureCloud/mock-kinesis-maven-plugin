package com.genesys.purecloud;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugin.logging.SystemStreamLog;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.eclipse.jetty.http.HttpVersion;
import org.eclipse.jetty.server.*;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.ssl.SslContextFactory;

@Mojo(threadSafe = true, name = "start", defaultPhase = LifecyclePhase.PRE_INTEGRATION_TEST)
public class MockKinesis extends AbstractMojo {
    protected static final Log log = new SystemStreamLog();

    private static Server server;
    private Servlet servlet;

    @Parameter(defaultValue = "8898", required = false)
    protected transient int port = 8898;

    @Parameter(defaultValue = "8899", required = false)
    protected transient int httpsPort = 8899;

    @Parameter(defaultValue = "kinesis-mock", required = false)
    protected transient String streamname = "kinesis-mock";

    @Parameter(defaultValue = "kinesis-mock2", required = false)
    protected transient String streamname2 = "kinesis-mock2";

    @Override
    public void execute()  {
        log.info(String.format("Starting mock kinesis stream %s on port %d", streamname, port));
        log.info(String.format("Starting second mock kinesis stream %s on port %d", streamname2, port));

        this.servlet = new Servlet();

        ServletHolder servlet = new ServletHolder(this.servlet);

        this.server = new Server(port);
        final HttpConfiguration httpConfiguration = new HttpConfiguration();
        httpConfiguration.setSecureScheme("https");
        httpConfiguration.setSecurePort(httpsPort);

        String keystorePath = MockKinesis.class.getResource("/keystore.jks").toExternalForm();
        final SslContextFactory sslContextFactory = new SslContextFactory(keystorePath);
        sslContextFactory.setKeyStorePassword("password");
        final HttpConfiguration httpsConfiguration = new HttpConfiguration(httpConfiguration);
        httpsConfiguration.addCustomizer(new SecureRequestCustomizer());
        final ServerConnector httpsConnector = new ServerConnector(server,
                new SslConnectionFactory(sslContextFactory, HttpVersion.HTTP_1_1.asString()),
                new HttpConnectionFactory(httpsConfiguration));
        httpsConnector.setPort(httpsPort);
        server.addConnector(httpsConnector);


        final ServerConnector http = new ServerConnector(server,
                new HttpConnectionFactory(httpConfiguration));
        http.setPort(port);
        server.addConnector(http);



        ServletContextHandler context = new ServletContextHandler(server, "/*");
        context.addServlet(servlet, "/*");

        try {
            this.server.start();
        }
        catch(Exception ex){}
        finally {

        }

        this.addStream(streamname);
        this.addStream(streamname2);

    }


    public void addStream(String streamName){
        servlet.addStream(streamName);
    }

    public static void main(String[] args) throws Exception {
        MockKinesis mock = new MockKinesis();
        mock.execute();

        MockKinesisClient client = new MockKinesisClient(8898);
        client.setRateLimitErrorRate("badstream", 1);
        client.setErrorRate("badstream", 1);

        server.join();

    }
}