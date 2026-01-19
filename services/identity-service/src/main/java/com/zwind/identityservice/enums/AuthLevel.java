package com.zwind.identityservice.enums;

public enum AuthLevel {
    ANONYMOUS(0),
    PASSWORD(1),
    OAUTH(2),
    MFA(3),
    HIGH(4);
    ;
    private final int level;

    AuthLevel(int level) {
        this.level = level;
    }

    public boolean gte(AuthLevel other) {
        return this.level >= other.level;
    }
}
