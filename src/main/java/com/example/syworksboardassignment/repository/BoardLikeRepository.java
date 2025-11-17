package com.example.syworksboardassignment.repository;

import com.example.syworksboardassignment.entity.BoardLike;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface BoardLikeRepository extends JpaRepository<BoardLike, Long> {
    Optional<BoardLike> findByBoardIdAndUserId(Long boardId, Long userId);
    boolean existsByBoardIdAndUserId(Long boardId, Long userId);
}