package com.appointment.service;

import com.appointment.repository.CommentRepository;
import com.appointment.repository.UserRepository;
import com.appointment.entity.Comment;
import com.appointment.entity.User;
import com.appointment.dto.CommentRequest;
import com.appointment.dto.CommentResponse;
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

    public CommentResponse addComment(CommentRequest request){
        
        Comment comment = new Comment();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails)auth.getPrincipal();
        
        // Fix: Use injected repository instance
        User user = userRepository.findByUsername(userDetails.getUsername())
                                  .orElseThrow(()->new RuntimeException("User not found"));
                                  
        comment.setText(request.getText());
        comment.setUser(user);
        comment.setDoctor(request.getDoctor().getId());
        comment.setCreatedDate(new Date());
        
        Comment saved = commentRepository.save(comment);

        return mapToResponse(saved);
    }

    private CommentResponse mapToResponse(Comment comment){
        // Fix: Use commas instead of semicolons for constructor arguments
        return new CommentResponse(
            comment.getId(),
            comment.getText(),
            comment.getDoctor(),
            comment.getUser().getName(),
            comment.getCreatedDate()
        );
    }
}