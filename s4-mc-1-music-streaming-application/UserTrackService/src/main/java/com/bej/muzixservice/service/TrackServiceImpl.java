package com.bej.muzixservice.service;

import com.bej.muzixservice.domain.Track;
import com.bej.muzixservice.domain.User;
import com.bej.muzixservice.exception.TrackAlreadyExistsException;
import com.bej.muzixservice.exception.TrackNotFoundException;
import com.bej.muzixservice.exception.UserAlreadyExistsException;
import com.bej.muzixservice.exception.UserNotFoundException;

import com.bej.muzixservice.repository.UserTrackRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class TrackServiceImpl implements ITrackService{
    private  UserTrackRepository userTrackRepository;

    // Autowire the UserTrackRepository using constructor autowiring
    @Autowired
    public TrackServiceImpl(UserTrackRepository userTrackRepository){
        this.userTrackRepository = userTrackRepository;
    }

    @Override
    public User registerUser(User user) throws UserAlreadyExistsException {
        Optional<User> existingUser = userTrackRepository.findById(user.getUserId());
        if (existingUser.isPresent()) {
            throw new UserAlreadyExistsException();
        }
        return userTrackRepository.save(user);
    }



    @Override
    public User saveUserTrackToWishList(Track track, String userId) throws TrackAlreadyExistsException, UserNotFoundException {
        // Save the tracks to the play list of user.
        //checking if user exists
//        User user = userTrackRepository.findById(userId)
//                .orElseThrow(() -> new UserNotFoundException());
//        //checking if the track already exists in the user's wish list
//        if(user.getTrackList().contains(track)){
//            throw new TrackAlreadyExistsException();
//        }
//        //Adding the track to the user's wishlist and save the user
//        user.getTrackList().add(track);
//        return userTrackRepository.save(user);
        User user = userTrackRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException());

        // Initialize the trackList if it is null
        if (user.getTrackList() == null) {
            user.setTrackList(new ArrayList<>());
        }

        // Checking if the track already exists in the user's wish list
        if (user.getTrackList().contains(track)) {
            throw new TrackAlreadyExistsException();
        }

        // Adding the track to the user's wishlist and save the user
        user.getTrackList().add(track);
        return userTrackRepository.save(user);

    }

    @Override
    public List<Track> getAllUserTracksFromWishList(String userId) throws Exception {
        // Get all the tracks for a specific user
        User user = userTrackRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException());
//        return user.getTrackList();

        User anotherUser = userTrackRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException());

        return user.getTrackList();

    }

    @Override
    public User deleteTrack(String userId, String trackId) throws TrackNotFoundException, UserNotFoundException {
        // delete the user details specified
        //checking if the user exists
        User user = userTrackRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException());

        //checking if the track exists in the user's wish list
        Track trackToRemove = user.getTrackList().stream()
                .filter(track -> track.getTrackId().equals(trackId))
                .findFirst()
                .orElseThrow(()-> new TrackNotFoundException());
        //Remove the track from the wishlist
        user.getTrackList().remove(trackToRemove);

        return userTrackRepository.save(user);
    }


    @Override
    public User updateUserTrackWishListWithGivenTrack(String userId, Track track) throws UserNotFoundException, TrackNotFoundException, TrackAlreadyExistsException {
        // Update the specific details of User
        //checking if the user exists
        User user = userTrackRepository.findById(userId)
                .orElseThrow(()-> new UserNotFoundException());

        //checking if the track exists in the user's wish list
        Track existingTrack = user.getTrackList().stream()
                .filter(t -> t.getTrackId().equals(track.getTrackId()))
                .findFirst()
                .orElseThrow(()-> new TrackNotFoundException());

        //checking if the updated track already exists in the wishlist
        if(!existingTrack.equals(track) && user.getTrackList().contains(track)){
            throw new TrackAlreadyExistsException();
        }
        //updating the track details in wish list
        user.getTrackList().remove(existingTrack);
        user.getTrackList().add(track);
        return userTrackRepository.save(user);
    }
}
