# spring-crud-pattern

A generic and extensible Spring Boot library that automates CRUD operations using abstract controllers, services, repositories, Java Generics and Reflection.

## Features

- Generic CRUD
- Abstract Controllers
- Abstract Services
- Reflection
- Dynamic queries
- Pagination
- DTO mapping
- Validation support

---

## Example

public class UserController extends AbstractCrudController<
UserDTO,
UserEntity,
Long>{

}

---

## Architecture

Controller
↓
AbstractCrudController
↓
AbstractCrudService
↓
Repository
