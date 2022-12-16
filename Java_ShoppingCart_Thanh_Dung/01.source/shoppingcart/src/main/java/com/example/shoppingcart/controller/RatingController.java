package com.example.shoppingcart.controller;

import com.example.shoppingcart.dto.RatingDTO;
import com.example.shoppingcart.service.impl.RatingServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

@RestController
@RequestMapping("/api/rating")
public class RatingController {
    @Autowired
    private RatingServiceImpl ratingService;

    @Autowired
    private HttpSession session;

    public RatingController(RatingServiceImpl ratingService) {
        this.ratingService = ratingService;
    }

    @PostMapping("/add")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_USER')")
    public ResponseEntity<?> vote(@Valid @RequestBody RatingDTO ratingDTO) {
        return ResponseEntity.ok(this.ratingService.vote(ratingDTO, session));
    }

    @DeleteMapping("/delete")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_USER')")
    public ResponseEntity<?> deleteByProductIdAndUserId(@RequestParam (value = "product_id") Long product_id) {
        return ResponseEntity.ok(this.ratingService.deleteByProductId(product_id, session));
    }
}
