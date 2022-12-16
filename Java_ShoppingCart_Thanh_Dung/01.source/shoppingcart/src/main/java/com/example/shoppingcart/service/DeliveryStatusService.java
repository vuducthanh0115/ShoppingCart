package com.example.shoppingcart.service;

import com.example.shoppingcart.dto.DeliveryDTO;
import com.example.shoppingcart.entity.Bill;
import com.example.shoppingcart.entity.DeliveryStatus;

import java.util.List;
import java.util.Set;

public interface DeliveryStatusService {
    void addDefaultDeliveryStatus(Bill bill);

    void editDeliveryStatus(String status, Bill bill, Long delivery_id);

    void deleteDeliveryStatus(Bill bill, long delivery_id);

    List<DeliveryDTO> viewDeliveryStatusByBillId(long bill_id);

}
