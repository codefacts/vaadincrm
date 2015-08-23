package io.crm;

public enum FailureCode {
    validationError(1, "Validation Error"), InternalServerError(2, "Internal Server Error");

    public final int code;
    public final String message;

    FailureCode(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
