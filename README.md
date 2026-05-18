# spring-crud-pattern

> A generic and extensible Spring Boot library that automates CRUD operations using abstract controllers, services, and repositories through Java Generics and Reflection.

**Version:** V1 (`0.0.1-SNAPSHOT`) — not yet released.

---

## Tech Stack

| Dependency | Version |
|---|---|
| Java | 21 (LTS) |
| Spring Boot | 4.0.6 |
| Spring Data JPA | managed by Boot |
| Jakarta Validation | managed by Boot |
| Lombok | managed by Boot |
| `spring-error-pattern` | `${project.version}` |
| `shared-core-lib` | `${project.version}` |

---

## Features

- **Generic CRUD** — full Create / Read / Update / Delete via abstract base classes
- **Abstract Controllers** — `PatternResource` provides ready-to-use REST endpoints
- **Abstract Services** — `PatternService` with lifecycle hooks (`doBeforeInsert`, `doAfterSave`, `doBeforeDelete`, etc.)
- **Reflection-based DI** — `@InjectionDefault` annotation for lazy repository/service resolution
- **Dynamic queries** — JPA `Specification` predicates built from query params
- **Pagination** — `PageableParamDTO` → `Pageable` with sort support
- **DTO ↔ Entity mapping** — automatic property copying with `@IgnoreProperties` control
- **Validation** — Jakarta Bean Validation + existence checks on update/delete

---

## Architecture

```text
PatternApi (interface — REST contract)
  └── PatternResource (controller — delegates to service)
        └── PatternServiceApi / CrudApi (interface — service contract)
              └── PatternService (service — CRUD logic + hooks)
                    └── JpaSpecificationRepository (JpaRepository + JpaSpecificationExecutor)
```

### Base classes

| Class | Layer | Responsibility |
|---|---|---|
| `AbstractResourceBase` | Controller | Lazy service injection via `@InjectionDefault` + reflection |
| `AbstractServiceBase` | Service | Generic type resolution, entity/DTO instantiation, pagination, validation |
| `AbstractEntityBase` | Entity | DTO ↔ Entity mapping, `@IgnoreProperties`, nested entity conversion |
| `AbstractResponseDTOBase` | DTO | Response wrapper with `PageableDTO` + content list |

### Custom annotations

| Annotation | Purpose |
|---|---|
| `@InjectionDefault` | Marks the default `@Autowired` field for reflection-based lazy loading |
| `@IgnoreProperties` | Properties to skip during DTO/Entity copy, scoped by `TypeEnum` (DTO or ENTITY) |

---

## Usage

### 1. Entity

```java
@Entity
public class UserEntity extends AbstractEntityBase<UserEntity, UserDTO> {
    @Id @GeneratedValue
    private Long id;
    private String name;
    private String email;
}
```

### 2. DTO

```java
@Data @NoArgsConstructor @AllArgsConstructor
public class UserDTO {
    private Long id;
    private String name;
    private String email;
}
```

### 3. Response DTO

```java
public class UserResponseDTO extends AbstractResponseDTOBase<UserDTO> { }
```

### 4. Repository

```java
public interface UserRepository extends JpaSpecificationRepository<UserEntity, Long> { }
```

### 5. Service

```java
@Service
public class UserService extends PatternService<UserEntity, UserResponseDTO, UserDTO> {
    @InjectionDefault
    private UserRepository userRepository;

    @Override
    protected void doBeforeInsert(UserDTO dto) throws GeneralException {
        // custom pre-insert logic
    }
}
```

### 6. Controller

```java
@RestController
@RequestMapping("/users")
public class UserController extends PatternResource<UserEntity, UserResponseDTO, UserDTO> {
    @InjectionDefault
    private UserService userService;
}
```

This gives you `GET /users`, `GET /users/filter`, `GET /users/{id}`, `POST /users`, `PUT /users`, `DELETE /users/{id}` out of the box.

---

## Lifecycle Hooks

| Hook | When |
|---|---|
| `doBeforeInsert(dto)` | Before inserting a new entity |
| `doAfterInsert(dto, id)` | After inserting |
| `doBeforeUpdate(dto)` | Before updating |
| `doAfterUpdate(dto, id)` | After updating |
| `doBeforeSave(entity, isUpdate)` | Before `repository.save()` |
| `doAfterSave(entity, isUpdate)` | After `repository.save()` |
| `doBeforeDelete(id)` | Before deleting |
| `doAfterDelete(id)` | After deleting |

All hooks are no-op by default — override in your service subclass.

---

## Dynamic Queries

`GET /users/filter?name=John&_page=0&_limit=10&_sort=name,asc`

Query params (excluding `_page`, `_limit`, `_sort`) are automatically converted to JPA `Specification` predicates using `AbstractServiceBase.validateParams()`.

---

## Dead code (pending cleanup)

The following classes under `com.mds.crud.utils.*` and `com.mds.crud.interfaces.*` are now unused after migration to `shared-core-lib` equivalents:

- `CollectionUtils`, `FunctionUtils`, `ObjectUtils`, `ReflectionUtils`
- `ExecutableObject`, `ExecutableVoid`
- `FunctionPatternException` (use `ExecutableException` from comm-pattern)
- `EnumerationPattern` (only used by `TypeEnum` — over-abstraction)
