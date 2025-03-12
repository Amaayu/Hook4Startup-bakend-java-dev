package com.cms.controller;

import com.cms.Dto.UserDto;
import com.cms.models.User;
import com.cms.models.UserProfile;
import com.cms.repository.CustomerRepo;
import com.cms.repository.PostRepo;
import com.cms.repository.UserProfileRepo;
import com.cms.services.PostServices;
import com.cms.services.UserProfileServices;
import com.cms.services.UserServices;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserServices userServices;

    @Autowired
    private CustomerRepo customerRepo;

    @Autowired
    private UserProfileRepo userProfileRepo;

    @Autowired
    private PostRepo postRepo;

    @Autowired
    private UserProfileServices userProfileServices;

    @Autowired
    private PostServices postServices;

    // Update user
    @PutMapping("/update")
    public ResponseEntity<?> updateUser(@RequestBody UserDto userDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        String username = authentication.getName();

        Optional<User> optionalUser = userServices.findCustomerByUserName(username); // ✅ Optional<User>
        if (!optionalUser.isPresent()) { // ✅ Check if user exists
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        try {
            User user1 = optionalUser.get(); // ✅ Extract User object
            user1.setUsername(userDto.getUsername());
            user1.setPassword(userDto.getPassword());
            userServices.customerSave(user1);

            UserProfile userProfileByUserId = userProfileRepo.findUserProfileByUserId(user1.getId());
            if (userProfileByUserId != null) {
                userProfileByUserId.setUsername(userDto.getUsername());
                userProfileRepo.save(userProfileByUserId);
            }

            return new ResponseEntity<>(true, HttpStatus.ACCEPTED);
        } catch (Exception e) {
            return new ResponseEntity<>("Error: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }


    @GetMapping("/users")
    public ResponseEntity<List<UserDto>> getAllUsers() {
        try {
            List<User> users = userServices.findAllCustomer();
            List<UserDto> userDtoList = users.stream().map(user ->
                    new UserDto(user.getId(), user.getUsername(), user.getPassword(), user.getEmail())
            ).collect(Collectors.toList());

            return new ResponseEntity<>(userDtoList, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(List.of(), HttpStatus.NOT_FOUND); // ✅ Empty List return करें
        }
    }



    // Create user profile
    @PostMapping("/profile/create")
    public ResponseEntity<?> createUserProfile(@RequestBody UserProfile userProfile) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()) {
                return new ResponseEntity<>("User is not authenticated", HttpStatus.UNAUTHORIZED);
            }
            String nameofUser = authentication.getName();

            Optional<User> userOptional = customerRepo.findByUsername(nameofUser);
            if (!userOptional.isPresent()) {
                return new ResponseEntity<>("User not found", HttpStatus.BAD_REQUEST);
            }
            User user = userOptional.get();

            if (user.isMakeProfileStatus()) {
                return new ResponseEntity<>("User already has a profile", HttpStatus.BAD_REQUEST);
            }

            // Ensure userProfile has a unique identifier
            userProfile.setId(new ObjectId().toString());

            // Set user properties
            user.setUsername(userProfile.getUsername());
            user.setMakeProfileStatus(true);
            customerRepo.save(user); // Save the user first

            // Set userProfile properties
            userProfile.setUserId(user); // Fixed
            userProfile.setFullName(userProfile.getFullName());
            userProfile.setBio(userProfile.getBio());
            userProfile.setProfilePictureUrl(userProfile.getProfilePictureUrl());
            userProfile.setNumberOfPosts(postRepo.findByUserId(user.getId()).size());
            userProfile.setNumberOfFollowers("45M");
            userProfile.setNumberOfFollowing("20");

            // Save the userProfile
            userProfileServices.ProfileSave(userProfile);

            // Update user with userProfile ID and save again
            user.setUserProfileId(userProfile); // Fixed
            customerRepo.save(user);

            return new ResponseEntity<>("User profile created", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Error: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Update user profile
    @PutMapping("/profile/update")
    public ResponseEntity<?> updateUserProfile(@RequestBody UserProfile userProfile) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()) {
                return new ResponseEntity<>("User is not authenticated", HttpStatus.UNAUTHORIZED);
            }
            String nameofUser = authentication.getName();

            Optional<User> userOptional = customerRepo.findByUsername(nameofUser);
            if (!userOptional.isPresent()) {
                return new ResponseEntity<>("User not found", HttpStatus.BAD_REQUEST);
            }
            User user = userOptional.get();

            UserProfile userProfileByUserId = userProfileRepo.findUserProfileByUserId(user.getId());
            if (userProfileByUserId == null) {
                return new ResponseEntity<>("User profile not found", HttpStatus.BAD_REQUEST);
            }

            userProfileByUserId.setUsername(userProfile.getUsername());
            user.setUsername(userProfile.getUsername());
            customerRepo.save(user);

            userProfileByUserId.setFullName(userProfile.getFullName());
            userProfileByUserId.setBio(userProfile.getBio());
            userProfileByUserId.setProfilePictureUrl(userProfile.getProfilePictureUrl());

            userProfileServices.userProfileUpdate(userProfileByUserId);
            return new ResponseEntity<>("User profile updated", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Error: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
