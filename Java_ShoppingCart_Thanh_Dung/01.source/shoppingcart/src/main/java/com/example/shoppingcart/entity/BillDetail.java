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
@Table(name ="bill_detail")
public class BillDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "bill_detail_id")
    private Long billDetailId;

    @Column(name = "bill_detail_price")
    private Long billDetailPrice;

    @Column(name = "bill_detail_quantity")
    private Integer billDetailQuantity;

    @Column(name = "product_id")
    private Long productId;

    @Column(name = "bill_id")
    private Long billId;

    @JsonBackReference(value = "billDetail_product")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id",updatable = false,insertable = false)
    private Product product;

    @JsonBackReference(value = "billDetail_bill")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bill_id",updatable = false,insertable = false)
    private Bill bill;


}
