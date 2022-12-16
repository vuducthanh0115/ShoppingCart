package com.example.shoppingcart.repository;

import com.example.shoppingcart.entity.BillDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BillDetailRepository extends JpaRepository<BillDetail, Long> {
    void deleteBillDetailsByBillId(Long bill_id);

    List<BillDetail> findBillDetailsByBillId(Long bill_id);
}
