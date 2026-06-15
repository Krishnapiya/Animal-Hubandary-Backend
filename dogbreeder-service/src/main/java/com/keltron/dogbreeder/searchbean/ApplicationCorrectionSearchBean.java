package com.keltron.dogbreeder.searchbean;

import lombok.Getter;
import lombok.Setter;
import com.keltron.utility.beans.abs.AbstractSearchBean;

@Getter
@Setter
public class ApplicationCorrectionSearchBean extends AbstractSearchBean {

    private Long id;
    private Long applicationId;
    private Long submittedBy;
    private String correctionSummary;
}