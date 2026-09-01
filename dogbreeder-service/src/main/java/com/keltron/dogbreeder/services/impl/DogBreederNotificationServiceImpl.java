package com.keltron.dogbreeder.services.impl;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import com.keltron.dogbreeder.entity.DogBreederRegistrationApplication;
import com.keltron.dogbreeder.repository.DogBreederNotificationRepository;
import com.keltron.dogbreeder.repository.DogBreederRegistrationApplicationRepository;
import com.keltron.utility.annotations.ReadTransactional;
import com.keltron.utility.annotations.WriteTransactional;
import com.keltron.utility.beans.dto.NotificationDto;
import com.keltron.utility.jpa.entity.Notification;
import com.keltron.utility.jpa.entity.Users;
import com.keltron.utility.jpa.repository.UsersRepository;
import com.keltron.utility.manage.service.abs.AbstractJpaService;

@Service
public class DogBreederNotificationServiceImpl extends
        AbstractJpaService<NotificationDto, Long, DogBreederNotificationRepository, Notification> {

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private DogBreederRegistrationApplicationRepository applicationRepository;

    /**
     * Returns the logged-in user's ID.
     */
    @ReadTransactional
    public Long getLoggedInUserId() {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !(authentication.getPrincipal() instanceof Jwt jwt)) {
            throw new RuntimeException("Unauthenticated user context");
        }

        String username = jwt.getSubject();

        Users user = usersRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));

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

        if (userId == null) {
            throw new IllegalArgumentException("Recipient User ID cannot be null when creating notification");
        }

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
     * Get all notifications of a user (Optimized to prevent N+1 queries & preserve historical status)
     */
    @ReadTransactional
    public List<NotificationDto> getNotifications(Long userId) {

        List<Notification> notifications = repository.findByUserIdOrderByIdDesc(userId);

        if (notifications.isEmpty()) {
            return List.of();
        }

        // 1. Collect all non-null reference IDs (Application IDs)
        List<Long> appIds = notifications.stream()
                .map(Notification::getReferenceId)
                .filter(Objects::nonNull)
                .distinct()
                .toList();

        // 2. Batch fetch applications in a single query
        Map<Long, DogBreederRegistrationApplication> applicationMap = applicationRepository.findAllById(appIds)
                .stream()
                .collect(Collectors.toMap(DogBreederRegistrationApplication::getId, Function.identity()));

        // 3. Map to DTOs in memory
        return notifications.stream()
                .map(notification -> {

                    NotificationDto dto = notification.toDTO();

                    // Set status header from the notification title to keep historical consistency
                    dto.setApplicationStatus(notification.getTitle());

                    if (notification.getReferenceId() != null) {
                        DogBreederRegistrationApplication application = applicationMap.get(notification.getReferenceId());

                        if (application != null) {
                            dto.setApplicationId(application.getId());
                            dto.setApplicationNumber(application.getApplicationNumber());

                            if (application.getDistrict() != null) {
                                dto.setDistrict(application.getDistrict().getName());
                            }
                        }
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
                .orElseThrow(() -> new RuntimeException("Notification not found with ID: " + id));

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

        if (!notifications.isEmpty()) {
            notifications.forEach(notification -> notification.setIsRead(true));
            repository.saveAll(notifications);
        }
    }

    /**
     * Helper method to trigger status variation notifications with standardized title and message formats.
     */
    @WriteTransactional
    public void triggerStatusNotification(
            DogBreederRegistrationApplication application,
            String newStatus,
            Long recipientUserId,
            String extraDetails) {

        if (application == null || recipientUserId == null) {
            return;
        }

        String title;
        String message;
        String notificationType = "STATUS_UPDATE";
        String appNum = application.getApplicationNumber() != null ? application.getApplicationNumber() : "";

        switch (newStatus) {
            case "DRAFT":
                title = "Application Draft Saved";
                message = "Your Dog Breeder Registration application (" + appNum + ") has been saved as draft.";
                break;

            case "SUBMITTED":
                title = "Application Submitted";
                message = "Your Dog Breeder Registration application (" + appNum + ") has been submitted successfully.";
                break;

            case "FORWARDED_TO_CVO":
                title = "Application Forwarded to CVO";
                message = "Your Dog Breeder Registration application (" + appNum + ") has been forwarded to Chief Veterinary Officer (CVO).";
                break;

            case "INSPECTION_SCHEDULED":
                title = "Inspection Scheduled";
                message = "Your Dog Breeder Registration application (" + appNum + ") has been Inspection Scheduled successfully."
                        + (extraDetails != null && !extraDetails.isBlank() ? " Date: " + extraDetails : "");
                break;

            case "VERIFIED_BY_CVO":
                title = "Application Verified by CVO";
                message = "Your Dog Breeder Registration application (" + appNum + ") has been verified by CVO.";
                break;

            case "REJECTED_BY_CVO":
                title = "Application Rejected by CVO";
                message = "Your Dog Breeder Registration application (" + appNum + ") was rejected by CVO.";
                break;

            case "APPLICATION_APPROVED":
                title = "Application Approved";
                message = "Congratulations! Your Dog Breeder Registration application (" + appNum + ") has been approved.";
                break;

            case "APPLICATION_REJECTED":
                title = "Application Rejected";
                message = "Your Dog Breeder Registration application (" + appNum + ") has been rejected.";
                break;

            default:
                title = "Application Status Updated";
                message = "Status of application " + appNum + " changed to " + newStatus + ".";
                break;
        }

        createNotification(
                recipientUserId,
                "DOG_BREEDER",
                application.getId(),
                title,
                message,
                notificationType
        );
    }

    /**
     * Overloaded method for standard status triggers without extra details.
     */
    @WriteTransactional
    public void triggerStatusNotification(
            DogBreederRegistrationApplication application,
            String newStatus,
            Long recipientUserId) {
        triggerStatusNotification(application, newStatus, recipientUserId, (String) null);
    }
}