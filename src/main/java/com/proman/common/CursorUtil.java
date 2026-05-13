package com.proman.common;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;
import java.util.UUID;

public final class CursorUtil {

    private CursorUtil() {}

    public static String encode(Instant createdAt, UUID id) {
        String raw = createdAt.toEpochMilli() + ":" + id.toString();
        return Base64.getUrlEncoder().withoutPadding()
            .encodeToString(raw.getBytes(StandardCharsets.UTF_8));
    }

    public static DecodedCursor decode(String cursor) {
        String raw = new String(
            Base64.getUrlDecoder().decode(cursor), StandardCharsets.UTF_8);
        String[] parts = raw.split(":", 2);
        return new DecodedCursor(
            Instant.ofEpochMilli(Long.parseLong(parts[0])),
            UUID.fromString(parts[1])
        );
    }

    public record DecodedCursor(Instant createdAt, UUID id) {}
}
