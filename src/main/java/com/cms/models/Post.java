package com.cms.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@AllArgsConstructor
@NoArgsConstructor
@Component
@Document(collection = "posts")
@Data

public class Post {

    @Id
    private String postId;

    @DBRef
    @JsonIgnoreProperties({"posts"}) // ❌ User ke andar Posts na aye
    private User userId;


    @CreatedDate
    private Date creationDate;

    @LastModifiedDate
    private Date lastModifiedDate;

    /*  private String caption;*/

    private String content;
    @DBRef
    private List<User> likes = new ArrayList<>();
    @DBRef
    private List<Comment> comments = new ArrayList<>();

    private String imageUrl;

    @Override
    public String toString() {
        return "UserPost{" + "id='" + postId + '\'' + ", userId='" + userId + '\'' + ", content='" + content + '\'' + ", creationDate=" + creationDate + ", likes=" + likes + ", comments=" + comments + ", imageUrl='" + imageUrl + '\'' + '}';

    }

}
