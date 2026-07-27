package com.keltron.utility.jpa.entity;

import com.keltron.utility.ValidationUtils;
import com.keltron.utility.beans.abs.AbstractDto;
import com.keltron.utility.beans.dto.NotificationDto;
import com.keltron.utility.responses.payload.DropdownPayload;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Entity
@NoArgsConstructor
@ToString
@Table(name = "notification", schema = "master")
public class Notification extends AbstractEntity {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "module")
    private String module;

    @Column(name = "reference_id")
    private Long referenceId;

    @Column(name = "title")
    private String title;

    @Column(name = "message")
    private String message;

    @Column(name = "notification_type")
    private String notificationType;

    @Column(name = "is_read")
    private Boolean isRead;

    public Notification(Long id) {
        this.id = id;
    }

    @Override
    public <K extends AbstractDto> void copyFromDTO(K dto) {

        NotificationDto notificationDto =
                (NotificationDto) dto;

        if (ValidationUtils.isValid(notificationDto.getId()))
            this.id = notificationDto.getId();

        this.userId = notificationDto.getUserId();
        this.module = notificationDto.getModule();
        this.referenceId = notificationDto.getReferenceId();
        this.title = notificationDto.getTitle();
        this.message = notificationDto.getMessage();
        this.notificationType = notificationDto.getNotificationType();
        this.isRead = notificationDto.getIsRead();
    }

    @Override
    public NotificationDto toDTO() {

        NotificationDto dto =
                new NotificationDto();

        dto.setId(id);
        dto.setUserId(userId);
        dto.setModule(module);
        dto.setReferenceId(referenceId);
        dto.setTitle(title);
        dto.setMessage(message);
        dto.setNotificationType(notificationType);
        dto.setIsRead(isRead);

        return dto;
    }

    @Override
    public DropdownPayload<Long> toDropDownPayload() {

        DropdownPayload<Long> payload =
                new DropdownPayload<>();

        payload.setId(id);
        payload.setName(title);

        return payload;
    }
}