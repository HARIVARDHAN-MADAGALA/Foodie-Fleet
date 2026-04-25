# 🔒 Spring Security & JWT - Implementation Guide

## ⚠️ IMPORTANT: Before You Start!

**You MUST configure the JWT_SECRET environment variable before running the services!**

See **[SECURITY_SETUP.md](SECURITY_SETUP.md)** for detailed instructions on:
- Setting up JWT_SECRET environment variable
- Generating secure secrets
- IntelliJ configuration
- Security best practices

**Without proper JWT_SECRET configuration, services will NOT start!**

---

## 📚 What We Built

Complete security layer for the Food Delivery Microservices project with:

✅ **User Authentication** - Register and login with JWT tokens  
✅ **Password Encryption** - BCrypt hashing for secure storage  
✅ **Role-Based Access Control** - 4 roles (CUSTOMER, RESTAURANT, DELIVERY, ADMIN)  
✅ **JWT Token System** - Stateless authentication across microservices  
✅ **Protected Endpoints** - Method-level security with @PreAuthorize  
✅ **Request Validation** - JWT filter on every API call  
✅ **Secure Secret Management** - Environment variable based configuration  

---

## 🏗️ Architecture Overview

```
Client (Postman/Frontend)
    ↓
1. POST /api/auth/register → Create account
    ↓
2. POST /api/auth/login → Get JWT token
    ↓
3. Send requests with token in header
    ↓ Authorization: Bearer <JWT>
    ↓
JWT Filter validates token
    ↓
Extract user info (ID, role)
    ↓
@PreAuthorize checks role
    ↓
Controller processes request
```

---

## 🔑 Roles and Permissions

### CUSTOMER
**Can:**
- ✅ Create orders (own orders only)
- ✅ View own order history
- ✅ View order details

**Cannot:**
- ❌ View other users' orders
- ❌ View all orders
- ❌ Update order status
- ❌ Assign delivery partners

### RESTAURANT
**Can:**
- ✅ Update order status (preparing, ready)
- ✅ View restaurant-specific orders

**Cannot:**
- ❌ Create orders
- ❌ View all orders
- ❌ Assign delivery partners

### DELIVERY
**Can:**
- ✅ Assign themselves to deliveries
- ✅ Update delivery status
- ✅ View assigned orders

**Cannot:**
- ❌ Create orders
- ❌ View all orders
- ❌ Update payment status

### ADMIN
**Can:**
- ✅ Everything! Full access to all endpoints
- ✅ View all orders
- ✅ Update any status
- ✅ Manage all resources

---

## 📝 Testing with Postman

### Step 1: Register a New User

**Request:**
```http
POST http://localhost:8082/api/auth/register
Content-Type: application/json

{
  "name": "John Doe",
  "email": "john@example.com",
  "phone": "1234567890",
  "password": "password123",
  "role": "CUSTOMER"
}
```

**Response (201 Created):**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "type": "Bearer",
  "userId": 1,
  "email": "john@example.com",
  "name": "John Doe",
  "role": "CUSTOMER"
}
```

**Save the token!** You'll need it for all other requests.

---

### Step 2: Login (Get JWT Token)

**Request:**
```http
POST http://localhost:8082/api/auth/login
Content-Type: application/json

{
  "email": "john@example.com",
  "password": "password123"
}
```

**Response (200 OK):**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "type": "Bearer",
  "userId": 1,
  "email": "john@example.com",
  "name": "John Doe",
  "role": "CUSTOMER"
}
```

---

### Step 3: Create an Order (Protected Endpoint)

**Request:**
```http
POST http://localhost:8083/api/orders
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
Content-Type: application/json

{
  "userId": 1,
  "restaurantId": 5,
  "addressId": 1,
  "items": [
    {
      "menuItemId": 101,
      "itemName": "Pizza",
      "quantity": 2,
      "price": 500.0
    }
  ]
}
```

**Response (201 Created):**
```json
{
  "id": 1,
  "userId": 1,
  "restaurantId": 5,
  "status": "PLACED",
  "paymentStatus": "PENDING",
  "totalAmount": 1000.0,
  "items": [...]
}
```

---

### Step 4: View Own Orders

**Request:**
```http
GET http://localhost:8083/api/orders/user/1
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

**Response (200 OK):**
```json
[
  {
    "id": 1,
    "userId": 1,
    "status": "PLACED",
    "totalAmount": 1000.0
  }
]
```

---

### Step 5: Try Accessing Admin Endpoint (Should Fail)

**Request:**
```http
GET http://localhost:8083/api/orders
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

**Response (403 Forbidden):**
```json
{
  "error": "Access Denied"
}
```

**Why?** Only ADMIN role can view all orders!

---

### Step 6: Register as ADMIN

**Request:**
```http
POST http://localhost:8082/api/auth/register
Content-Type: application/json

{
  "name": "Admin User",
  "email": "admin@example.com",
  "phone": "9876543210",
  "password": "admin123",
  "role": "ADMIN"
}
```

**Response:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "userId": 2,
  "role": "ADMIN"
}
```

---

### Step 7: View All Orders (As ADMIN)

**Request:**
```http
GET http://localhost:8083/api/orders
Authorization: Bearer <ADMIN_TOKEN>
```

**Response (200 OK):**
```json
[
  {
    "id": 1,
    "userId": 1,
    "status": "PLACED",
    "totalAmount": 1000.0
  },
  {
    "id": 2,
    "userId": 3,
    "status": "DELIVERED",
    "totalAmount": 1500.0
  }
]
```

**Success!** Admin can see all orders!

---

## 🔐 JWT Token Structure

Your JWT token contains:

```
eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VySWQiOjEsImVtYWlsIjoiam9obkBleGFtcGxlLmNvbSIsInJvbGUiOiJDVVNUT01FUiIsImlhdCI6MTczMDU2MzIwMCwiZXhwIjoxNzMwNTY2ODAwfQ.signature

Decoded Header:
{
  "alg": "HS256",
  "typ": "JWT"
}

Decoded Payload:
{
  "userId": 1,
  "email": "john@example.com",
  "role": "CUSTOMER",
  "iat": 1730563200,  // Issued at
  "exp": 1730566800   // Expires in 1 hour
}

Signature: (verified by server)
```

**Decode your token at:** https://jwt.io/

---

## 🛡️ Security Endpoints Summary

### User Service (Port 8082)

| Endpoint | Method | Access | Description |
|----------|--------|--------|-------------|
| `/api/auth/register` | POST | Public | Register new user |
| `/api/auth/login` | POST | Public | Login and get JWT |

### Order Service (Port 8083)

| Endpoint | Method | Roles | Description |
|----------|--------|-------|-------------|
| `POST /api/orders` | POST | CUSTOMER | Create new order |
| `GET /api/orders/{id}` | GET | ALL | Get order by ID |
| `GET /api/orders` | GET | ADMIN | Get all orders |
| `GET /api/orders/user/{userId}` | GET | CUSTOMER, ADMIN | Get user's orders |
| `PUT /api/orders/{id}/status` | PUT | RESTAURANT, ADMIN | Update order status |
| `PUT /api/orders/{id}/payment-status` | PUT | ADMIN | Update payment status |
| `PUT /api/orders/{id}/assign-delivery` | PUT | DELIVERY, ADMIN | Assign delivery partner |

---

## 🧪 Testing Scenarios

### Scenario 1: Customer Creates Order

1. Register as CUSTOMER
2. Get JWT token
3. Create order → ✅ Success
4. Try to view all orders → ❌ 403 Forbidden
5. View own orders → ✅ Success

### Scenario 2: Restaurant Updates Order

1. Register as RESTAURANT
2. Get JWT token
3. Try to create order → ❌ 403 Forbidden
4. Update order status → ✅ Success

### Scenario 3: Admin Full Access

1. Register as ADMIN
2. Get JWT token
3. View all orders → ✅ Success
4. Update any status → ✅ Success
5. Assign delivery → ✅ Success

### Scenario 4: Token Expiration

1. Login and get token
2. Wait 1 hour (or change JWT expiration to 1 minute for testing)
3. Try to access protected endpoint → ❌ 401 Unauthorized
4. Login again to get new token

---

## 📊 Error Responses

### 401 Unauthorized
**Cause:** No JWT token or invalid token
```json
{
  "error": "Unauthorized",
  "message": "Full authentication is required to access this resource"
}
```

### 403 Forbidden
**Cause:** Valid token but insufficient permissions
```json
{
  "error": "Access Denied",
  "message": "Access is denied"
}
```

### 400 Bad Request
**Cause:** Validation errors
```json
{
  "status": 400,
  "error": "Bad Request",
  "message": "Email already exists: john@example.com"
}
```

---

## 🔧 Configuration

### JWT Settings (application.yml)

```yaml
jwt:
  secret: ${JWT_SECRET}  # ← Environment variable (SECURE)
  expiration: ${JWT_EXPIRATION:3600000}  # Default: 1 hour
```

**⚠️ CRITICAL: JWT_SECRET must be set as environment variable!**

See **[SECURITY_SETUP.md](SECURITY_SETUP.md)** for setup instructions.

**Change expiration for testing:**
- 60000 = 1 minute
- 300000 = 5 minutes
- 3600000 = 1 hour (default)
- 86400000 = 24 hours

---

## 🚀 Quick Start Commands

### 0. Configure JWT Secret (FIRST!)
```bash
# Set environment variable (Mac/Linux)
export JWT_SECRET=yourVerySecureRandomString64CharactersLongGeneratedSecurely
export JWT_EXPIRATION=3600000

# Set environment variable (Windows CMD)
set JWT_SECRET=yourVerySecureRandomString64CharactersLongGeneratedSecurely
set JWT_EXPIRATION=3600000
```

See **[SECURITY_SETUP.md](SECURITY_SETUP.md)** for detailed instructions!

### 1. Start Services
```bash
# Start Eureka Server
java -jar eureka-server/target/eureka-server.jar

# Start User Service (Port 8082)
java -jar user-service/target/user-service.jar

# Start Order Service (Port 8083)
java -jar order-service/target/order-service.jar
```

### 2. Register Test Users
```bash
# CUSTOMER
curl -X POST http://localhost:8082/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"name":"John Doe","email":"john@example.com","phone":"1234567890","password":"password123","role":"CUSTOMER"}'

# ADMIN
curl -X POST http://localhost:8082/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"name":"Admin User","email":"admin@example.com","phone":"9876543210","password":"admin123","role":"ADMIN"}'
```

### 3. Login and Save Token
```bash
TOKEN=$(curl -X POST http://localhost:8082/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"john@example.com","password":"password123"}' \
  | jq -r '.token')

echo $TOKEN
```

### 4. Make Authenticated Request
```bash
curl -X GET http://localhost:8083/api/orders/user/1 \
  -H "Authorization: Bearer $TOKEN"
```

---

## 📁 Project Structure

```
user-service/
├── entity/
│   ├── User.java              ← User entity with role and encrypted password
│   └── Role.java              ← Enum: CUSTOMER, RESTAURANT, DELIVERY, ADMIN
├── security/
│   ├── JwtUtil.java           ← JWT generation and validation
│   ├── CustomUserDetails.java ← Spring Security UserDetails
│   ├── JwtAuthenticationFilter.java ← Validates JWT on every request
│   └── SecurityConfig.java    ← Spring Security configuration
├── dto/
│   ├── RegisterRequest.java   ← Registration DTO
│   ├── LoginRequest.java      ← Login DTO
│   └── AuthResponse.java      ← Response with JWT token
├── service/
│   ├── AuthService.java       ← Register and login logic
│   └── CustomUserDetailsService.java ← Load user for authentication
└── controller/
    └── AuthController.java    ← /register and /login endpoints

order-service/
├── security/
│   ├── JwtUtil.java           ← JWT validation
│   ├── JwtAuthenticationFilter.java ← JWT filter
│   └── SecurityConfig.java    ← Security configuration
└── controller/
    └── OrderController.java   ← Protected with @PreAuthorize
```

---

## ✅ What Changed?

### Before Security:
```java
// Anyone could access
@GetMapping("/api/orders")
public List<OrderDTO> getAllOrders() {
    return orderService.getAllOrders();
}
```

### After Security:
```java
// Only ADMIN can access
@PreAuthorize("hasRole('ADMIN')")
@GetMapping("/api/orders")
public List<OrderDTO> getAllOrders() {
    return orderService.getAllOrders();
}
```

---

## 🎓 Key Concepts Implemented

1. **BCrypt Password Encryption**
   - Passwords hashed with salt
   - Irreversible encryption
   - Secure password comparison

2. **JWT Stateless Authentication**
   - No session storage needed
   - Token contains all user info
   - Works across microservices

3. **Role-Based Access Control (RBAC)**
   - Method-level security
   - @PreAuthorize annotations
   - Flexible permission system

4. **Spring Security Filter Chain**
   - JWT filter on every request
   - Extract and validate token
   - Set authentication context

5. **CORS Configuration**
   - Allow cross-origin requests
   - Configured for all origins (development)
   - Can be restricted in production

---

## 🔍 Troubleshooting

### Issue: 401 Unauthorized
**Solution:** Check if Authorization header is present and starts with "Bearer "

### Issue: 403 Forbidden
**Solution:** Check user role matches required role in @PreAuthorize

### Issue: Token expired
**Solution:** Login again to get new token

### Issue: Email already exists
**Solution:** Use different email or login with existing account

---

## 🌟 Next Steps

Want to enhance security further? Consider adding:

1. **Refresh Tokens** - Long-lived tokens for getting new access tokens
2. **Email Verification** - Confirm email before activation
3. **Password Reset** - Forgot password flow
4. **Rate Limiting** - Prevent brute force attacks
5. **OAuth2** - Social login (Google, Facebook)
6. **Two-Factor Authentication** - Additional security layer

---

Congratulations! You now have a **production-ready security layer** in your microservices! 🎉
