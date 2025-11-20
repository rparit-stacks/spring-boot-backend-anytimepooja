# 🚀 Render Configuration - Quick Reference

## ⚙️ Current Render Form Settings

### Basic Configuration
- **Service Type**: Web Service ✅
- **Name**: `spring-boot-backend-anytimepooja` ✅
- **Language**: Docker ✅
- **Branch**: `main` ✅
- **Region**: Oregon (US West) ✅

### 🔴 CRITICAL: Root Directory
**Set this to**: `anytime-pooja-backend`

This is **REQUIRED** because your repository is a monorepo and the Dockerfile is in the `anytime-pooja-backend` folder.

### Instance Type
- Start with **Free** for testing
- Upgrade to **Starter ($7/month)** for production (always-on)

---

## 📝 Environment Variables to Add

Click **"Add Environment Variable"** for each of these:

### 1. Database Configuration (PostgreSQL from Render)

**First, create a PostgreSQL addon:**
- Go to Render Dashboard → **Addons** → **PostgreSQL**
- Create a new database
- Copy the **Internal Database URL** (format: `postgresql://user:password@host:port/database`)

**Then add these environment variables:**

```
SPRING_DATASOURCE_URL=jdbc:postgresql://<host>:<port>/<database>?sslmode=require
SPRING_DATASOURCE_USERNAME=<username>
SPRING_DATASOURCE_PASSWORD=<password>
SPRING_DATASOURCE_DRIVER_CLASS_NAME=org.postgresql.Driver
SPRING_JPA_HIBERNATE_DIALECT=org.hibernate.dialect.PostgreSQLDialect
```

**Or use Render's auto-provided variables:**
```
SPRING_DATASOURCE_URL=${DATABASE_URL}
SPRING_DATASOURCE_USERNAME=${DATABASE_USERNAME}
SPRING_DATASOURCE_PASSWORD=${DATABASE_PASSWORD}
```

### 2. Application Settings

```
SPRING_PROFILES_ACTIVE=production
JAVA_OPTS=-Xmx512m -Xms256m
PORT=8080
```

### 3. Email Configuration (Gmail)

```
SPRING_MAIL_USERNAME=your-email@gmail.com
SPRING_MAIL_PASSWORD=your-gmail-app-password
```

### 4. Payment Gateway (Razorpay)

```
RAZORPAY_KEY_ID=rzp_test_YOUR_KEY_ID
RAZORPAY_KEY_SECRET=YOUR_KEY_SECRET
```

### 5. SMS (Twilio) - Optional

```
TWILIO_ACCOUNT_SID=YOUR_ACCOUNT_SID
TWILIO_AUTH_TOKEN=YOUR_AUTH_TOKEN
TWILIO_PHONE_NUMBER=+1234567890
```

### 6. CORS Configuration

```
APP_CORS_ALLOWED_ORIGINS=https://your-frontend.onrender.com,http://localhost:3000
```

---

## ✅ Final Steps

1. **Set Root Directory**: `anytime-pooja-backend` ⚠️ **IMPORTANT**
2. **Add all environment variables** listed above
3. **Health Check Path**: `/api/actuator/health` (your app uses `/api` context path)
4. **Click "Create Web Service"**

---

## 🔍 After Deployment

1. **Check Logs**: Monitor the Logs tab for any errors
2. **Test Health**: Visit `https://your-app.onrender.com/api/actuator/health`
3. **Test API**: Try `https://your-app.onrender.com/api/...`

---

## ⚠️ Important Notes

- **Database**: The app now supports both MySQL (local) and PostgreSQL (Render)
- **File Uploads**: Local file storage won't persist. Consider cloud storage for production
- **Free Tier**: Spins down after 15 min inactivity (cold start ~30 seconds)
- **Auto-Deploy**: Enable to deploy on every push to `main` branch

---

## 🆘 Troubleshooting

**Build fails?**
- Check Root Directory is set to `anytime-pooja-backend`
- Verify Dockerfile exists in that directory
- Check logs for specific errors

**App won't start?**
- Verify all environment variables are set
- Check database connection string format
- Ensure PostgreSQL addon is running

**Database connection error?**
- Verify `SPRING_DATASOURCE_URL` format
- Check PostgreSQL addon is created and running
- Ensure database credentials are correct

