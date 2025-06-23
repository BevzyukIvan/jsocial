package com.example.jsocial.controller.api;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.example.jsocial.model.photo.Photo;
import com.example.jsocial.security.access.AccessControlService;
import com.example.jsocial.service.CloudinaryService;
import com.example.jsocial.service.photo.PhotoService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api/photos")
public class PhotoRestController {

    private final PhotoService photoService;
    private final AccessControlService accessControlService;

    private final CloudinaryService cloudinaryService;


    public PhotoRestController(PhotoService photoService,
                               AccessControlService accessControlService,
                               CloudinaryService cloudinaryService) {
        this.photoService = photoService;
        this.accessControlService = accessControlService;
        this.cloudinaryService = cloudinaryService;
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id,
                                       @AuthenticationPrincipal UserDetails user) throws IOException {

        Photo photo = photoService.findById(id);
        String username = user.getUsername();

        if (!accessControlService.canEditPhoto(photo, username)) {
            return ResponseEntity.status(403).build();
        }

        cloudinaryService.deleteImage(photo.getUrl());
        photoService.delete(photo);

        return ResponseEntity.noContent().build();
    }
}
