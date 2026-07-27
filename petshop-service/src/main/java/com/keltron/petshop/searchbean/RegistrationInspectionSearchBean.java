package com.keltron.petshop.searchbean;

import org.springframework.data.domain.Sort;

import com.keltron.utility.beans.abs.AbstractSearchBean;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegistrationInspectionSearchBean
        extends AbstractSearchBean {

    private static final long serialVersionUID = 1L;

    private Long id;

    private Long applicationId;

    private String status;

    public RegistrationInspectionSearchBean() {

        dataSort = Sort.by(
                Sort.Order.desc("id"));
    }
}