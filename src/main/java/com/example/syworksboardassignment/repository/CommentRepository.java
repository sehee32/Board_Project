package com.example.syworksboardassignment.repository;

import com.example.syworksboardassignment.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByBoardIdOrderByCreatedDateAsc(Long boardId);
}