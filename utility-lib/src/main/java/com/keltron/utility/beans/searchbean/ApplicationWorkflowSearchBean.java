package com.keltron.utility.beans.searchbean;

import com.keltron.utility.beans.abs.AbstractSearchBean;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApplicationWorkflowSearchBean
        extends AbstractSearchBean {

    private Long applicationId;

    private Long fromStatusId;

    private Long toStatusId;

    private Long actionBy;

    private String moduleName;
    
    private Long id;
}