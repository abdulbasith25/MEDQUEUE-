package com.apointmen.service

import com.appointment.repostitory.CommentRepository;
import lombok.RequiredArgsConstructor;

@Service
public class CommentService{
    public CommentResponse addComment(CommentRequest request){
        
        Comment comment = new Comment();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails)auth.getPrincipal();
        User user = UserRepository.findByUserName(userDetails.getUsername()).orElseThrow(()->new RuntimeException());
        comment.setText(request.getText());
        comment.setUser(user);
        comment.setDoctor(request.getDoctor().getId());
        comment.setCreatedDate(new Date());
        
        Comment saved = commentRepository.save(comment);

        return mapToResponse(saved);
    }

    private CommentResponse mapToResponse(Comment comment){
        return new CommentResponse(
            comment.getId();
            comment.getText();
            comment.getDoctor();
            comment.getUser().getName();
            comment.getCreatedDate();
        );
        
    }
}