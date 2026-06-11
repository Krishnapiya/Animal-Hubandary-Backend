package com.keltron.petshop.searchbean;

import com.keltron.utility.beans.abs.AbstractSearchBean;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApplicationDeclarationSearchBean
        extends AbstractSearchBean {

    private Long id;

    private Long applicationId;

    private String declarationPlace;

    private String affidavitDeponentName;
}