package com.example.jsocial.controller;

import com.example.jsocial.dto.*;
import com.example.jsocial.model.user.User;
import com.example.jsocial.security.access.AccessControlService;
import com.example.jsocial.service.CloudinaryService;
import com.example.jsocial.service.UserService;
import com.example.jsocial.service.photo.PhotoService;
import com.example.jsocial.service.post.PostService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Controller
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final AccessControlService accessControlService;
    private final PhotoService photoService;
    private final PostService postService;

    private final CloudinaryService cloudinaryService;

    public UserController(UserService userService,
                          AccessControlService accessControlService,
                          PhotoService photoService,
                          PostService postService,
                          CloudinaryService cloudinaryService) {
        this.userService = userService;
        this.accessControlService = accessControlService;
        this.photoService = photoService;
        this.postService = postService;
        this.cloudinaryService = cloudinaryService;
    }

    @GetMapping("/{username}")
    public String userProfile(@PathVariable String username,
                              @RequestParam(defaultValue = "photos") String tab,
                              @RequestParam(defaultValue = "0") int page,
                              Model model,
                              @AuthenticationPrincipal User currentUser,
                              @RequestHeader(value = "X-Requested-With", required = false) String requestedWith) {

        UserProfileDTO user = userService.getProfile(username, currentUser);
        model.addAttribute("user", user);

        Pageable pageable = PageRequest.of(page, 9);

        if ("photos".equals(tab)) {
            Page<PhotoCardDTO> photos = photoService.getUserPhotoCards(username, pageable);
            model.addAttribute("photos", photos.getContent());
        } else {
            Page<PostCardDTO> posts = postService.getUserPostCards(username, pageable);
            model.addAttribute("posts", posts.getContent());
        }

        model.addAttribute("activeTab", tab);

        boolean isAjax = "XMLHttpRequest".equals(requestedWith);
        return isAjax
                ? ("photos".equals(tab)
                ? "fragments/photos :: photosFragment"
                : "fragments/posts  :: postsFragment")
                : "user/profile";
    }



    @GetMapping("/{username}/edit")
    public String editProfile(@PathVariable String username, Model model,
                              @AuthenticationPrincipal User currentUser) {
        User user = userService.findByUsername(username);
        if (!accessControlService.canEditUser(user, currentUser)) {
            return "redirect:/access-denied";
        }

        model.addAttribute("user", user);
        return "user/edit_user";
    }

    @PostMapping("/{username}/edit")
    public String updateProfile(@PathVariable String username,
                                @RequestParam("username") String newUsername,
                                @RequestParam("avatar") MultipartFile file,
                                @RequestParam(value = "deleteAvatar", required = false) boolean deleteAvatar,
                                @AuthenticationPrincipal User currentUser,
                                Model model) throws IOException {

        User user = userService.findByUsername(username);
        if (!accessControlService.canEditUser(user, currentUser)) {
            return "redirect:/access-denied";
        }

        if (!username.equals(newUsername) && userService.usernameExists(newUsername)) {
            model.addAttribute("user", user);
            model.addAttribute("errorMessage", "Користувач з таким іменем вже існує!");
            return "user/edit_user";
        }

        user.setUsername(newUsername);

        if (deleteAvatar && user.getAvatar() != null) {
            cloudinaryService.deleteImage(user.getAvatar());
            user.setAvatar(null);
        } else if (!file.isEmpty()) {
            if (user.getAvatar() != null) {
                cloudinaryService.deleteImage(user.getAvatar());
            }

            String secureUrl = cloudinaryService.uploadImage(file, "avatars", newUsername);
            user.setAvatar(secureUrl);
        }

        userService.save(user);

        if (username.equals(currentUser.getUsername())) {
            UsernamePasswordAuthenticationToken newAuth = new UsernamePasswordAuthenticationToken(
                    user,
                    currentUser.getPassword(),
                    currentUser.getAuthorities()
            );
            SecurityContextHolder.getContext().setAuthentication(newAuth);
        }

        return "redirect:/users/" + newUsername;
    }


    @PostMapping("/{username}/follow")
    public String followUser(@PathVariable String username, @AuthenticationPrincipal User currentUser) {
        User target = userService.findByUsername(username);
        if (!currentUser.equals(target)) {
            userService.follow(currentUser, target);
        }
        return "redirect:/users/" + username;
    }

    @PostMapping("/{username}/unfollow")
    public String unfollowUser(@PathVariable String username, @AuthenticationPrincipal User currentUser) {
        User target = userService.findByUsername(username);
        userService.unfollow(currentUser, target);
        return "redirect:/users/" + username;
    }

    @GetMapping("/{username}/followers")
    public String showFollowers(
            @PathVariable String username,
            @RequestParam(defaultValue = "followers") String tab,
            @RequestParam(defaultValue = "") String query,
            @RequestParam(defaultValue = "0") int page,
            Model model,
            @RequestHeader(value = "X-Requested-With", required = false) String requestedWith) {

        UserProfileDTO userProfile = userService.getProfile(username, null);
        model.addAttribute("user", userProfile);
        model.addAttribute("activeTab", tab);
        model.addAttribute("search", query);

        Pageable pageable = PageRequest.of(page, 20);

        Page<UserSimpleDTO> pageResult = "followers".equals(tab)
                ? userService.findFollowersByUsername(username, query, pageable)
                : userService.findFollowingByUsername(username, query, pageable);

        model.addAttribute("userItems", pageResult.getContent());
        model.addAttribute("page", pageResult);

        boolean isAjax = "XMLHttpRequest".equals(requestedWith);

        return isAjax ? "fragments/userList :: userListFragment" : "user/follow_list";
    }
}
