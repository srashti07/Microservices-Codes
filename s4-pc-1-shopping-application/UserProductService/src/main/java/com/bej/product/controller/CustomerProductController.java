package com.bej.product.controller;

import com.bej.product.domain.Customer;
import com.bej.product.domain.Product;
import com.bej.product.exception.CustomerAlreadyExistsException;
import com.bej.product.exception.CustomerNotFoundException;
import com.bej.product.exception.ProductNotFoundException;
import com.bej.product.service.ICustomerProductService;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class CustomerProductController {
    private ICustomerProductService iCustomerProductService;
    private ResponseEntity<?> responseEntity;
    // Autowire ICustomerProductService using constructor autowiring
    @Autowired
    public CustomerProductController(ICustomerProductService iCustomerProductService){
        this.iCustomerProductService = iCustomerProductService;
    }
    @PostMapping("/register")
    public ResponseEntity<?> registerCustomer(@RequestBody Customer customer) throws CustomerAlreadyExistsException {
        // Register a new user and save to db,
        // return 201 status if user is saved else 500 status
        try{
            Customer savedCustomer = iCustomerProductService.registerCustomer(customer);
            return new ResponseEntity<>("User with Id"+ savedCustomer.getCustomerId()+"created successfully",HttpStatus.CREATED);
        }catch(CustomerAlreadyExistsException e){
            return new ResponseEntity<>("User already exists",HttpStatus.CONFLICT);
        }
    }

    @PostMapping("/customer/saveProduct")
    public ResponseEntity<?> saveCustomerProductToList(@RequestBody Product product, HttpServletRequest request) throws CustomerNotFoundException {
        // add a product to a specific customer,
        // return 201 status if product is saved else 500 status

        try {
            System.out.println("header" + request.getHeader("Authorization"));
            Claims claims = (Claims) request.getAttribute("claims");
            System.out.println("customerid for claim ::" + claims.getSubject());
            String customerId = claims.getSubject();
            System.out.println("customerId ::" + customerId);

            // Call the service method to save the product for the user
            Customer savedCustomer = iCustomerProductService.saveCustomerProductToList(product, Integer.parseInt(customerId));

            // Return a response entity with the saved user and HTTP status 201
            return new ResponseEntity<>(savedCustomer, HttpStatus.CREATED);
        } catch (CustomerNotFoundException e) {
            // Handle the case where the user is not found
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            // Handle other exceptions
            return new ResponseEntity<>("Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/customer/getAllProducts")
    public ResponseEntity<?> getAllCustomerProductsFromList() throws CustomerNotFoundException {

        // list all products of a specific customer,
        // and return 200 status
        try {

            responseEntity = new ResponseEntity<>(iCustomerProductService.getAllUserProductsFromList(1),HttpStatus.OK);


        }catch (Exception e) {
            // Handle other exceptions
            return new ResponseEntity<>("Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return responseEntity;
    }

    @DeleteMapping("/customer/{productCode}")
    public ResponseEntity<?> deleteCustomerProductFromList(@PathVariable String productCode) throws ProductNotFoundException, CustomerNotFoundException, ProductNotFoundException {

        // delete product of a specific customer,
        // return 200 status if product is deleted else 500 status

        try{
            responseEntity = new ResponseEntity<>(iCustomerProductService.deleteCustomerProductFromList(1,productCode),HttpStatus.OK);
        }catch (Exception e){
            return new ResponseEntity<>("Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return responseEntity;
    }
}
