# Exception Handling Layer - Order Service

## Overview
This document explains the **Global Exception Handling Layer** implemented in Order Service using Spring Boot's `@RestControllerAdvice`.

## Architecture

```
Controller Layer
    ↓ (throws exception)
Service Layer
    ↓ (throws custom exception)
GlobalExceptionHandler (@RestControllerAdvice)
    ↓ (catches & handles)
ErrorResponse (Standardized JSON)
    ↓
Client receives consistent error format
```

---

## Custom Exceptions

### 1. **ResourceNotFoundException**
- **HTTP Status**: 404 NOT FOUND
- **When to use**: Resource (order, restaurant, user) not found in database
- **Example**:
```java
throw new ResourceNotFoundException("Order", "id", orderId);
// Returns: "Order not found with id: '123'"
```

### 2. **OrderProcessingException**
- **HTTP Status**: 400 BAD REQUEST
- **When to use**: Business logic validation fails
- **Example**:
```java
throw new OrderProcessingException(
    "Restaurant is currently not accepting orders", 
    "RESTAURANT_UNAVAILABLE"
);
```

### 3. **InvalidOrderException**
- **HTTP Status**: 400 BAD REQUEST
- **When to use**: Order data validation fails
- **Example**:
```java
if (orderDTO.getItems() == null || orderDTO.getItems().isEmpty()) {
    throw new InvalidOrderException("Order must contain at least one item");
}
```

### 4. **RestaurantServiceException**
- **HTTP Status**: 503 SERVICE UNAVAILABLE
- **When to use**: Communication with Restaurant Service fails
- **Example**:
```java
throw new RestaurantServiceException(
    "Restaurant Service is temporarily unavailable",
    throwable
);
```

---

## Error Response Format

All exceptions return a standardized JSON response:

```json
{
  "timestamp": "2025-10-23T10:30:45",
  "status": 404,
  "error": "Not Found",
  "message": "Order not found with id: '123'",
  "path": "/api/orders/123",
  "details": null
}
```

For validation errors with multiple fields:
```json
{
  "timestamp": "2025-10-23T10:30:45",
  "status": 400,
  "error": "Validation Failed",
  "message": "Input validation failed",
  "path": "/api/orders",
  "details": [
    "userId: must not be null",
    "items: must not be empty"
  ]
}
```

---

## Exception Handler Methods

### GlobalExceptionHandler.java

| Exception Type | HTTP Status | Handler Method |
|---------------|-------------|----------------|
| ResourceNotFoundException | 404 | handleResourceNotFoundException() |
| OrderProcessingException | 400 | handleOrderProcessingException() |
| InvalidOrderException | 400 | handleInvalidOrderException() |
| RestaurantServiceException | 503 | handleRestaurantServiceException() |
| MethodArgumentNotValidException | 400 | handleValidationException() |
| MethodArgumentTypeMismatchException | 400 | handleTypeMismatchException() |
| IllegalArgumentException | 400 | handleIllegalArgumentException() |
| Exception (all others) | 500 | handleGlobalException() |

---

## Usage Examples

### Example 1: Order Not Found

**Request:**
```bash
GET http://localhost:8083/api/orders/999
```

**Response:**
```json
{
  "timestamp": "2025-10-23T10:30:45",
  "status": 404,
  "error": "Not Found",
  "message": "Order not found with id: '999'",
  "path": "/api/orders/999"
}
```

---

### Example 2: Invalid Order Data

**Request:**
```bash
POST http://localhost:8083/api/orders
Content-Type: application/json

{
  "userId": null,
  "items": []
}
```

**Response:**
```json
{
  "timestamp": "2025-10-23T10:30:45",
  "status": 400,
  "error": "Invalid Order",
  "message": "User ID is required",
  "path": "/api/orders"
}
```

---

### Example 3: Restaurant Service Unavailable (Circuit Breaker)

**Request:**
```bash
POST http://localhost:8083/api/orders
Content-Type: application/json

{
  "userId": 1,
  "restaurantId": 1,
  "items": [...]
}
```

**Response (when Restaurant Service is down):**
```json
{
  "timestamp": "2025-10-23T10:30:45",
  "status": 503,
  "error": "Service Unavailable",
  "message": "Restaurant service is currently unavailable. Please try again later.",
  "path": "/api/orders"
}
```

---

## Benefits

✅ **Consistent Error Responses**: All errors follow same JSON structure  
✅ **Better Client Experience**: Clear, descriptive error messages  
✅ **Centralized Handling**: All exceptions handled in one place  
✅ **Proper HTTP Status Codes**: RESTful best practices  
✅ **Logging**: All errors logged with appropriate log levels  
✅ **Production Ready**: Hides internal details in generic 500 errors  

---

## How It Works

1. **Controller** receives HTTP request
2. **Service layer** processes request, throws custom exception if error occurs
3. **@RestControllerAdvice** intercepts the exception
4. **@ExceptionHandler** method matching the exception type is called
5. Creates **ErrorResponse** with appropriate HTTP status
6. **Spring** converts ErrorResponse to JSON and sends to client

---

## Adding New Custom Exceptions

### Step 1: Create Exception Class
```java
package com.fooddelivery.order.exception;

public class PaymentFailedException extends RuntimeException {
    public PaymentFailedException(String message) {
        super(message);
    }
}
```

### Step 2: Add Handler Method
```java
@ExceptionHandler(PaymentFailedException.class)
public ResponseEntity<ErrorResponse> handlePaymentFailedException(
        PaymentFailedException ex, 
        WebRequest request) {
    
    ErrorResponse errorResponse = new ErrorResponse(
        HttpStatus.PAYMENT_REQUIRED.value(),
        "Payment Failed",
        ex.getMessage(),
        request.getDescription(false).replace("uri=", "")
    );
    
    return new ResponseEntity<>(errorResponse, HttpStatus.PAYMENT_REQUIRED);
}
```

### Step 3: Use in Service
```java
if (paymentFailed) {
    throw new PaymentFailedException("Insufficient balance");
}
```

---

## Testing Exception Handling

### Test 1: Resource Not Found
```bash
# Order doesn't exist
curl -X GET http://localhost:8083/api/orders/99999
# Expected: 404 with "Order not found" message
```

### Test 2: Invalid Data
```bash
# Empty order items
curl -X POST http://localhost:8083/api/orders \
  -H "Content-Type: application/json" \
  -d '{"userId": 1, "restaurantId": 1, "items": []}'
# Expected: 400 with "Order must contain at least one item"
```

### Test 3: Service Unavailable
```bash
# Stop Restaurant Service first
# Then try to create order
curl -X POST http://localhost:8083/api/orders \
  -H "Content-Type: application/json" \
  -d '{"userId": 1, "restaurantId": 1, "items": [...]}'
# Expected: 503 with "Restaurant service is currently unavailable"
```

---

## Best Practices

1. ✅ **Use specific exceptions** for different scenarios
2. ✅ **Include context** in error messages (IDs, values)
3. ✅ **Log errors** before throwing exceptions
4. ✅ **Don't expose internal details** in production errors
5. ✅ **Use appropriate HTTP status codes**
6. ✅ **Provide actionable error messages** to clients

---

## Files Created

```
order-service/src/main/java/com/fooddelivery/order/
├── exception/
│   ├── GlobalExceptionHandler.java        # Central exception handler
│   ├── ResourceNotFoundException.java     # 404 errors
│   ├── OrderProcessingException.java      # 400 business logic errors
│   ├── InvalidOrderException.java         # 400 validation errors
│   └── RestaurantServiceException.java    # 503 service errors
├── dto/
│   └── ErrorResponse.java                 # Standardized error response
└── service/
    └── OrderService.java                  # Updated to use custom exceptions
```

---

## Integration with Circuit Breaker

The exception handling layer works seamlessly with the Circuit Breaker pattern:

- **Circuit CLOSED**: Normal exceptions (404, 400) thrown
- **Circuit OPEN**: RestaurantServiceException (503) thrown
- **Fallback**: Circuit breaker triggers fallback → throws RestaurantServiceException
- **Global Handler**: Catches exception → Returns 503 with user-friendly message

This ensures clients always receive proper error responses, even during service failures!

---

## Summary

The exception handling layer provides:
- **Production-ready** error handling
- **RESTful** HTTP status codes
- **Consistent** error response format
- **Centralized** exception management
- **Integration** with Circuit Breaker and other patterns

This makes the Order Service **robust, maintainable, and user-friendly**! 🚀
