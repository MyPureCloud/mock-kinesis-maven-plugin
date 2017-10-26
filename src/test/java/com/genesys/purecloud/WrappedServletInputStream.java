package com.genesys.purecloud;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;

public class WrappedServletInputStream extends ServletInputStream {


    ByteArrayInputStream input;

    public WrappedServletInputStream(String body) {
        input = new ByteArrayInputStream(body.getBytes());
    }

    @Override
    public int read() throws IOException {
        return input.read();
    }

    @Override
    public boolean isFinished() {
        try{
            return input.available() == 0;
        }catch(Exception ex){
            return true;
        }

    }

    @Override
    public boolean isReady() {
        return true;
    }

    @Override
    public void setReadListener(ReadListener readListener) {

    }
}