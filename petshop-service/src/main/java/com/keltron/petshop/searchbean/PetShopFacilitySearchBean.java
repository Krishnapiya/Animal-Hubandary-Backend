package com.keltron.petshop.searchbean;

import lombok.Getter;
import lombok.Setter;
import com.keltron.utility.beans.abs.AbstractSearchBean;
@Getter
@Setter
public class PetShopFacilitySearchBean extends AbstractSearchBean {

    private Long id;

    private Long petShopDetailId;
}

