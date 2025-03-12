package com.cms.repository;

import com.cms.models.UserProfile;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserProfileRepo extends MongoRepository<UserProfile, String> {
    UserProfile  findUserProfileByUserId(String userId);
}
