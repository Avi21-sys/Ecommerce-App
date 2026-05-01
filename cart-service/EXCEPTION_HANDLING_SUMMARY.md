# Cart-Service Exception Handling Implementation

## Overview
All custom exceptions, error response DTO, and global exception handler have been successfully implemented for the Cart-Service.

## Files Created

### 1. Exception Classes (exception package)
**Location:** `src/main/java/com/ecommerce/cart_service/exception/`

#### CartNotFoundException.java
- Custom exception for cart-related not found scenarios
- Extends RuntimeException
- Used when cart items don't exist or user is unauthorized

#### InsufficientStockException.java
- Custom exception for stock-related errors
- Extends RuntimeException
- Ready for future inventory validation features

### 2. Error Response DTO (dto package)
**Location:** `src/main/java/com/ecommerce/cart_service/dto/ErrorResponse.java`

Features:
- `status`: HTTP status code
- `message`: Error message
- `timestamp`: When the error occurred
- `path`: Request path
- `fieldErrors`: Map of field validation errors

### 3. Global Exception Handler (exception package)
**Location:** `src/main/java/com/ecommerce/cart_service/exception/GlobalExceptionHandler.java`

Handlers implemented:
- **CartNotFoundException** → HTTP 404 Not Found
- **InsufficientStockException** → HTTP 409 Conflict
- **MethodArgumentNotValidException** → HTTP 400 Bad Request (with field error map)
- **JwtException** → HTTP 401 Unauthorized
- **Exception** → HTTP 500 Internal Server Error

### 4. Updated CartService
**Location:** `src/main/java/com/ecommerce/cart_service/service/CartService.java`

Changes:
- Added import for CartNotFoundException
- Replaced generic RuntimeException with CartNotFoundException in remove() method:
  - "Item not found" → CartNotFoundException with detailed message
  - "Unauthorized" → CartNotFoundException with user context

## Exception Flow

1. When a cart item is not found:
   ```
   throw new CartNotFoundException("Cart item with id " + id + " not found")
   ↓
   GlobalExceptionHandler catches it
   ↓
   Returns HTTP 404 with ErrorResponse JSON
   ```

2. When user tries to access another user's cart item:
   ```
   throw new CartNotFoundException("Unauthorized: Cart item does not belong to user ...")
   ↓
   GlobalExceptionHandler catches it
   ↓
   Returns HTTP 404 with ErrorResponse JSON
   ```

## Response Format

All exceptions return JSON in the following format:
```json
{
  "status": 404,
  "message": "Cart item with id 123 not found",
  "timestamp": "2026-05-01T14:30:00.123456",
  "path": "/api/cart/123"
}
```

For validation errors (400):
```json
{
  "status": 400,
  "message": "Validation failed",
  "timestamp": "2026-05-01T14:30:00.123456",
  "path": "/api/cart",
  "fieldErrors": {
    "price": "must be greater than 0",
    "quantity": "must be positive"
  }
}
```

## Dependencies
- All required dependencies are already in pom.xml (Spring Web, Spring Security, JJWT, Lombok)
- No additional dependencies needed

## Testing
The exceptions will be automatically caught by Spring's exception handling mechanism when:
1. API endpoints throw these exceptions
2. Validation fails on request bodies
3. JWT tokens are invalid or expired

## Future Enhancements
- InsufficientStockException can be used when inventory validation is added
- Additional field validation can be added to DTOs with @NotNull, @Min, etc. annotations
- Logging can be added to GlobalExceptionHandler methods for audit trails

