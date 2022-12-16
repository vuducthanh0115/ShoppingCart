package com.example.shoppingcart.repository;

import com.example.shoppingcart.entity.Rating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public interface RatingRepository extends JpaRepository<Rating, Long> {
//    @Modifying
//    @Query(value = "INSERT INTO rating(rating_id, vote, product_id, user_id)" +
//            " values (?, ?, ?, ?)", nativeQuery = true)
//    public void vote(Long rating_id, Integer vote, Long product_id, Long user_id);

    @Query(value = "SELECT COUNT(r) FROM Rating r WHERE r.productId=?1 and r.userId=?2")
    public Integer countVote(Long product_id, Long user_id);

    @Query(value = "SELECT * FROM rating r WHERE r.product_id=?1 and r.user_id=?2", nativeQuery = true)
    public Rating getRatingByProductAndUser(Long product_id, Long user_id);

    @Modifying
    @Query(value = "DELETE FROM rating r WHERE r.product_id=?1 and r.user_id=?2", nativeQuery = true)
    public void deleteByProductIdAndUserId(Long product_id, Long user_id);
}
