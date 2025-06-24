package com.aplazo.bnpl.credit.api.credit_api.services;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.aplazo.bnpl.credit.api.credit_api.entities.CreditLine;
import com.aplazo.bnpl.credit.api.credit_api.entities.PaymentSchema;
import com.aplazo.bnpl.credit.api.credit_api.entities.Role;
import com.aplazo.bnpl.credit.api.credit_api.repositories.CreditLineRepository;
import com.aplazo.bnpl.credit.api.credit_api.repositories.PaymentSchemaRepository;
import com.aplazo.bnpl.credit.api.credit_api.repositories.RoleRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class SeedServiceImpl implements SeedService {

    private static final Logger logger = LoggerFactory.getLogger(SeedServiceImpl.class);
    private static final String CREDIT_LINE_RULES_CATALOG_PATH = "credit_line_rules.json";
    private static final String PAYMENT_SCHEMAS_CATALOG_PATH = "payment_schemas.json";
    // Ya no necesitas archivo roles.json si los roles son fijos

    @Autowired
    private CreditLineService creditLineService;
    @Autowired
    private CreditLineRepository creditLineRepository;
    @Autowired
    private PaymentSchemaRepository paymentSchemaRepository;
    @Autowired
    private RoleRepository roleRepository; // Agrega el repositorio de Roles
    @Autowired
    private ObjectMapper objectMapper;

    @Transactional
    @Override
    public boolean runSeed() {
        logger.info("Iniciando proceso de seeding para todos los catálogos.");
        boolean success = true;

        try {
            // Seed CreditLineRules
            logger.info("Iniciando seeding para CreditLineRules.");
            creditLineService.deleteAllRules();
            logger.info("Contenido existente de credit_line_rules borrado.");

            ClassPathResource creditLineResource = new ClassPathResource(CREDIT_LINE_RULES_CATALOG_PATH);
            List<CreditLine> creditLineRulesToInsert;

            try (InputStream inputStream = creditLineResource.getInputStream()) {
                creditLineRulesToInsert = objectMapper.readValue(inputStream, new TypeReference<List<CreditLine>>() {
                });
                logger.info("Leídas {} reglas de línea de crédito del archivo JSON.", creditLineRulesToInsert.size());
                creditLineRepository.saveAll(creditLineRulesToInsert);
                logger.info("Reglas de línea de crédito insertadas exitosamente.");
            }
        } catch (IOException e) {
            logger.error("Error al leer o procesar el archivo JSON de catálogo de reglas de línea de crédito ({}): {}",
                    CREDIT_LINE_RULES_CATALOG_PATH, e.getMessage(), e);
            success = false;
        } catch (Exception e) {
            logger.error("Error durante el seeding de CreditLineRules: {}", e.getMessage(), e);
            success = false;
            throw e;
        }

        if (success) {
            try {
                // Seed PaymentSchemas
                logger.info("Iniciando seeding para PaymentSchemas.");

                paymentSchemaRepository.deleteAll();
                logger.info("Contenido existente de payment_schemas borrado.");

                ClassPathResource paymentSchemaResource = new ClassPathResource(PAYMENT_SCHEMAS_CATALOG_PATH);
                List<PaymentSchema> paymentSchemasToInsert;

                try (InputStream inputStream = paymentSchemaResource.getInputStream()) {
                    paymentSchemasToInsert = objectMapper.readValue(inputStream,
                            new TypeReference<List<PaymentSchema>>() {
                            });
                    logger.info("Leídos {} esquemas de pago del archivo JSON.", paymentSchemasToInsert.size());
                    paymentSchemaRepository.saveAll(paymentSchemasToInsert);
                    logger.info("Esquemas de pago insertados exitosamente.");
                }
            } catch (IOException e) {
                logger.error("Error al leer o procesar el archivo JSON de catálogo de esquemas de pago ({}): {}",
                        PAYMENT_SCHEMAS_CATALOG_PATH, e.getMessage(), e);
                success = false;
            } catch (Exception e) {
                logger.error("Error durante el seeding de PaymentSchemas: {}", e.getMessage(), e);
                success = false;
                throw e;
            }
        }

        if (success) {
            try {
                // Seed Roles
                logger.info("Iniciando seeding para Roles.");

                roleRepository.deleteAll(); // Borra roles existentes
                logger.info("Roles existentes borrados.");

                Role adminRole = new Role("ADMIN");
                Role userRole = new Role("USER");

                List<Role> rolesToInsert = Arrays.asList(adminRole, userRole);
                roleRepository.saveAll(rolesToInsert);

                logger.info("Roles ADMIN y USER insertados exitosamente.");
            } catch (Exception e) {
                logger.error("Error durante el seeding de Roles: {}", e.getMessage(), e);
                success = false;
                throw e;
            }
        }

        logger.info("Proceso de seeding finalizado. Éxito: {}", success);
        return success;
    }
}
