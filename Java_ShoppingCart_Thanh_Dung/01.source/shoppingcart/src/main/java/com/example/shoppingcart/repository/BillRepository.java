package com.example.shoppingcart.repository;

import com.example.shoppingcart.entity.Bill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BillRepository extends JpaRepository<Bill, Long> {
    List<Bill> findBillByUserId(long userId);

    List<Bill> findBillByUserUsername(String username);

    Bill findBillByBillId(long billId);

    Bill findBillByToken(String token);

}
