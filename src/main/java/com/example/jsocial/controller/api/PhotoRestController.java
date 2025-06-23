package com.example.jsocial.controller.api;

import com.example.jsocial.model.photo.Photo;
import com.example.jsocial.model.user.User;
import com.example.jsocial.security.access.AccessControlService;
import com.example.jsocial.service.photo.PhotoService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.Principal;

@RestController
@RequestMapping("/api/photos")
public class PhotoRestController {

    private final PhotoService photoService;
    private final AccessControlService accessControlService;

    public PhotoRestController(PhotoService photoService,
                               AccessControlService accessControlService) {
        this.photoService = photoService;
        this.accessControlService = accessControlService;
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id,
                                       @AuthenticationPrincipal UserDetails user)
            throws IOException {

        Photo photo = photoService.findById(id);
        String username = user.getUsername();

        if (!accessControlService.canEditPhoto(photo, username)) {
            return ResponseEntity.status(403).build();
        }

        Path path = Path.of(System.getProperty("user.dir") + photo.getUrl());
        Files.deleteIfExists(path);

        photoService.delete(photo);
        return ResponseEntity.noContent().build();
    }
}
