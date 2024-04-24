package com.bej.product.service;

import com.bej.product.domain.User;
import com.bej.product.domain.Product;
import com.bej.product.exception.UserAlreadyExistsException;
import com.bej.product.exception.UserNotFoundException;
import com.bej.product.exception.ProductNotFoundException;
import com.bej.product.repository.UserProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
@Service
public class UserProductServiceImpl implements IUserProductService {
    private UserProductRepository userProductRepository;
    // Autowire the UserProductRepository using constructor autowiring
    @Autowired
    public UserProductServiceImpl(UserProductRepository userProductRepository){
        this.userProductRepository = userProductRepository;
    }

    @Override
    public User registerUser(User user) throws UserAlreadyExistsException {
        if(userProductRepository.findById(user.getUserId()).isPresent())
        {
            throw new UserAlreadyExistsException();
        }

        // Register a new user

        return userProductRepository.save(user);
    }

    @Override
    public User saveUserProductToList(Product product, String userId)throws UserNotFoundException {
        // Save the product to the User
        if(userProductRepository.findById(userId).isEmpty()){
            throw new UserNotFoundException();
        }
        User user = userProductRepository.findByUserId(userId);
        if(user.getProductList() ==null)
        {
            user.setProductList(Arrays.asList(product));
        }
        else {
            List<Product>products = user.getProductList();
            products.add(product);
            user.setProductList(products);
        }
        return userProductRepository.save(user);

    }

    @Override
    public User deleteUserProductFromList(String userId, String productCode)   {
        // Delete a product from the user list
        boolean productCodeIsPresent = false;
        User user = userProductRepository.findByUserId(userId);
        List<Product> products = user.getProductList();
        productCodeIsPresent = products.removeIf(x->x.getProductCode().equals(userId));
        user.setProductList(products);
        return userProductRepository.save(user);
    }

    @Override
    public List<Product> getAllUserProductsFromList(String userId) throws UserNotFoundException {
        // Get all products from the User list
        if(userProductRepository.findById(userId).isEmpty()){
            throw new UserNotFoundException();
        }
        return userProductRepository.findById(userId).get().getProductList();

    }




}
