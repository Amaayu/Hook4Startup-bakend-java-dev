package com.cms.controller;

import com.cms.services.CloudinaryImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@CrossOrigin("*")
@RequestMapping("/cloudinary/upload")
public class CloudinaryImageUploadController {
    @Autowired
    private CloudinaryImageService cloudinaryImageService;
    @PostMapping
    public ResponseEntity<Map> uploadImage(@RequestParam("image")MultipartFile file){
    Map data = this.cloudinaryImageService.uplode(file);
    return new ResponseEntity<>(data, HttpStatus.OK);
    }

}
