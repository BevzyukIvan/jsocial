package com.example.jsocial.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

@Service
public class CloudinaryService {
    private final Cloudinary cloudinary;

    public CloudinaryService(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }


    public String uploadImage(MultipartFile file,
                              String folder,
                              String publicIdPrefix) throws IOException {

        Map uploadResult = cloudinary.uploader().upload(
                file.getBytes(),
                ObjectUtils.asMap(
                        "folder",    folder,
                        "public_id", publicIdPrefix + "_" + UUID.randomUUID()
                )
        );
        return (String) uploadResult.get("secure_url");
    }

    public void deleteImage(String imageUrl) throws IOException {
        String publicId = extractPublicId(imageUrl);
        cloudinary.uploader().destroy(
                publicId,
                ObjectUtils.asMap("invalidate", true)
        );
    }

    private String extractPublicId(String url) {
        String path = url.substring(url.indexOf("/upload/") + 8);

        while (path.contains("/") && path.substring(0, path.indexOf('/')).contains(",")) {
            path = path.substring(path.indexOf('/') + 1);
        }

        if (path.startsWith("v") && path.indexOf('/') > 0) {
            path = path.substring(path.indexOf('/') + 1);
        }

        return path.replaceAll("\\.[a-zA-Z0-9]+$", "");
    }

}
