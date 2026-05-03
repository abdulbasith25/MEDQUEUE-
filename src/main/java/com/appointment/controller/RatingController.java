package com.appointment.controller;
import com.appointment.dto.RatingResponse;
import com.appointment.dto.CommentResponse;
import com.appointment.dto.CommentRequest;
import com.appointment.dto.RatingRequest;
import com.appointment.service.CommentService;
import com.appointment.service.RatingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

@RestController 
@RequestMapping("/rating")
@RequiredArgsConstructor
public class RatingController{
    private final RatingService ratingService;
    private final CommentService commentService;
    @PostMapping("/add")
    public ResponseEntity<RatingResponse> addRating(@Valid @RequestBody RatingRequest request){
        RatingResponse response = ratingService.addRating(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/average")
    public ResponseEntity<RatingResponse> getRating(@RequestParam Long doctorId){
        RatingResponse response = ratingService.getDoctorRating(doctorId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/add-comment")
    public ResponseEntity<CommentResponse> addComment(@Valid @RequestBody CommentRequest request){
        CommentResponse response = commentService.addComment(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }


}