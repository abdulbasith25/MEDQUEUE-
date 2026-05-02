package com.appointment.repository

import org.springframework.data.jpa.repository.JpaRepository;
import com.appointment.entity.Comment;
import org.springframework.stereotype.Repository;

@Repository
public interface commentRepository extends JpaRepository<Comment, Long>{
    
}