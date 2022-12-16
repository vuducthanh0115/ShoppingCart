package com.example.shoppingcart.dto;

import com.example.shoppingcart.entity.BillDetail;
import com.example.shoppingcart.entity.DeliveryStatus;
import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.CascadeType;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BillDTO {

    private long billId;

    private String purchaseDate;

    private long totalPrice;

    private Set<BillDetail> billDetails;

    private Set<DeliveryStatus> deliveryStatuses;
}
