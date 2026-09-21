package com.mds.crud.fixture;

import com.mds.crud.annotation.InjectionDefault;
import com.mds.crud.service.PatternService;

public class TestCrudService extends PatternService<TestEntity, TestResponseDTO, TestDTO> {

  @InjectionDefault
  private TestRepository repository;

  public void setRepository(TestRepository repository) {
    this.repository = repository;
  }
}
