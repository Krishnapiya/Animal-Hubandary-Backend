package com.keltron.utility.beans.searchbean;

import org.springframework.data.domain.Sort;

import com.keltron.utility.beans.abs.AbstractSearchBean;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AnimalSpeciesSearchBean extends AbstractSearchBean {

    private Long id;

    private String speciesName;

    private Boolean isActive;

    private String search;

    public AnimalSpeciesSearchBean() {
        dataSort = Sort.by(Sort.Order.asc("id"));
    }
}