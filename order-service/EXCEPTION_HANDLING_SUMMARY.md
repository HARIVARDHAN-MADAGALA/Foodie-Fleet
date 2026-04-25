# Exception Handling Layer - Quick Reference

## 📦 What Was Added

### Custom Exceptions (4 types)
```
exception/
├── ResourceNotFoundException.java       → 404 errors
├── OrderProcessingException.java        → 400 business errors
├── InvalidOrderException.java           → 400 validation errors
└── RestaurantServiceException.java      → 503 service unavailable
```

### Global Handler
```
exception/
└── GlobalExceptionHandler.java          → @RestControllerAdvice
```

### Error Response
```
dto/
└── ErrorResponse.java                   → Standardized JSON format
```

---

## 🎯 Error Flow

```
Client Request
    ↓
OrderController
    ↓
OrderService (throws InvalidOrderException)
    ↓
GlobalExceptionHandler (catches exception)
    ↓
ErrorResponse (converts to JSON)
    ↓
Client Response (400 Bad Request)
```

---

## 📋 HTTP Status Codes

| Exception | Status | Code |
|-----------|--------|------|
| ResourceNotFoundException | Not Found | 404 |
| InvalidOrderException | Bad Request | 400 |
| OrderProcessingException | Bad Request | 400 |
| RestaurantServiceException | Service Unavailable | 503 |
| General Exception | Internal Server Error | 500 |

---

## 🧪 Test Examples

### Test 1: Order Not Found
```bash
GET http://localhost:8083/api/orders/999
```
**Response:**
```json
{
  "status": 404,
  "error": "Not Found",
  "message": "Order not found with id: '999'"
}
```

### Test 2: Invalid Order
```bash
POST http://localhost:8083/api/orders
{
  "userId": null,
  "items": []
}
```
**Response:**
```json
{
  "status": 400,
  "error": "Invalid Order",
  "message": "User ID is required"
}
```

### Test 3: Service Down
```bash
# Stop Restaurant Service, then:
POST http://localhost:8083/api/orders
{
  "userId": 1,
  "restaurantId": 1,
  "items": [...]
}
```
**Response:**
```json
{
  "status": 503,
  "error": "Service Unavailable",
  "message": "Restaurant service is currently unavailable..."
}
```

---

## ✅ Benefits

- ✅ Consistent error responses across all endpoints
- ✅ Proper HTTP status codes
- ✅ Clear, actionable error messages
- ✅ Production-ready error handling
- ✅ Integrated with Circuit Breaker
- ✅ Centralized exception management

---

## 📚 Full Documentation

See `EXCEPTION_HANDLING_GUIDE.md` for complete details and examples.
