package com.cms.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;



@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostDto {

    private String userId;

    private String postId;

    private String content;

    private int NumberOfComment;

    private int NumberOfLike;

    private int NumberOfPost;

    private  boolean profileStatus;



}
