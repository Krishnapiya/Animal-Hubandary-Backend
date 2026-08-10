package com.keltron.citizen.searchbean;

import org.springframework.data.domain.Sort;

import com.keltron.utility.beans.abs.AbstractSearchBean;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ComplaintRegistrationSearchBean
        extends AbstractSearchBean {

    private static final long serialVersionUID = 1L;

    private Long id;

    private String complaintNumber;

    private Long citizenUserId;

    private String status;

    public ComplaintRegistrationSearchBean() {

        dataSort = Sort.by(
                Sort.Order.desc("id"));
    }
}