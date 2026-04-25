# 🚀 Complete Setup Guide for IntelliJ IDEA (No CMD Required)

This guide helps you run the Food Delivery Microservices application in IntelliJ IDEA using only GUI (no command line needed).

---

## 📋 STEP 1: Install Prerequisites (Download & Install)

### 1.1 Java Development Kit (JDK 17)
1. **Download**: Go to https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html
   - OR use OpenJDK: https://adoptium.net/temurin/releases/?version=17
2. **Install**: 
   - Windows: Run the `.exe` installer → Click Next → Next → Install
   - Mac: Run the `.dmg` installer → Drag to Applications
3. **Verify**: After installation, JDK will be at:
   - Windows: `C:\Program Files\Java\jdk-17`
   - Mac: `/Library/Java/JavaVirtualMachines/jdk-17.jdk`

### 1.2 MySQL Server 8.0
1. **Download**: https://dev.mysql.com/downloads/mysql/
   - Choose: MySQL Community Server 8.0
2. **Install**:
   - Windows: Run installer → Choose "Developer Default" → Set root password as `password`
   - Mac: Run `.dmg` → Set root password as `password`
3. **MySQL Workbench** (Included in installer):
   - This is your GUI tool to manage databases
   - We'll use this instead of CMD!

### 1.3 Redis Server
**Windows:**
1. Download: https://github.com/tporadowski/redis/releases
2. Download `Redis-x64-5.0.14.msi`
3. Install: Run installer → Next → Next → Install
4. Redis will auto-start as Windows Service

**Mac:**
1. Download: https://redis.io/download
2. Or use Homebrew GUI: https://brew.sh/
3. After Homebrew: Open Terminal once → Type `brew install redis`
4. Start Redis: `brew services start redis`

### 1.4 Apache Kafka + Zookeeper
1. **Download**: https://kafka.apache.org/downloads
   - Choose: Scala 2.13 → kafka_2.13-3.6.0.tgz
2. **Extract**:
   - Windows: Extract to `C:\kafka`
   - Mac: Extract to `/usr/local/kafka`

### 1.5 IntelliJ IDEA
1. **Download**: https://www.jetbrains.com/idea/download/
   - Community Edition (Free) is enough!
2. **Install**: Run installer → Next → Next → Install

---

## 📦 STEP 2: Create MySQL Databases (Using MySQL Workbench - GUI)

### 2.1 Open MySQL Workbench
1. Start **MySQL Workbench** (installed with MySQL)
2. Click on "Local instance MySQL80" connection
3. Enter password: `password`

### 2.2 Create Databases (Click-based)
1. In MySQL Workbench, click **"Create Schema"** button (cylinder icon with +)
2. Create these 6 databases one by one:

   **Database 1:**
   - Schema Name: `restaurant_db`
   - Click **Apply** → Click **Apply** again → Click **Finish**

   **Database 2:**
   - Schema Name: `user_db`
   - Click **Apply** → Apply → Finish

   **Database 3:**
   - Schema Name: `order_db`
   - Click **Apply** → Apply → Finish

   **Database 4:**
   - Schema Name: `delivery_db`
   - Click **Apply** → Apply → Finish

   **Database 5:**
   - Schema Name: `payment_db`
   - Click **Apply** → Apply → Finish

   **Database 6:**
   - Schema Name: `rating_db`
   - Click **Apply** → Apply → Finish

### 2.3 Verify Databases
You should see all 6 databases in the left panel under "Schemas"

---

## ⚙️ STEP 3: Start Kafka & Zookeeper (Using Windows GUI)

### For Windows:

#### 3.1 Create Batch Files (Double-click to run - No CMD typing!)

**Create File 1: `start-zookeeper.bat`**
1. Right-click on Desktop → New → Text Document
2. Name it: `start-zookeeper.bat`
3. Right-click → Edit → Paste this:
   ```batch
   cd C:\kafka
   bin\windows\zookeeper-server-start.bat config\zookeeper.properties
   pause
   ```
4. Save and close

**Create File 2: `start-kafka.bat`**
1. Right-click on Desktop → New → Text Document
2. Name it: `start-kafka.bat`
3. Right-click → Edit → Paste this:
   ```batch
   cd C:\kafka
   bin\windows\kafka-server-start.bat config\server.properties
   pause
   ```
4. Save and close

#### 3.2 Start Services (Just Double-Click!)
1. **Double-click** `start-zookeeper.bat`
   - A window will open → Wait 10 seconds → You'll see "binding to port 0.0.0.0/0.0.0.0:2181"
   - **Keep this window open!**

2. **Wait 10 seconds**, then **Double-click** `start-kafka.bat`
   - A window will open → Wait 15 seconds → You'll see "started (kafka.server.KafkaServer)"
   - **Keep this window open!**

✅ Kafka & Zookeeper are now running!

### For Mac:

**Create Shell Scripts:**

**File 1: `start-zookeeper.command`**
```bash
#!/bin/bash
cd /usr/local/kafka
bin/zookeeper-server-start.sh config/zookeeper.properties
```
Make executable: Right-click → Get Info → Open with: Terminal

**File 2: `start-kafka.command`**
```bash
#!/bin/bash
cd /usr/local/kafka
bin/kafka-server-start.sh config/server.properties
```

Double-click to run!

---

## 🎯 STEP 4: Open Project in IntelliJ IDEA

### 4.1 Import Project
1. Open **IntelliJ IDEA**
2. Click **Open**
3. Navigate to your project folder (where `pom.xml` is)
4. Select the **root folder** → Click **OK**
5. IntelliJ will detect Maven → Click **Trust Project**

### 4.2 Wait for Maven Download
- IntelliJ will automatically download all dependencies
- Look at bottom-right corner → Wait for "Indexing..." to finish (2-5 minutes)
- Wait for "Downloading dependencies..." to finish

### 4.3 Configure JDK
1. Go to **File** → **Project Structure** (or press `Ctrl+Alt+Shift+S`)
2. Under **Project**:
   - SDK: Select JDK 17 (if not listed, click **Add SDK** → **Download JDK** → Version 17)
   - Language Level: 17
3. Click **Apply** → **OK**

---

## 🏃 STEP 5: Run Services in IntelliJ (GUI - No CMD!)

### 5.1 Configure Run Configurations

IntelliJ IDEA has a built-in GUI to run Spring Boot applications. Here's how:

#### Service 1: Eureka Server (Service Discovery)

1. In Project Explorer, navigate to:
   ```
   eureka-server/src/main/java/com/fooddelivery/eureka/EurekaServerApplication.java
   ```

2. **Right-click** on `EurekaServerApplication.java`

3. Select **Run 'EurekaServerApplication'** (or click the green ▶️ play button)

4. Wait for console to show:
   ```
   🌐 Eureka Server Started Successfully on Port 8761!
   ```

5. **Verify**: Open browser → http://localhost:8761
   - You should see Eureka Dashboard

#### Service 2: API Gateway

1. Navigate to:
   ```
   api-gateway/src/main/java/com/fooddelivery/gateway/ApiGatewayApplication.java
   ```

2. **Right-click** → **Run 'ApiGatewayApplication'**

3. Wait for:
   ```
   🚪 API Gateway Started Successfully on Port 8080!
   ```

#### Service 3: Restaurant Service

1. Navigate to:
   ```
   restaurant-service/src/main/java/com/fooddelivery/restaurant/RestaurantServiceApplication.java
   ```

2. **Right-click** → **Run 'RestaurantServiceApplication'**

3. Wait for:
   ```
   🍽️ Restaurant Service Started Successfully on Port 8081!
   ```

#### Service 4: User Service

1. Navigate to:
   ```
   user-service/src/main/java/com/fooddelivery/user/UserServiceApplication.java
   ```

2. **Right-click** → **Run 'UserServiceApplication'**

3. Wait for:
   ```
   👤 User Service Started Successfully on Port 8082!
   ```

#### Service 5: Order Service

1. Navigate to:
   ```
   order-service/src/main/java/com/fooddelivery/order/OrderServiceApplication.java
   ```

2. **Right-click** → **Run 'OrderServiceApplication'**

3. Wait for:
   ```
   📦 Order Service Started Successfully on Port 8083!
   ```

#### Service 6: Payment Service

1. Navigate to:
   ```
   payment-service/src/main/java/com/fooddelivery/payment/PaymentServiceApplication.java
   ```

2. **Right-click** → **Run 'PaymentServiceApplication'**

3. Wait for:
   ```
   💳 Payment Service Started Successfully on Port 8085!
   ```

#### Service 7: Delivery Service

1. Navigate to:
   ```
   delivery-service/src/main/java/com/fooddelivery/delivery/DeliveryServiceApplication.java
   ```

2. **Right-click** → **Run 'DeliveryServiceApplication'**

3. Wait for:
   ```
   🚗 Delivery Service Started Successfully on Port 8084!
   ```

#### Service 8: Notification Service

1. Navigate to:
   ```
   notification-service/src/main/java/com/fooddelivery/notification/NotificationServiceApplication.java
   ```

2. **Right-click** → **Run 'NotificationServiceApplication'**

3. Wait for:
   ```
   📧 Notification Service Started Successfully on Port 8086!
   ```

#### Service 9: Rating Service

1. Navigate to:
   ```
   rating-service/src/main/java/com/fooddelivery/rating/RatingServiceApplication.java
   ```

2. **Right-click** → **Run 'RatingServiceApplication'**

3. Wait for:
   ```
   ⭐ Rating Service Started Successfully on Port 8087!
   ```

---

## 🎮 STEP 6: Managing Multiple Services in IntelliJ

### 6.1 Services Tool Window
1. Go to **View** → **Tool Windows** → **Services** (or press `Alt+8`)
2. You'll see all running services here
3. You can **Stop/Restart** any service by right-clicking

### 6.2 Run Dashboard
1. Go to **View** → **Tool Windows** → **Run Dashboard**
2. Shows all services in one view
3. Green circle = Running, Red circle = Stopped

### 6.3 Compound Run Configuration (Run All at Once!)

**Create a Single-Click Startup:**

1. Click **Run** → **Edit Configurations**
2. Click **+** (Add) → Select **Compound**
3. Name: `All Microservices`
4. Click **+** in the right panel
5. Add services **in this order**:
   - EurekaServerApplication
   - ApiGatewayApplication
   - RestaurantServiceApplication
   - UserServiceApplication
   - OrderServiceApplication
   - PaymentServiceApplication
   - DeliveryServiceApplication
   - NotificationServiceApplication
   - RatingServiceApplication

6. Click **Apply** → **OK**

**Now you can start ALL services with one click!**
- Select "All Microservices" from dropdown → Click ▶️ Play

---

## 🧪 STEP 7: Test the Application (Using Postman - GUI Tool)

### 7.1 Install Postman
1. Download: https://www.postman.com/downloads/
2. Install and open Postman

### 7.2 Create Test Requests

#### Test 1: Create a Restaurant
1. In Postman, create **New Request**
2. Method: **POST**
3. URL: `http://localhost:8080/api/restaurants`
4. Go to **Body** → **raw** → **JSON**
5. Paste:
   ```json
   {
     "name": "Pizza Palace",
     "address": "123 Main St",
     "phone": "1234567890",
     "cuisineType": "Italian"
   }
   ```
6. Click **Send**
7. You should get response with restaurant ID

#### Test 2: Create a User
1. Method: **POST**
2. URL: `http://localhost:8080/api/users`
3. Body (JSON):
   ```json
   {
     "name": "John Doe",
     "email": "john@example.com",
     "phone": "9876543210",
     "address": "456 Oak Ave"
   }
   ```
4. Click **Send**

#### Test 3: Create an Order (Full Flow Test!)
1. Method: **POST**
2. URL: `http://localhost:8080/api/orders`
3. Body (JSON):
   ```json
   {
     "userId": 1,
     "restaurantId": 1,
     "items": [
       {
         "menuItemId": 1,
         "quantity": 2,
         "price": 299.99
       }
     ],
     "deliveryAddress": "456 Oak Ave"
   }
   ```
4. Click **Send**

**Watch the Magic!** 🎉

Go back to IntelliJ → Check the console logs:

1. **Order Service** → Creates order → Publishes ORDER_CREATED event
2. **Payment Service** → Receives event → Processes payment → Publishes PAYMENT_COMPLETED
3. **Delivery Service** → Receives event → Assigns partner → Publishes DELIVERY_ASSIGNED
4. **Notification Service** → Sends notifications at each step!

You'll see colorful logs showing the entire flow!

---

## 📊 STEP 8: Monitor Services (GUI Tools)

### 8.1 Eureka Dashboard
- URL: http://localhost:8761
- See all registered services

### 8.2 MySQL Workbench
- Open MySQL Workbench
- Right-click database → **Select Rows** → See data being created

### 8.3 IntelliJ Database Tool
1. In IntelliJ: **View** → **Tool Windows** → **Database**
2. Click **+** → **Data Source** → **MySQL**
3. Add connection:
   - Host: `localhost`
   - Port: `3306`
   - User: `root`
   - Password: `password`
   - Database: `order_db` (repeat for all 6 databases)
4. Now you can browse database tables right in IntelliJ!

---

## 🛠️ Common Issues & Solutions

### Issue 1: Port Already in Use
**Solution in IntelliJ:**
1. Stop the conflicting service
2. Go to **Run** → **Edit Configurations**
3. Change the port in `application.yml`

### Issue 2: MySQL Connection Failed
**Check in MySQL Workbench:**
1. Verify all 6 databases exist
2. Test Connection button should be green
3. Password should be `password`

### Issue 3: Kafka Not Running
**For Windows:**
1. Check if both batch file windows are still open
2. If closed, double-click `start-zookeeper.bat` and `start-kafka.bat` again

### Issue 4: Redis Connection Failed
**For Windows:**
1. Open **Services** (Windows Search → "Services")
2. Find "Redis" → Right-click → **Start**

**For Mac:**
1. Open **Activity Monitor**
2. Search for "redis" → Should be running

---

## ✅ Startup Checklist (Every Time You Start)

Before running services in IntelliJ:

- ☑️ MySQL is running (check MySQL Workbench connection)
- ☑️ Redis is running (Windows Services / Mac Activity Monitor)
- ☑️ Zookeeper is running (batch file window open)
- ☑️ Kafka is running (batch file window open)

Then in IntelliJ:
- ☑️ Run "All Microservices" compound configuration
- ☑️ Wait for all services to show "Started Successfully"
- ☑️ Check Eureka Dashboard (http://localhost:8761)

---

## 🎓 Learning Tips

1. **Start Small**: Run one service at a time to understand each
2. **Read Logs**: IntelliJ console shows detailed logs with emojis
3. **Use Breakpoints**: Set breakpoints to debug code step-by-step
4. **Database GUI**: Use IntelliJ's Database tool to see data changes
5. **Postman Collections**: Save all your test requests in Postman

---

## 🚀 Quick Start Script (Bookmark This!)

**Every day startup routine:**

1. **Double-click** `start-zookeeper.bat` (keep window open)
2. **Wait 10 seconds**
3. **Double-click** `start-kafka.bat` (keep window open)
4. **Wait 10 seconds**
5. **Open IntelliJ IDEA**
6. **Click** "All Microservices" → **Play** ▶️
7. **Wait 2 minutes** for all services to start
8. **Open** http://localhost:8761 (verify all 7 services registered)
9. **Open Postman** → Start testing!

---

**You're all set! No CMD/Terminal needed!** 🎉
