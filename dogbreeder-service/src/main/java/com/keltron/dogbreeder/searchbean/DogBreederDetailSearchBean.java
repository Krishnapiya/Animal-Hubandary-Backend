package com.keltron.dogbreeder.searchbean;

import lombok.Getter;
import lombok.Setter;
import com.keltron.utility.beans.abs.AbstractSearchBean;

@Getter
@Setter
public class DogBreederDetailSearchBean extends AbstractSearchBean {

    private Long applicationId;
    private String breederName;
    private String city;
    private String contactMobile;
    private String contactEmail;
    private Long id;
}