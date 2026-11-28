package com.upc.webmarketar.serviceimpl;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;

/** Match PostgreSQL timestamptz precision in the first response and subsequent replays. */
final class Times {
    private Times() {}

    static OffsetDateTime now() {
        return database(OffsetDateTime.now(ZoneOffset.UTC));
    }

    static OffsetDateTime database(OffsetDateTime value) {
        return value.withOffsetSameInstant(ZoneOffset.UTC).truncatedTo(ChronoUnit.MICROS);
    }
}
