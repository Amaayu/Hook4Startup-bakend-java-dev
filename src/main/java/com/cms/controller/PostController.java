package com.cms.controller;

import com.cms.Dto.PostDto;
import com.cms.models.Post;
import com.cms.models.User;
import com.cms.models.UserProfile;
import com.cms.repository.CustomerRepo;
import com.cms.repository.PostRepo;
import com.cms.repository.UserProfileRepo;
import com.cms.services.PostServices;
import lombok.extern.slf4j.Slf4j;
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

@Slf4j
@RestController
@CrossOrigin("*")
@RequestMapping("/post")
public class PostController {
    @Autowired
    private UserProfileRepo userProfileRepo;

    @Autowired
    private PostServices postServices;

    @Autowired
    private CustomerRepo customerRepo;

    @Autowired
    private PostRepo postRepo;

    // Create post by user
    @PostMapping("/create")
    public ResponseEntity<?> createPost(@RequestBody PostDto postDto) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()) {
                return new ResponseEntity<>("User is not authenticated", HttpStatus.UNAUTHORIZED);
            }

            String nameofUser = authentication.getName();

            // Optional.ofNullable hataya, seedha store kiya
            Optional<User> userOptional = customerRepo.findByUsername(nameofUser);
            if (userOptional.isEmpty()) {
                return new ResponseEntity<>("User not found", HttpStatus.BAD_REQUEST);
            }
            User user = userOptional.get();

            if (!user.isMakeProfileStatus()) {
                return new ResponseEntity<>("UserProfile not found, please create it", HttpStatus.BAD_REQUEST);
            }

            // Spring se inject newPost hata diya, manually object create kiya
            Post newPost = new Post();
            newPost.setPostId(new ObjectId().toString());
            newPost.setUserId(user);
            newPost.setContent(postDto.getContent());
            postServices.PostSave(newPost);

            user.getPosts().add(newPost);
            customerRepo.save(user);

            UserProfile userProfileByUserId = userProfileRepo.findUserProfileByUserId(user.getId());
            userProfileByUserId.setNumberOfPosts(postRepo.findByUserId(user.getId()).size());
            userProfileRepo.save(userProfileByUserId);

            return new ResponseEntity<>("Created Done", HttpStatus.CREATED);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("Error: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    // Update post by user
    @PutMapping("/update")
    public ResponseEntity<?> PostUpdate(@RequestBody PostDto postDto) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()) {
                return new ResponseEntity<>("User is not authenticated", HttpStatus.UNAUTHORIZED);
            }

            String nameofUser = authentication.getName();
            Optional<User> userOptional = customerRepo.findByUsername(nameofUser);
            if (userOptional.isEmpty()) {
                return new ResponseEntity<>("User not found", HttpStatus.BAD_REQUEST);
            }
            User user = userOptional.get();

            if (!user.isMakeProfileStatus()) {
                return new ResponseEntity<>("UserProfile not found, please create it", HttpStatus.BAD_REQUEST);
            }

            Optional<Post> byId = postRepo.findById(postDto.getPostId());
            if (byId.isEmpty()) {
                return new ResponseEntity<>("Post not found", HttpStatus.BAD_REQUEST);
            }
            Post post = byId.get();
            post.setContent(postDto.getContent());
            postServices.postUpdate(post);

            return new ResponseEntity<>("Update successfully", HttpStatus.OK);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // Get all posts
    @GetMapping("/all")
    public ResponseEntity<List<PostDto>> getAllPosts() {
        try {
            List<Post> postList = postRepo.findAll();
            List<PostDto> postDTOList = postList.stream().map(post ->
                    new PostDto(
                            post.getUserId().getUsername(),
                            post.getPostId(),
                            post.getContent(),
                            post.getComments().size(),
                            post.getLikes().size(),
                            post.getUserId().getPosts().size(),
                            post.getUserId().isMakeProfileStatus()
                    )
            ).collect(Collectors.toList());
            return new ResponseEntity<>(postDTOList, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
