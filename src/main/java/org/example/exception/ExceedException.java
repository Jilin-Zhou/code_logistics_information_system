package org.example.exception;


public class ExceedException extends RuntimeException{

    String message;

    public ExceedException(){
        super();
    }

    public ExceedException(String msg){
        super();
        this.message = msg;
    }

    @Override
    public String getMessage() {
        return message;
    }

}
