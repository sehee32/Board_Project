package com.example.syworksboardassignment.service;

import com.example.syworksboardassignment.entity.Board;
import com.example.syworksboardassignment.entity.BoardLike;
import com.example.syworksboardassignment.entity.User;
import com.example.syworksboardassignment.repository.BoardRepository;
import com.example.syworksboardassignment.repository.BoardLikeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BoardLikeService {

    private final BoardLikeRepository likeRepository;
    private final BoardRepository boardRepository;

    @Transactional
    public void toggle(Long boardId, User user) {
        Board board = boardRepository.findById(boardId).orElseThrow();

        BoardLike like = likeRepository.findByBoardIdAndUserId(boardId, user.getId())
                .orElse(null);

        if (like != null) {
            // 좋아요 취소
            likeRepository.delete(like);
            board.setLikeCount(board.getLikeCount() - 1);
        } else {
            // 좋아요 추가
            BoardLike newLike = new BoardLike();
            newLike.setBoard(board);
            newLike.setUser(user);
            likeRepository.save(newLike);
            board.setLikeCount(board.getLikeCount() + 1);
        }
    }
}