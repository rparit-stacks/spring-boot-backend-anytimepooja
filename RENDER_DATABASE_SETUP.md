# 🗄️ Render Database Setup Guide

## Step-by-Step: Create PostgreSQL Database in Render

### Step 1: Create PostgreSQL Addon

1. **Go to Render Dashboard**
   - Log in to [render.com](https://render.com)
   - You should see your dashboard

2. **Create New PostgreSQL Database**
   - Click **"New +"** button (top right)
   - Select **"PostgreSQL"** from the dropdown

3. **Configure Database**
   - **Name**: `anytime-pooja-db` (or any name you prefer)
   - **Database**: Leave default or name it (e.g., `anytime_pooja`)
   - **User**: Leave default or customize
   - **Region**: Choose **Oregon (US West)** (same as your web service)
   - **Plan**: 
     - **Free** for testing (limited connections, spins down)
     - **Starter ($7/month)** for production (always on)

4. **Click "Create Database"**
   - Render will create your PostgreSQL database
   - This takes 1-2 minutes

---

### Step 2: Get Database Connection Details

After the database is created:

1. **Click on your database** in the dashboard
2. **Go to "Info" tab** (or "Connections" tab)
3. **You'll see these details:**

   - **Internal Database URL**: `postgresql://user:password@host:port/database`
   - **Host**: `dpg-xxxxx-a.oregon-postgres.render.com`
   - **Port**: `5432` (usually)
   - **Database Name**: `your_database_name`
   - **User**: `your_username`
   - **Password**: `your_password` (click "Show" to reveal)

4. **Copy these values** - You'll need them for environment variables

---

### Step 3: Connect Database to Your Web Service

#### Option A: Automatic Connection (Recommended)

1. **In your Web Service settings:**
   - Go to your web service → **Environment** tab
   - Scroll down to **"Addons"** section
   - Click **"Link Database"**
   - Select your PostgreSQL database
   - Render will automatically add these environment variables:
     - `DATABASE_URL`
     - `DATABASE_USERNAME`
     - `DATABASE_PASSWORD`
     - `DATABASE_HOST`
     - `DATABASE_PORT`
     - `DATABASE_NAME`

2. **Then add these Spring Boot variables:**
   ```
   SPRING_DATASOURCE_URL=jdbc:postgresql://${DATABASE_HOST}:${DATABASE_PORT}/${DATABASE_NAME}?sslmode=require
   SPRING_DATASOURCE_USERNAME=${DATABASE_USERNAME}
   SPRING_DATASOURCE_PASSWORD=${DATABASE_PASSWORD}
   ```

#### Option B: Manual Connection

1. **Copy database details** from Step 2
2. **Go to your Web Service** → **Environment** tab
3. **Add these environment variables manually:**

   ```
   SPRING_DATASOURCE_URL=jdbc:postgresql://dpg-xxxxx-a.oregon-postgres.render.com:5432/your_database_name?sslmode=require
   SPRING_DATASOURCE_USERNAME=your_username
   SPRING_DATASOURCE_PASSWORD=your_password
   SPRING_DATASOURCE_DRIVER_CLASS_NAME=org.postgresql.Driver
   SPRING_JPA_HIBERNATE_DIALECT=org.hibernate.dialect.PostgreSQLDialect
   ```

   **Replace:**
   - `dpg-xxxxx-a.oregon-postgres.render.com` → Your actual host
   - `your_database_name` → Your actual database name
   - `your_username` → Your actual username
   - `your_password` → Your actual password

---

### Step 4: Verify Connection

1. **Deploy your service** (or redeploy if already deployed)
2. **Check Logs** in Render dashboard
3. **Look for:**
   - ✅ `HikariPool-1 - Start completed` (connection successful)
   - ✅ `Started PoojaApplication` (app started)
   - ❌ Any database connection errors

4. **Test Health Endpoint:**
   - Visit: `https://your-app.onrender.com/api/actuator/health`
   - Should return: `{"status":"UP"}`

---

## 📋 Quick Checklist

- [ ] Created PostgreSQL addon in Render
- [ ] Copied database connection details
- [ ] Added database environment variables to web service
- [ ] Set `SPRING_DATASOURCE_DRIVER_CLASS_NAME=org.postgresql.Driver`
- [ ] Set `SPRING_JPA_HIBERNATE_DIALECT=org.hibernate.dialect.PostgreSQLDialect`
- [ ] Deployed/Redeployed service
- [ ] Verified connection in logs
- [ ] Tested health endpoint

---

## 🔍 Where to Find Database Details in Render

### Method 1: Database Info Tab
1. Click on your PostgreSQL database
2. Go to **"Info"** tab
3. See all connection details

### Method 2: Internal Database URL
- Format: `postgresql://user:password@host:port/database`
- Example: `postgresql://anytime_user:abc123@dpg-abc123-a.oregon-postgres.render.com:5432/anytime_pooja`

**To convert to JDBC URL:**
- Change `postgresql://` to `jdbc:postgresql://`
- Add `?sslmode=require` at the end
- Example: `jdbc:postgresql://dpg-abc123-a.oregon-postgres.render.com:5432/anytime_pooja?sslmode=require`

---

## ⚠️ Important Notes

1. **Internal vs External URL:**
   - Use **Internal Database URL** if web service and database are in same region
   - Use **External Database URL** if connecting from outside Render

2. **SSL Required:**
   - Always add `?sslmode=require` to your JDBC URL
   - Render databases require SSL connections

3. **Free Tier Limitations:**
   - Database spins down after inactivity
   - First connection after spin-down takes ~30 seconds
   - Limited to 90 connections
   - Upgrade to paid plan for always-on database

4. **Connection Pooling:**
   - Your app uses HikariCP (configured in application.properties)
   - Max pool size: 10 connections
   - This is fine for most applications

---

## 🆘 Troubleshooting

### Database Connection Failed
- ✅ Check host, port, database name, username, password are correct
- ✅ Verify `sslmode=require` is in JDBC URL
- ✅ Ensure database is running (check status in Render)
- ✅ Check if database and web service are in same region

### "Database does not exist" Error
- ✅ Verify database name is correct
- ✅ Check if you're using the right database (not the default `postgres`)

### "Connection timeout" Error
- ✅ Check if using Internal URL (not External)
- ✅ Verify database is not spinning down (upgrade plan if needed)
- ✅ Check firewall/network settings

### "SSL required" Error
- ✅ Add `?sslmode=require` to JDBC URL
- ✅ Verify using correct connection string format

---

## 📝 Example Environment Variables

After setting up, your environment variables should look like:

```
SPRING_DATASOURCE_URL=jdbc:postgresql://dpg-abc123xyz-a.oregon-postgres.render.com:5432/anytime_pooja?sslmode=require
SPRING_DATASOURCE_USERNAME=anytime_user
SPRING_DATASOURCE_PASSWORD=your_secure_password_here
SPRING_DATASOURCE_DRIVER_CLASS_NAME=org.postgresql.Driver
SPRING_JPA_HIBERNATE_DIALECT=org.hibernate.dialect.PostgreSQLDialect
```

---

## ✅ Next Steps

After database is set up:
1. Deploy your application
2. Tables will be auto-created (because `spring.jpa.hibernate.ddl-auto=update`)
3. Test your API endpoints
4. Monitor logs for any issues

