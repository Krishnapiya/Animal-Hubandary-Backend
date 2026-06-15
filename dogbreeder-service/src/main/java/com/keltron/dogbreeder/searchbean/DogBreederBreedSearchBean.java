package com.keltron.dogbreeder.searchbean;

import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Sort;

import com.keltron.utility.beans.abs.AbstractSearchBean;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DogBreederBreedSearchBean extends AbstractSearchBean {

    private static final long serialVersionUID = 1L;

    private Long id;

    private List<Long> dogBreederDetailId;

    private String breedName;

    private Integer dogCount;

    private String search;

    private String sortBy = "id";

    private String sortOrder = "asc";

    private static final Set<String> ALLOWED_SORT_FIELDS = Set.of(
        "id",
        "breedName",
        "dogCount"
    );

    public DogBreederBreedSearchBean() {
        dataSort = Sort.by(Sort.Order.asc(sortBy));
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