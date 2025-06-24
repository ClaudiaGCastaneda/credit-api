package com.aplazo.bnpl.credit.api.credit_api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(name = "CustomerRequest", description = "Details for registering a new customer")
public class ClientRegistrationDTO {

    @NotBlank(message = "First name is required and cannot be empty")
    @Schema(description = "Customer's first name", example = "Juan", requiredMode = Schema.RequiredMode.REQUIRED)
    private String firstName;

    @NotBlank(message = "Last name is required and cannot be empty")
    @Schema(description = "Customer's last name", example = "López", requiredMode = Schema.RequiredMode.REQUIRED)
    private String lastName;

    @NotBlank(message = "Second last name is required and cannot be empty")
    @Schema(description = "Customer's second last name", example = "Pérez", requiredMode = Schema.RequiredMode.REQUIRED)
    private String secondLastName;

    @NotBlank(message = "Date of birth is required and cannot be empty")
    @Schema(description = "Customer's date of birth in YYYY-MM-DD format", example = "1990/05/15", requiredMode = Schema.RequiredMode.REQUIRED)
    private String dateOfBirth;

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getSecondLastName() {
        return secondLastName;
    }

    public void setSecondLastName(String secondLastName) {
        this.secondLastName = secondLastName;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

}
