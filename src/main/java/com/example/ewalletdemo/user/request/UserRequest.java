package com.example.ewalletdemo.user.request;

import com.example.ewalletdemo.user.model.WalletUser;
import lombok.*;

//@Component
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class UserRequest {
    private String userId;
    private String emailId;
    private String name;
    private int age;

    // to convert userRequest->User
    public WalletUser buildUserFromUserRequest() {
        return WalletUser.builder()
                .userId(this.userId)
                .emailId(this.emailId)
                .name(this.name)
                .age(this.age)
                .build();
    }
}
