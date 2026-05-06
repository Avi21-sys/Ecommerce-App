# Logging Implementation - Cart Service

## Overview
This document outlines the comprehensive logging implementation for the Cart-Service microservice.

## Logging Framework
- **Framework**: SLF4J with Logback (default in Spring Boot)
- **Logger**: Static Logger initialized per class
- **Pattern**: `%d{yyyy-MM-dd HH:mm:ss} - %logger{36} - %msg%n`

---

## Logging Configuration (application.properties)

### Root Logging Levels
```properties
logging.level.root=INFO
logging.level.com.ecommerce.cart_service=DEBUG
logging.level.com.ecommerce.cart_service.controller=INFO
logging.level.com.ecommerce.cart_service.service=INFO
logging.level.com.ecommerce.cart_service.security=INFO
logging.level.com.ecommerce.cart_service.exception=WARN
logging.level.org.springframework.web=INFO
logging.level.org.hibernate=WARN
```

### Log File Configuration
- **File Location**: `logs/cart-service.log`
- **Max Size**: 10MB (rotates when reached)
- **Retention**: 10 files (max-history)

---

## Logging by Component

### 1. CartController (`com.ecommerce.cart_service.controller`)
**Purpose**: Log HTTP request/response lifecycle

#### Log Entries:
- **getCart()**
  - `INFO`: User ID and request start
  - `DEBUG`: Retrieved items count
  
- **add()**
  - `INFO`: Product add request with user ID
  - `DEBUG`: Product details (name, price, quantity)
  - `INFO`: Successful addition with cart item ID
  
- **delete()**
  - `INFO`: Delete request with item ID and user ID
  - `INFO`: Successful deletion confirmation

#### Example Logs:
```
2026-05-02 14:30:15 - CartController - GET request: Fetching cart for user with ID: 123
2026-05-02 14:30:16 - CartController - Retrieved 3 items from cart for user: 123
2026-05-02 14:30:20 - CartController - POST request: Adding product 456 to cart for user 123
2026-05-02 14:30:21 - CartController - Successfully added product to cart. Cart item ID: 789
```

---

### 2. CartService (`com.ecommerce.cart_service.service`)
**Purpose**: Log business logic operations and state changes

#### Log Entries:
- **getCart()**
  - `DEBUG`: Starting cart fetch for user
  - `INFO`: Successful fetch with item count
  
- **addToCart()**
  - `DEBUG`: Add operation details (productId, quantity)
  - `DEBUG`: Product already exists → update quantity
  - `DEBUG`: Adding new product to cart
  - `INFO`: Successful add/update with full details
  
- **remove()**
  - `DEBUG`: Remove operation start
  - `WARN`: Item not found
  - `WARN`: Unauthorized access attempt
  - `INFO`: Successful removal

#### Example Logs:
```
2026-05-02 14:30:15 - CartService - Fetching cart items for user: 123
2026-05-02 14:30:16 - CartService - Cart fetch successful. Found 3 items for user: 123
2026-05-02 14:30:20 - CartService - Adding to cart - ProductId: 456, UserId: 123, Quantity: 2
2026-05-02 14:30:21 - CartService - Product 456 already exists in cart for user 123. Updating quantity from 1 to 3
2026-05-02 14:30:21 - CartService - Successfully updated cart item 789 for user 123
```

---

### 3. JwtFilter (`com.ecommerce.cart_service.security`)
**Purpose**: Log JWT authentication and validation

#### Log Entries:
- **doFilter()**
  - `DEBUG`: No Authorization header found
  - `DEBUG`: JWT token received for request
  - `INFO`: Token validated successfully (username, userId, request)
  - `WARN`: Token missing userId claim
  - `WARN`: JWT validation failed (exception, message, request)

#### Example Logs:
```
2026-05-02 14:30:15 - JwtFilter - No valid Authorization header found for request: /api/cart
2026-05-02 14:30:15 - JwtFilter - Processing JWT token for request: /api/cart
2026-05-02 14:30:15 - JwtFilter - JWT token validated successfully - Username: john_doe, UserId: 123, Request: /api/cart
2026-05-02 14:30:20 - JwtFilter - JWT validation failed - Exception: JwtException, Message: Invalid signature, Request: /api/cart
```

---

### 4. GlobalExceptionHandler (`com.ecommerce.cart_service.exception`)
**Purpose**: Log exception handling and error responses

#### Log Entries:
- **CartNotFoundException**
  - `WARN`: Cart not found with message and path
  
- **InsufficientStockException**
  - `WARN`: Stock error with message and path
  
- **MethodArgumentNotValidException**
  - `WARN`: Validation failure with field errors
  
- **JwtException**
  - `WARN`: JWT validation error
  
- **Exception (Generic)**
  - `ERROR`: Unexpected exception (includes full exception trace)

#### Example Logs:
```
2026-05-02 14:30:25 - GlobalExceptionHandler - CartNotFoundException occurred - Message: Cart item with id 999 not found, Path: /api/cart/999
2026-05-02 14:30:26 - GlobalExceptionHandler - Validation failed - Path: /api/cart, Field Errors: {productId=must not be null, quantity=must be greater than 0}
2026-05-02 14:30:30 - GlobalExceptionHandler - Unexpected exception occurred - Message: Database connection failed, Path: /api/cart, Exception: SQLException
```

---

## Log Levels Explanation

| Level | Usage | Example |
|-------|-------|---------|
| **DEBUG** | Detailed flow information | Method entry, intermediate calculations, conditional branches |
| **INFO** | General informational messages | Successful operations, user actions, key events |
| **WARN** | Potential issues requiring attention | Validation failures, authorization denials, not found errors |
| **ERROR** | Error conditions | Exceptions, system failures |

---

## How to Use Logs

### 1. Development
- Set `logging.level.com.ecommerce.cart_service=DEBUG` to see detailed flow
- Monitor console output in IDE

### 2. Production
- Set `logging.level.root=INFO` for performance
- Store logs in files with rotation: `logs/cart-service.log`
- Use log aggregation tools (ELK Stack, Splunk, etc.)

### 3. Troubleshooting
- **User not found**: Search logs for "Unauthorized" or "userId"
- **Product not found**: Search logs for "CartNotFoundException"
- **JWT issues**: Search logs for "JwtFilter" or "JWT validation failed"
- **Performance**: Enable WARN for Hibernate: `logging.level.org.hibernate=WARN`

---

## Security Considerations

✅ **What's Logged**:
- User IDs (non-sensitive)
- Product IDs and names
- HTTP paths and methods
- Exception types and messages
- JWT validation results

❌ **What's NOT Logged**:
- JWT tokens themselves
- Passwords or credentials
- Sensitive payment information
- Full exception stack traces in production

---

## Examples of Common Log Searches

### Find all cart operations for a user
```bash
grep "UserId: 123" logs/cart-service.log
```

### Find all errors
```bash
grep "ERROR" logs/cart-service.log
```

### Find all authorization failures
```bash
grep "Unauthorized\|JWT validation failed" logs/cart-service.log
```

### Find all validation errors
```bash
grep "Validation failed" logs/cart-service.log
```

---

## Future Enhancements

1. **Add correlation IDs**: Track requests across microservices
2. **Structured logging**: Use JSON format for better log parsing
3. **Metrics logging**: Log performance metrics (response times, DB queries)
4. **Async logging**: Use async appenders for high-throughput scenarios
5. **Log rotation**: Already configured with max-size and max-history

