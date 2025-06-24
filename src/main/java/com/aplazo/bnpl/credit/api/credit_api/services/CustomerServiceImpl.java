package com.aplazo.bnpl.credit.api.credit_api.services;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.aplazo.bnpl.credit.api.credit_api.dto.ClientRegistrationDTO;
import com.aplazo.bnpl.credit.api.credit_api.dto.ClientResponseDTO;
import com.aplazo.bnpl.credit.api.credit_api.entities.CreditLine;
import com.aplazo.bnpl.credit.api.credit_api.entities.Customer;
import com.aplazo.bnpl.credit.api.credit_api.repositories.CustomerRepository;
import com.aplazo.bnpl.credit.api.credit_api.util.AgeCalculator;

@Service
public class CustomerServiceImpl implements CustomerService {

    @Autowired
    private CustomerRepository repository;
    @Autowired
    private CreditLineService creditLineService;

    @Value("${config.credit.min.age}")
    private int minAge;
    @Value("${config.credit.max.age}")
    private int maxAge;
    @Autowired
    private AgeCalculator ageCalculator;

    @Transactional
    @Override
    public ClientResponseDTO save(ClientRegistrationDTO clientDTO) {

        int clientAge = ageCalculator.calculateAge(clientDTO.getDateOfBirth());
        Customer customerToDB = null;

        if (clientAge < minAge || clientAge > maxAge) {
            throw new IllegalArgumentException("No es posible otorgarle una línea de crédito. Su edad actual es "
                    + clientAge + " años. La edad debe estar entre " + minAge + " y " + maxAge + " años.");
        }

        Optional<CreditLine> creditOptional = creditLineService.get(clientAge);

        if (creditOptional.isPresent()) {

            CreditLine foundCreditLine = creditOptional.get();

            System.out.println(foundCreditLine.toString());

            customerToDB = new Customer();
            customerToDB.setName(
                    clientDTO.getFirstName() + " " + clientDTO.getLastName() + " " + clientDTO.getSecondLastName());
            customerToDB.setBirthDate(clientDTO.getDateOfBirth());
            customerToDB.setAvailable_credit(foundCreditLine.getCreditLine());
            customerToDB.setCredit_limit_by_id(foundCreditLine.getId());

            Customer savedClient = repository.save(customerToDB);

            return new ClientResponseDTO(savedClient.getId(), savedClient.getAvailable_credit());
        }

        return null;

    }

    @Transactional(readOnly = true)
    @Override
    public List<Customer> findAll() {

        return (List<Customer>) repository.findAll();
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<Customer> findById(UUID id) {

        return repository.findById(id);
    }

    @Transactional
    @Override
    public Optional<Customer> update(UUID id, Customer customer) {
        Optional<Customer> clientOptional = repository.findById(customer.getId());
        if (clientOptional.isPresent()) {
            Customer customerDb = clientOptional.orElseThrow();

            customerDb.setName(customer.getName());
            customerDb.setAvailable_credit(customer.getAvailable_credit());
            return Optional.of(repository.save(customerDb));
        }
        return clientOptional;
    }

    @Transactional
    @Override
    public Optional<Customer> updateCredit(UUID id, Customer client) {
        Optional<Customer> clientOptional = repository.findById(client.getId());
        if (clientOptional.isPresent()) {
            Customer clientDb = clientOptional.orElseThrow();

            clientDb.setAvailable_credit(client.getAvailable_credit());
            return Optional.of(repository.save(clientDb));
        }
        return clientOptional;
    }

    @Transactional
    @Override
    public Optional<Customer> delete(Customer customer) {
        Optional<Customer> clientOptional = repository.findById(customer.getId());
        clientOptional.ifPresent(clientDb -> {
            repository.delete(clientDb);
        });
        return clientOptional;
    }

}
