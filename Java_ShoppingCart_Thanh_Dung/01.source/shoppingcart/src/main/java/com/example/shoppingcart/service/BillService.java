package com.example.shoppingcart.service;

import com.example.shoppingcart.dto.BillDTO;
import com.example.shoppingcart.entity.Bill;

import java.util.List;

public interface BillService {

    List<BillDTO> findBillByUserId();

    List<Bill> findBillByUserUsername(String username);

    Bill findBillByBillId(long billId);

    void checkout();

    void updateBill(long billId, String[] product_name, int[] quantity);

    void deleteBill(long billId);

    String confirmPurchase(String token);
}
