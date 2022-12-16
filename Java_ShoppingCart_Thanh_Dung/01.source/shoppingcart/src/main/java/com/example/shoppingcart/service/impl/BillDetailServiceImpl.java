package com.example.shoppingcart.service.impl;

import com.example.shoppingcart.entity.BillDetail;
import com.example.shoppingcart.exception.NotFoundException;
import com.example.shoppingcart.repository.BillDetailRepository;
import com.example.shoppingcart.service.BillDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BillDetailServiceImpl implements BillDetailService {
    @Autowired
    private BillDetailRepository billDetailRepository;

    public BillDetailServiceImpl(BillDetailRepository billDetailRepository) {
        this.billDetailRepository = billDetailRepository;
    }

    @Override
    public List<BillDetail> getAllBillDetails() {
        List<BillDetail> billDetails = billDetailRepository.findAll();
        if (!billDetails.isEmpty()) {
            return billDetails;
        }
        else throw new NotFoundException("No bill detail found!");
    }

    @Override
    public void deleteBillDetail(Long bill_detail_id) {
        BillDetail billDetail = billDetailRepository.findById(bill_detail_id).get();
        if (billDetail != null) {
            billDetailRepository.deleteById(bill_detail_id);
        }
        else throw new NotFoundException("No bill detail with id = " + bill_detail_id + " found!");
    }

    @Override
    public List<BillDetail> getAllBillDetailsByBillId(long bill_id) {
        List<BillDetail> billDetails = billDetailRepository.findBillDetailsByBillId(bill_id);
        if (!billDetails.isEmpty()) {
            return billDetails;
        }
        else throw new NotFoundException("No bill detail found!");
    }
}
