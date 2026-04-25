# 🔒 Spring Security & JWT - Complete Concepts Guide

## 📚 Table of Contents
1. [What is Spring Security?](#what-is-spring-security)
2. [Authentication vs Authorization](#authentication-vs-authorization)
3. [What is JWT (JSON Web Token)?](#what-is-jwt)
4. [How JWT Works](#how-jwt-works)
5. [Spring Security Architecture](#spring-security-architecture)
6. [Complete Security Flow](#complete-security-flow)
7. [Role-Based Access Control](#role-based-access-control)

---

# What is Spring Security?

**Spring Security** is a framework that provides:
- ✅ **Authentication** - Verify who the user is
- ✅ **Authorization** - Check what the user can do
- ✅ **Protection** - Secure your REST APIs
- ✅ **Password Encryption** - Store passwords safely

Think of it as a **security guard** for your application!

---

# Authentication vs Authorization

## 🔑 Authentication = "WHO are you?"

```
User: "I'm John Doe"
System: "Prove it - enter your password"
User: "MyPassword123"
System: "Verified! You ARE John Doe"
System: "Here's your JWT token as ID badge"
```

**Example:**
```
Login Request:
POST /api/auth/login
{
  "email": "john@example.com",
  "password": "mypassword"
}

Response:
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "userId": 1,
  "email": "john@example.com",
  "role": "CUSTOMER"
}
```

---

## 🛡️ Authorization = "WHAT can you do?"

```
User: "I want to delete a restaurant"
System: "Let me check your token... You're a CUSTOMER"
System: "Access Denied! Only ADMINs can delete restaurants"
```

**Example:**
```
Request with JWT:
DELETE /api/restaurants/5
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...

System checks:
1. Is token valid? ✅
2. User role from token: CUSTOMER
3. Required role: ADMIN
4. Result: ❌ Access Denied (403 Forbidden)
```

---

# What is JWT (JSON Web Token)?

JWT is a **secure way to represent user information** that can be verified and trusted.

## JWT Structure

A JWT has **3 parts** separated by dots:

```
eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c

↓ Decoded:

HEADER.PAYLOAD.SIGNATURE
```

### 1. Header (Algorithm & Type)
```json
{
  "alg": "HS256",
  "typ": "JWT"
}
```
Tells us: This is a JWT signed with HMAC-SHA256

### 2. Payload (User Data / Claims)
```json
{
  "userId": 1,
  "email": "john@example.com",
  "role": "CUSTOMER",
  "iat": 1730563200,
  "exp": 1730566800
}
```
Contains:
- User information
- Issued at time (iat)
- Expiration time (exp)

### 3. Signature (Verification)
```
HMACSHA256(
  base64UrlEncode(header) + "." +
  base64UrlEncode(payload),
  secret-key
)
```
Ensures:
- Token wasn't tampered with
- Token was created by your server

---

## Why JWT?

### Traditional Sessions (OLD WAY):
```
User logs in
    ↓
Server creates session, stores in database
    ↓
Server sends session ID to client (cookie)
    ↓
Client sends session ID with each request
    ↓
Server looks up session in database every time
```

**Problems:**
- ❌ Database lookup on every request (slow)
- ❌ Hard to scale (sessions stored on one server)
- ❌ Doesn't work well with microservices

### JWT Tokens (MODERN WAY):
```
User logs in
    ↓
Server creates JWT token (contains all user info)
    ↓
Server sends JWT to client
    ↓
Client sends JWT with each request
    ↓
Server validates JWT signature (NO database lookup!)
    ↓
Server reads user info from JWT payload
```

**Benefits:**
- ✅ No database lookup (fast)
- ✅ Stateless (no session storage needed)
- ✅ Works great with microservices
- ✅ Can be validated by any service

---

# How JWT Works

## Complete Flow:

### Step 1: User Registration
```
POST /api/auth/register
{
  "name": "John Doe",
  "email": "john@example.com",
  "password": "mypassword",
  "role": "CUSTOMER"
}

Server:
1. Encrypt password: mypassword → $2a$10$EIXQExqF...
2. Save user to database
3. Return success message
```

### Step 2: User Login
```
POST /api/auth/login
{
  "email": "john@example.com",
  "password": "mypassword"
}

Server:
1. Find user by email
2. Compare passwords (encrypted)
3. Generate JWT token
4. Return token to client

Response:
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "type": "Bearer",
  "userId": 1,
  "email": "john@example.com",
  "role": "CUSTOMER"
}
```

### Step 3: Access Protected Endpoint
```
GET /api/orders/my-orders
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...

Server:
1. Extract JWT from Authorization header
2. Validate signature (is it authentic?)
3. Check expiration (is it still valid?)
4. Extract user info from payload
5. Check permissions (does user have access?)
6. If all checks pass → Process request
7. Return user's orders
```

---

# Spring Security Architecture

## Key Components:

### 1. SecurityFilterChain
```
Every HTTP Request
    ↓
Filter 1: CORS Filter
    ↓
Filter 2: JWT Authentication Filter (our custom filter)
    ↓
Filter 3: Authorization Filter
    ↓
Filter 4: Exception Handling Filter
    ↓
Controller (if all filters pass)
```

### 2. JWT Authentication Filter (Custom)
```java
For each request:
1. Extract JWT from Authorization header
2. Validate JWT token
3. If valid:
   - Extract user details
   - Set authentication in SecurityContext
   - Continue to next filter
4. If invalid:
   - Throw exception (401 Unauthorized)
```

### 3. UserDetailsService
```java
Spring Security asks: "Who is this user?"
    ↓
UserDetailsService loads user from database
    ↓
Returns UserDetails object with:
    - Username/email
    - Encrypted password
    - Roles/authorities
    - Account status (enabled/disabled)
```

### 4. PasswordEncoder
```java
Registration:
plainPassword → BCryptPasswordEncoder → $2a$10$EIXQExqF...
                                        ↑ Stored in database

Login:
userInput → BCryptPasswordEncoder → Compare with stored hash
                                   ↓
                                 ✅ Match or ❌ No match
```

---

# Complete Security Flow

## Scenario: User Creates an Order

### Without Security (Current - INSECURE):
```
Client → POST /api/orders
{
  "userId": 999,  ← Can create order for ANY user!
  "restaurantId": 5
}
    ↓
OrderController → Directly processes
    ↓
Anyone can create orders for anyone!
```

### With Security (SECURE):
```
Step 1: Login
Client → POST /api/auth/login
{
  "email": "john@example.com",
  "password": "mypassword"
}
    ↓
Response: JWT token
{
  "token": "eyJhbG..."
}

Step 2: Create Order (with JWT)
Client → POST /api/orders
Authorization: Bearer eyJhbG...
{
  "userId": 1,
  "restaurantId": 5
}
    ↓
JWT Filter:
    - Validates token ✅
    - Extracts userId: 1 from token
    - Sets authentication in SecurityContext
    ↓
OrderController:
    - Gets current user from SecurityContext
    - Verifies userId in request matches current user
    - If match → Process order
    - If mismatch → Reject (403 Forbidden)
    ↓
User can ONLY create orders for themselves!
```

---

# Role-Based Access Control

## Roles in Our Project:

### 1. CUSTOMER
**Can:**
- ✅ View restaurants and menus
- ✅ Create orders for themselves
- ✅ View their own orders
- ✅ Rate restaurants

**Cannot:**
- ❌ View other users' orders
- ❌ Modify restaurant information
- ❌ View all deliveries

### 2. RESTAURANT
**Can:**
- ✅ Update their restaurant info
- ✅ Manage their menu items
- ✅ View orders for their restaurant
- ✅ Update order status (preparing, ready)

**Cannot:**
- ❌ View other restaurants' data
- ❌ Modify other restaurants
- ❌ Assign delivery partners

### 3. DELIVERY
**Can:**
- ✅ View assigned deliveries
- ✅ Update delivery status
- ✅ View delivery history

**Cannot:**
- ❌ View unassigned deliveries
- ❌ Modify orders
- ❌ Access restaurant data

### 4. ADMIN
**Can:**
- ✅ Everything! Full access to all endpoints
- ✅ View all orders, restaurants, users
- ✅ Delete/modify any data
- ✅ Manage system settings

---

## Code Examples:

### Protect Endpoint by Role:
```java
// Only customers can create orders
@PreAuthorize("hasRole('CUSTOMER')")
@PostMapping("/api/orders")
public OrderDTO createOrder(@RequestBody OrderDTO orderDTO) {
    // Only logged-in customers reach here
}

// Only restaurant owners can update menu
@PreAuthorize("hasRole('RESTAURANT')")
@PutMapping("/api/restaurants/{id}/menu")
public MenuItemDTO updateMenuItem(...) {
    // Only restaurant owners reach here
}

// Only admins can view all orders
@PreAuthorize("hasRole('ADMIN')")
@GetMapping("/api/orders/all")
public List<OrderDTO> getAllOrders() {
    // Only admins reach here
}

// Multiple roles allowed
@PreAuthorize("hasAnyRole('ADMIN', 'RESTAURANT')")
@GetMapping("/api/orders/restaurant/{restaurantId}")
public List<OrderDTO> getRestaurantOrders() {
    // Admins OR restaurant owners reach here
}
```

### Get Current Logged-In User:
```java
@PostMapping("/api/orders")
public OrderDTO createOrder(
    @RequestBody OrderDTO orderDTO,
    @AuthenticationPrincipal UserDetails userDetails  // ← Current user
) {
    // Extract current user's ID
    Long currentUserId = ((CustomUserDetails) userDetails).getId();
    
    // Ensure user creates order for themselves only
    if (!orderDTO.getUserId().equals(currentUserId)) {
        throw new AccessDeniedException("You can only create orders for yourself!");
    }
    
    return orderService.createOrder(orderDTO);
}
```

---

# Key Security Concepts Summary

## 1. Password Encryption
```
Never store plain passwords!

User registers: password123
    ↓
BCrypt: $2a$10$EIXQExqF8fyuh/R6XMRN1O
    ↓
Stored in database (irreversible!)
```

## 2. JWT Token Expiration
```
Token created at: 2:00 PM
Token expires at: 3:00 PM (1 hour later)

At 2:30 PM: ✅ Token valid
At 3:01 PM: ❌ Token expired (user must login again)
```

## 3. Stateless Authentication
```
Traditional: Server stores sessions in database
JWT: Server doesn't store anything (stateless)

Benefits:
- Faster (no database lookup)
- Scalable (any server can validate)
- Microservice-friendly
```

## 4. Authorization Levels
```
Public endpoints    → No authentication required
Authenticated       → Must have valid JWT
Role-based          → Must have specific role
Resource ownership  → Must own the resource
```

---

# What We'll Build

## Components:

### 1. Security Models
- User entity with roles
- CustomUserDetails (Spring Security integration)
- Authentication request/response DTOs

### 2. JWT Utilities
- JwtUtil class (generate/validate tokens)
- Secret key configuration
- Token expiration settings

### 3. Authentication Service
- Register endpoint
- Login endpoint
- Password encryption

### 4. Security Filters
- JwtAuthenticationFilter (validate every request)
- Extract user from token
- Set authentication context

### 5. Security Configuration
- SecurityFilterChain
- CORS configuration
- Public vs protected endpoints

### 6. Protected Endpoints
- Order Service with role checks
- Restaurant Service with ownership checks
- User-specific data filtering

---

# Next Steps

Now that you understand the concepts, we'll implement:

1. ✅ Add dependencies (Spring Security, JWT)
2. ✅ Create User entity with roles and password encryption
3. ✅ Build JWT utility for token generation
4. ✅ Create authentication endpoints (register/login)
5. ✅ Implement JWT filter for request validation
6. ✅ Configure Spring Security
7. ✅ Protect endpoints with @PreAuthorize
8. ✅ Test everything with Postman

Ready to build? Let's go! 🚀
