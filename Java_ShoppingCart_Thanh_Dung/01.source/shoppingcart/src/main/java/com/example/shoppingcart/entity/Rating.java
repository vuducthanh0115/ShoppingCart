package com.example.shoppingcart.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name ="rating")
public class Rating {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "rating_id")
    private Long ratingId;

    @Column(name = "vote")
    private Integer vote;

    @Column(name = "product_id")
    private Long productId;

    @Column(name = "user_id")
    private Long userId;


    @ManyToOne(fetch = FetchType.LAZY)
    @JsonBackReference(value = "rating_user")
    @JoinColumn(name = "user_id",updatable = false,insertable = false)
    private User user;


    @ManyToOne(fetch = FetchType.LAZY)
    @JsonBackReference(value = "rating_product")
    @JoinColumn(name = "product_id",updatable = false,insertable = false)
    private Product product;

}
