package com.bej.authentication.controller;

import com.bej.authentication.exception.UserAlreadyExistsException;
import com.bej.authentication.exception.InvalidCredentialsException;
import com.bej.authentication.security.SecurityTokenGenerator;
import com.bej.authentication.service.IUserService;
import com.bej.authentication.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class UserController {
    private IUserService iUserService;
    private SecurityTokenGenerator securityTokenGenerator;
    // Autowire the IUserService, SecurityTokenGenerator using constructor autowiring
    @Autowired
    public UserController(IUserService iUserService, SecurityTokenGenerator securityTokenGenerator){
        this.iUserService = iUserService;
        this.securityTokenGenerator = securityTokenGenerator;
    }


    @PostMapping("/save")
    public ResponseEntity<?> saveCustomer(@RequestBody User user){
        try{
            User savedUser = iUserService.saveUser(user);
            return new ResponseEntity<>("User with Id"+ savedUser.getUserId()+"created successfully",HttpStatus.CREATED);
        }catch(UserAlreadyExistsException e){
            return new ResponseEntity<>("User already exists",HttpStatus.CONFLICT);
        }

    }
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User user) {
        try{
            User retrievedUser = iUserService.getUserByUserIdAndPassword(user.getUserId(), user.getPassword());
            String token = securityTokenGenerator.createToken(user);
            return new ResponseEntity<>(token,HttpStatus.OK);
        }catch (InvalidCredentialsException e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Invalid credential");
        }


        // Generate the token on login,
        // return 200 status if user is saved else 500 status

    }
}
