package com.mds.crud.format;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;


/**
 * Utility methods for splitting text into {@link java.util.List} structures.
 *
 * @author MDS
 * @since 0.0.1-SNAPSHOT
 */
public class FormatListUtil {

  private static final Logger logger = LoggerFactory.getLogger(FormatListUtil.class);

  private FormatListUtil() {
  }

  /**
   * Split text into List. Split string is used.
   *
   * @param text     Text to split
   * @param splitter Splitter Regex
   * @return Array Split of type List
   */
  public static List<String> splitTextIntoList(String text, String splitter) {

    List<String> arrayString = null;

    try {
      if (StringUtils.hasText(text)) {

        var convert = (text.contains(splitter) ? text.split(Pattern.quote(splitter)) : new String[]{text});

        arrayString = Arrays.asList(convert);
      }
    } catch (Exception e) {
      logger.error("[FormatListUtil] - (splitTextIntoList): Erro ao converter Objeto do tipo String para Array<String>", e);
    }

    return arrayString;
  }
}
