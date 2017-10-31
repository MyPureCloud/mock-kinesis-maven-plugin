package com.genesys.purecloud.errors;

public class ResourceNotFoundException extends KinesisError{
    public ResourceNotFoundException(){
        super("The requested resource could not be found. The stream might not be specified correctly.","ResourceNotFoundException");
    }
}
