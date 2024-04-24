package com.bej.authentication.controller;

import com.bej.authentication.exception.CustomerAlreadyExistsException;
import com.bej.authentication.exception.InvalidCredentialsException;
import com.bej.authentication.security.SecurityTokenGenerator;
import com.bej.authentication.service.ICustomerService;
import com.bej.authentication.domain.Customer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v2")
public class CustomerController {

    // Autowire the ICustomerService, SecurityTokenGenerator using constructor autowiring
    private ICustomerService iCustomerService;
    private SecurityTokenGenerator securityTokenGenerator;
    @Autowired
    public CustomerController(ICustomerService iCustomerService,SecurityTokenGenerator securityTokenGenerator){
        this.iCustomerService = iCustomerService;
        this.securityTokenGenerator = securityTokenGenerator;
    }

    @PostMapping("/saveCustomer")
    public ResponseEntity<?> saveCustomer(@RequestBody Customer customer) throws CustomerAlreadyExistsException {
        // Write the logic to save a customer,
        // return 201 status if customer is saved else 500 status
        try{
            Customer savedCustomer = iCustomerService.saveCustomer(customer);
            return new ResponseEntity<>("Customer with Id"+savedCustomer.getCustomerId()+"created Successfully",HttpStatus.CREATED);
        }catch (CustomerAlreadyExistsException e){
            return new ResponseEntity<>("Customer already exists",HttpStatus.CONFLICT);
        }

    }
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Customer customer) throws InvalidCredentialsException
    {
        // Generate the token on login,
        // return 200 status if customer is logged in  else 500 status

        try{
            Customer retrievedCustomer = iCustomerService.getCustomerByCustomerIdAndPassword(customer.getCustomerId(), customer.getCustomerPassword());
            String token = securityTokenGenerator.createToken(customer);
            return new ResponseEntity<>(token,HttpStatus.OK);
        }catch (InvalidCredentialsException e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Invalid credential");
        }
    }
}
