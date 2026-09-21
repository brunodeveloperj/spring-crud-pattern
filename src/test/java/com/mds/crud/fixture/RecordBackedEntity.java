package com.mds.crud.fixture;

import com.mds.crud.base.AbstractEntityBase;

/**
 * Entity whose DTO is an immutable record — proves the canonical-constructor
 * copy path used by hexagonal adapters that keep records in their ports.
 */
public class RecordBackedEntity extends AbstractEntityBase<RecordBackedEntity, TestRecordDTO> {

  private Long id;
  private String name;
  private Integer capacity;
  private Boolean active;

  @Override
  public RecordBackedEntity getInstance() {
    return this;
  }

  @Override
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Integer getCapacity() {
    return capacity;
  }

  public void setCapacity(Integer capacity) {
    this.capacity = capacity;
  }

  public Boolean getActive() {
    return active;
  }

  public void setActive(Boolean active) {
    this.active = active;
  }
}
