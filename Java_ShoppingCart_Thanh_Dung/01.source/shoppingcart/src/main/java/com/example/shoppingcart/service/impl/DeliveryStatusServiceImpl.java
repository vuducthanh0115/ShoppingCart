package com.example.shoppingcart.service.impl;

import com.example.shoppingcart.dto.DeliveryDTO;
import com.example.shoppingcart.entity.Bill;
import com.example.shoppingcart.entity.DeliveryStatus;
import com.example.shoppingcart.exception.NotFoundException;
import com.example.shoppingcart.repository.BillRepository;
import com.example.shoppingcart.repository.DeliveryStatusRepository;
import com.example.shoppingcart.service.DeliveryStatusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class DeliveryStatusServiceImpl implements DeliveryStatusService {
    @Autowired
    private BillRepository billRepository;

    @Autowired
    private DeliveryStatusRepository deliveryStatusRepository;

    public DeliveryStatusServiceImpl(BillRepository billRepository, DeliveryStatusRepository deliveryStatusRepository) {
        this.billRepository = billRepository;
        this.deliveryStatusRepository = deliveryStatusRepository;
    }

    @Override
    public void addDefaultDeliveryStatus(Bill bill) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss");

        DeliveryStatus deliveryStatus = new DeliveryStatus();
        deliveryStatus.setStatus("Pending");
        deliveryStatus.setDeliveryTime(LocalDateTime.now().format(formatter));
        deliveryStatus.setBillId(bill.getBillId());

        Set<DeliveryStatus> deliveryStatusSet = new HashSet<>();
        deliveryStatusSet.add(deliveryStatus);
        bill.setDeliveryStatuses(deliveryStatusSet);
        deliveryStatusRepository.save(deliveryStatus);
    }

    @Override
    @Transactional
    public void editDeliveryStatus(String status, Bill bill, Long delivery_id) {
        Set<DeliveryStatus> deliveryStatusSet = bill.getDeliveryStatuses();

        deliveryStatusSet.stream().forEach(deliveryStatus1 -> {
            if (deliveryStatus1.getDeliveryStatusId() == delivery_id) {
                deliveryStatus1.setStatus(status);
                deliveryStatus1.setBillId(bill.getBillId());
            }
        });

        DeliveryStatus deliveryStatus = new DeliveryStatus();
        deliveryStatus.setStatus(status);
        deliveryStatus.setBillId(bill.getBillId());

        bill.setDeliveryStatuses(deliveryStatusSet);
        billRepository.save(bill);
        deliveryStatusRepository.save(deliveryStatus);
    }

    @Override
    @Transactional
    public void deleteDeliveryStatus(Bill bill, long delivery_id) {
        Set<DeliveryStatus> deliveryStatusSet = bill.getDeliveryStatuses();
        DeliveryStatus deliveryStatus = deliveryStatusRepository.findById(delivery_id).get();
        if (deliveryStatus == null) {
            throw new NotFoundException("Delivery status not exist!");
        }
        deliveryStatusSet.remove(deliveryStatus);
        deliveryStatusRepository.deleteDeliveryStatusById(delivery_id);
    }

    @Override
    public List<DeliveryDTO> viewDeliveryStatusByBillId(long bill_id) {
        Bill bill = billRepository.findBillByBillId(bill_id);
        if (bill != null) {
            if (bill.getStatus() == "Pending") {
                throw new NotFoundException("Please confirm purchase first");
            }
            if (bill.getStatus() == "Already paid") {
                List<DeliveryDTO> deliveryDTOS = deliveryStatusRepository.getDeliveryStatusByBillId(bill_id)
                        .stream().map(data -> {
                            DeliveryDTO deliveryDTO = new DeliveryDTO();
                            deliveryDTO.setDeliveryTime(data.getDeliveryTime());
                            deliveryDTO.setStatus(data.getStatus());
                            return deliveryDTO;
                        }).collect(Collectors.toList());
                if (!deliveryDTOS.isEmpty()) {
                    return deliveryDTOS;
                } else throw new NotFoundException("No delivery status found!");
            }
            else {
                throw new NotFoundException("Token out of time");
            }
        }
        throw new NotFoundException("Bill id = " + bill_id + " not exist!");
    }
}
