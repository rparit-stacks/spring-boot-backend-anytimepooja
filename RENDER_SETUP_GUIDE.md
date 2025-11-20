# Render Setup Configuration Guide

## ⚙️ Render Web Service Configuration

### Basic Settings

1. **Service Type**: Web Service ✅
2. **Name**: `spring-boot-backend-anytimepooja` ✅
3. **Language**: Docker ✅
4. **Branch**: `main` ✅
5. **Region**: Oregon (US West) ✅

### ⚠️ IMPORTANT: Root Directory

Since this is a **monorepo**, you MUST set:

**Root Directory**: `anytime-pooja-backend`

This tells Render where to find the Dockerfile and build the application.

### Instance Type

- **Free** (for testing) - Spins down after inactivity
- **Starter ($7/month)** - Recommended for production (always on)

### Environment Variables

Add these environment variables in Render:

#### 🔴 CRITICAL - Database Configuration

First, create a **PostgreSQL** addon in Render, then add:

```
SPRING_DATASOURCE_URL=${DATABASE_URL}
SPRING_DATASOURCE_USERNAME=${DATABASE_USERNAME}
SPRING_DATASOURCE_PASSWORD=${DATABASE_PASSWORD}
```

Or if using the internal database URL from Render Postgres addon:
```
SPRING_DATASOURCE_URL=jdbc:postgresql://<host>:<port>/<database>?sslmode=require
SPRING_DATASOURCE_USERNAME=<username>
SPRING_DATASOURCE_PASSWORD=<password>
SPRING_DATASOURCE_DRIVER_CLASS_NAME=org.postgresql.Driver
```

**Note**: You'll need to add PostgreSQL driver to `pom.xml` if not already present.

#### Application Settings

```
SPRING_PROFILES_ACTIVE=production
JAVA_OPTS=-Xmx512m -Xms256m
PORT=8080
```

#### Email Configuration (Gmail SMTP)

```
SPRING_MAIL_HOST=smtp.gmail.com
SPRING_MAIL_PORT=587
SPRING_MAIL_USERNAME=your-email@gmail.com
SPRING_MAIL_PASSWORD=your-app-password
SPRING_MAIL_PROPERTIES_MAIL_SMTP_AUTH=true
SPRING_MAIL_PROPERTIES_MAIL_SMTP_STARTTLS_ENABLE=true
SPRING_MAIL_PROPERTIES_MAIL_SMTP_STARTTLS_REQUIRED=true
```

#### Payment Gateway (Razorpay)

```
RAZORPAY_KEY_ID=rzp_test_YOUR_KEY_ID
RAZORPAY_KEY_SECRET=YOUR_KEY_SECRET
RAZORPAY_CURRENCY=INR
RAZORPAY_COMPANY_NAME=Anytime Pooja
```

#### SMS Configuration (Twilio)

```
TWILIO_ACCOUNT_SID=YOUR_ACCOUNT_SID
TWILIO_AUTH_TOKEN=YOUR_AUTH_TOKEN
TWILIO_PHONE_NUMBER=+1234567890
```

#### Firebase (Push Notifications)

```
FIREBASE_CONFIG_PATH=classpath:firebase-service-account.json
FIREBASE_DATABASE_URL=https://anytime-pooja.firebaseio.com
```

#### CORS Configuration

```
APP_CORS_ALLOWED_ORIGINS=https://your-frontend-domain.com,http://localhost:3000
APP_CORS_ALLOWED_METHODS=GET,POST,PUT,DELETE,OPTIONS
APP_CORS_ALLOWED_HEADERS=*
APP_CORS_ALLOW_CREDENTIALS=true
```

### Health Check

**Health Check Path**: `/api/actuator/health`

(Note: Your app uses context path `/api`, so the health check includes that)

### Advanced Settings

- **Auto-Deploy**: Enable (deploys on every push to `main`)
- **Docker Build Context**: Leave empty (uses Root Directory)

## 📋 Quick Setup Checklist

- [ ] Set **Root Directory** to `anytime-pooja-backend`
- [ ] Create **PostgreSQL** addon
- [ ] Add database environment variables
- [ ] Add application environment variables
- [ ] Add email/SMS/payment API keys
- [ ] Set health check path: `/api/actuator/health`
- [ ] Click **Create Web Service**

## 🔍 After Deployment

1. Check **Logs** tab for any errors
2. Test health endpoint: `https://your-app.onrender.com/api/actuator/health`
3. Test API: `https://your-app.onrender.com/api/...`

## ⚠️ Important Notes

1. **Database Migration**: Your app uses `spring.jpa.hibernate.ddl-auto=update`, which will auto-create tables on first run
2. **File Storage**: Local file storage won't persist on Render. Consider using cloud storage (S3, Cloudinary) for uploads
3. **Free Tier**: Free instances spin down after 15 minutes of inactivity (cold starts take ~30 seconds)
4. **PostgreSQL**: You'll need to add PostgreSQL driver dependency if using PostgreSQL instead of MySQL

