package com.example.jsocial.controller.photo;

import com.example.jsocial.model.photo.Photo;
import com.example.jsocial.model.photo.PhotoComment;
import com.example.jsocial.model.user.User;
import com.example.jsocial.security.access.AccessControlService;
import com.example.jsocial.service.photo.PhotoCommentService;
import com.example.jsocial.service.photo.PhotoService;
import com.example.jsocial.service.UserService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@Controller
@RequestMapping("/photos/{photoId}/comments")
public class PhotoCommentController {

    private final PhotoService photoService;
    private final PhotoCommentService photoCommentService;
    private final UserService userService;
    private final AccessControlService accessControlService;

    public PhotoCommentController(PhotoService photoService,
                                  PhotoCommentService photoCommentService,
                                  UserService userService,
                                  AccessControlService accessControlService) {
        this.photoService = photoService;
        this.photoCommentService = photoCommentService;
        this.userService = userService;
        this.accessControlService = accessControlService;
    }

    @PostMapping
    public String addComment(@PathVariable Long photoId,
                             @ModelAttribute("photoComment") @Valid PhotoComment photoComment,
                             BindingResult result,
                             Principal principal,
                             Model model) {
        Photo photo = photoService.findById(photoId);

        if (result.hasErrors()) {
            model.addAttribute("photo", photo);
            model.addAttribute("comments", photoCommentService.getCommentsForPhoto(photoId));
            return "photo/show_photo";
        }

        User user = userService.findByUsername(principal.getName());
        photoComment.setUser(user);
        photoComment.setPhoto(photo);
        photoCommentService.save(photoComment);

        return "redirect:/photos/" + photoId;
    }

    @PostMapping("/{commentId}/delete")
    public String deleteComment(@PathVariable Long photoId,
                                @PathVariable Long commentId,
                                Principal principal) {
        PhotoComment photoComment = photoCommentService.findById(commentId);
        User currentUser = userService.findByUsername(principal.getName());

        if (!accessControlService.canEditPhotoComment(photoComment, currentUser)) {
            return "redirect:/access-denied";
        }

        photoCommentService.delete(photoComment);
        return "redirect:/photos/" + photoId;
    }
}
