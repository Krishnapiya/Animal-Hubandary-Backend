package com.keltron.utility.jpa.entity;


import com.keltron.utility.ValidationUtils;
import com.keltron.utility.beans.abs.AbstractDto;
import com.keltron.utility.beans.dto.RoleMasterDto;
import com.keltron.utility.responses.payload.DropdownPayload;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Table(name = "role_master", schema = "master")
@Entity
@ToString
@NoArgsConstructor
public class RoleMaster extends AbstractEntity {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(unique = true, nullable = false)
	private Integer id;

	@Column(name = "role_name", nullable = false)
	private String roleName;

	@Override
	public <K extends AbstractDto> void copyFromDTO(K dto) {
		RoleMasterDto roleMasterDto = (RoleMasterDto) dto;
		if (ValidationUtils.isValid(roleMasterDto.getId()))
			id = roleMasterDto.getId();
		if (ValidationUtils.isValid(roleMasterDto.getRoleName()))
			roleName = roleMasterDto.getRoleName();

	}

	@SuppressWarnings("unchecked")
	@Override
	public RoleMasterDto toDTO() {
		RoleMasterDto dto = new RoleMasterDto();
		dto.setId(id);
		dto.setRoleName(roleName);
		return dto;

	}

	@Override
	public DropdownPayload<Integer> toDropDownPayload() {
		// TODO Auto-generated method stub
		DropdownPayload<Integer> payLoad = new DropdownPayload<Integer>();
		payLoad.setId(id);
		payLoad.setName(roleName);
		return payLoad;
	}

	 public RoleMaster(Integer id) {
	        this.id = id;
	    }


}

