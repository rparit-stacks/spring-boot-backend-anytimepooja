# Render Deployment Guide

## ✅ Pre-deployment Checklist

1. **Application Properties Updated**
   - ✅ `server.port=${PORT:8080}` - Now supports Render's dynamic port

2. **Build & Test Locally**
   ```bash
   ./mvnw clean package
   java -jar target/pooja-0.0.1-SNAPSHOT.jar
   ```

## 🚀 Render Deployment Steps

### 1. Prepare Repository
```bash
git add .
git commit -m "Prepare for Render deployment"
git push origin main
```

### 2. Create Render Account
- Go to [render.com](https://render.com)
- Sign up using GitHub (recommended for easiest flow)

### 3. Create New Web Service
- Dashboard → **New** → **Web Service**
- Connect your GitHub repository
- Select branch (e.g., `main`)

### 4. Configure Build Settings

**Option A: Using Maven (Recommended)**
- **Environment**: Java (auto-detect)
- **Build Command**: `./mvnw -B clean package -DskipTests`
- **Start Command**: `java -jar target/pooja-0.0.1-SNAPSHOT.jar`

**Option B: Using Docker**
- **Environment**: Docker
- Render will automatically detect and use the `Dockerfile`

### 5. Environment Variables

Add these in Render Dashboard → Environment → Add Environment Variable:

#### Required Database Configuration
```
SPRING_DATASOURCE_URL=${DATABASE_URL}
SPRING_DATASOURCE_USERNAME=<from_database_addon>
SPRING_DATASOURCE_PASSWORD=<from_database_addon>
```

#### Application Configuration
```
SPRING_PROFILES_ACTIVE=production
JAVA_OPTS=-Xmx512m -Xms256m
PORT=8080
```

#### Email Configuration (Gmail SMTP)
```
SPRING_MAIL_USERNAME=your-email@gmail.com
SPRING_MAIL_PASSWORD=your-app-password
```

#### Payment Gateway (Razorpay)
```
RAZORPAY_KEY_ID=rzp_test_YOUR_KEY_ID
RAZORPAY_KEY_SECRET=YOUR_KEY_SECRET
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
```

### 6. Database Setup

1. In Render Dashboard → **Addons** → **PostgreSQL**
2. Create a new PostgreSQL database
3. Copy the **Internal Database URL** or **External Database URL**
4. Add to environment variables:
   - `SPRING_DATASOURCE_URL` = The database URL from Render
   - `SPRING_DATASOURCE_USERNAME` = Database username
   - `SPRING_DATASOURCE_PASSWORD` = Database password

**Note**: Update `application.properties` to use environment variables:
```properties
spring.datasource.url=${SPRING_DATASOURCE_URL:jdbc:mysql://localhost:3306/anytime_pooja_web_backend?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=Asia/Kolkata}
spring.datasource.username=${SPRING_DATASOURCE_USERNAME:root}
spring.datasource.password=${SPRING_DATASOURCE_PASSWORD:}
```

### 7. Health Check
- **Health Check Path**: `/actuator/health`
- Render will use this to monitor your service

### 8. Deploy
- Click **Create Web Service**
- Render will build and deploy automatically
- Monitor the **Logs** tab for build/runtime errors

### 9. Post-Deployment

#### Test Your Deployment
- Visit the provided public URL
- Check health: `https://your-app.onrender.com/api/actuator/health`
- Test API endpoints

#### Custom Domain (Optional)
- Dashboard → Settings → **Add Custom Domain**
- Add DNS records as instructed by Render

#### Automatic Deploys
- Enable **Auto-Deploy** on push to your branch
- Every push will trigger a new deployment

## 🔧 Troubleshooting

### Build Fails
- Check logs in Render Dashboard
- Verify Maven wrapper is executable: `chmod +x mvnw`
- Ensure Java version matches (Java 21)

### Application Won't Start
- Check environment variables are set correctly
- Verify database connection string
- Check logs for specific error messages

### Database Connection Issues
- Ensure PostgreSQL addon is created and running
- Verify database URL format is correct
- Check firewall/network settings

### Port Issues
- Ensure `server.port=${PORT:8080}` is in application.properties
- Render automatically sets PORT environment variable

## 📝 Notes

- Render provides free tier with limitations (spins down after inactivity)
- For production, consider paid plans for always-on service
- File uploads: Consider using cloud storage (S3, Cloudinary) instead of local filesystem
- Logs are available in Render Dashboard → Logs tab

## 🔐 Security Reminders

- Never commit passwords or API keys to Git
- Use environment variables for all sensitive data
- Update `.gitignore` to exclude sensitive files
- Regularly rotate API keys and passwords

