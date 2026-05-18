package com.mds.crud.dto.utility;

import static com.mds.crud.keys.CrudKeys.ONE_INDEX;
import static com.mds.crud.keys.CrudKeys.ZERO_INDEX;

import com.mds.crud.format.FormatValueUtil;
import org.springframework.data.domain.Pageable;


/**
 * Internal helper that performs the arithmetic needed to derive pagination
 * metadata (total pages, offset, more-elements flag) from a page size,
 * page number and total element count.
 *
 * @author MDS
 * @since 0.0.1-SNAPSHOT
 */
public class PageableUtilityDTO {

  private final int pageablePageSize;
  private final int pageablePageNumber;
  private final Long resultOfDivision;
  private final Long modOfDivision;
  private final Long offSetMath;
  private final Long totalPagesMath;
  private final boolean pageableMoreElements;

  PageableUtilityDTO(int pageablePageSize, int pageablePageNumber, Long resultOfDivision, Long modOfDivision, Long offSetMath) {
    this.pageablePageSize = pageablePageSize;
    this.pageablePageNumber = pageablePageNumber;
    this.resultOfDivision = resultOfDivision;
    this.modOfDivision = modOfDivision;
    this.offSetMath = offSetMath;
    this.totalPagesMath = calculateTotalPages();
    this.pageableMoreElements = calculateMoreElements();
  }

  public static PageableUtilityDTO generate(Long amountOfElements, Pageable pageable) {
    final int pageSize = pageable.getPageSize();
    final int pageNumber = pageable.getPageNumber();

    return new PageableUtilityDTO(pageSize, pageNumber, (amountOfElements / pageSize), (amountOfElements % pageSize), (((long) pageNumber * pageSize) + 1));
  }

  private Long calculateTotalPages() {
    Long totalPagesResult = resultOfDivision;

    if (modOfDivision > ZERO_INDEX) {
      totalPagesResult = resultOfDivision + ONE_INDEX;
    }

    return totalPagesResult;
  }

  boolean calculateMoreElements() {
    return modOfDivision < ZERO_INDEX || (pageablePageNumber + ONE_INDEX) < totalPagesMath;
  }

  public String getPageablePageSize() {
    return FormatValueUtil.convertObjectToString(pageablePageSize);
  }

  public String getPageablePageNumber() {
    return FormatValueUtil.convertObjectToString(pageablePageNumber);
  }

  public String getResultOfDivision() {
    return FormatValueUtil.convertObjectToString(resultOfDivision);
  }

  public String getModOfDivision() {
    return FormatValueUtil.convertObjectToString(modOfDivision);
  }

  public String getOffSetMath() {
    return FormatValueUtil.convertObjectToString(offSetMath);
  }

  public String getTotalPagesMath() {
    return FormatValueUtil.convertObjectToString(totalPagesMath);
  }

  public boolean isPageableMoreElements() {
    return pageableMoreElements;
  }

}
