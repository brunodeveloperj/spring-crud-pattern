package com.mds.crud.keys;

import java.util.List;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Utility class holding constant keys used to extract pagination parameters
 * ({@code _page}, {@code _limit}, {@code _sort}) from HTTP query strings.
 *
 * <p>The {@link #PAGEABLE_PARAMS} list aggregates all recognised keys so that
 * they can be filtered out when building JPA {@code Specification} predicates.
 *
 * @author MDS
 * @since 0.0.1-SNAPSHOT
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class PageableKeys {

  public static final String PAGE = "_page";
  public static final String LIMIT = "_limit";
  public static final String SORT = "_sort";
  public static final List<String> PAGEABLE_PARAMS = List.of(PAGE, LIMIT, SORT);

}
