package com.example.syworksboardassignment.service;

import com.example.syworksboardassignment.entity.Board;
import com.example.syworksboardassignment.entity.Comment;
import com.example.syworksboardassignment.entity.User;
import com.example.syworksboardassignment.repository.BoardRepository;
import com.example.syworksboardassignment.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final BoardRepository boardRepository;

    public List<Comment> getList(Long boardId) {
        // 모든 댓글을 가져온 후 계층 구조로 정렬
        List<Comment> allComments = commentRepository.findByBoardIdOrderByCreatedDateAsc(boardId);

        // 최상위 댓글만 필터링
        return allComments.stream()
                .filter(comment -> comment.getParent() == null)
                .collect(Collectors.toList());
    }

    @Transactional
    public void create(Long boardId, String content, Long parentId, User user) {
        Board board = boardRepository.findById(boardId).orElseThrow();

        Comment comment = new Comment();
        comment.setContent(content);
        comment.setBoard(board);
        comment.setWriter(user);

        if (parentId != null) {
            Comment parent = commentRepository.findById(parentId).orElseThrow();
            comment.setParent(parent);
        }

        commentRepository.save(comment);
    }

    @Transactional
    public void update(Long id, String content, User user) {
        Comment comment = commentRepository.findById(id).orElseThrow();
        if (!comment.getWriter().getId().equals(user.getId())) {
            throw new RuntimeException("권한이 없습니다");
        }
        comment.setContent(content);
    }

    @Transactional
    public void delete(Long id, User user) {
        Comment comment = commentRepository.findById(id).orElseThrow();
        if (!comment.getWriter().getId().equals(user.getId())) {
            throw new RuntimeException("권한이 없습니다");
        }
        commentRepository.delete(comment);
    }
}