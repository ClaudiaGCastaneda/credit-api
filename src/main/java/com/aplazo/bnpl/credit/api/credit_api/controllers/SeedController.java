package com.aplazo.bnpl.credit.api.credit_api.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.aplazo.bnpl.credit.api.credit_api.services.SeedService;

@RestController
@RequestMapping("/v1/seed")
public class SeedController {

    @Autowired
    private SeedService seedService;

    @PostMapping
    public ResponseEntity<String> executeSeed() {
        try {
            boolean success = seedService.runSeed();

            if (success) {
                return ResponseEntity.ok("Catálogo de reglas de línea de crédito cargado exitosamente.");
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Fallo al cargar el catálogo de reglas de línea de crédito.");
            }

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error inesperado durante el seeding: " + e.getMessage());
        }

    }

}
