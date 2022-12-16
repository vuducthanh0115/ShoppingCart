package com.example.shoppingcart.service.impl;

import com.example.shoppingcart.dto.BillDTO;
import com.example.shoppingcart.entity.Bill;
import com.example.shoppingcart.entity.BillDetail;
import com.example.shoppingcart.entity.DeliveryStatus;
import com.example.shoppingcart.entity.User;
import com.example.shoppingcart.exception.NotFoundException;
import com.example.shoppingcart.repository.*;
import com.example.shoppingcart.service.BillService;
import com.example.shoppingcart.service.CartService;
import com.example.shoppingcart.service.DeliveryStatusService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.naming.AuthenticationException;
import javax.servlet.http.HttpSession;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class BillServiceImpl implements BillService {
    private ProductRepository productRepository;

    private BillRepository billRepository;

    private BillDetailRepository billDetailRepository;

    private CartServiceImpl cartService;

    private DeliveryStatusService deliveryStatusService;

    private UserRepository userRepository;

    @Autowired
    private HttpSession session;

    public BillServiceImpl(ProductRepository productRepository, BillRepository billRepository,
                           BillDetailRepository billDetailRepository, CartServiceImpl cartService,
                           DeliveryStatusService deliveryStatusService, UserRepository userRepository) {
        this.productRepository = productRepository;
        this.billRepository = billRepository;
        this.billDetailRepository = billDetailRepository;
        this.cartService = cartService;
        this.deliveryStatusService = deliveryStatusService;
        this.userRepository = userRepository;
    }


    @Override
    public List<BillDTO> findBillByUserId() {
        Long userId = (Long) session.getAttribute("user");
        List<BillDTO> bills = billRepository.findBillByUserId(userId).stream()
                .map(data -> {
                    BillDTO billDTO = new BillDTO();
                    BeanUtils.copyProperties(data, billDTO);
                    return billDTO;
                }).collect(Collectors.toList());

        if (!bills.isEmpty()) {
            return bills;
        }
        else throw new NotFoundException("No bill found!");
    }

    @Override
    public List<Bill> findBillByUserUsername(String username) {
        List<Bill> bills = billRepository.findBillByUserUsername(username);
        if (!bills.isEmpty()) {
            return bills;
        }
        else throw new NotFoundException("No bill found!");
    }

    @Override
    public Bill findBillByBillId(long billId) {
        Bill bill = billRepository.findBillByBillId(billId);
        if (bill != null) {
            return bill;
        }
        else throw new NotFoundException("No bill id = " + bill + " found!");
    }

    @Autowired
    private JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String sender;

    @Override
    public void checkout() {
        Long user_id = (Long) session.getAttribute("user");
        if (!userRepository.findById(user_id).get().isCheckStatus()) {
            throw new NotFoundException("Please authenticate account to start check out!");
        }

        Bill bill = new Bill();
        Set<BillDetail> billDetailSet = cartService.convertToBillDetails();

        bill.setUserId(user_id);
        bill.setTotalPrice(cartService.getTotalPrice(user_id));

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        bill.setPurchaseDate(LocalDate.now().format(formatter));

        try {
            billRepository.save(bill);
        } catch (Exception ex) {
            throw new NotFoundException("Cart must not empty. Please check again!");
        }

        bill.setBillDetails(billDetailSet);

        billDetailSet.forEach(billDetail -> billDetail.setBillId(bill.getBillId()));
        billDetailSet.forEach(billDetail -> billDetailRepository.save(billDetail));

        billRepository.save(bill);
        //Delete cart when check out
//        cartService.resetCart();

        //Send mail to confirm purchase
        try {
            User user = userRepository.findById(user_id).get();
            String email = user.getUserEmail();
            UUID token = UUID.randomUUID();
            bill.setToken(String.valueOf(token));
            bill.setCreatedAt(LocalDateTime.now());
            bill.setStatus("Pending");
            billRepository.save(bill);
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper mimeMessageHelper;

            mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);
            mimeMessageHelper.setFrom(sender);
            mimeMessageHelper.setTo(email);
            mimeMessageHelper.setText("TOKEN : " + token +
                    "<br>Or <a href='fb.com'>click here</a> to confirm check out. " +
                    "<br>Token valid for 10 minutes", true);
            mimeMessageHelper.setSubject("Purchase confirmation");

            javaMailSender.send(mimeMessage);
        } catch (MessagingException e) {
            throw new NotFoundException("Send mail error!");
        }
    }

    @Override
    public String confirmPurchase(String token) {
        Bill bill = billRepository.findBillByToken(token);
        if (bill != null) {
            Duration duration = Duration.between(LocalDateTime.now(), bill.getCreatedAt());
            long diff = -duration.toMinutes();
            System.out.println(diff);
            System.out.println(bill.getStatus());
            Long user_id = (Long) session.getAttribute("user");
            if (diff >= 10) {
                bill.setStatus("Token out of time");
                return "Token out of time";
            }
            if (bill.getUserId().equals(user_id) && bill.getStatus().equals("Pending")) {
                bill.setStatus("Already paid");
                billRepository.save(bill);
                cartService.resetCart();
                deliveryStatusService.addDefaultDeliveryStatus(bill);
                return "Purchase confirmation done";
            }
            else if (bill.getStatus().equals("Already paid")) {
                return "Bill has already paid";
            }
            else {
                return "Unknown status";
            }
        }
        throw new NotFoundException("Bill token does not exist!");
    }

    @Override
    @Transactional
    public void updateBill(long billId, String[] product_name, int[] quantity) {
        Set<BillDetail> billDetailSet = new HashSet<>();
        long total_price = 0;

        for (int i =0; i < product_name.length; i++) {
            if (quantity[i] < 0) {
                throw new NotFoundException("Quantity must greater than 0!");
            }

            BillDetail billDetail = new BillDetail();
            billDetail.setBillDetailQuantity(quantity[i]);
            long product_id;
            try {
                product_id = productRepository.getProductIdByProductName(product_name[i]);
            } catch (Exception ex) {
                throw new NotFoundException("Product name: " + product_name[i] + " invalid!");
            }
            total_price += productRepository.getProductPrice(product_id) * quantity[i];

            billDetail.setProductId(product_id);
            billDetail.setBillDetailPrice(total_price);
            billDetail.setBillId(billId);

            billDetailSet.add(billDetail);
        }

        Bill old_bill = billRepository.findBillByBillId(billId);
        Long userId = (Long) session.getAttribute("user");
        if (old_bill.getUserId() != userId) {
            throw new NotFoundException("User dont have permission to edit bill " + billId);
        }
        if (old_bill == null) {
            throw new NotFoundException("Bill does not exist!");
        } else {
            billDetailRepository.deleteBillDetailsByBillId(billId);
            old_bill.setTotalPrice(total_price);
            old_bill.setBillDetails(billDetailSet);
            billRepository.save(old_bill);
        }
    }

    @Override
    public void deleteBill(long billId) {
        Bill bill = billRepository.findBillByBillId(billId);
        if (bill != null) {
            billRepository.deleteById(billId);
        }
        else throw new NotFoundException("Bill " + billId + " does not exist!");
    }


}
