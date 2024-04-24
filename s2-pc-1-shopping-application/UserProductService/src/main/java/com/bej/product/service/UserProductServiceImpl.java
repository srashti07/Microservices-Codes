package com.bej.product.service;

import com.bej.product.domain.User;
import com.bej.product.domain.Product;
import com.bej.product.exception.UserAlreadyExistsException;
import com.bej.product.exception.UserNotFoundException;
import com.bej.product.exception.ProductNotFoundException;
import com.bej.product.repository.UserProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class UserProductServiceImpl implements IUserProductService {

    private UserProductRepository userProductRepository;
    @Autowired
    public UserProductServiceImpl(UserProductRepository userProductRepository){
        this.userProductRepository = userProductRepository;
    }

    // Autowire the UserProductRepository using constructor autowiring

    @Override
    public User registerUser(User user) throws UserAlreadyExistsException {
        Optional<User> existingUser = userProductRepository.findById(user.getUserId());
        if (existingUser.isPresent()) {
            throw new UserAlreadyExistsException();
        }
        return userProductRepository.save(user);
    }



    @Override
    public User saveUserProductToList(Product product, String userId) {
        // Save the product to the User
        User user = userProductRepository.findByUserId(userId);
        if(user.getProductList()==null)
        {
            user.setProductList(Arrays.asList(product));
        }
        else {
            List<Product> products = user.getProductList();
            products.add(product);
            user.setProductList(products);
        }
        return userProductRepository.save(user);
    }


    @Override
    public User deleteUserProductFromList(String userId, String productCode)  {
        // Delete a product from the user list
        boolean productCodeIsPresent = false;
        User user = userProductRepository.findByUserId(userId);
        List<Product> products = user.getProductList();
        productCodeIsPresent = products.removeIf(x->x.getProductCode().equals(userId));
        user.setProductList(products);
        return userProductRepository.save(user);
    }

    @Override
    public List<Product> getAllUserProductsFromList(String userId) {
        // Get all products from the User list

        return userProductRepository.findByUserId(userId).getProductList();

    }




}
