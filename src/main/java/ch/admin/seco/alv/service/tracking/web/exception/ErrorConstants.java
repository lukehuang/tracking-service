package ch.admin.seco.alv.service.tracking.web.exception;

import java.net.URI;

public final class ErrorConstants {

    public static final String ERR_VALIDATION = "error.validation";

    public static final String PROBLEM_BASE_URL = "http://www.job-room.ch/tracking-service/problem";

    public static final URI DEFAULT_TYPE = URI.create(PROBLEM_BASE_URL + "/problem-with-message");

    public static final URI CONSTRAINT_VIOLATION_TYPE = URI.create(PROBLEM_BASE_URL + "/constraint-violation");

    public static final URI ENTITY_NOT_FOUND_TYPE = URI.create(PROBLEM_BASE_URL + "/entity-not-found");

    private ErrorConstants() {
    }
}
