package com.keltron.utility.beans.searchbean;

import lombok.Getter;
import lombok.Setter;
import com.keltron.utility.beans.abs.AbstractSearchBean;

@Getter
@Setter
public class PetShopDetailSearchBean extends AbstractSearchBean {

    private Long applicationId;
    private String shopName;
    private String ownerName;
    private String city;
    private String contactMobile;
    private String contactEmail;
    private Long id;
}