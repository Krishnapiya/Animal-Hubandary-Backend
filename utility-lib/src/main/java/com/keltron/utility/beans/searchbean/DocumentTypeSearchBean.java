package com.keltron.utility.beans.searchbean;

import org.springframework.data.domain.Sort;

import com.keltron.utility.beans.abs.AbstractSearchBean;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DocumentTypeSearchBean extends AbstractSearchBean {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String code;
    private String name;
    private String entityScope;

    public DocumentTypeSearchBean() {
        dataSort = Sort.by(Sort.Order.asc("id"));
    }
}