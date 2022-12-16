package com.example.shoppingcart.controller;

import com.example.shoppingcart.service.impl.BillDetailServiceImpl;
import com.example.shoppingcart.service.impl.BillServiceImpl;
import com.example.shoppingcart.service.impl.DeliveryStatusServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/delivery")
public class DeliveryStatusController {
    @Autowired
    private DeliveryStatusServiceImpl deliveryStatusService;

    @Autowired
    private BillServiceImpl billService;

    public DeliveryStatusController(DeliveryStatusServiceImpl deliveryStatusService) {
        this.deliveryStatusService = deliveryStatusService;
    }

    @PostMapping("/edit")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    public ResponseEntity<?> editDeliveryStatus(@RequestParam Long delivery_id, @RequestParam String status, @RequestParam Long billId) {
        deliveryStatusService.editDeliveryStatus(status, billService.findBillByBillId(billId), delivery_id);
        return ResponseEntity.ok("Edit delivery status success");
    }

    @DeleteMapping("/delete")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    public ResponseEntity<?> deleteDeliveryStatus(@RequestParam Long delivery_id, @RequestParam Long billId) {
        deliveryStatusService.deleteDeliveryStatus(billService.findBillByBillId(billId), delivery_id);
        return ResponseEntity.ok("Delivery status deleted!");
    }

    @GetMapping("/view")
    @PreAuthorize("hasAnyAuthority('ROLE_USER')")
    public ResponseEntity<?> getDeliveryStatus(@RequestParam Long billId) {
        return new ResponseEntity<>(deliveryStatusService.viewDeliveryStatusByBillId(billId), HttpStatus.OK);
    }
}
