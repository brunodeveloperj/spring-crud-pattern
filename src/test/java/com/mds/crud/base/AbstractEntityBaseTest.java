package com.mds.crud.base;

import static org.assertj.core.api.Assertions.assertThat;

import com.mds.crud.fixture.RecordBackedEntity;
import com.mds.crud.fixture.TestDTO;
import com.mds.crud.fixture.TestEntity;
import com.mds.crud.fixture.TestRecordDTO;
import org.junit.jupiter.api.Test;

class AbstractEntityBaseTest {

  @Test
  void copiesEntityToBeanDtoIncludingPrimitiveField() {
    TestEntity entity = new TestEntity();
    entity.setId(7L);
    entity.setName("Hall");
    entity.setCapacity(40);
    entity.setActive(true);
    entity.setInternalCode("INT-1");

    TestDTO dto = entity.copyPropertiesToDTO();

    assertThat(dto.getId()).isEqualTo(7L);
    assertThat(dto.getName()).isEqualTo("Hall");
    assertThat(dto.getCapacity()).isEqualTo(40);
    assertThat(dto.getActive()).isTrue();
  }

  @Test
  void copiesWrapperValuesIntoPrimitiveEntityField() {
    TestEntity entity = new TestEntity();
    TestDTO dto = new TestDTO(null, "Hall", 40, true);

    entity.copyPropertiesToEntity(dto);

    assertThat(entity.getCapacity()).isEqualTo(40);
    assertThat(entity.getName()).isEqualTo("Hall");
    assertThat(entity.getActive()).isTrue();
  }

  @Test
  void skipsNullDtoValuesWhenCopyingToEntity() {
    TestEntity entity = new TestEntity();
    entity.setName("Hall");
    entity.setCapacity(40);
    entity.setInternalCode("INT-1");

    entity.copyPropertiesToEntity(new TestDTO(null, null, null, null));

    assertThat(entity.getName()).isEqualTo("Hall");
    assertThat(entity.getCapacity()).isEqualTo(40);
    assertThat(entity.getInternalCode()).isEqualTo("INT-1");
  }

  @Test
  void buildsRecordDtoThroughCanonicalConstructor() {
    RecordBackedEntity entity = new RecordBackedEntity();
    entity.setId(3L);
    entity.setName("Gym");
    entity.setCapacity(20);
    entity.setActive(true);

    TestRecordDTO dto = entity.copyPropertiesToDTO();

    assertThat(dto.id()).isEqualTo(3L);
    assertThat(dto.name()).isEqualTo("Gym");
    assertThat(dto.capacity()).isEqualTo(20);
    assertThat(dto.active()).isTrue();
  }

  @Test
  void buildsRecordDtoWithDefaultsForMissingEntityProperties() {
    RecordBackedEntity entity = new RecordBackedEntity();

    TestRecordDTO dto = entity.copyPropertiesToDTO();

    assertThat(dto.id()).isNull();
    assertThat(dto.name()).isNull();
    assertThat(dto.capacity()).isNull();
    assertThat(dto.active()).isNull();
  }
}
