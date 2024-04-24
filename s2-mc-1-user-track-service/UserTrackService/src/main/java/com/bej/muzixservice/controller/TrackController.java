package com.bej.muzixservice.controller;

import com.bej.muzixservice.domain.Track;
import com.bej.muzixservice.domain.User;
import com.bej.muzixservice.exception.TrackAlreadyExistsException;
import com.bej.muzixservice.exception.TrackNotFoundException;
import com.bej.muzixservice.exception.UserAlreadyExistsException;
import com.bej.muzixservice.exception.UserNotFoundException;
import com.bej.muzixservice.service.ITrackService;
import com.bej.muzixservice.service.TrackServiceImpl;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("api/v2")
public class TrackController {
    private ITrackService iTrackService;
    private ResponseEntity<?> responseEntity;
    // Auto wire the service layer object
    @Autowired
    public TrackController(ITrackService iTrackService){
        this.iTrackService = iTrackService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerCustomer(@RequestBody User user) {
        try {
            User savedUser = iTrackService.registerUser(user);
            return new ResponseEntity<>("User with Id " + savedUser.getUserId() + " created successfully", HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>("User or User ID must not be null", HttpStatus.BAD_REQUEST);
        } catch (UserAlreadyExistsException e) {
            return new ResponseEntity<>("User already exists", HttpStatus.CONFLICT);
        }
    }





    //    @PostMapping("user/track")
//    public ResponseEntity<?> saveTrack(@RequestBody Track track, HttpServletRequest request) {
//       // add a track to a specific user, return 201 status if track is saved else 500 status
//        try{
//            System.out.println("header"+request.getHeader("Authorization"));
//            Claims claims = (Claims) request.getAttribute("claims");
//            System.out.println("userId from claims ::"+claims.getSubject());
//            String userId = claims.getSubject();
//            System.out.println("userId ::"+userId);
//            responseEntity = new ResponseEntity<>(iTrackService.saveUserTrackToWishList(track,userId),HttpStatus.CREATED);
//        }catch (TrackAlreadyExistsException e){
//            return new ResponseEntity<>("Track with the provided Id already exists in the wish list",HttpStatus.CONFLICT);
//        }catch (UserNotFoundException e){
//            return new ResponseEntity<>("User not found",HttpStatus.INTERNAL_SERVER_ERROR);
//        }
//        return responseEntity;
//    }
    @PostMapping("user/track")
    public ResponseEntity<?> saveTrack(@RequestBody Track track, HttpServletRequest request) {
        try {
            String authHeader = request.getHeader("Authorization");

            // Check if Authorization header is present
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return new ResponseEntity<>("Missing or invalid Authorization header", HttpStatus.UNAUTHORIZED);
            }

            String token = authHeader.substring(7);

            // Verify and extract claims from the token
            Claims claims = Jwts.parser().setSigningKey("mysecret").parseClaimsJws(token).getBody();

            // Check if claims is null
            if (claims == null) {
                return new ResponseEntity<>("Invalid token", HttpStatus.UNAUTHORIZED);
            }

            String userId = claims.getSubject();
            System.out.println("userId ::" + userId);

            return new ResponseEntity<>(iTrackService.saveUserTrackToWishList(track, userId), HttpStatus.CREATED);
        } catch (TrackAlreadyExistsException e) {
            return new ResponseEntity<>("Track with the provided Id already exists in the wish list", HttpStatus.CONFLICT);
        } catch (UserNotFoundException e) {
            return new ResponseEntity<>("User not found", HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (JwtException e) {
            return new ResponseEntity<>("Invalid token", HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            return new ResponseEntity<>("Error occurred while saving the track", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @GetMapping("user/tracks")
    public ResponseEntity<?> getAllTracks(HttpServletRequest request) {
        // display all the tracks of a specific user, extract user id from claims,
        // return 200 status if user is saved else 500 status
        try{
            System.out.println("header"+request.getHeader("Authorization"));
            Claims claims = (Claims) request.getAttribute("claims");
            System.out.println("userId from claims ::"+claims.getSubject());
            String userId = claims.getSubject();
            System.out.println("userId ::"+userId);
            responseEntity = new ResponseEntity<>(iTrackService.getAllUserTracksFromWishList(userId),HttpStatus.OK);
        }catch (Exception e){
            return new ResponseEntity<>("Track Not found",HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return responseEntity;
    }

    @DeleteMapping("user/track/{trackId}")
    public ResponseEntity<?> deleteTrack(@PathVariable String trackId,HttpServletRequest request) throws TrackNotFoundException {
        // delete a track based on user id and track id, extract user id from claims
        // return 200 status if user is saved else 500 status
        try {
            System.out.println("header" + request.getHeader("Authorization"));
            Claims claims = (Claims) request.getAttribute("claims");
            System.out.println("userId from claims ::" + claims.getSubject());
            String userId = claims.getSubject();
            System.out.println("userId ::" + userId);
            User DeletedUser = iTrackService.deleteTrack(userId, trackId);
            return new ResponseEntity<>(DeletedUser, HttpStatus.OK);
        }catch (TrackNotFoundException e){
            return new ResponseEntity<>("Track with ID " + trackId + " not found", HttpStatus.NOT_FOUND);
        }catch (UserNotFoundException e) {
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>("Error occurred while deleting the track", HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @PutMapping("user/track")
    public ResponseEntity<?> updateTrack(@RequestBody Track track, HttpServletRequest request){
        // update a track based on user id and track id, extract user id from claims
        // return 200 status if user is saved else 500 status
        try {
            System.out.println("header" + request.getHeader("Authorization"));
            Claims claims = (Claims) request.getAttribute("claims");
            System.out.println("userId from claims ::" + claims.getSubject());
            String userId = claims.getSubject();
            System.out.println("userId ::" + userId);
            User updatedUser = iTrackService.updateUserTrackWishListWithGivenTrack(userId,track);
            return new ResponseEntity<>(updatedUser,HttpStatus.OK);
        } catch (UserNotFoundException e) {
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        } catch (TrackNotFoundException e) {
            return new ResponseEntity<>("Track not found", HttpStatus.NOT_FOUND);
        } catch (TrackAlreadyExistsException e) {
            return new ResponseEntity<>("Track with the provided ID already exists in the wish list", HttpStatus.CONFLICT);
        } catch (Exception e) {
            return new ResponseEntity<>("Error occurred while updating the track", HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

}