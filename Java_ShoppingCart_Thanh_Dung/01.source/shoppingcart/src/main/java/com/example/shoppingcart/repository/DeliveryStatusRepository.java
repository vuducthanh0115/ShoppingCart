package com.example.shoppingcart.repository;

import com.example.shoppingcart.entity.DeliveryStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface DeliveryStatusRepository extends JpaRepository<DeliveryStatus, Long> {
    @Modifying
    @Query("DELETE FROM DeliveryStatus ds WHERE ds.deliveryStatusId = ?1")
    void deleteDeliveryStatusById(long id);

    @Query("SELECT d FROM DeliveryStatus d WHERE d.billId = ?1")
    List<DeliveryStatus> getDeliveryStatusByBillId(long bill_id);
}
