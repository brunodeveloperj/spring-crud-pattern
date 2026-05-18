package com.mds.crud.format;

import static com.mds.crud.keys.CrudKeys.MINUS_SIGNAL;
import static com.mds.crud.keys.CrudKeys.STR_COMMA;
import static com.mds.crud.keys.CrudKeys.ZERO_INDEX;

import java.util.ArrayList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;
import org.springframework.util.StringUtils;

/**
 * Utility methods for converting objects to {@link String} and building
 * Spring {@link Sort} instances from query-string values.
 *
 * @author MDS
 * @since 0.0.1-SNAPSHOT
 */
public final class FormatValueUtil {

  private static final Logger logger = LoggerFactory.getLogger(FormatValueUtil.class);

  private FormatValueUtil() {
  }

  public static String convertObjectToString(Object value) {
    String response = null;

    try {
      if (value != null) {
        response = String.valueOf(value);
      }
    } catch (Exception e) {
      logger.error("[FormatValueUtil] - (convertObjectToString): Erro ao converter o objeto da query para String", e);
    }

    return response;
  }

  public static Sort convertStringToSort(String sorts) {
    Sort sort = Sort.by("id");
    if (StringUtils.hasText(sorts)) {
      var joinSort = Sort.by(new ArrayList<>());
      for (String s : FormatListUtil.splitTextIntoList(sorts, STR_COMMA)) {
        joinSort = buildSort(s, String.valueOf(s.charAt(ZERO_INDEX)), joinSort);
      }
      sort = joinSort;
    }
    return sort;
  }

  private static Sort buildSort(String param, String signal, Sort sort) {
    param = RegexUtil.removeSpecialCharacters(param);
    return organizeSort(signal, Sort.by(param), sort);
  }

  private static Sort organizeSort(String signal, Sort sortSource, Sort sortTarget) {
    if (signal.contains(MINUS_SIGNAL)) {
      sortSource = sortSource.descending();
    }
    return sortTarget.and(sortSource);
  }
}
