package com.example.jsocial.controller;

import com.example.jsocial.dto.UserSimpleDTO;
import com.example.jsocial.service.UserService;
import com.example.jsocial.model.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class SearchController {

    private final UserService userService;

    public SearchController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/search")
    public String searchPage(
            @RequestParam(value = "q", required = false) String query,
            @RequestParam(defaultValue = "0") int page,
            Model model,
            @RequestHeader(value = "X-Requested-With", required = false) String requestedWith) {

        Pageable pageable = PageRequest.of(page, 20);
        Page<UserSimpleDTO> resultsPage =
                userService.searchUserSimpleDTOs(query, pageable);

        model.addAttribute("results", resultsPage.getContent());
        model.addAttribute("page", resultsPage);
        model.addAttribute("query", query);

        boolean isAjax = "XMLHttpRequest".equals(requestedWith);
        return isAjax ? "fragments/userCards :: userCards" : "search";
    }

}
