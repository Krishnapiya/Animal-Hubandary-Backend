package com.keltron.utility.jpa.entity;

import com.keltron.utility.ValidationUtils;
import com.keltron.utility.beans.abs.AbstractDto;
import com.keltron.utility.beans.dto.OfficeDto;
import com.keltron.utility.responses.payload.DropdownPayload;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Entity
@Table(name = "office", schema = "master")
@ToString
@NoArgsConstructor
public class Office extends AbstractEntity {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true, nullable = false)
    private Integer id;

    @Column(name = "office_type", nullable = false, length = 100)
    private String officeType;

    @Column(nullable = false, length = 255)
    private String name;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "parent_id")
    private Office parent;

    @Override
    public <K extends AbstractDto> void copyFromDTO(K dto) {
        OfficeDto d = (OfficeDto) dto;
        if (ValidationUtils.isValid(d.getId())) {
            id = d.getId();
        }
        if (ValidationUtils.isValid(d.getOfficeType())) {
            officeType = d.getOfficeType();
        }
        if (ValidationUtils.isValid(d.getName())) {
            name = d.getName();
        }
        if (ValidationUtils.isValid(d.getParentId())) {
            if (id != null && d.getParentId().equals(id)) {
                parent = null;
            } else {
                Office ref = new Office();
                ref.setId(d.getParentId());
                parent = ref;
            }
        } else {
            parent = null;
        }
    }

    @Override
    public OfficeDto toDTO() {
        OfficeDto dto = new OfficeDto();
        dto.setId(id);
        dto.setOfficeType(officeType);
        dto.setName(name);
        if (parent != null) {
            dto.setParentId(parent.getId());
            dto.setParentName(parent.getName());
        } else {
            dto.setParentId(null);
            dto.setParentName(null);
        }
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
