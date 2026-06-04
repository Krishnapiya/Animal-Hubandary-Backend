package com.keltron.utility.jpa.entity;

import com.keltron.utility.ValidationUtils;
import com.keltron.utility.beans.abs.AbstractDto;
import com.keltron.utility.beans.dto.DistrictDto;
import com.keltron.utility.responses.payload.DropdownPayload;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Entity
@Table(name = "district", schema = "awb")
@NoArgsConstructor
@ToString
public class District extends AbstractEntity {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true)
    private String code;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false)
    private Boolean active = true;

    @Override
    public <K extends AbstractDto> void copyFromDTO(K dto) {

        DistrictDto districtDto = (DistrictDto) dto;

        if (ValidationUtils.isValid(districtDto.getId()))
            this.id = districtDto.getId();

        if (ValidationUtils.isValid(districtDto.getCode()))
            this.code = districtDto.getCode();

        if (ValidationUtils.isValid(districtDto.getName()))
            this.name = districtDto.getName();

        if (districtDto.getActive() != null)
            this.active = districtDto.getActive();
    }

    @Override
    public DistrictDto toDTO() {

        DistrictDto dto = new DistrictDto();

        dto.setId(id);
        dto.setCode(code);
        dto.setName(name);
        dto.setActive(active);

        return dto;
    }

    @Override
    public DropdownPayload<Integer> toDropDownPayload() {

        DropdownPayload<Integer> payload = new DropdownPayload<>();

        payload.setId(id);
        payload.setName(name);

        return payload;
    }

    public District(Integer id) {
        this.id = id;
    }
}