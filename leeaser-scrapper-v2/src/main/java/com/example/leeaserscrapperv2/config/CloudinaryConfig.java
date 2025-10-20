package com.example.leeaserscrapperv2.config;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CloudinaryConfig {

    @Bean
    public Cloudinary cloudinary() {
        return new Cloudinary(ObjectUtils.asMap(
                "cloud_name", "dgvmzhujn",
                "api_key", "732297268557195",
                "api_secret", "mLsVMT4MyRCGyzr2FzKaLrMpY6o"
        ));
    }
}
