package com.appointment.service;

import com.appointment.dto.CommentRequest;
import com.appointment.dto.CommentResponse;
import com.appointment.entity.Comment;
import com.appointment.entity.User;
import com.appointment.entity.Doctor;
import com.appointment.repository.CommentRepository;
import com.appointment.repository.UserRepository;
import com.appointment.repository.DoctorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class CommentService{
    
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final DoctorRepository doctorRepository;

    public CommentResponse addComment(CommentRequest request) {
        Comment comment = new Comment();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) auth.getPrincipal();

        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        Doctor doctor = doctorRepository.findById(request.getDoctorId())
                .orElseThrow(() -> new RuntimeException("Doctor not found"));

        comment.setText(request.getCommentText());
        comment.setUser(user);
        comment.setDoctor(doctor);
        comment.setCreatedDate(new Date());

        Comment saved = commentRepository.save(comment);

        return mapToResponse(saved);
    }

    private CommentResponse mapToResponse(Comment comment) {
        return CommentResponse.builder()
                .id(comment.getId())
                .commentText(comment.getText())
                .patientId(comment.getUser().getId())
                .doctorId(comment.getDoctor().getId())
                .build();
    }
}