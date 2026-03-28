package com.malgn.domain.member.entity;

public enum Role {
    ADMIN, USER;

    public String toAuthority() {
        return "ROLE_" + this.name();
    }
}
