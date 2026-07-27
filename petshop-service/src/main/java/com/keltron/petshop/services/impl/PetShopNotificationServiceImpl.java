package com.keltron.petshop.services.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import com.keltron.petshop.repository.PetShopNotificationRepository;
import com.keltron.petshop.entity.PetShopRegistrationApplication;
import com.keltron.petshop.repository.PetShopRegistrationApplicationRepository;
import com.keltron.utility.jpa.entity.Office;
import com.keltron.utility.annotations.ReadTransactional;
import com.keltron.utility.annotations.WriteTransactional;
import com.keltron.utility.beans.dto.NotificationDto;
import com.keltron.utility.jpa.entity.Notification;
import com.keltron.utility.jpa.entity.Users;
import com.keltron.utility.jpa.repository.UsersRepository;
import com.keltron.utility.manage.service.abs.AbstractJpaService;

@Service
public class PetShopNotificationServiceImpl extends
        AbstractJpaService<NotificationDto, Long, PetShopNotificationRepository, Notification> {

    @Autowired
    private UsersRepository usersRepository;
    
    @Autowired
    private PetShopRegistrationApplicationRepository applicationRepository;

    /**
     * Returns the logged-in user's ID.
     */
    @ReadTransactional
    public Long getLoggedInUserId() {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        Jwt jwt = (Jwt) authentication.getPrincipal();

        String username = jwt.getSubject();

        Users user = usersRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found : " + username));

        return user.getId();
    }

    /**
     * Create Notification
     */
    @WriteTransactional
    public Notification createNotification(
            Long userId,
            String module,
            Long referenceId,
            String title,
            String message,
            String notificationType) {

        Notification notification = new Notification();

        notification.setUserId(userId);
        notification.setModule(module);
        notification.setReferenceId(referenceId);
        notification.setTitle(title);
        notification.setMessage(message);
        notification.setNotificationType(notificationType);
        notification.setIsRead(false);

        return repository.save(notification);
    }

    /**
     * Get all notifications of a user
     */
    @ReadTransactional
    public List<NotificationDto> getNotifications(Long userId) {

        return repository.findByUserIdOrderByIdDesc(userId)
                .stream()
                .map(notification -> {

                    NotificationDto dto = notification.toDTO();

                    if (notification.getReferenceId() != null) {

                        applicationRepository.findById(notification.getReferenceId())
                                .ifPresent(application -> {

                                    dto.setApplicationId(application.getId());
                                    dto.setApplicationNumber(application.getApplicationNumber());

                                    if (application.getDistrict() != null) {
                                        dto.setDistrict(application.getDistrict().getName());
                                    }

                                    if (application.getStatus() != null) {
                                        dto.setApplicationStatus(
                                                application.getStatus().getStatusName());
                                    }
                                });
                    }

                    return dto;
                })
                .toList();
    }

    /**
     * Get unread notification count
     */
    @ReadTransactional
    public Long getUnreadCount(Long userId) {

        return repository.countByUserIdAndIsReadFalse(userId);
    }

    /**
     * Mark one notification as read
     */
    @WriteTransactional
    public Notification markAsRead(Long id) {

        Notification notification = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notification not found"));

        notification.setIsRead(true);

        return repository.save(notification);
    }

    /**
     * Mark all notifications as read
     */
    @WriteTransactional
    public void markAllAsRead(Long userId) {

        List<Notification> notifications =
                repository.findByUserIdAndIsReadFalseOrderByIdDesc(userId);

        notifications.forEach(notification -> notification.setIsRead(true));

        repository.saveAll(notifications);
    }
}