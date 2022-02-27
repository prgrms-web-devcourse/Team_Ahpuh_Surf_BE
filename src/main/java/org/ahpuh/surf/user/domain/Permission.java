package org.ahpuh.surf.user.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

@Getter
@JsonFormat(shape = JsonFormat.Shape.STRING)
public enum Permission {
    ROLE_USER("USER"),
    ROLE_ADMIN("ADMIN");

    @JsonValue
    private final String role;

    Permission(final String role) {
        this.role = role;
    }
}
