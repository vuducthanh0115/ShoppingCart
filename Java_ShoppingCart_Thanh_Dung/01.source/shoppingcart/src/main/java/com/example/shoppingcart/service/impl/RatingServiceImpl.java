package com.example.shoppingcart.service.impl;

import com.example.shoppingcart.dto.RatingDTO;
import com.example.shoppingcart.entity.Product;
import com.example.shoppingcart.entity.Rating;
import com.example.shoppingcart.entity.User;
import com.example.shoppingcart.exception.NotFoundException;
import com.example.shoppingcart.repository.ProductRepository;
import com.example.shoppingcart.repository.RatingRepository;
import com.example.shoppingcart.repository.UserRepository;
import com.example.shoppingcart.service.RatingService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Service
//@Transactional
public class RatingServiceImpl implements RatingService {
    @Autowired
    private RatingRepository ratingRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    public RatingServiceImpl(RatingRepository ratingRepository, UserRepository userRepository,
                             ProductRepository productRepository) {
        this.ratingRepository = ratingRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
    }

    @Override
    public String vote(RatingDTO ratingDTO, HttpSession session) {
        Long user_id = (Long) session.getAttribute("user");
        ratingDTO.setUserId(user_id);
        if (userRepository.checkIfUserBuyProduct(ratingDTO.getUserId(), ratingDTO.getProductId()) == null) {
            return "User must buy the product first!";
        }
        User editUser = userRepository.findById(ratingDTO.getUserId())
                .orElseThrow(() -> new NotFoundException("User " + ratingDTO.getUserId() + " not found"));
        Product editProduct = productRepository.findById(ratingDTO.getProductId())
                .orElseThrow(() -> new NotFoundException("Product " + ratingDTO.getProductId() + " not found"));
        if (ratingRepository.countVote(ratingDTO.getProductId(), ratingDTO.getUserId()) < 1) {
            Rating rating = new Rating();
            BeanUtils.copyProperties(ratingDTO, rating);
            editUser.getRating().add(rating);
            editProduct.getRatings().add(rating);
            float totalRating = 0;
            int sizeOfRatings = 0;
            if (editProduct.getRatings() != null) {
                sizeOfRatings = editProduct.getRatings().size();
                totalRating = editProduct.getProductRating() * (sizeOfRatings-1);
            }
            editProduct.setProductRating((totalRating + ratingDTO.getVote()) / (float) (sizeOfRatings));
            ratingRepository.save(rating);
            userRepository.save(editUser);
            productRepository.save(editProduct);
            return "Rating created!";
        }
        else {
            return editVote(ratingDTO);
        }
    }

    @Override
    public String editVote(RatingDTO ratingDTO) {
        if (ratingRepository.countVote(ratingDTO.getProductId(), ratingDTO.getUserId()) > 0) {
            Rating rating = ratingRepository.getRatingByProductAndUser(ratingDTO.getProductId(), ratingDTO.getUserId());
            int oldVote = rating.getVote();
            rating.setVote(ratingDTO.getVote());
            Product editProduct = productRepository.findById(ratingDTO.getProductId()).get();
            float totalRating = 0;
            int sizeOfRatings = 0;
            if (editProduct.getRatings() != null) {
                sizeOfRatings = editProduct.getRatings().size();
                totalRating = editProduct.getProductRating() * sizeOfRatings;
            }
            editProduct.setProductRating((totalRating + ratingDTO.getVote() - oldVote) / (float) (sizeOfRatings));
            ratingRepository.save(rating);
            productRepository.save(editProduct);
            return "Rating updated!";
        }
        else throw new NotFoundException("Rating does not exist!");
    }

    @Override
    public String deleteByProductId(Long product_id, HttpSession session) {
        Long user_id = (Long) session.getAttribute("user");
        if (ratingRepository.countVote(product_id, user_id) > 0) {
            Rating rating = ratingRepository.getRatingByProductAndUser(product_id, user_id);
            int oldVote = rating.getVote();
            Product editProduct = productRepository.findById(product_id).get();
            int sizeOfRatings = editProduct.getRatings().size();
            float totalRating = editProduct.getProductRating() * sizeOfRatings;
            if (sizeOfRatings <= 1) {
                editProduct.setProductRating((float) 0);
            }
            else {
                editProduct.setProductRating((totalRating - oldVote) / (float) (sizeOfRatings-1));
            }
            //ratingRepository.deleteByProductIdAndUserId(product_id, user_id);
            ratingRepository.delete(rating);
            productRepository.save(editProduct);
            return "Rating deleted!";
        }
        else throw new NotFoundException("Rating does not exist!");
    }


}
