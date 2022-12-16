package com.example.shoppingcart.controller;

import com.example.shoppingcart.dto.BillEditDTO;
import com.example.shoppingcart.service.impl.BillServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/bill")
public class BillController {
    @Autowired
    private BillServiceImpl billService;

    public BillController(BillServiceImpl billService) {
        this.billService = billService;
    }

    @GetMapping("/view")
    @PreAuthorize("hasAnyAuthority('ROLE_USER')")
    public ResponseEntity<?> getBill() {
        return new ResponseEntity<>(billService.findBillByUserId(), HttpStatus.OK);
    }

    @PutMapping("/edit")
    @PreAuthorize("hasAnyAuthority('ROLE_USER')")
    public ResponseEntity<?> editBill(@RequestBody BillEditDTO billEditDTO) {
        billService.updateBill(billEditDTO.getBillId(), billEditDTO.getName_products(), billEditDTO.getQuantities());
        return ResponseEntity.ok("Bill "+ billEditDTO.getBillId() + " updated!");
    }

    @DeleteMapping("/delete")
    @PreAuthorize("hasAnyAuthority('ROLE_USER')")
    public ResponseEntity<?> deleteBill(@RequestParam Long billId) {
        billService.deleteBill(billId);
        return ResponseEntity.ok("Bill " + billId + " deleted");
    }

    @GetMapping("/check-out")
    @PreAuthorize("hasAnyAuthority('ROLE_USER')")
    public ResponseEntity<?> checkOut() {
        billService.checkout();
        return new ResponseEntity<>(billService.findBillByUserId(), HttpStatus.OK);
    }

    @GetMapping("/confirm-purchase")
    @PreAuthorize("hasAnyAuthority('ROLE_USER')")
    public ResponseEntity<?> confirmPurchase(@RequestParam String token) {
        return ResponseEntity.ok(billService.confirmPurchase(token));
    }
}
