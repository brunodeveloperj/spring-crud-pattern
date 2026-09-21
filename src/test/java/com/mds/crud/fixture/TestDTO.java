package com.mds.crud.fixture;

public class TestDTO {

  private Long id;
  private String name;
  private Integer capacity;
  private Boolean active;

  public TestDTO() {
  }

  public TestDTO(Long id, String name, Integer capacity, Boolean active) {
    this.id = id;
    this.name = name;
    this.capacity = capacity;
    this.active = active;
  }

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
