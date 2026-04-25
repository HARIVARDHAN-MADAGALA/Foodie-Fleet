# 🔒 Security Testing Checklist

## ✅ Pre-Testing Setup

Before testing, you MUST configure environment variables:

### Step 1: Set JWT_SECRET Environment Variable

#### IntelliJ IDEA (Recommended):
1. Open **Run → Edit Configurations**
2. Select **UserServiceApplication**
3. Click **Modify options → Environment variables**
4. Add:
   ```
   JWT_SECRET=myVerySecureRandomString64CharactersLongForProductionUseGenerateWithSecureRandomGenerator
   JWT_EXPIRATION=3600000
   ```
5. Click **OK**
6. **Repeat for OrderServiceApplication**

#### Command Line (Alternative):
```bash
# Windows CMD
set JWT_SECRET=myVerySecureRandomString64CharactersLongForProductionUseGenerateWithSecureRandomGenerator
set JWT_EXPIRATION=3600000

# Mac/Linux
export JWT_SECRET=myVerySecureRandomString64CharactersLongForProductionUseGenerateWithSecureRandomGenerator
export JWT_EXPIRATION=3600000
```

---

## 🚀 Testing Flow

### Test 1: Start Services (In Order)

1. ✅ **Start Eureka Server** (Port 8761)
   - Run `EurekaServerApplication.java`
   - Verify at: http://localhost:8761

2. ✅ **Start User Service** (Port 8082)
   - Run `UserServiceApplication.java`
   - Check Eureka dashboard - should show "USER-SERVICE"

3. ✅ **Start Order Service** (Port 8083)
   - Run `OrderServiceApplication.java`
   - Check Eureka dashboard - should show "ORDER-SERVICE"

---

### Test 2: Register Users (Postman)

#### Register CUSTOMER
```http
POST http://localhost:8082/api/auth/register
Content-Type: application/json

{
  "name": "John Customer",
  "email": "customer@test.com",
  "phone": "1234567890",
  "password": "password123",
  "role": "CUSTOMER"
}
```

**Expected Response (201 Created):**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "type": "Bearer",
  "userId": 1,
  "email": "customer@test.com",
  "name": "John Customer",
  "role": "CUSTOMER"
}
```

#### Register RESTAURANT
```http
POST http://localhost:8082/api/auth/register
Content-Type: application/json

{
  "name": "Pizza Palace",
  "email": "restaurant@test.com",
  "phone": "2345678901",
  "password": "password123",
  "role": "RESTAURANT"
}
```

#### Register DELIVERY
```http
POST http://localhost:8082/api/auth/register
Content-Type: application/json

{
  "name": "Delivery Partner",
  "email": "delivery@test.com",
  "phone": "3456789012",
  "password": "password123",
  "role": "DELIVERY"
}
```

#### Register ADMIN
```http
POST http://localhost:8082/api/auth/register
Content-Type: application/json

{
  "name": "Admin User",
  "email": "admin@test.com",
  "phone": "4567890123",
  "password": "admin123",
  "role": "ADMIN"
}
```

---

### Test 3: Login and Get JWT Token

```http
POST http://localhost:8082/api/auth/login
Content-Type: application/json

{
  "email": "customer@test.com",
  "password": "password123"
}
```

**Expected Response (200 OK):**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VySWQiOjEsImVtYWlsIjoiY3VzdG9tZXJAdGVzdC5jb20iLCJyb2xlIjoiQ1VTVE9NRVIiLCJpYXQiOjE3MzA4MTAwMDAsImV4cCI6MTczMDgxMzYwMH0.signature",
  "type": "Bearer",
  "userId": 1,
  "email": "customer@test.com",
  "name": "John Customer",
  "role": "CUSTOMER"
}
```

**📋 Copy the token!** You'll use it in the next tests.

---

### Test 4: Access Protected Endpoints

#### Test 4A: Create Order (CUSTOMER Role)

```http
POST http://localhost:8083/api/orders
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
Content-Type: application/json

{
  "userId": 1,
  "restaurantId": 1,
  "addressId": 1,
  "items": [
    {
      "menuItemId": 1,
      "itemName": "Margherita Pizza",
      "quantity": 2,
      "price": 500.0
    }
  ]
}
```

**Expected Response (201 Created):**
```json
{
  "id": 1,
  "userId": 1,
  "restaurantId": 1,
  "status": "PLACED",
  "paymentStatus": "PENDING",
  "totalAmount": 1000.0,
  "items": [...]
}
```

---

#### Test 4B: Get User Orders (CUSTOMER Role)

```http
GET http://localhost:8083/api/orders/user/1
Authorization: Bearer <CUSTOMER_TOKEN>
```

**Expected Response (200 OK):**
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

#### Test 4C: Try to Access Other User's Orders (Should FAIL)

```http
GET http://localhost:8083/api/orders/user/2
Authorization: Bearer <CUSTOMER_TOKEN>
```

**Expected Response (403 Forbidden):**
```json
{
  "error": "Access Denied"
}
```

✅ **Security Working!** Customers can only see their own orders.

---

#### Test 4D: Try to View All Orders as CUSTOMER (Should FAIL)

```http
GET http://localhost:8083/api/orders
Authorization: Bearer <CUSTOMER_TOKEN>
```

**Expected Response (403 Forbidden):**
```json
{
  "error": "Access Denied",
  "message": "Access is denied"
}
```

✅ **Security Working!** Only ADMIN can view all orders.

---

### Test 5: ADMIN Access (Full Permissions)

#### Login as ADMIN
```http
POST http://localhost:8082/api/auth/login
Content-Type: application/json

{
  "email": "admin@test.com",
  "password": "admin123"
}
```

#### View All Orders (ADMIN Role)
```http
GET http://localhost:8083/api/orders
Authorization: Bearer <ADMIN_TOKEN>
```

**Expected Response (200 OK):**
```json
[
  {
    "id": 1,
    "userId": 1,
    "status": "PLACED"
  },
  {
    "id": 2,
    "userId": 2,
    "status": "DELIVERED"
  }
]
```

✅ **Success!** Admin can see all orders.

---

### Test 6: RESTAURANT Updates Order Status

#### Login as RESTAURANT
```http
POST http://localhost:8082/api/auth/login
Content-Type: application/json

{
  "email": "restaurant@test.com",
  "password": "password123"
}
```

#### Update Order Status
```http
PUT http://localhost:8083/api/orders/1/status?status=PREPARING
Authorization: Bearer <RESTAURANT_TOKEN>
```

**Expected Response (200 OK):**
```json
{
  "id": 1,
  "status": "PREPARING",
  "paymentStatus": "PENDING"
}
```

✅ **Success!** Restaurant can update order status.

---

### Test 7: DELIVERY Assigns Partner

#### Login as DELIVERY
```http
POST http://localhost:8082/api/auth/login
Content-Type: application/json

{
  "email": "delivery@test.com",
  "password": "password123"
}
```

#### Assign Delivery Partner
```http
PUT http://localhost:8083/api/orders/1/assign-delivery?deliveryPartnerId=5
Authorization: Bearer <DELIVERY_TOKEN>
```

**Expected Response (200 OK):**
```json
{
  "id": 1,
  "deliveryPartnerId": 5,
  "status": "PREPARING"
}
```

✅ **Success!** Delivery partner can assign themselves.

---

### Test 8: Unauthorized Access (No Token)

```http
GET http://localhost:8083/api/orders/1
```

**Expected Response (401 Unauthorized):**
```json
{
  "error": "Unauthorized",
  "message": "Full authentication is required to access this resource"
}
```

✅ **Security Working!** Endpoints are protected.

---

### Test 9: Invalid Token

```http
GET http://localhost:8083/api/orders/1
Authorization: Bearer invalid_token_here
```

**Expected Response (401 Unauthorized):**
```json
{
  "error": "Unauthorized"
}
```

✅ **Security Working!** Invalid tokens are rejected.

---

### Test 10: Token Expiration

1. Login and get token
2. Change JWT_EXPIRATION to 60000 (1 minute)
3. Restart services
4. Login again
5. Wait 1 minute
6. Try to access protected endpoint

**Expected Response (401 Unauthorized):**
```json
{
  "error": "Unauthorized",
  "message": "JWT token has expired"
}
```

✅ **Security Working!** Expired tokens are rejected.

---

## 📊 Test Results Summary

| Test | Endpoint | Role | Expected | Status |
|------|----------|------|----------|--------|
| Register | POST /api/auth/register | Public | 201 Created | ✅ |
| Login | POST /api/auth/login | Public | 200 OK | ✅ |
| Create Order | POST /api/orders | CUSTOMER | 201 Created | ✅ |
| Get Own Orders | GET /api/orders/user/{id} | CUSTOMER | 200 OK | ✅ |
| Get Other Orders | GET /api/orders/user/{id} | CUSTOMER | 403 Forbidden | ✅ |
| View All Orders | GET /api/orders | CUSTOMER | 403 Forbidden | ✅ |
| View All Orders | GET /api/orders | ADMIN | 200 OK | ✅ |
| Update Status | PUT /api/orders/{id}/status | RESTAURANT | 200 OK | ✅ |
| Assign Delivery | PUT /api/orders/{id}/assign-delivery | DELIVERY | 200 OK | ✅ |
| No Token | GET /api/orders/{id} | None | 401 Unauthorized | ✅ |
| Invalid Token | GET /api/orders/{id} | None | 401 Unauthorized | ✅ |
| Expired Token | GET /api/orders/{id} | CUSTOMER | 401 Unauthorized | ✅ |

---

## 🐛 Common Issues and Solutions

### Issue 1: "Could not resolve jwt.secret"
**Cause:** JWT_SECRET environment variable not set

**Solution:**
1. Set JWT_SECRET in IntelliJ Run Configuration
2. Restart service
3. Verify with: `echo %JWT_SECRET%` (Windows) or `echo $JWT_SECRET` (Mac/Linux)

### Issue 2: "401 Unauthorized" even with valid token
**Cause:** Different JWT secrets between User Service and Order Service

**Solution:**
- Use the SAME JWT_SECRET value for both services
- Restart both services

### Issue 3: "Key length must be at least 256 bits"
**Cause:** JWT secret too short

**Solution:**
- Use a secret with at least 64 characters
- Generate new secret: `openssl rand -base64 64`

### Issue 4: "Email already exists"
**Cause:** User already registered

**Solution:**
- Use different email
- OR login with existing credentials

---

## ✅ Security Checklist

- [ ] JWT_SECRET environment variable configured
- [ ] Same JWT_SECRET used across all services
- [ ] All services started successfully
- [ ] Services registered with Eureka
- [ ] Users registered with different roles
- [ ] Login successful and JWT token received
- [ ] Protected endpoints require authentication
- [ ] Role-based access control working
- [ ] Customers can only access their own data
- [ ] Admin has full access to all resources
- [ ] Invalid tokens rejected with 401
- [ ] Expired tokens rejected with 401
- [ ] Insufficient permissions return 403

---

## 🎉 Congratulations!

If all tests pass, you have successfully implemented:

✅ **Complete Spring Security & JWT Authentication**  
✅ **Role-Based Access Control (RBAC)**  
✅ **Password Encryption with BCrypt**  
✅ **Stateless Authentication Across Microservices**  
✅ **Production-Ready Security Layer**  

Your microservices are now **SECURE** and ready for production! 🔒
