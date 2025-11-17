package com.example.syworksboardassignment.repository;

import com.example.syworksboardassignment.entity.Board;
import org.springframework.data.jpa.repository.JpaRepository;


public interface BoardRepository extends JpaRepository<Board, Long> {
}