package com.mds.crud.dto.general;

import static com.mds.shared.core.pattern.utils.FunctionUtils.executableObjectNullSafe;

import com.mds.crud.dto.utility.PageableUtilityDTO;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;

import com.mds.crud.format.FormatValueUtil;
import org.springframework.data.domain.Pageable;

/**
 * Serializable DTO that carries pagination metadata returned alongside
 * every paged API response.
 *
 * @author MDS
 * @since 0.0.1-SNAPSHOT
 */
@JsonInclude(value = Include.NON_EMPTY)
public class PageableDTO implements Serializable {

  private static final long serialVersionUID = -3010475309429583191L;

  @JsonProperty("_moreElements")
  private boolean moreElements;

  @JsonProperty("_limit")
  private String limit;

  @JsonProperty("_offset")
  private String offSet;

  @JsonProperty("_pageNumber")
  private String pageNumber;

  @JsonProperty("_pageElements")
  private String pageElements;

  @JsonProperty("_totalPages")
  private String totalPages;

  @JsonProperty("_totalElements")
  private String totalElements;

  public PageableDTO() {
  }

  public PageableDTO(boolean moreElements, String limit, String offSet, String pageNumber, String pageElements, String totalPages, String totalElements) {
    this.moreElements = moreElements;
    this.limit = limit;
    this.offSet = offSet;
    this.pageNumber = pageNumber;
    this.pageElements = pageElements;
    this.totalPages = totalPages;
    this.totalElements = totalElements;
  }

  public PageableDTO(final boolean moreElements) {
    this.moreElements = moreElements;
  }

  //*************************** DDD_METHODS ***************************
  public static PageableDTO compute(Long count, int sum, Pageable pageable) {

    return executableObjectNullSafe(() -> {
      PageableUtilityDTO pageableUtility = PageableUtilityDTO.generate(count, pageable);

      PageableDTO pageableDTO = new PageableDTO();
      pageableDTO.setMoreElements(pageableUtility.isPageableMoreElements());
      pageableDTO.setLimit(pageableUtility.getPageablePageSize());
      pageableDTO.setOffSet(pageableUtility.getOffSetMath());
      pageableDTO.setPageNumber(pageableUtility.getPageablePageNumber());
      pageableDTO.setTotalPages(pageableUtility.getTotalPagesMath());
      pageableDTO.setPageElements(FormatValueUtil.convertObjectToString(sum));
      pageableDTO.setTotalElements(FormatValueUtil.convertObjectToString(count));

      return pageableDTO;
    }, PageableDTO::new);

  }

  public boolean isMoreElements() {
    return moreElements;
  }

  public void setMoreElements(boolean moreElements) {
    this.moreElements = moreElements;
  }

  public String getLimit() {
    return limit;
  }

  public void setLimit(String limit) {
    this.limit = limit;
  }

  public String getOffSet() {
    return offSet;
  }

  public void setOffSet(String offSet) {
    this.offSet = offSet;
  }

  public String getPageNumber() {
    return pageNumber;
  }

  public void setPageNumber(String pageNumber) {
    this.pageNumber = pageNumber;
  }

  public String getPageElements() {
    return pageElements;
  }

  public void setPageElements(String pageElements) {
    this.pageElements = pageElements;
  }

  public String getTotalPages() {
    return totalPages;
  }

  public void setTotalPages(String totalPages) {
    this.totalPages = totalPages;
  }

  public String getTotalElements() {
    return totalElements;
  }

  public void setTotalElements(String totalElements) {
    this.totalElements = totalElements;
  }


}