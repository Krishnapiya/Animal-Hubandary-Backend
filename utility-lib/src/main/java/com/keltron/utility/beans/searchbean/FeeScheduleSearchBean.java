package com.keltron.utility.beans.searchbean;

import org.springframework.data.domain.Sort;

import com.keltron.utility.beans.abs.AbstractSearchBean;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FeeScheduleSearchBean extends AbstractSearchBean {

    private static final long serialVersionUID = 1L;
    private Integer id;
    private String entityType;
    private String feeKind;

    public FeeScheduleSearchBean() {
        dataSort = Sort.by(Sort.Order.asc("id"));
    }
}