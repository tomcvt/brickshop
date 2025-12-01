package com.tomcvt.brickshop.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tomcvt.brickshop.enums.PaymentStatus;
import com.tomcvt.brickshop.model.Order;
import com.tomcvt.brickshop.model.TransactionEntity;
import com.tomcvt.brickshop.repository.TransactionEntityRepository;

@Service
public class TransactionCreator {
    
    private final PaymentHandler paymentHandler;
    private final TransactionEntityRepository transactionRepository;

    public TransactionCreator(PaymentHandler paymentHandler, 
    TransactionEntityRepository transactionRepository) {
        this.paymentHandler = paymentHandler;
        this.transactionRepository = transactionRepository;
    }
    @Transactional
    public TransactionEntity createTransaction(Order order) {
        TransactionEntity transaction = new TransactionEntity();
        transaction.setOrder(order);
        transaction.setAmount(order.getTotalAmount());
        transaction.setStatus(PaymentStatus.PENDING);
        transaction.setPaymentMethod(order.getPaymentMethod());
        /*
        String paymentToken = paymentHandler.getNewToken(
                order.getPaymentMethod(), order.getTotalAmount()
        );
        */
        //TODO: in mock payment we just generate a dummy token, later integrate real payment gateway
        String paymentToken = "PAYMENT-TOKEN-" + order.getOrderId();
        transaction.setPaymentToken(paymentToken);
        transaction = transactionRepository.save(transaction);
        // Additional transaction setup can be done here
        return transaction;
    }
}
