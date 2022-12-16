package com.example.shoppingcart.service;

import com.example.shoppingcart.dto.RatingDTO;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public interface RatingService {
    public String vote(RatingDTO ratingDTO, HttpSession session);

    public String editVote(RatingDTO ratingDTO);

    public String deleteByProductId(Long product_id, HttpSession session);
}
