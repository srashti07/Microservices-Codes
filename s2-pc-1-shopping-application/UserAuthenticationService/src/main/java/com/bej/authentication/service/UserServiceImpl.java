package com.bej.authentication.service;

import com.bej.authentication.domain.User;
import com.bej.authentication.exception.UserAlreadyExistsException;
import com.bej.authentication.exception.InvalidCredentialsException;
import com.bej.authentication.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements IUserService {

private UserRepository userRepository;
    // Autowire the UserRepository using constructor autowiring
    @Autowired
    public UserServiceImpl(UserRepository userRepository){
        this.userRepository = userRepository;
    }

    @Override
    public User saveUser(User user) throws UserAlreadyExistsException {
       if(userRepository.findById(user.getUserId()).isPresent())
       {
           throw new UserAlreadyExistsException();
       }
       System.out.println(user);

        return userRepository.save(user);
    }

    @Override
    public User getUserByUserIdAndPassword(String userId, String password) throws InvalidCredentialsException {
        System.out.println("userId"+userId);
        System.out.println("password"+password);
        User loggedInUser = userRepository.findByUserIdAndPassword(userId,password);
        System.out.println(loggedInUser);
        if(loggedInUser == null){
            throw new InvalidCredentialsException();
        }
        return loggedInUser;
    }

}
