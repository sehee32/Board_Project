package com.example.syworksboardassignment.service;

import com.example.syworksboardassignment.entity.Board;
import com.example.syworksboardassignment.entity.User;
import com.example.syworksboardassignment.repository.BoardRepository;
import com.example.syworksboardassignment.repository.BoardLikeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BoardService {

    private final BoardRepository boardRepository;
    private final BoardLikeRepository boardLikeRepository;

    public Page<Board> getList(int page, int size) {
        return boardRepository.findAll(PageRequest.of(page, size));
    }

    public Board getDetail(Long id) {
        return boardRepository.findById(id).orElseThrow();
    }

    @Transactional
    public void create(String title, String content, User user) {
        Board board = new Board();
        board.setTitle(title);
        board.setContent(content);
        board.setWriter(user);
        boardRepository.save(board);
    }

    @Transactional
    public void update(Long id, String title, String content, User user) {
        Board board = boardRepository.findById(id).orElseThrow();
        if (!board.getWriter().getId().equals(user.getId())) {
            throw new RuntimeException("권한이 없습니다");
        }
        board.setTitle(title);
        board.setContent(content);
    }

    @Transactional
    public void delete(Long id, User user) {
        Board board = boardRepository.findById(id).orElseThrow();
        if (!board.getWriter().getId().equals(user.getId())) {
            throw new RuntimeException("권한이 없습니다");
        }
        boardRepository.delete(board);
    }

    public boolean isLiked(Long boardId, Long userId) {
        return boardLikeRepository.existsByBoardIdAndUserId(boardId, userId);
    }
}