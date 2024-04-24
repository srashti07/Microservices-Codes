package com.bej.product.service;

import com.bej.product.domain.Customer;
import com.bej.product.domain.Product;
import com.bej.product.exception.CustomerAlreadyExistsException;
import com.bej.product.exception.CustomerNotFoundException;
import com.bej.product.exception.ProductNotFoundException;

import com.bej.product.proxy.CustomerProxy;
import com.bej.product.repository.CustomerProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;


import java.util.Arrays;
import java.util.List;
@Service
public class CustomerProductServiceImpl implements ICustomerProductService{
    private CustomerProductRepository customerProductRepository;
    private CustomerProxy customerProxy;
    // Autowire the CustomerProductRepository using constructor autowiring
    @Autowired
    public CustomerProductServiceImpl(CustomerProductRepository customerProductRepository, CustomerProxy customerProxy){
        this.customerProductRepository = customerProductRepository;
        this.customerProxy = customerProxy;
    }
    @Override
    public Customer registerCustomer(Customer customer) throws CustomerAlreadyExistsException {
        // Register a new user
        if(customerProductRepository.findById(customer.getCustomerId()).isPresent())
        {
            throw new CustomerAlreadyExistsException();
        }
        Customer saveCustomer = customerProductRepository.save(customer);
        if(saveCustomer.getCustomerId() != 0){
            ResponseEntity r = customerProxy.saveCustomer(customer);
            System.out.println(r.getBody());
        }

        // Register a new user

        return saveCustomer;
    }



    @Override
    public Customer saveCustomerProductToList(Product product, int customerId) throws CustomerNotFoundException {
        // Save the product to the customer list
        if(customerProductRepository.findById(customerId).isEmpty()){
            throw new CustomerNotFoundException();
        }
        Customer customer = customerProductRepository.findByCustomerId(customerId);
        if(customer.getProductList() ==null)
        {
            customer.setProductList(Arrays.asList(product));
        }
        else {
            List<Product>products = customer.getProductList();
            products.add(product);
            customer.setProductList(products);
        }
        return customerProductRepository.save(customer);
    }

    @Override
    public Customer deleteCustomerProductFromList(int customerId, String productCode) throws CustomerNotFoundException, ProductNotFoundException {
        // Delete a product from the customer list
        boolean productCodeIsPresent = false;
        Customer customer = customerProductRepository.findByCustomerId(customerId);
        List<Product> products = customer.getProductList();
        productCodeIsPresent = products.removeIf(x->x.getProductCode().equals(customerId));
        customer.setProductList(products);
        return customerProductRepository.save(customer);
    }

    @Override
    public List<Product> getAllUserProductsFromList(int customerId) throws CustomerNotFoundException {
        // Get all products from the customer list
        if(customerProductRepository.findById(customerId).isEmpty()){
            throw new CustomerNotFoundException();
        }
        return customerProductRepository.findById(customerId).get().getProductList();


    }


}
