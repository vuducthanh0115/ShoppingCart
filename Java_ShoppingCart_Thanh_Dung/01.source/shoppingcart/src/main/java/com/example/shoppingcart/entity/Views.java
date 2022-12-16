package com.example.shoppingcart.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "user_product")
@IdClass(SetPK.class)
public class Views {

    @Id
    @Column(name = "product_id")
    private Long productId;

    @Id
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "views")
    private Long views;

    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "user_id", updatable = false, insertable = false)
    private User user;

}