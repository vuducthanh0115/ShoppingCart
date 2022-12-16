package com.example.shoppingcart.repository;

import com.example.shoppingcart.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public
interface UserRepository extends JpaRepository<User, Long> {

    User findByUsername(String username);
    User findByUserEmail(String email);

    @Query(value = """
            select * from users u
            where u.token = ?1 and (current_timestamp() - u.created_at) < 1000
            """, nativeQuery = true)
    User findByToken(String token);

    @Query(value = """
            select * from users u
            where u.token = ?1
            """, nativeQuery = true)
    User findByTokenLogin(String token);

    @Modifying
    @Query(value = """
            delete from users 
            where user_id = ?1 ;
            """, nativeQuery = true)
    void deleteUserById(Long userId);

    @Query("""
            select u from User u
            where u.userFullname = ?1
            """)
    List<User> findAllByFullName(String fullname);

    @Query("""
            select count(u) from User u
            where u.username = ?1
            """)
    Integer countUsername(String username);

    @Query("""
            select count(u) from User u
            where u.userEmail = ?1
            """)
    Integer countEmail(String userEmail);

    @Query("""
            select count(u) from User u
            where u.userPhone = ?1
            """)
    Integer countPhone(String userPhone);

    @Query("""
            select count(u) from User u
            where u.userId = ?1
            """)
    Integer countId(Long userId);

    @Query(nativeQuery = true, value = "SELECT DISTINCT u.* FROM users u JOIN bill b on u.user_id = b.user_id " +
            "join bill_detail bd on b.bill_id = bd.bill_id where u.user_id = ?1 and bd.product_id = ?2")
    User checkIfUserBuyProduct(Long user_id, Long product_id);
}
