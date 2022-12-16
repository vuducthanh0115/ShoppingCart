package com.example.shoppingcart.controller;

import com.example.shoppingcart.service.impl.BillDetailServiceImpl;
import com.example.shoppingcart.service.impl.BillServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/bill-detail")
public class BillDetailController {
    @Autowired
    private BillDetailServiceImpl billDetailService;

    @Autowired
    private BillServiceImpl billService;

    public BillDetailController(BillDetailServiceImpl billDetailService, BillServiceImpl billService) {
        this.billDetailService = billDetailService;
        this.billService = billService;
    }

    @GetMapping("/view")
    @PreAuthorize("hasAnyAuthority('ROLE_USER')")
    public ResponseEntity<?> getAllBillDetailByBillId(@RequestParam Long bill_id) {
        return new ResponseEntity<>(billService.findBillByBillId(bill_id).getBillDetails(), HttpStatus.OK);
    }

    @DeleteMapping("/delete")
    @PreAuthorize("hasAnyAuthority('ROLE_USER')")
    public ResponseEntity<?> deleteBillDetailByBillDetailId(@RequestParam Long bill_detail_id) {
        billDetailService.deleteBillDetail(bill_detail_id);
        return ResponseEntity.ok("Bill detail " + bill_detail_id + " deleted!");
    }
}
