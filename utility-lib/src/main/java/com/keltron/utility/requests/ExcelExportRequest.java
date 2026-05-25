package com.keltron.utility.requests;

//ExcelExportRequest.java
import java.util.List;
import java.util.Map;

import com.keltron.utility.requests.bean.XlsConfig;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ExcelExportRequest {
 private int pageNo;
 private int pageSize;
 private boolean xls;
 private List<XlsConfig> xls_config;
 private Map<String, Object> filters; // Optional: If extra filters are sent
}
