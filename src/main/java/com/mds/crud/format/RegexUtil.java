package com.mds.crud.format;

import com.mds.crud.keys.CrudKeys;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

/**
 * Utility methods for regex-based text manipulation (matcher creation,
 * special-character removal).
 *
 * @author MDS
 * @since 0.0.1-SNAPSHOT
 */
public class RegexUtil {

  private static final Logger logger = LoggerFactory.getLogger(RegexUtil.class);
  private static final String ALPHANUMERIC = "[^a-zA-Z.]";

  private RegexUtil() {
  }

  /**
   * <pre>
   *   Return Matcher Object according regex and text.
   * </pre>
   *
   * @param regex Regex String
   * @param text  Text to apply regex
   * @return Matcher
   */
  public static Matcher createMatcher(String regex, String text) {
    final Pattern pattern = Pattern.compile(regex);
    return pattern.matcher(text);
  }

  /**
   * Removes all non-alphanumeric characters (except {@code .}) from the
   * given string.
   *
   * @param value the input string
   * @return the sanitised string, or an empty string when input is blank
   */
  public static String removeSpecialCharacters(String value) {
    try {
      value = StringUtils.hasText(value) ? createMatcher(ALPHANUMERIC, value).replaceAll(CrudKeys.STR_EMPTY) : CrudKeys.STR_EMPTY;
    } catch (Exception e) {
      logger.error("[RegexUtil] - (removeSpecialCharacters): Error ao remover caracteres especiais.");
    }
    return value;
  }
}
