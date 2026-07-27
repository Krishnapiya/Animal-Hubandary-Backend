package com.keltron.utility.beans.dto;

import org.springframework.http.HttpMethod;

import com.keltron.utility.beans.abs.AbstractDto;
import com.keltron.utility.jpa.entity.Notification;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class NotificationDto extends AbstractDto {

    private static final long serialVersionUID = 1L;

    private Long id;

    private Long userId;

    private String module;

    private Long referenceId;

    private String title;

    private String message;

    private String notificationType;

    private Boolean isRead;
    
    private Long applicationId;

    private String applicationNumber;

    private String district;

    private String office;

    private String applicationStatus;

    @Override
    public Notification toEntity() {

        Notification notification = new Notification();

        notification.setId(id);
        notification.setUserId(userId);
        notification.setModule(module);
        notification.setReferenceId(referenceId);
        notification.setTitle(title);
        notification.setMessage(message);
        notification.setNotificationType(notificationType);
        notification.setIsRead(isRead);

        return notification;
    }

    @Override
    public boolean isValid(HttpMethod httpMethod) {

        return true;
    }
}