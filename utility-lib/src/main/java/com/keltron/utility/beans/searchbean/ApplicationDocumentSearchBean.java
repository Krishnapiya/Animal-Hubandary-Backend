package com.keltron.utility.beans.searchbean;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;

import com.keltron.utility.beans.abs.AbstractSearchBean;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApplicationDocumentSearchBean extends AbstractSearchBean {

    private static final long serialVersionUID = 1L;

    private Long id;

    // Single dropdown support
    private Long applicationId;
    private Long documentTypeId;
    private Long uploadedBy;

    // Multiple dropdown support
    private List<Long> applicationIds;
    private List<Long> documentTypeIds;
    private List<Long> uploadedByIds;

    private String fileName;
    private String filePath;
    private String mimeType;
    private String search;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate uploadedAtFrom;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate uploadedAtTo;

    private String sortBy = "id";
    private String sortOrder = "asc";

    private static final Set<String> ALLOWED_SORT_FIELDS = Set.of(
            "id",
            "fileName",
            "filePath",
            "mimeType",
            "uploadedAt",
            "fileSizeBytes"
    );

    public ApplicationDocumentSearchBean() {
        dataSort = Sort.by(Sort.Order.asc("id"));
    }

    public Sort getSort() {
        return "desc".equalsIgnoreCase(sortOrder)
                ? Sort.by(Sort.Order.desc(sortBy))
                : Sort.by(Sort.Order.asc(sortBy));
    }

    public void setSortBy(String sortBy) {
        if (ALLOWED_SORT_FIELDS.contains(sortBy)) {
            this.sortBy = sortBy;
        } else {
            this.sortBy = "id";
        }

        super.setDataSort(getSort());
    }

    public void setSortOrder(String sortOrder) {
        this.sortOrder = sortOrder;
        super.setDataSort(getSort());
    }
}