package com.example.ewalletdemo.user.exception;

import com.example.ewalletdemo.user.constants.ResponseCodes;
import com.example.ewalletdemo.user.response.TerminalResponse;

public class FinalException extends Exception {
    private TerminalResponse errorResponse;

    public FinalException(int errorCode, String errorMessage) {
        super(errorMessage);
        errorResponse = new TerminalResponse();
        errorResponse.setCode(errorCode);
        errorResponse.setMessage(errorMessage);
    }


    @Override
    public String getMessage() {
        if (errorResponse != null)
            return errorResponse.getMessage();

        return super.getMessage();
    }


    public int getCode() {
        if (errorResponse != null)
            return errorResponse.getCode();

        return ResponseCodes.GENERIC_ERROR;
    }
}
