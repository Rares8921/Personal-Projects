package com.example.ihas.models;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class Audit {
    private String action;
    private LocalDateTime timestamp;

    public Audit() {}

    public Audit(String _action, LocalDateTime _timestamp) {
        action = _action;
        timestamp = _timestamp;
    }

}
