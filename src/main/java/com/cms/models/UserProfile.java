package com.cms.models;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Objects;

@AllArgsConstructor
@NoArgsConstructor
@Component
@Document(collection = "userProfiles")
@Data
public class UserProfile {
    @Id
    private String id;
   @DBRef
    private User userId;

    @CreatedDate
    private Date creationDate;

    @LastModifiedDate
    private Date lastModifiedDate;
    @NonNull
    private String username;
    @NonNull
    private String fullName;

    private String bio;

    private String profilePictureUrl;

    private int numberOfPosts;

    private String numberOfFollowers;

    private String numberOfFollowing;

 @Override
 public boolean equals(Object o) {
  if (this == o) return true;
  if (o == null || getClass() != o.getClass()) return false;
  UserProfile that = (UserProfile) o;
  return Objects.equals(id, that.id); // Use unique identifier
 }

 @Override
 public int hashCode() {
  return Objects.hash(id); // Use unique identifier
 }


 @Override
    public String toString() {
        return "InstagramUserProfile{" +
                "username='" + username + '\'' +
                ", fullName='" + fullName + '\'' +
                ", bio='" + bio + '\'' +
                ", profilePictureUrl='" + profilePictureUrl + '\'' +
                ", numberOfPosts=" + numberOfPosts +
                ", numberOfFollowers=" + numberOfFollowers +
                ", numberOfFollowing=" + numberOfFollowing +
                '}';
    }
}
