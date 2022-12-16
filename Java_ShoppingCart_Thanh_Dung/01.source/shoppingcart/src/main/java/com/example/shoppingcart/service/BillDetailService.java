package com.example.shoppingcart.service;

import com.example.shoppingcart.entity.BillDetail;

import java.util.List;

public interface BillDetailService {
    public List<BillDetail> getAllBillDetails();

    public void deleteBillDetail(Long bill_detail_id);

    public List<BillDetail> getAllBillDetailsByBillId(long bill_id);
}
