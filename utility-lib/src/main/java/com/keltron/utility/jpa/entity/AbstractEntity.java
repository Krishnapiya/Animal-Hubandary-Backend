package com.keltron.utility.jpa.entity;

import java.io.Serializable;
import java.sql.Timestamp;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.keltron.utility.beans.abs.AbstractDto;
import com.keltron.utility.responses.payload.DropdownPayload;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Getter(AccessLevel.PUBLIC)
@Setter(AccessLevel.PUBLIC)
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class AbstractEntity implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = 3991306286865338290L;

	@CreatedBy
	@Column(name = "created_by")
	protected String createdBy;

	@CreatedDate
	@Column(name = "created_at", nullable = false, insertable = false)
	protected Timestamp createdAt;

	@LastModifiedBy
	@Column(name = "last_modified_by")
	protected String lastModifiedBy;

	@LastModifiedDate
	@Column(name = "last_modified_at")
	protected Timestamp lastModifiedAt;


	public abstract <K extends AbstractDto> void copyFromDTO(K dto);

	public abstract <K extends AbstractDto> K toDTO();

	public DropdownPayload<?> toDropDownPayload() {
		return null;
	}

	public <K extends AbstractDto> K toMinDTO() {
		return null;
	}
}
