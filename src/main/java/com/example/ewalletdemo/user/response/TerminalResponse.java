package com.example.ewalletdemo.user.response;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class TerminalResponse {

    private int code;
    private String message;

    @NonNull // this will contain the response part of body.
    private Object data;

    public TerminalResponse(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
