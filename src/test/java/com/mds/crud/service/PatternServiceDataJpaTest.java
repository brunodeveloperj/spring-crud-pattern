package com.mds.crud.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.mds.crud.dto.general.PageableParamDTO;
import com.mds.crud.fixture.TestApplication;
import com.mds.crud.fixture.TestCrudService;
import com.mds.crud.fixture.TestDTO;
import com.mds.crud.fixture.TestEntity;
import com.mds.crud.fixture.TestRepository;
import com.mds.crud.fixture.TestResponseDTO;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.EmptyResultDataAccessException;

/**
 * Integration coverage for the pattern engine against a real JPA
 * persistence context (H2). The service is wired manually so the test only
 * boots the repository infrastructure.
 */
@SpringBootTest(classes = TestApplication.class)
class PatternServiceDataJpaTest {

  @Autowired
  private TestRepository repository;

  private TestCrudService service;

  @BeforeEach
  void setUp() {
    service = new TestCrudService();
    service.setRepository(repository);
    repository.deleteAll();
  }

  @Test
  void findAllByParamsHonoursPageLimitAndSort() {
    seed(12);

    Map<String, Object> params = new HashMap<>();
    params.put("_page", 1);
    params.put("_limit", 5);
    params.put("_sort", "-name");

    TestResponseDTO response = service.findAllByParams(params);

    assertThat(response.getContent()).hasSize(5);
    assertThat(response.getPageable().getPageNumber()).isEqualTo("1");
    assertThat(response.getPageable().isMoreElements()).isTrue();
    assertThat(response.getPageable().getTotalElements()).isEqualTo("12");
    // Desc order: Item-11..Item-07 on page 0, so page 1 starts at Item-06.
    assertThat(response.getContent().get(0).getName()).isEqualTo("Item-06");
  }

  @Test
  void findAllByFiltersAppliesEqualityPredicateWithPagination() {
    seed(6);
    repository.save(build("Special", 1));

    TestResponseDTO response =
        service.findAllByFilters(
            Map.of("name", "Special"), PageableParamDTO.builder().limit(10).build());

    assertThat(response.getContent()).hasSize(1);
    assertThat(response.getContent().get(0).getName()).isEqualTo("Special");
  }

  @Test
  void insertAndReturnPersistsAndExposesGeneratedId() {
    TestDTO saved = service.insertAndReturn(new TestDTO(null, "Hall", 40, true));

    assertThat(saved.getId()).isNotNull();
    assertThat(service.findDtoById(saved.getId()).getName()).isEqualTo("Hall");
  }

  @Test
  void updateMergesOntoManagedEntityPreservingFieldsOutsideTheDto() {
    TestEntity persisted = repository.save(build("Hall", 40));
    persisted.setInternalCode("INT-9");
    persisted.setOwnerId(42L);
    persisted = repository.saveAndFlush(persisted);
    Long versionBefore = persisted.getVersion();

    TestDTO updated =
        service.updateAndReturn(new TestDTO(persisted.getId(), "Hall renamed", 60, false));

    assertThat(updated.getName()).isEqualTo("Hall renamed");
    assertThat(updated.getCapacity()).isEqualTo(60);

    TestEntity reloaded = repository.findById(persisted.getId()).orElseThrow();
    assertThat(reloaded.getInternalCode()).isEqualTo("INT-9");
    assertThat(reloaded.getOwnerId()).isEqualTo(42L);
    assertThat(reloaded.getVersion()).isNotNull().isGreaterThanOrEqualTo(versionBefore);
  }

  @Test
  void updateRejectsUnknownIdentifiers() {
    assertThatThrownBy(() -> service.update(new TestDTO(999L, "Ghost", 1, true)))
        .hasRootCauseInstanceOf(EmptyResultDataAccessException.class);
  }

  @Test
  void deleteAndExistsByIdFollowEntityState() {
    Long id = service.insert(new TestDTO(null, "Hall", 40, true));

    assertThat(service.existsById(id)).isTrue();
    service.delete(id);
    assertThat(service.existsById(id)).isFalse();
    assertThat(service.findDtoById(id)).isNull();
  }

  private void seed(int amount) {
    for (int i = 0; i < amount; i++) {
      repository.save(build(String.format("Item-%02d", i), i));
    }
  }

  private TestEntity build(String name, int capacity) {
    TestEntity entity = new TestEntity();
    entity.setName(name);
    entity.setCapacity(capacity);
    entity.setActive(true);
    return entity;
  }
}
