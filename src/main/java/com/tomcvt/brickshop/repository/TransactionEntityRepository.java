package com.tomcvt.brickshop.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tomcvt.brickshop.model.Order;
import com.tomcvt.brickshop.model.TransactionEntity;

@Repository
public interface TransactionEntityRepository extends JpaRepository<TransactionEntity, Long> {
    Optional<TransactionEntity> findByTransactionId(UUID transactionId);
    List<TransactionEntity> findByOrder(Order order);
}
