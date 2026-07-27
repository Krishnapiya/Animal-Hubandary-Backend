package com.keltron.petshop.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.keltron.utility.jpa.entity.Notification;
import com.keltron.utility.jpa.repository.AbstractRepository;

@Repository
public interface PetShopNotificationRepository
        extends AbstractRepository<Notification, Long> {

    List<Notification> findByUserIdOrderByIdDesc(Long userId);

    List<Notification> findByUserIdAndIsReadFalseOrderByIdDesc(Long userId);

    Long countByUserIdAndIsReadFalse(Long userId);
}