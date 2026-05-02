package com.appointment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.appointment.entity.Comment;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface commentRepository extends JpaRepository<Comment, Long>{
    List<Comment> findByDoctorId(Long doctorId);
}