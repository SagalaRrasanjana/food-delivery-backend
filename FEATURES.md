# New Features — restaurant-service, menu items, order-service

Applied on top of the existing create/read-only backend. Full behavioral detail lives in
each service's docs (`docs/restaurant-service/CLAUDE.md`, `docs/order-service/CLAUDE.md`,
`docs/CLAUDE.md`); this file is the short summary of *what* changed and *why*.

## Cross-cutting

- **JWT now carries `role` and `id`** (auth-service `jwtUtil.generateToken`), so
  restaurant-service and order-service can enforce "only the owner of this resource, or an
  admin" instead of just "any authenticated user." Each service's `JwtAuthenticationFilter`
  extracts these into a `ROLE_<role>` authority (for `@PreAuthorize`) and a `JwtUserDetails`
  (`userId`, `role`) on `Authentication.getDetails()`.
- **Bean Validation** (`spring-boot-starter-validation`) added to restaurant-service and
  order-service; request DTOs now reject blank names, non-positive prices, missing
  quantities, etc. with a 400 instead of silently persisting garbage.
- **`@RestControllerAdvice` error handling** added to both services: not-found → 404,
  ownership/role failures → 403, illegal state (bad transition, duplicate review, cancel
  after PENDING) → 409, bad input → 400, validation failures → 400 with a field-error map.

## restaurant-service

- `PUT` / `DELETE /api/restaurants/{id}` and `PUT /api/menu-items/{id}` — full CRUD, where
  previously only create+read existed.
- `PATCH /api/menu-items/{id}/availability` — toggle sold-out without a full update payload.
- Ownership enforcement: `POST /api/restaurants` derives `ownerId` from the caller's JWT
  (no longer a client-supplied field); all writes require the caller to own the resource or
  be an `ADMIN`.
- `GET /api/restaurants?search=&tag=&isOpen=` — filtering via JPA Specifications.
- `imageUrl` on menu items.
- Ratings/reviews: new `Review` entity, `POST`/`GET /api/restaurants/{id}/reviews`,
  `Restaurant.averageRating`/`ratingCount` maintained as a running average.
- Pagination (`Page<T>`) on `GET /api/restaurants` and `GET /api/menu-items/restaurant/{id}`.

## order-service

- `OrderStatus` enum with an explicit transition graph (`OrderStatus.canTransitionTo`)
  instead of a free-text status string with no validation.
- `PUT /api/orders/{id}/status` now requires the calling restaurant's owner (or `ADMIN`) —
  verified via a Feign call to restaurant-service — instead of any authenticated user.
- `POST /api/orders/{id}/cancel` — customer self-service cancellation, only while `PENDING`.
- `GET /api/orders/restaurant/{id}` — restaurant-facing order queue (paginated), mirroring
  the existing customer-facing `/history`.
- `POST /api/orders/estimate` — prices a cart (subtotal + delivery fee + total) without
  placing an order.
- Delivery info: `deliveryAddress` (required) / `deliveryNotes` (optional) on `OrderRequest`
  and `Order`; `subtotal` and `deliveryFee` (`order.delivery-fee` property) tracked
  separately from `totalAmount`.
- Feign resilience: `RestaurantClient` has a `RestaurantClientFallback`
  (`feign.circuitbreaker.enabled=true`) plus connect/read timeouts, so a down/slow
  restaurant-service returns a clean 503 instead of hanging `placeOrder`.
- Pagination on `GET /api/orders/history`.

## Breaking changes to be aware of

- `GET /api/restaurants`, `GET /api/menu-items/restaurant/{id}`, and
  `GET /api/orders/history` now return a Spring `Page<T>` JSON object (`content`,
  `totalElements`, ...) instead of a bare array.
- `POST /api/orders` requires `deliveryAddress` in the request body.
- `RestaurantRequest` no longer accepts `ownerId` — it's derived from the JWT.
- `PUT /api/orders/{id}/status` and restaurant/menu-item writes now 403 for callers who
  aren't the resource's owner or an `ADMIN` (previously any authenticated user could call
  them).

## Deliberately not done

- **Shared JWT library.** The repo has no parent/aggregator POM; extracting the duplicated
  `JwtAuthenticationFilter`/`JwtUserDetails` into a shared module would mean publishing a
  new artifact each service depends on — a bigger structural change than the features it
  would dedupe. Each service still keeps its own copy (documented in `docs/CLAUDE.md`
  under "Known gaps").
