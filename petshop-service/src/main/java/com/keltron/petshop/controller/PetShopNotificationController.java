package com.keltron.petshop.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.keltron.petshop.services.impl.PetShopNotificationServiceImpl;
import com.keltron.utility.ResponseBuilder;
import com.keltron.utility.responses.AbstractResponse;
import com.keltron.utility.web.controller.abs.AbstractController;

@RestController
@RequestMapping("petshop/auth/notifications")
public class PetShopNotificationController extends AbstractController {

    @Autowired
    private PetShopNotificationServiceImpl serviceImpl;

    /**
     * Get all notifications of the logged-in user.
     */
    @GetMapping("/list")
    public ResponseEntity<AbstractResponse> getNotifications() {

        Long userId = serviceImpl.getLoggedInUserId();

        return new ResponseBuilder()
                .withData(serviceImpl.getNotifications(userId))
                .build();
    }

    /**
     * Get unread notification count.
     */
    @GetMapping("/unread-count")
    public ResponseEntity<AbstractResponse> getUnreadCount() {

        Long userId = serviceImpl.getLoggedInUserId();

        return new ResponseBuilder()
                .withData(serviceImpl.getUnreadCount(userId))
                .build();
    }

    /**
     * Mark one notification as read.
     */
    @PatchMapping("/read/{id}")
    public ResponseEntity<AbstractResponse> markAsRead(
            @PathVariable Long id) {

        return new ResponseBuilder()
                .withData(serviceImpl.markAsRead(id).toDTO())
                .build();
    }

    /**
     * Mark all notifications as read.
     */
    @PatchMapping("/read-all")
    public ResponseEntity<AbstractResponse> markAllAsRead() {

        Long userId = serviceImpl.getLoggedInUserId();

        serviceImpl.markAllAsRead(userId);

        return new ResponseBuilder()
                .withData("All notifications marked as read.")
                .build();
    }
}