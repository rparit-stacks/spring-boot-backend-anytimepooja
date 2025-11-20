# 🚀 Render Environment Variables - Quick Copy List

## 📋 Copy & Paste These Into Render

### 🔴 REQUIRED - Database (PostgreSQL)
```
SPRING_DATASOURCE_URL=jdbc:postgresql://YOUR_HOST:5432/YOUR_DATABASE?sslmode=require
SPRING_DATASOURCE_USERNAME=YOUR_DB_USERNAME
SPRING_DATASOURCE_PASSWORD=YOUR_DB_PASSWORD
SPRING_DATASOURCE_DRIVER_CLASS_NAME=org.postgresql.Driver
SPRING_JPA_HIBERNATE_DIALECT=org.hibernate.dialect.PostgreSQLDialect
```

### 🔴 REQUIRED - Application
```
SPRING_PROFILES_ACTIVE=production
JAVA_OPTS=-Xmx512m -Xms256m
PORT=8080
```

### 🔴 REQUIRED - Email (Gmail)
```
SPRING_MAIL_USERNAME=your-email@gmail.com
SPRING_MAIL_PASSWORD=your-gmail-app-password
```

### 🔴 REQUIRED - Payment (Razorpay)
```
RAZORPAY_KEY_ID=rzp_test_YOUR_KEY_ID
RAZORPAY_KEY_SECRET=YOUR_KEY_SECRET
RAZORPAY_CURRENCY=INR
RAZORPAY_COMPANY_NAME=Anytime Pooja
```

### 🔴 REQUIRED - CORS
```
APP_CORS_ALLOWED_ORIGINS=https://your-frontend.onrender.com,http://localhost:3000
APP_CORS_ALLOWED_METHODS=GET,POST,PUT,DELETE,OPTIONS
APP_CORS_ALLOWED_HEADERS=*
APP_CORS_ALLOW_CREDENTIALS=true
```

### 🟡 OPTIONAL - SMS (Twilio)
```
TWILIO_ACCOUNT_SID=YOUR_ACCOUNT_SID
TWILIO_AUTH_TOKEN=YOUR_AUTH_TOKEN
TWILIO_PHONE_NUMBER=+1234567890
```

### 🟡 OPTIONAL - Firebase
```
FIREBASE_CONFIG_PATH=classpath:firebase-service-account.json
FIREBASE_DATABASE_URL=https://anytime-pooja.firebaseio.com
```

### 🟡 OPTIONAL - Logging
```
LOGGING_LEVEL_ROOT=INFO
LOGGING_LEVEL_COM_ANYTIME_POOJA=INFO
```

---

## 📝 Instructions

1. **For Database**: First create a PostgreSQL addon in Render, then copy the connection details
2. **Replace placeholders**: Replace all `YOUR_*` and `your-*` values with actual values
3. **Add one by one**: In Render, click "Add Environment Variable" for each line above
4. **Format**: 
   - **Name** = Left side (before =)
   - **Value** = Right side (after =)

## ⚠️ Important

- Remove the backticks (```) when copying
- Each line is a separate environment variable
- Replace placeholder values with your actual credentials
- Database values come from Render's PostgreSQL addon

