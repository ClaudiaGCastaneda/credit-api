package com.aplazo.bnpl.credit.api.credit_api.controllers;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.aplazo.bnpl.credit.api.credit_api.dto.ClientRegistrationDTO;
import com.aplazo.bnpl.credit.api.credit_api.dto.ClientResponseDTO;
import com.aplazo.bnpl.credit.api.credit_api.dto.ErrorResponseDTO;
import com.aplazo.bnpl.credit.api.credit_api.entities.Customer;
import com.aplazo.bnpl.credit.api.credit_api.services.CustomerService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.links.Link;
import io.swagger.v3.oas.annotations.links.LinkParameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/v1/customers")
@Tag(name = "Customers", description = "Manage customers")
public class CustomerController {

    @Autowired
    private CustomerService service;

    @GetMapping
    public List<Customer> list() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get customer identified by customerId", operationId = "getCustomerById", tags = {
            "Customers" }, parameters = {
                    @Parameter(name = "id", description = "Customer's unique identifier", required = true, schema = @Schema(type = "string", format = "uuid", description = "UUID of the customer"))
            }, responses = {
                    @ApiResponse(responseCode = "200", description = "Ok", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ClientResponseDTO.class))),
                    @ApiResponse(responseCode = "401", description = "Authentication required or invalid credentials", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDTO.class), examples = @ExampleObject(name = "Unauthorized Request Example", value = "{\"code\":\"APZ000002\",\"error\":\"UNAUTHORIZED\",\"timestamp\":1739397485,\"message\":\"Authentication token is missing or invalid\",\"path\":\"/v1/customers\"}"))),
                    @ApiResponse(responseCode = "404", description = "Not found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDTO.class), examples = {
                            @ExampleObject(name = "Customer not found response", description = "Customer not found", value = "{\"code\":\"APZ000005\",\"error\":\"CUSTOMER_NOT_FOUND\",\"timestamp\":1739397485,\"message\":\"Error detail\",\"path\":\"/v1/customers/{customerId}\"}")
                    })),
                    @ApiResponse(responseCode = "500", description = "Unexpected internal server error", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDTO.class), examples = @ExampleObject(name = "Internal Server Error Example", value = "{\"code\":\"APZ000000\",\"error\":\"INTERNAL_SERVER_ERROR\",\"timestamp\":1739397485,\"message\":\"An unexpected error occurred\",\"path\":\"/v1/customers\"}")))
            })
    public ResponseEntity<?> view(@PathVariable UUID id) {

        Optional<Customer> clientOptional = service.findById(id);

        if (clientOptional.isPresent()) {
            Customer foundCustomer = clientOptional.orElseThrow();
            ClientResponseDTO responseDTO = new ClientResponseDTO(
                    foundCustomer.getId(),
                    foundCustomer.getAvailable_credit());
            return ResponseEntity.ok(responseDTO);
        }
        ErrorResponseDTO errorResponse = new ErrorResponseDTO(
                "APZ000005",
                "CUSTOMER_NOT_FOUND",
                System.currentTimeMillis() / 1000,
                "Customer not found with ID: " + id,
                "/v1/customers/" + id.toString());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    @PostMapping
    @Operation(summary = "Create a customer", operationId = "createCustomer", requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Customer request", required = true, content = @Content(mediaType = "application/json", schema = @Schema(implementation = ClientRegistrationDTO.class), examples = {
            @ExampleObject(name = "Customer under 18", summary = "Under age customer example", description = "Under age customer", value = "{\"firstName\": \"Juan\", \"lastName\": \"López\", \"secondLastName\": \"Pérez\", \"dateOfBirth\": \"2009/11/02\"}"),
            @ExampleObject(name = "Customer accepted", summary = "Customer with accepted age example", description = "Customer which age is in accepted range", value = "{\"firstName\": \"Pepe\", \"lastName\": \"García\", \"secondLastName\": \"Flores\", \"dateOfBirth\": \"1998/07/21\"}")
    })),

            responses = {
                    @ApiResponse(responseCode = "201", description = "Created", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ClientResponseDTO.class)), headers = {
                            @Header(name = "Location", description = "Relative path to search for newly created customer", schema = @Schema(type = "string", format = "uri-reference"), example = "/v1/customers/3fa85f64-5717-4562-b3fc-2c963f66afa7")
                    // OMITTED: X-Auth-Token as requested
                    }, links = {
                            @Link(name = "GetCustomerById", operationId = "getCustomerById", parameters = @LinkParameter(name = "id", expression = "$response.body#/id"), description = "The `id` value returned in the response can be used as "
                                    +
                                    "the `customerId` parameter in `GET /customers/{customerId}`.")
                    }),
                    @ApiResponse(responseCode = "400", description = "Bad Request")
            })
    public ResponseEntity<?> create(@Valid @RequestBody ClientRegistrationDTO clientDTO, BindingResult result) {

        if (result.hasFieldErrors()) {
            return validation(result);

        }

        ClientResponseDTO savedClient = service.save(clientDTO);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(savedClient.getId())
                .toUri();

        return ResponseEntity.created(location).body(savedClient);

    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@Valid @RequestBody Customer client,
            BindingResult result, @Valid @PathVariable UUID id) {

        if (result.hasFieldErrors()) {
            return validation(result);

        }
        Optional<Customer> clientOptional = service.update(id, client);

        if (clientOptional.isPresent()) {
            return ResponseEntity.status(HttpStatus.CREATED).body(clientOptional.orElseThrow());

        }
        return ResponseEntity.notFound().build();

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable UUID id) {
        Customer client = new Customer();
        client.setId(id);
        Optional<Customer> clientOptional = service.delete(client);
        if (clientOptional.isPresent()) {
            return ResponseEntity.ok(clientOptional.orElseThrow());
        }
        return ResponseEntity.notFound().build();
    }

    private ResponseEntity<?> validation(BindingResult result) {
        Map<String, String> errors = new HashMap<>();
        result.getFieldErrors().forEach(err -> {
            errors.put(err.getField(), "El campo " + err.getField() + " " + err.getDefaultMessage());
        });
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);

    }

}
