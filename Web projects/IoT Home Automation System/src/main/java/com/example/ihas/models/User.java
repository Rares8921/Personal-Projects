package com.example.ihas.models;

import lombok.Getter;
import lombok.Setter;
import java.util.UUID;

@Getter
@Setter
public class User {
    @Getter
    private UUID id;
    @Getter
    private String email;
    @Getter
    private String password;
    @Getter
    private boolean enabled;
    @Getter
    private String verificationToken;

    public User(String _email, String _password, String _verificationToken) {
        id = UUID.randomUUID();
        email = _email;
        password = _password;
        enabled = false;
        verificationToken = _verificationToken;
    }
}
