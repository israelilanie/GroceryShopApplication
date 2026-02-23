# Testing Guide (Target: 80%+ coverage)

This project can be tested in layers. The best strategy is:

1. **Unit tests** for business rules in services (`Mockito` for repositories and collaborators).
2. **Controller tests** with `MockMvc` to validate endpoints and request validation.
3. **Repository integration tests** with H2 for custom queries.
4. **End-to-end tests** with Selenium for user flow verification.

## 1) Unit testing with Mockito

Focus first on `GroceryShopService` because it contains business logic:

- item creation initializes inventory to 0.
- order creation decreases stock.
- insufficient stock throws exception.
- order status updates timestamp.

Pattern:

- `@ExtendWith(MockitoExtension.class)`
- `@Mock` repositories
- `@InjectMocks` service under test
- use `when(...)` + `thenReturn(...)`
- verify side effects with `verify(...)`

## 2) Controller testing with MockMvc

Use `MockMvcBuilders.standaloneSetup(...)` to test each controller quickly:

- happy path status codes (`200`, `201`).
- invalid payload validation (`400`) and error fields from `ApiExceptionHandler`.

Recommended controllers:

- `GroceryItemController`
- `InventoryController`
- `OrderController`

## 3) Integration tests (next step)

Add `@DataJpaTest` tests for repositories, for example:

- `InventoryRepository.findByGroceryItem(...)`
- order search queries

Integration tests run against H2 and ensure JPA mappings really work.

## 4) Selenium tests (next step)

For Selenium, start with 3 smoke tests:

- browse catalog page and assert products appear.
- add item to cart and verify cart total updates.
- place order and verify success message/order number.

Keep Selenium tests in a separate suite and run them in CI nightly (they are slower).

## Coverage workflow

- Aim for **70%+** from unit + controller tests first.
- Add repository integration tests to push to **80%+**.
- Use Selenium for confidence in real user flows, not for boosting raw coverage.

