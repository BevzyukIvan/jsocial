package com.example.jsocial.controller;

import com.example.jsocial.dto.FeedItemDTO;
import com.example.jsocial.service.FeedService;
import com.example.jsocial.service.photo.PhotoService;
import com.example.jsocial.service.post.PostService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class HomeController {

    private final FeedService feedService;

    public HomeController(FeedService feedService) {
        this.feedService = feedService;
    }

    @GetMapping("/")
    public String home(@RequestParam(defaultValue = "0") int page,
                       Model model,
                       @RequestHeader(value = "X-Requested-With", required = false) String requestedWith) {

        Pageable pageable = PageRequest.of(page, 10);
        Page<FeedItemDTO> feedPage = feedService.getCombinedFeed(pageable);

        model.addAttribute("feedItems", feedPage.getContent());
        model.addAttribute("page", feedPage);

        boolean isAjax = "XMLHttpRequest".equals(requestedWith);

        return isAjax ? "fragments/feedItems :: feedItems" : "home";
    }
}

