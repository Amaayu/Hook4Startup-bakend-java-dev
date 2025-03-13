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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@AllArgsConstructor
@NoArgsConstructor
@Component
@Document(collection = "users")
@Data
public class User {
    @Id
    private String id;

    @NonNull
    private String username;

    @NonNull
    private String password; // Hashed password store karna best practice hai

    @NonNull
    private String email;

    @DBRef
    private UserProfile userProfileId;

    @CreatedDate
    private Date creationDate;

    @LastModifiedDate
    private Date lastModifiedDate;

    @DBRef
    private List<Post> posts = new ArrayList<>();

    private String roles = "User_Role";

    private boolean makeProfileStatus;

    public User(String username, String password , String email) {
        this.username = username;
        this.password = password;
        this.email = email;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "User{" + "id=" + id + ", username='" + username + '\'' + '}';
    }
}
