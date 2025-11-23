package com.javanauta.taskscheduler.security;

import org.springframework.stereotype.Component;

@Component
public class TokenCleaner {
    public String clean(String rawToken) {
        if (rawToken == null) return null;
        return rawToken.replace("Bearer ", "").trim();
    }
}