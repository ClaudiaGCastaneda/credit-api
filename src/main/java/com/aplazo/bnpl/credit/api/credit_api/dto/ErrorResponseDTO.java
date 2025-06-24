package com.aplazo.bnpl.credit.api.credit_api.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "ErrorResponse", description = "Represents a standardized error response from the API")
public class ErrorResponseDTO {

    @Schema(description = "Internal error code, e.g., 'APZ000001'", pattern = "^APZ\\d{6}$", example = "APZ000001")
    private String code;

    @Schema(description = "Internal error name, e.g., 'VALIDATION_ERROR'", example = "VALIDATION_ERROR")
    private String error;

    @Schema(description = "Unix timestamp when the error occurred (in seconds)", type = "integer", format = "int64", example = "1739397485")
    private Long timestamp; // Usamos Long para int64

    @Schema(description = "Detailed error message or description for the client", example = "Validation failed: field 'firstName' cannot be blank")
    private String message;

    @Schema(description = "The API path where the error occurred", example = "/v1/customers")
    private String path;

    public ErrorResponseDTO(String code, String error, Long timestamp, String message, String path) {
        this.code = code;
        this.error = error;
        this.timestamp = timestamp;
        this.message = message;
        this.path = path;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

}
