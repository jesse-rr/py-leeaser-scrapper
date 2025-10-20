package com.example.leeaserscrapperv2.util;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class ImageUploader {

    private final Cloudinary cloudinary;

    public String uploadFromURL(String imageUrl) {
        try {
            Map uploadResult = cloudinary.uploader().upload(imageUrl, ObjectUtils.emptyMap());
            return (String) uploadResult.get("secure_url");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
