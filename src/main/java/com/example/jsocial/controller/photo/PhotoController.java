package com.example.jsocial.controller.photo;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.example.jsocial.model.photo.Photo;
import com.example.jsocial.model.photo.PhotoComment;
import com.example.jsocial.model.user.User;
import com.example.jsocial.security.access.AccessControlService;
import com.example.jsocial.service.CloudinaryService;
import com.example.jsocial.service.photo.PhotoCommentService;
import com.example.jsocial.service.photo.PhotoService;
import com.example.jsocial.service.UserService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;
import java.util.Map;

@Controller
@RequestMapping("/photos")
public class PhotoController {

    private final PhotoService photoService;
    private final UserService userService;
    private final AccessControlService accessControlService;

    private final PhotoCommentService photoCommentService;

    private final CloudinaryService cloudinaryService;

    public PhotoController(PhotoService photoService,
                           UserService userService,
                           AccessControlService accessControlService,
                           PhotoCommentService photoCommentService,
                           CloudinaryService cloudinaryService) {
        this.photoService = photoService;
        this.userService = userService;
        this.accessControlService = accessControlService;
        this.photoCommentService = photoCommentService;
        this.cloudinaryService = cloudinaryService;
    }

    @GetMapping("/new")
    public String newPhotoForm(Model model) {
        model.addAttribute("photoForm", new Photo());
        return "photo/new_photo";
    }

    @PostMapping("/new")
    public String uploadPhoto(@ModelAttribute("photoForm") @Valid Photo form,
                              BindingResult result,
                              @RequestParam("file") MultipartFile file,
                              Principal principal,
                              Model model) throws IOException {

        if (result.hasErrors() || file.isEmpty()) {
            model.addAttribute("uploadError", "Оберіть файл");
            return "photo/new_photo";
        }

        String photoUrl = cloudinaryService.uploadImage(file, "photos", principal.getName());

        User user = userService.findByUsername(principal.getName());

        Photo photo = new Photo();
        photo.setUser(user);
        photo.setUrl(photoUrl);
        photo.setDescription(form.getDescription());

        photoService.save(photo);

        return "redirect:/users/" + user.getUsername() + "?tab=photos";
    }


    @GetMapping("/{id}")
    public String showPhoto(@PathVariable Long id, Model model) {
        model.addAttribute("photo", photoService.findDTOById(id));
        model.addAttribute("photoComments", photoCommentService.getCommentDTOs(id));
        model.addAttribute("photoComment", new PhotoComment());
        return "photo/show_photo";
    }

    @GetMapping("/{id}/edit")
    public String editPhotoForm(@PathVariable Long id, Principal principal, Model model) {
        Photo photo = photoService.findById(id);
        if (!accessControlService.canEditPhoto(photo, principal.getName())) {
            return "redirect:/access-denied";
        }
        model.addAttribute("photoForm", photo);
        return "photo/edit_photo";
    }

    @PostMapping("/{id}/edit")
    public String updatePhoto(@PathVariable Long id,
                              @ModelAttribute("photoForm") @Valid Photo form,
                              BindingResult result,
                              Principal principal) {
        Photo photo = photoService.findById(id);
        if (!accessControlService.canEditPhoto(photo, principal.getName())) {
            return "redirect:/access-denied";
        }

        if (result.hasErrors()) {
            return "photo/edit_photo";
        }

        photo.setDescription(form.getDescription());
        photoService.save(photo);

        return "redirect:/photos/" + id;
    }
}
