package com.example.ewalletdemo.user.util;

import com.example.ewalletdemo.user.constants.ResponseCodes;
import com.example.ewalletdemo.user.exception.FinalException;
import com.example.ewalletdemo.user.response.TerminalResponse;

public class ResponseBuilder {

    private ResponseBuilder() {
    }

    public static TerminalResponse successResponse(Object data) {
        return new TerminalResponse(ResponseCodes.SUCCESS_STATUS, ResponseCodes.SUCCESS_MESSAGE, data);
    }

    public static TerminalResponse genericErrorResponse() {
        return new TerminalResponse(ResponseCodes.GENERIC_ERROR, ResponseCodes.GENERIC_ERROR_MESSAGE);
    }

    public static TerminalResponse genericErrorResponse(FinalException fex) {
        return new TerminalResponse(fex.getCode(), fex.getMessage());
    }

}
