package com.cms.controller;

import com.cms.Dto.CommentDto;
import com.cms.models.Comment;
import com.cms.models.Post;
import com.cms.models.User;
import com.cms.repository.CommentRepo;
import com.cms.repository.CustomerRepo;
import com.cms.repository.PostRepo;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@CrossOrigin("*")
@RequestMapping("/comment")
public class CommentController {
    @Autowired
    CustomerRepo customerRepo;
    @Autowired
    PostRepo postRepo;
    @Autowired
    Comment newComment;
    @Autowired
    CommentRepo commentRepo;

    @Transactional
    @PostMapping("/create")
    // create comment for any post
    public ResponseEntity<?> commentPost(@RequestBody CommentDto comment) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()) {
                return new ResponseEntity<>("User is not authenticated", HttpStatus.UNAUTHORIZED);
            }
            String nameofUser = authentication.getName();
            Optional<?> userOptional = Optional.ofNullable(customerRepo.findByUsername(nameofUser));
            if (!userOptional.isPresent()) {
                return new ResponseEntity<>("User not found", HttpStatus.BAD_REQUEST);
            }
            User user = (User) userOptional.get();

            if (!user.isMakeProfileStatus()){
                return new ResponseEntity<>("UserProfile not found plz make", HttpStatus.BAD_REQUEST);
            }

            Optional<Post> byId = postRepo.findById(comment.getPostId());
            if (!byId.isPresent()) {
                return new ResponseEntity<>("Post not found", HttpStatus.BAD_REQUEST);
            }
            newComment.setCommentId(new ObjectId().toString());
            newComment.setPostId(byId.get());
            newComment.setContent(comment.getContent());
            newComment.setUserId(user);
            commentRepo.save(newComment);

            Post post = byId.get();
            post.getComments().add(newComment);
            postRepo.save(post);

            return new ResponseEntity<>("create comment  Done ", HttpStatus.OK);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }


    }

}
