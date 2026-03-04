package main.pack;

import java.util.Objects;


public record PackObjectHeader(
        PackObjectType type,
        int size
) {
    public PackObjectHeader {
        Objects.requireNonNull(type, "Pack object type cannot be null");

        if (size < 0) {
            throw new IllegalArgumentException(
                    "Pack object size cannot be negative: " + size + ". (Possible parser overflow or corrupt packfile)"
            );
        }
    }
}
