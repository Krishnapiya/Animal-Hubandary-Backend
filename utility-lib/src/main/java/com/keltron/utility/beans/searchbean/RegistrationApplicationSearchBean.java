package com.keltron.utility.beans.searchbean;

import org.springframework.data.domain.Sort;

import com.keltron.utility.beans.abs.AbstractSearchBean;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegistrationApplicationSearchBean
        extends AbstractSearchBean {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String applicationNumber;
    private Long statusId;
    private Integer districtId;

    public RegistrationApplicationSearchBean() {
        dataSort = Sort.by(
                Sort.Order.desc("id"));
    }
}