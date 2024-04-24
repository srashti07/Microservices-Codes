package com.bej.product.controller;

import com.bej.product.domain.User;
import com.bej.product.domain.Product;
import com.bej.product.exception.UserAlreadyExistsException;
import com.bej.product.exception.UserNotFoundException;
import com.bej.product.exception.ProductNotFoundException;
import com.bej.product.service.IUserProductService;
import com.bej.product.util.JwtUtil;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/v1")
public class UserProductController {
    // Autowire IUserProductService using constructor autowiring
    private IUserProductService iUserProductService;
    private ResponseEntity<?> responseEntity;
    @Autowired
    public UserProductController(IUserProductService iUserProductService){
        this.iUserProductService = iUserProductService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        // Register a new user and save to db,
        // return 201 status if user is saved else 500 status
        // return 201 status if user is saved else 500 status
        try{
            User savedUser = iUserProductService.registerUser(user);
            return new ResponseEntity<>("User with Id"+ savedUser.getUserId()+"created successfully",HttpStatus.CREATED);
        }catch(UserAlreadyExistsException e){
            return new ResponseEntity<>("User already exists",HttpStatus.CONFLICT);
        }
    }

    @PostMapping("/user/saveProduct")
    public ResponseEntity<?> saveProductToList(@RequestBody Product product, HttpServletRequest request) {
        // add a product to a specific user,
        // return 201 status if track is saved else 500 status
        try {
            System.out.println("header" + request.getHeader("Authorization"));
            Claims claims = (Claims) request.getAttribute("claims");
            System.out.println("userid for claim ::" + claims.getSubject());
            String userId = claims.getSubject();
            System.out.println("userId ::" + userId);

            // Call the service method to save the product for the user
            User savedUser = iUserProductService.saveUserProductToList(product, userId);

            // Return a response entity with the saved user and HTTP status 201
            return new ResponseEntity<>(savedUser, HttpStatus.CREATED);
        } catch (UserNotFoundException e) {
            // Handle the case where the user is not found
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            // Handle other exceptions
            return new ResponseEntity<>("Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/user/getAllProducts")
    public ResponseEntity<?> getAllProductsFromList(HttpServletRequest request)  {
        // list all products of a specific user,
        // return 200 status if track is saved else 500 status
        try {
            System.out.println("header" + request.getHeader("Authorization"));
            Claims claims = (Claims) request.getAttribute("claims");
            System.out.println("useid from claims :: " + claims.getSubject());
            String userId = claims.getSubject();
            System.out.println("userid :: " + userId);
            responseEntity = new ResponseEntity<>(iUserProductService.getAllUserProductsFromList(userId),HttpStatus.OK);


        }catch (Exception e) {
            // Handle other exceptions
            return new ResponseEntity<>("Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return responseEntity;
    }

    @DeleteMapping("/user/{productCode}")
    public ResponseEntity<?> deleterProductFromList(@PathVariable String productCode,HttpServletRequest request)  {
        // delete product of a specific user,
        // return 200 status if track is saved else 500 status
        System.out.println("header" + request.getHeader("Authorization"));
        Claims claims = (Claims) request.getAttribute("claims");
        System.out.println("useid from claims :: " + claims.getSubject());
        String userId = claims.getSubject();
        System.out.println("userid :: " + userId);
        try{
            responseEntity = new ResponseEntity<>(iUserProductService.deleteUserProductFromList(userId,productCode),HttpStatus.OK);
        }catch (Exception e){
            return new ResponseEntity<>("Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return responseEntity;
    }


    // Read the user id present in the claims from the request
    private String getIdFromClaims(HttpServletRequest request){
       return null;
    }
}
