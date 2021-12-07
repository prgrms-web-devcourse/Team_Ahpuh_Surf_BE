package org.ahpuh.surf.common.exception;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.function.Supplier;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class EntityExceptionSuppliers {

    public static final Supplier<RuntimeException> CategoryNotFound = () -> {
        throw new IllegalArgumentException("Category with given id not found.");
    };

    public static final Supplier<RuntimeException> UserNotFound = () -> {
        throw new IllegalArgumentException("User with given id not found.");
    };

    public static final Supplier<RuntimeException> PostNotFound = () -> {
        throw new IllegalArgumentException("Post with given id not found.");
    };
}
