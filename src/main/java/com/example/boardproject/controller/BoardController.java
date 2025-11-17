package com.example.boardproject.controller;

import com.example.boardproject.entity.Board;
import com.example.boardproject.entity.Comment;
import com.example.boardproject.entity.User;
import com.example.boardproject.service.BoardService;
import com.example.boardproject.service.CommentService;
import com.example.boardproject.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;
    private final CommentService commentService;
    private final UserService userService;

    @GetMapping("/")
    public String root() {
        return "redirect:/boards";
    }

    @GetMapping("/boards")
    public String list(@RequestParam(defaultValue = "0") int page,
                       @RequestParam(defaultValue = "10") int size,
                       Authentication auth, Model model) {
        Page<Board> boards = boardService.getList(page, size);

        if (auth != null) {
            User user = userService.getByUsername(auth.getName());
            model.addAttribute("currentUserId", user.getId());
        }

        model.addAttribute("boards", boards);
        return "boards";
    }

    @GetMapping("/boards/{id}")
    public String detail(@PathVariable Long id, Authentication auth, Model model) {
        Board board = boardService.getDetail(id);
        List<Comment> comments = commentService.getList(id);

        if (auth != null) {
            User user = userService.getByUsername(auth.getName());
            boolean liked = boardService.isLiked(id, user.getId());
            model.addAttribute("liked", liked);
            model.addAttribute("currentUser", user);
        }

        model.addAttribute("board", board);
        model.addAttribute("comments", comments);
        return "board-detail";
    }

    @GetMapping("/boards/new")
    public String createForm() {
        return "board-form";
    }

    @PostMapping("/boards/new")
    public String create(@RequestParam String title,
                         @RequestParam String content,
                         Authentication auth) {
        User user = userService.getByUsername(auth.getName());
        boardService.create(title, content, user);
        return "redirect:/boards";
    }

    @GetMapping("/boards/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        Board board = boardService.getDetail(id);
        model.addAttribute("board", board);
        return "board-form";
    }

    @PostMapping("/boards/{id}/edit")
    public String update(@PathVariable Long id,
                         @RequestParam String title,
                         @RequestParam String content,
                         Authentication auth) {
        User user = userService.getByUsername(auth.getName());
        boardService.update(id, title, content, user);
        return "redirect:/boards/" + id;
    }

    @PostMapping("/boards/{id}/delete")
    public String delete(@PathVariable Long id, Authentication auth) {
        User user = userService.getByUsername(auth.getName());
        boardService.delete(id, user);
        return "redirect:/boards";
    }
}