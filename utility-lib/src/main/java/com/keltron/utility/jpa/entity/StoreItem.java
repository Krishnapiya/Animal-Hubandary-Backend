package com.keltron.utility.jpa.entity;

import com.keltron.utility.ValidationUtils;
import com.keltron.utility.beans.abs.AbstractDto;
import com.keltron.utility.beans.dto.StoreItemDto;
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
@Entity
@Table(name = "store_item", schema = "master")
@ToString
@NoArgsConstructor
public class StoreItem extends AbstractEntity {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true, nullable = false)
    private Integer id;

    @Column(nullable = false, length = 200)
    private String name;

    @Override
    public <K extends AbstractDto> void copyFromDTO(K dto) {
        StoreItemDto d = (StoreItemDto) dto;
        if (ValidationUtils.isValid(d.getId())) {
            id = d.getId();
        }
        if (ValidationUtils.isValid(d.getName())) {
            name = d.getName();
        }
    }

    @Override
    public StoreItemDto toDTO() {
        StoreItemDto dto = new StoreItemDto();
        dto.setId(id);
        dto.setName(name);
        return dto;
    }

    @Override
    public DropdownPayload<Integer> toDropDownPayload() {
        DropdownPayload<Integer> payload = new DropdownPayload<>();
        payload.setId(id);
        payload.setName(name);
        return payload;
    }
}
