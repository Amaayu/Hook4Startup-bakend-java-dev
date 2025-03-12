package com.cms.services;

import com.cms.models.Post;
import com.cms.repository.PostRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PostServices {

    @Autowired
    private PostRepo postRepo;

    // Save userPost by created user
    @Transactional
    public void PostSave(Post userPost) {
        postRepo.save(userPost);

    }

    public  boolean postUpdate( Post userUpdatePost){
        postRepo.save(userUpdatePost);
        return true;
    }

    public List<Post> findAllPosts() {

        return postRepo.findAll();
    }
}

