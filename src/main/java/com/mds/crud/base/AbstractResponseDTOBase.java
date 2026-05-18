package com.mds.crud.base;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.mds.crud.dto.general.PageableDTO;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Abstract base for paginated API response DTOs, carrying a
 * {@link PageableDTO} header and a content list.
 *
 * @param <T> the element type held in the content list
 *
 * @author MDS
 * @since 0.0.1-SNAPSHOT
 */
public abstract class AbstractResponseDTOBase<T> {

  /**
   * The pageable information.
   */
  @JsonProperty(value = "_pageable")
  private PageableDTO pageable = new PageableDTO(false);

  /**
   * The content.
   */
  @JsonProperty(value = "_content")
  private List<T> content = new ArrayList<>();

  /**
   * Protected constructor.
   */
  protected AbstractResponseDTOBase() {
  }

  /**
   * Gets the pageable information.
   *
   * @return The pageable information.
   */
  public PageableDTO getPageable() {
    return this.pageable;
  }

  /**
   * Sets the pageable information.
   *
   * @param pageable The pageable information.
   */
  @JsonProperty(value = "_pageable")
  public void setPageable(final PageableDTO pageable) {
    this.pageable = Optional.ofNullable(pageable).orElse(new PageableDTO(false));
  }

  /**
   * Gets the content.
   *
   * @return The content.
   */
  public List<T> getContent() {
    List<T> contentList = new ArrayList<>();
    if (content != null) {
      contentList = new ArrayList<>(content);
    }
    return contentList;
  }

  /**
   * Sets the content.
   *
   * @param content The content.
   */
  @JsonProperty(value = "_content")
  public void setContent(final List<T> content) {
    this.content = Optional.ofNullable(content).orElse(new ArrayList<>());
  }

  /**
   * Adds a register to the content.
   *
   * @param register The register to be added.
   */
  public void addRegisterInContent(T register) {
    if (content == null) {
      content = new ArrayList<>();
    }
    if (register != null) {
      content.add(register);
    }
  }
}
