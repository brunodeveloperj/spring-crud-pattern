package com.mds.crud.dto.general;

import static com.mds.crud.keys.CrudKeys.TENTH_INDEX;
import static com.mds.crud.keys.CrudKeys.ZERO_INDEX;
import static com.mds.shared.core.pattern.utils.ObjectUtils.nonNull;
import static com.mds.crud.format.FormatValueUtil.convertStringToSort;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

/**
 * Inbound DTO that captures pagination query parameters ({@code _page},
 * {@code _limit}, {@code _sort}) and converts them into a Spring
 * {@link org.springframework.data.domain.Pageable}.
 *
 * @author MDS
 * @since 0.0.1-SNAPSHOT
 */
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class PageableParamDTO {

  @JsonProperty("_limit") private Integer limit;
  @JsonProperty("_page") private Integer pageNumber;
  @JsonProperty("_sort") private String sorts;

  public Pageable convertToPageable() {
    limit = nonNull(limit) ? limit : TENTH_INDEX;
    pageNumber = nonNull(pageNumber) ? pageNumber : ZERO_INDEX;
    return PageRequest.of(pageNumber, limit, convertStringToSort(sorts));
  }

}
