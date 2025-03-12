package com.cms.repository;

import com.cms.models.Post;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface PostRepo extends MongoRepository<Post, String> {
    // Additional custom queries can be defined here if needed
    List<Post> findByUserId(String userId);


}
