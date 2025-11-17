package com.example.syworksboardassignment.controller;

import com.example.syworksboardassignment.entity.User;
import com.example.syworksboardassignment.service.BoardLikeService;
import com.example.syworksboardassignment.service.CommentService;
import com.example.syworksboardassignment.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ApiController {

    private final BoardLikeService likeService;
    private final CommentService commentService;
    private final UserService userService;

    @PostMapping("/boards/{id}/like")
    public String toggleLike(@PathVariable Long id, Authentication auth) {
        User user = userService.getByUsername(auth.getName());
        likeService.toggle(id, user);
        return "ok";
    }

    @PostMapping("/comments")
    public String createComment(@RequestParam Long boardId,
                                @RequestParam String content,
                                @RequestParam(required = false) Long parentId,
                                Authentication auth) {
        User user = userService.getByUsername(auth.getName());
        commentService.create(boardId, content, parentId, user);
        return "ok";
    }

    @PutMapping("/comments/{id}")
    public String updateComment(@PathVariable Long id,
                                @RequestParam String content,
                                Authentication auth) {
        User user = userService.getByUsername(auth.getName());
        commentService.update(id, content, user);
        return "ok";
    }

    @DeleteMapping("/comments/{id}")
    public String deleteComment(@PathVariable Long id, Authentication auth) {
        User user = userService.getByUsername(auth.getName());
        commentService.delete(id, user);
        return "ok";
    }
}