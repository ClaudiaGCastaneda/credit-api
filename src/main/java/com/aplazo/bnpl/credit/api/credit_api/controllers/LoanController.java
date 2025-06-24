package com.aplazo.bnpl.credit.api.credit_api.controllers;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.aplazo.bnpl.credit.api.credit_api.dto.ErrorResponseDTO;
import com.aplazo.bnpl.credit.api.credit_api.dto.LoanRequestDTO;
import com.aplazo.bnpl.credit.api.credit_api.dto.LoanResponseDTO;
import com.aplazo.bnpl.credit.api.credit_api.entities.Loan;
import com.aplazo.bnpl.credit.api.credit_api.exception.PurchaseException;
import com.aplazo.bnpl.credit.api.credit_api.services.LoanService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.links.Link;
import io.swagger.v3.oas.annotations.links.LinkParameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/v1/loans")
@Tag(name = "loans", description = "Manage loans")
public class LoanController {

    @Autowired
    private LoanService service;

    @PostMapping
    @Operation(summary = "Create a loan", operationId = "createLoan", tags = {
            "Loans" }, requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Loan request", required = true, content = @Content(mediaType = "application/json", schema = @Schema(implementation = LoanRequestDTO.class), // Referencia
                                                                                                                                                                                                                                                      // a
                                                                                                                                                                                                                                                      // tu
                                                                                                                                                                                                                                                      // LoanRequestDTO
                    examples = {
                            @ExampleObject(name = "Simple request", description = "Minimal request", value = "{\"customerId\": \"b2863d62-0746-4b26-a6e3-edcb4b9578f2\", \"amount\": 400.80}")
                    })), responses = {
                            @ApiResponse(responseCode = "201", description = "Created", content = @Content(mediaType = "application/json", schema = @Schema(implementation = LoanResponseDTO.class) // LoanResponseDTO
                            ), headers = {
                                    @Header(name = "Location", description = "Relative path to search for newly created loan", schema = @Schema(type = "string", format = "uri-reference"), example = "/v1/loans/3fa85f64-5717-4562-b3fc-2c963f66afa7")
                            }, links = {
                                    @Link(name = "GetLoanById", operationId = "getLoanById", // Apunta al operationId
                                                                                             // del GET por ID
                                            parameters = @LinkParameter(name = "id", expression = "$response.body#/id"), // Obtiene

                                            description = "The `id` value returned in the response can be used as " +
                                                    "the `loanId` parameter in `GET /v1/loans/{loanId}`.")
                            }),
                            @ApiResponse(responseCode = "400", description = "Bad request", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDTO.class), examples = @ExampleObject(name = "Invalid request response", description = "Not valid request error", value = "{\"code\":\"APZ000006\",\"error\":\"INVALID_LOAN_REQUEST\",\"timestamp\":1739397485,\"message\":\"Error detail\",\"path\":\"/v1/loans\"}"))),
                            @ApiResponse(responseCode = "401", description = "Authentication required or invalid credentials", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDTO.class), examples = @ExampleObject(name = "Unauthorized Request Example", value = "{\"code\":\"APZ000002\",\"error\":\"UNAUTHORIZED\",\"timestamp\":1739397485,\"message\":\"Authentication token is missing or invalid\",\"path\":\"/v1/loans\"}"))),
                            @ApiResponse(responseCode = "500", description = "Unexpected internal server error", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDTO.class), examples = @ExampleObject(name = "Internal Server Error Example", value = "{\"code\":\"APZ000000\",\"error\":\"INTERNAL_SERVER_ERROR\",\"timestamp\":1739397485,\"message\":\"An unexpected error occurred\",\"path\":\"/v1/loans\"}")))
                    })
    public ResponseEntity<?> createLoan(@Valid @RequestBody LoanRequestDTO loanRequestDTO, BindingResult result) {

        if (result.hasFieldErrors()) {
            return validation(result);
        }
        System.out.println(loanRequestDTO.getCustomerId());
        System.out.println(loanRequestDTO.getAmount());
        try {
            Loan savedLoan = service.save(loanRequestDTO);
            LoanResponseDTO responseDTO = new LoanResponseDTO(
                    savedLoan.getId().toString(),
                    savedLoan.getIdCliente().toString(),
                    savedLoan.getAmount(),
                    savedLoan.getStatus().toString(),
                    savedLoan.getPurchaseDate());

            URI location = ServletUriComponentsBuilder
                    .fromCurrentRequest()
                    .path("/{id}")
                    .buildAndExpand(savedLoan.getId().toString())
                    .toUri();

            return ResponseEntity.created(location).body(responseDTO);
        } catch (PurchaseException e) {
            ErrorResponseDTO errorResponse = new ErrorResponseDTO(
                    "APZ000006",
                    "INVALID_LOAN_REQUEST",
                    System.currentTimeMillis() / 1000,
                    e.getMessage(),
                    "/v1/loans");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);

        } catch (Exception e) {
            ErrorResponseDTO errorResponse = new ErrorResponseDTO(
                    "APZ000000",
                    "INTERNAL_SERVER_ERROR",
                    System.currentTimeMillis() / 1000,
                    e.getMessage(),
                    "/v1/loans");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);

        }

    }

    private ResponseEntity<?> validation(BindingResult result) {
        Map<String, String> errors = new HashMap<>();
        result.getFieldErrors().forEach(err -> {
            errors.put(err.getField(), "El campo " + err.getField() + " " + err.getDefaultMessage());
        });
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);

    }

}
