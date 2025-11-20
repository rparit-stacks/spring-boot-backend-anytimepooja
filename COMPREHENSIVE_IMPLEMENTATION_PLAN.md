# Comprehensive Implementation Plan - Anytime Pooja Backend
## Enterprise-Grade Spring Boot Application

---

## 📋 Table of Contents
1. [Executive Summary](#executive-summary)
2. [System Architecture](#system-architecture)
3. [Technology Stack](#technology-stack)
4. [Database Design](#database-design)
5. [Module-Wise Implementation](#module-wise-implementation)
6. [API Endpoints (80+ APIs)](#api-endpoints)
7. [Security Architecture](#security-architecture)
8. [File Management System](#file-management-system)
9. [Payment Integration](#payment-integration)
10. [Real-Time Features](#real-time-features)
11. [Notification System](#notification-system)
12. [Analytics & Reporting](#analytics-reporting)
13. [Testing Strategy](#testing-strategy)
14. [Deployment Architecture](#deployment-architecture)

---

## 1. Executive Summary

### Project Overview
**Anytime Pooja** is a comprehensive digital platform connecting users with verified pandits for religious services, coupled with an e-commerce marketplace for pooja-related products.

### Budget: ₹65,000
### Timeline: 3-4 months
### Team Size: Backend Developer(s) + Frontend Developer(s)

### Core Modules (5)
1. **Pandit Application** - Pandit onboarding, profile management, booking handling
2. **User Application** - Service discovery, booking, payments
3. **Admin Panel** - Platform management, analytics, operations
4. **E-commerce (User)** - Product marketplace
5. **E-commerce (Admin)** - Inventory & order management

---

## 2. System Architecture

### Architecture Pattern
- **Layered Architecture**
  - Controller Layer (REST APIs)
  - Service Layer (Business Logic)
  - Repository Layer (Data Access)
  - DTO Layer (Data Transfer Objects)
  - Security Layer (Authentication & Authorization)

### Design Patterns
- **Repository Pattern** - Data access abstraction
- **DTO Pattern** - Data transfer and validation
- **Builder Pattern** - Complex object creation
- **Strategy Pattern** - Payment processing
- **Observer Pattern** - Notification system
- **Factory Pattern** - Report generation

---

## 3. Technology Stack

### Core Technologies
```yaml
Backend Framework: Spring Boot 3.5.7
Language: Java 21
Build Tool: Maven
Database: MySQL 8.0+
ORM: Hibernate (JPA)
Security: Spring Security + JWT
Validation: Hibernate Validator
Documentation: Swagger/OpenAPI 3.0
Testing: JUnit 5, Mockito, TestContainers
```

### Additional Dependencies
```xml
<!-- Already included -->
- spring-boot-starter-web
- spring-boot-starter-data-jpa
- spring-boot-starter-security
- spring-boot-starter-validation
- mysql-connector-j
- lombok

<!-- TO BE ADDED -->
- spring-boot-starter-mail (Email notifications)
- spring-boot-starter-websocket (Real-time chat)
- spring-boot-starter-cache (Redis/Caffeine)
- spring-boot-starter-actuator (Health monitoring)
- springdoc-openapi-ui (API Documentation)
- jjwt (JWT tokens)
- apache-commons-io (File handling)
- apache-poi (Excel reports)
- itextpdf (PDF generation)
- twilio-sdk (SMS notifications)
- razorpay-java (Payment gateway)
- firebase-admin (Push notifications)
- mapstruct (DTO mapping)
- querydsl (Dynamic queries)
```

---

## 4. Database Design

### Enhanced Entity Relationship Diagram

#### 4.1 User Management (6 tables)
```
users
├── id (PK)
├── name
├── email (unique)
├── phone (unique, indexed)
├── password (encrypted)
├── role (ENUM: USER, PANDIT, ADMIN)
├── is_active (boolean)
├── is_email_verified
├── is_phone_verified
├── profile_image_url
├── fcm_token (for push notifications)
├── last_login
├── created_at
└── updated_at

addresses
├── id (PK)
├── user_id (FK)
├── address_type (ENUM: HOME, WORK, OTHER)
├── street
├── landmark
├── city
├── state
├── country
├── zip_code
├── latitude
├── longitude
├── is_default
└── created_at

user_preferences
├── id (PK)
├── user_id (FK)
├── language (hindi, english, etc.)
├── notification_enabled
├── email_notification
├── sms_notification
├── push_notification
└── theme (light/dark)
```

#### 4.2 Pandit Management (8 tables)
```
pandit_profiles
├── id (PK)
├── user_id (FK, unique)
├── bio (text)
├── experience_years
├── languages (JSON/Set)
├── service_areas (JSON array of cities)
├── rating (calculated)
├── total_bookings
├── is_verified (KYC approved)
├── is_available (online/offline)
├── verification_date
├── rejection_reason
└── created_at

kyc_details
├── id (PK)
├── pandit_id (FK)
├── document_type (ENUM: AADHAR, PAN, PASSPORT)
├── document_number (encrypted)
├── front_image_url
├── back_image_url
├── selfie_image_url
├── status (ENUM: PENDING, APPROVED, REJECTED, RESUBMIT)
├── verified_by (admin_id)
├── verified_at
├── rejection_reason
├── submitted_at
└── updated_at

bank_details
├── id (PK)
├── pandit_id (FK, unique)
├── account_holder_name
├── account_number (encrypted)
├── ifsc_code
├── bank_name
├── branch_name
├── account_type (SAVINGS/CURRENT)
├── upi_id
├── is_verified
└── added_at

pandit_services
├── id (PK)
├── pandit_id (FK)
├── service_name
├── category_id (FK to service_categories)
├── description
├── price
├── duration_minutes
├── materials_included (boolean)
├── home_visit (boolean)
├── max_distance_km
├── is_active
├── image_url
└── created_at

service_categories
├── id (PK)
├── name (e.g., "Marriage", "Havan", "Grih Pravesh")
├── description
├── icon_url
├── is_active
└── display_order

pandit_availability
├── id (PK)
├── pandit_id (FK)
├── date
├── start_time
├── end_time
├── is_booked
├── slot_duration (default 60 min)
└── created_at

pandit_earnings
├── id (PK)
├── pandit_id (FK)
├── booking_id (FK)
├── amount
├── commission_percentage
├── commission_amount
├── net_amount
├── payout_status (ENUM: PENDING, PROCESSED, FAILED)
├── payout_date
├── payout_reference
└── earned_at

pandit_ratings_summary
├── pandit_id (PK, FK)
├── average_rating
├── total_reviews
├── five_star_count
├── four_star_count
├── three_star_count
├── two_star_count
├── one_star_count
└── last_updated
```

#### 4.3 Booking Management (5 tables)
```
bookings
├── id (PK)
├── booking_number (unique, indexed)
├── user_id (FK)
├── pandit_id (FK)
├── service_id (FK)
├── booking_date
├── booking_time
├── end_time
├── status (ENUM: PENDING, CONFIRMED, IN_PROGRESS, COMPLETED, CANCELLED, REJECTED)
├── cancellation_reason
├── cancelled_by (USER/PANDIT/ADMIN)
├── address_id (FK)
├── special_instructions (text)
├── total_amount
├── discount_amount
├── coupon_code
├── final_amount
├── payment_id
├── payment_method (ONLINE/COD)
├── payment_status (PENDING, PAID, FAILED, REFUNDED)
├── refund_amount
├── refund_status
├── confirmed_at
├── completed_at
├── created_at
└── updated_at

booking_timeline
├── id (PK)
├── booking_id (FK)
├── status
├── description
├── created_by (user_id)
└── created_at

reviews
├── id (PK)
├── booking_id (FK, unique)
├── user_id (FK)
├── pandit_id (FK)
├── rating (1-5)
├── comment (text)
├── is_anonymous
├── admin_response
├── is_reported
├── created_at
└── updated_at

support_tickets
├── id (PK)
├── ticket_number (unique)
├── user_id (FK)
├── booking_id (FK, nullable)
├── category (ENUM: BOOKING, PAYMENT, TECHNICAL, OTHER)
├── priority (ENUM: LOW, MEDIUM, HIGH, URGENT)
├── subject
├── message (text)
├── status (ENUM: OPEN, IN_PROGRESS, RESOLVED, CLOSED)
├── assigned_to (admin_id)
├── admin_reply (text)
├── resolved_at
├── created_at
└── updated_at

ticket_messages
├── id (PK)
├── ticket_id (FK)
├── sender_id (FK to users)
├── message (text)
├── attachment_url
└── created_at
```

#### 4.4 E-commerce (10 tables)
```
categories
├── id (PK)
├── name
├── description
├── parent_id (FK, nullable - for subcategories)
├── image_url
├── icon_url
├── is_active
├── display_order
└── created_at

products
├── id (PK)
├── sku (unique, indexed)
├── category_id (FK)
├── vendor_id (FK, nullable)
├── name
├── description (text)
├── short_description
├── price
├── mrp
├── discount_percentage
├── stock_quantity
├── low_stock_threshold
├── weight (grams)
├── dimensions (JSON: length, width, height)
├── is_featured
├── is_active
├── rating (calculated)
├── total_sales
├── views_count
├── tags (JSON array)
├── created_at
└── updated_at

product_images
├── id (PK)
├── product_id (FK)
├── image_url
├── display_order
├── is_primary
└── uploaded_at

product_variants
├── id (PK)
├── product_id (FK)
├── variant_name (e.g., "Size: Small")
├── sku
├── price
├── stock_quantity
└── created_at

carts
├── id (PK)
├── user_id (FK, unique)
├── subtotal
├── discount_amount
├── tax_amount
├── total_amount
├── coupon_code
├── created_at
└── updated_at

cart_items
├── id (PK)
├── cart_id (FK)
├── product_id (FK)
├── variant_id (FK, nullable)
├── quantity
├── price (snapshot at time of adding)
└── added_at

orders
├── id (PK)
├── order_number (unique, indexed)
├── user_id (FK)
├── subtotal
├── discount_amount
├── tax_amount
├── shipping_charges
├── total_amount
├── coupon_code
├── status (ENUM: PLACED, CONFIRMED, PACKED, SHIPPED, OUT_FOR_DELIVERY, DELIVERED, CANCELLED, RETURNED)
├── payment_id
├── payment_method
├── payment_status
├── shipping_address (JSON or address_id FK)
├── billing_address (JSON)
├── tracking_number
├── courier_partner
├── estimated_delivery
├── delivered_at
├── cancelled_at
├── cancellation_reason
├── return_initiated_at
├── return_reason
├── refund_amount
├── created_at
└── updated_at

order_items
├── id (PK)
├── order_id (FK)
├── product_id (FK)
├── variant_id (FK, nullable)
├── product_name (snapshot)
├── sku
├── quantity
├── price
├── discount
├── total
└── created_at

wishlists
├── id (PK)
├── user_id (FK)
├── product_id (FK)
└── added_at

coupons
├── id (PK)
├── code (unique, uppercase)
├── description
├── type (ENUM: PERCENTAGE, FIXED_AMOUNT)
├── discount_value
├── min_order_value
├── max_discount_amount
├── usage_limit (total)
├── usage_per_user
├── usage_count
├── applicable_categories (JSON array)
├── applicable_products (JSON array)
├── user_type (ALL, NEW_USER, PANDIT)
├── start_date
├── expiry_date
├── is_active
└── created_at
```

#### 4.5 Communication & Notifications (4 tables)
```
chat_conversations
├── id (PK)
├── user_id (FK)
├── pandit_id (FK)
├── booking_id (FK, nullable)
├── last_message
├── last_message_at
├── unread_count_user
├── unread_count_pandit
└── created_at

chat_messages
├── id (PK)
├── conversation_id (FK)
├── sender_id (FK)
├── message (text)
├── message_type (TEXT, IMAGE, FILE)
├── attachment_url
├── is_read
├── read_at
└── sent_at

notifications
├── id (PK)
├── user_id (FK)
├── title
├── message
├── type (BOOKING, PAYMENT, PROMOTION, SYSTEM)
├── reference_id (booking_id, order_id, etc.)
├── reference_type
├── is_read
├── read_at
├── data (JSON - extra data)
└── created_at

push_notification_logs
├── id (PK)
├── user_ids (JSON array)
├── title
├── message
├── data (JSON)
├── sent_by (admin_id)
├── total_sent
├── success_count
├── failure_count
└── sent_at
```

#### 4.6 System & Analytics (6 tables)
```
admin_users
├── id (PK)
├── name
├── email (unique)
├── password
├── role (SUPER_ADMIN, ADMIN, MODERATOR)
├── permissions (JSON array)
├── is_active
├── last_login
└── created_at

audit_logs
├── id (PK)
├── user_id (FK)
├── user_type (USER, PANDIT, ADMIN)
├── action (CREATE, UPDATE, DELETE, LOGIN, LOGOUT)
├── entity_type (USER, BOOKING, PRODUCT, etc.)
├── entity_id
├── old_value (JSON)
├── new_value (JSON)
├── ip_address
├── user_agent
└── created_at

app_settings
├── key (PK)
├── value (text)
├── description
├── data_type (STRING, NUMBER, BOOLEAN, JSON)
├── is_public
└── updated_at

promocodes_usage
├── id (PK)
├── coupon_id (FK)
├── user_id (FK)
├── order_id (FK, nullable)
├── booking_id (FK, nullable)
├── discount_amount
└── used_at

revenue_reports
├── id (PK)
├── report_date (unique)
├── booking_revenue
├── product_revenue
├── total_revenue
├── commission_earned
├── payouts_processed
├── active_users
├── new_users
├── total_bookings
├── total_orders
└── generated_at

failed_jobs
├── id (PK)
├── job_type (EMAIL, SMS, NOTIFICATION, PAYMENT)
├── payload (JSON)
├── exception (text)
├── failed_at
└── retried_at
```

### Total Tables: **45+ tables**

---

## 5. Module-Wise Implementation

### MODULE 1: Pandit Application (12 Features)

#### Feature 1: Pandit Registration & Login
**APIs:**
- `POST /api/auth/register/pandit`
- `POST /api/auth/login`
- `POST /api/auth/verify-otp`
- `POST /api/auth/forgot-password`
- `POST /api/auth/reset-password`

**Implementation:**
```java
// Enhanced registration with OTP verification
- Email/Phone verification
- Password encryption (BCrypt)
- JWT token generation (access + refresh)
- Role assignment
- Profile initialization
```

#### Feature 2: KYC Verification System
**APIs:**
- `POST /api/pandit/kyc/submit` (Multipart)
- `GET /api/pandit/kyc/status`
- `PUT /api/pandit/kyc/resubmit`
- `GET /api/pandit/kyc/documents`

**Implementation:**
```java
// Document upload
- File validation (size, type)
- Image compression
- Secure storage (filesystem/S3)
- Aadhar/PAN verification (Digilocker API)
- OCR for document data extraction
- Admin approval workflow
```

#### Feature 3: Profile Management
**APIs:**
- `GET /api/pandit/profile`
- `PUT /api/pandit/profile`
- `POST /api/pandit/profile/image`
- `PUT /api/pandit/service-areas`
- `PUT /api/pandit/languages`

#### Feature 4: Skills & Service Management
**APIs:**
- `GET /api/pandit/services`
- `POST /api/pandit/services`
- `PUT /api/pandit/services/{id}`
- `DELETE /api/pandit/services/{id}`
- `GET /api/service-categories`

**Business Logic:**
- Multiple services per pandit
- Dynamic pricing
- Service duration management
- Materials included option
- Home visit radius calculation

#### Feature 5: Calendar & Availability Management
**APIs:**
- `GET /api/pandit/availability?month=2025-01`
- `POST /api/pandit/availability/bulk` (Create multiple slots)
- `PUT /api/pandit/availability/{id}`
- `DELETE /api/pandit/availability/{id}`
- `POST /api/pandit/availability/recurring` (Every Saturday)

**Features:**
- Time slot management (30min/1hr/custom)
- Recurring availability
- Break time management
- Emergency leave
- Block specific dates

#### Feature 6: Booking Management
**APIs:**
- `GET /api/pandit/bookings?status=PENDING&page=0&size=10`
- `GET /api/pandit/bookings/{id}`
- `PUT /api/pandit/bookings/{id}/accept`
- `PUT /api/pandit/bookings/{id}/reject`
- `PUT /api/pandit/bookings/{id}/complete`
- `PUT /api/pandit/bookings/{id}/cancel`
- `GET /api/pandit/bookings/today`
- `GET /api/pandit/bookings/upcoming`

**Features:**
- Booking status transitions
- Real-time notifications
- Auto-rejection after timeout
- Rescheduling support

#### Feature 7: Earnings Dashboard
**APIs:**
- `GET /api/pandit/earnings/summary`
- `GET /api/pandit/earnings/details?from=2025-01-01&to=2025-01-31`
- `GET /api/pandit/earnings/graph?period=MONTHLY`
- `GET /api/pandit/payouts`
- `GET /api/pandit/payouts/{id}/invoice`

**Features:**
- Commission calculation
- Daily/Weekly/Monthly reports
- Downloadable invoices (PDF)
- Payment history
- Pending payout tracking

#### Feature 8: Bank/Payout Details
**APIs:**
- `POST /api/pandit/bank-details`
- `GET /api/pandit/bank-details`
- `PUT /api/pandit/bank-details`
- `POST /api/pandit/bank-details/verify` (Penny drop)

#### Feature 9: Chat with Users
**APIs (WebSocket + REST):**
- `GET /api/pandit/conversations`
- `GET /api/pandit/conversations/{id}/messages`
- `WS /ws/chat` (WebSocket connection)
- `POST /api/pandit/conversations/{id}/message`

#### Feature 10: Notifications
**APIs:**
- `GET /api/pandit/notifications?page=0`
- `PUT /api/pandit/notifications/{id}/read`
- `PUT /api/pandit/notifications/read-all`
- `DELETE /api/pandit/notifications/{id}`
- `POST /api/pandit/fcm-token`

#### Feature 11: Reviews Management
**APIs:**
- `GET /api/pandit/reviews?page=0`
- `GET /api/pandit/reviews/summary`
- `POST /api/pandit/reviews/{id}/reply`

#### Feature 12: Performance Analytics
**APIs:**
- `GET /api/pandit/analytics/overview`
- `GET /api/pandit/analytics/bookings-trend`
- `GET /api/pandit/analytics/popular-services`
- `GET /api/pandit/analytics/cancellation-rate`

---

### MODULE 2: User Application (14 Features)

#### Feature 1: User Registration & Login
**APIs:**
- `POST /api/auth/register/user`
- `POST /api/auth/login`
- `POST /api/auth/social-login` (Google/Facebook)
- `POST /api/auth/verify-otp`
- `POST /api/auth/refresh-token`

#### Feature 2: Location-Based Pandit Search
**APIs:**
- `GET /api/users/pandits/search`
  - Query params: `lat`, `lng`, `radius`, `category`, `date`, `minRating`, `maxPrice`, `sort`, `page`
- `GET /api/users/pandits/nearby?lat=28.6139&lng=77.2090&radius=10`
- `GET /api/users/pandits/featured`

**Algorithm:**
- Haversine formula for distance calculation
- Availability-based filtering
- Rating-based sorting
- Price range filtering

#### Feature 3: Pooja Category Filtering
**APIs:**
- `GET /api/users/categories`
- `GET /api/users/categories/{id}/pandits`
- `GET /api/users/services/search?category=Marriage&city=Delhi`

#### Feature 4: Pandit Profile View
**APIs:**
- `GET /api/users/pandits/{id}`
- `GET /api/users/pandits/{id}/services`
- `GET /api/users/pandits/{id}/reviews?page=0`
- `GET /api/users/pandits/{id}/availability?date=2025-01-20`

**Response includes:**
- Full profile details
- Services offered
- Ratings & reviews
- Available time slots
- Approximate distance

#### Feature 5: Booking System
**APIs:**
- `POST /api/users/bookings/check-availability`
- `POST /api/users/bookings/create`
- `GET /api/users/bookings/{id}`
- `PUT /api/users/bookings/{id}/cancel`
- `POST /api/users/bookings/{id}/reschedule`

**Flow:**
1. Check availability
2. Lock time slot (5 min)
3. Initiate payment
4. Confirm booking on payment success
5. Send notifications (User + Pandit)

#### Feature 6: Payment Integration
**APIs:**
- `POST /api/payments/create-order` (Razorpay)
- `POST /api/payments/verify`
- `POST /api/payments/callback`
- `GET /api/payments/{id}/status`
- `POST /api/payments/{id}/refund`

**Supported Methods:**
- Credit/Debit Cards
- UPI
- Net Banking
- Wallets
- COD (for products)

#### Feature 7: Booking Status Tracking
**APIs:**
- `GET /api/users/bookings/{id}/timeline`
- `GET /api/users/bookings/{id}/track`
- `POST /api/users/bookings/{id}/contact-pandit`

#### Feature 8: Chat with Pandit
**APIs:**
- `GET /api/users/conversations`
- `POST /api/users/conversations/start?panditId=123`
- `GET /api/users/conversations/{id}/messages`
- `WS /ws/chat`

#### Feature 9: Reviews & Ratings
**APIs:**
- `POST /api/users/reviews`
- `GET /api/users/reviews/my-reviews`
- `PUT /api/users/reviews/{id}`
- `DELETE /api/users/reviews/{id}`

**Validation:**
- Can review only completed bookings
- One review per booking
- Rating (1-5) required

#### Feature 10: Digital Receipt Generation
**APIs:**
- `GET /api/users/bookings/{id}/receipt`
- `GET /api/users/bookings/{id}/receipt/download` (PDF)
- `POST /api/users/bookings/{id}/receipt/email`

**PDF Contents:**
- Booking details
- Pandit information
- Service details
- Payment breakdown
- QR code for verification

#### Feature 11: Support Tickets
**APIs:**
- `POST /api/users/support/tickets`
- `GET /api/users/support/tickets`
- `GET /api/users/support/tickets/{id}`
- `POST /api/users/support/tickets/{id}/message`
- `PUT /api/users/support/tickets/{id}/close`

#### Feature 12: Order History
**APIs:**
- `GET /api/users/bookings?page=0&status=COMPLETED`
- `GET /api/users/orders?page=0` (E-commerce)
- `GET /api/users/transactions?page=0`

#### Feature 13: Profile Management
**APIs:**
- `GET /api/users/profile`
- `PUT /api/users/profile`
- `POST /api/users/profile/image`
- `PUT /api/users/password`
- `DELETE /api/users/account`

#### Feature 14: Addresses Management
**APIs:**
- `GET /api/users/addresses`
- `POST /api/users/addresses`
- `PUT /api/users/addresses/{id}`
- `DELETE /api/users/addresses/{id}`
- `PUT /api/users/addresses/{id}/set-default`

---

### MODULE 3: Admin Panel (15 Features)

#### Feature 1: Admin Authentication
**APIs:**
- `POST /api/admin/auth/login`
- `POST /api/admin/auth/refresh-token`
- `POST /api/admin/auth/logout`

#### Feature 2: Dashboard Analytics
**APIs:**
- `GET /api/admin/dashboard/stats`
- `GET /api/admin/dashboard/revenue-chart?period=MONTHLY`
- `GET /api/admin/dashboard/booking-trends`
- `GET /api/admin/dashboard/top-pandits`
- `GET /api/admin/dashboard/recent-activities`

**Metrics:**
- Total users, pandits, bookings, orders
- Revenue (booking + e-commerce)
- Growth percentages
- Active vs inactive users
- Geographic distribution

#### Feature 3: User Management
**APIs:**
- `GET /api/admin/users?page=0&search=&role=USER`
- `GET /api/admin/users/{id}`
- `PUT /api/admin/users/{id}/status` (Block/Unblock)
- `DELETE /api/admin/users/{id}`
- `GET /api/admin/users/{id}/activity-log`
- `GET /api/admin/users/export` (CSV/Excel)

#### Feature 4: Pandit Management
**APIs:**
- `GET /api/admin/pandits?page=0&verified=true`
- `GET /api/admin/pandits/{id}`
- `PUT /api/admin/pandits/{id}/verify`
- `PUT /api/admin/pandits/{id}/status`
- `GET /api/admin/pandits/{id}/earnings`

#### Feature 5: KYC Verification System
**APIs:**
- `GET /api/admin/kyc/pending?page=0`
- `GET /api/admin/kyc/{id}`
- `PUT /api/admin/kyc/{id}/approve`
- `PUT /api/admin/kyc/{id}/reject`
- `POST /api/admin/kyc/{id}/request-resubmit`
- `GET /api/admin/kyc/statistics`

**Features:**
- Document viewer
- Approval workflow
- Rejection with reason
- Bulk actions
- Verification statistics

#### Feature 6: Booking Management
**APIs:**
- `GET /api/admin/bookings?page=0&status=&from=&to=`
- `GET /api/admin/bookings/{id}`
- `PUT /api/admin/bookings/{id}/cancel`
- `GET /api/admin/bookings/disputes`
- `POST /api/admin/bookings/{id}/resolve-dispute`

#### Feature 7: Payouts & Settlements
**APIs:**
- `GET /api/admin/payouts/pending`
- `GET /api/admin/payouts/processed`
- `POST /api/admin/payouts/process` (Bulk)
- `POST /api/admin/payouts/{id}/process` (Single)
- `GET /api/admin/payouts/{id}/details`
- `GET /api/admin/payouts/summary?month=2025-01`

**Features:**
- Commission calculation
- Bulk payout processing
- Bank transfer integration
- Payout reports

#### Feature 8: Service Category Management
**APIs:**
- `GET /api/admin/service-categories`
- `POST /api/admin/service-categories`
- `PUT /api/admin/service-categories/{id}`
- `DELETE /api/admin/service-categories/{id}`
- `PUT /api/admin/service-categories/reorder`

#### Feature 9: Support Ticket Management
**APIs:**
- `GET /api/admin/support/tickets?status=OPEN&priority=HIGH`
- `GET /api/admin/support/tickets/{id}`
- `POST /api/admin/support/tickets/{id}/reply`
- `PUT /api/admin/support/tickets/{id}/assign?adminId=5`
- `PUT /api/admin/support/tickets/{id}/priority`
- `PUT /api/admin/support/tickets/{id}/close`

**Features:**
- Ticket assignment
- Priority management
- Canned responses
- SLA tracking

#### Feature 10: CMS Management
**APIs:**
- `GET /api/admin/cms/pages`
- `POST /api/admin/cms/pages`
- `PUT /api/admin/cms/pages/{id}`
- `DELETE /api/admin/cms/pages/{id}`
- `GET /api/cms/page/{slug}` (Public)

**Pages:**
- About Us
- Terms & Conditions
- Privacy Policy
- FAQs
- How It Works

#### Feature 11: Push Notifications
**APIs:**
- `POST /api/admin/notifications/send`
- `POST /api/admin/notifications/send-bulk`
- `GET /api/admin/notifications/history`
- `GET /api/admin/notifications/{id}/stats`

**Target Options:**
- All users
- All pandits
- Specific user segments
- Based on location
- Based on activity

#### Feature 12: Revenue Reports
**APIs:**
- `GET /api/admin/reports/revenue?from=&to=&type=BOOKING`
- `GET /api/admin/reports/booking-summary`
- `GET /api/admin/reports/product-sales`
- `GET /api/admin/reports/commission-earned`
- `GET /api/admin/reports/download?type=REVENUE&format=PDF`

**Export Formats:**
- PDF
- Excel
- CSV

#### Feature 13: Coupon Management
**APIs:**
- `GET /api/admin/coupons`
- `POST /api/admin/coupons`
- `PUT /api/admin/coupons/{id}`
- `DELETE /api/admin/coupons/{id}`
- `GET /api/admin/coupons/{id}/usage-stats`

#### Feature 14: App Settings
**APIs:**
- `GET /api/admin/settings`
- `PUT /api/admin/settings`
- `GET /api/admin/settings/{key}`
- `PUT /api/admin/settings/{key}`

**Settings:**
- Commission percentage
- Payment gateway keys
- Email/SMS credentials
- Firebase config
- Map API keys
- File upload limits

#### Feature 15: Audit Logs
**APIs:**
- `GET /api/admin/audit-logs?page=0&action=&entity=`
- `GET /api/admin/audit-logs/{id}`
- `GET /api/admin/audit-logs/export`

---

### MODULE 4: E-commerce User Side (10 Features)

#### Feature 1: Product Browsing
**APIs:**
- `GET /api/products?page=0&category=&search=&sort=POPULAR`
- `GET /api/products/{id}`
- `GET /api/products/featured`
- `GET /api/products/trending`
- `GET /api/products/related/{id}`

**Filters:**
- Category
- Price range
- Rating
- Availability
- Brand (if applicable)

#### Feature 2: Category Navigation
**APIs:**
- `GET /api/categories`
- `GET /api/categories/{id}/products`
- `GET /api/categories/tree` (with subcategories)

#### Feature 3: Shopping Cart
**APIs:**
- `GET /api/cart`
- `POST /api/cart/add`
- `PUT /api/cart/items/{id}?quantity=2`
- `DELETE /api/cart/items/{id}`
- `DELETE /api/cart/clear`
- `POST /api/cart/apply-coupon`
- `DELETE /api/cart/remove-coupon`

#### Feature 4: Wishlist
**APIs:**
- `GET /api/wishlist`
- `POST /api/wishlist/add`
- `DELETE /api/wishlist/{productId}`
- `POST /api/wishlist/move-to-cart/{productId}`

#### Feature 5: Checkout System
**APIs:**
- `POST /api/checkout/validate`
- `GET /api/checkout/summary`
- `POST /api/checkout/place-order`

**Flow:**
1. Validate cart items
2. Apply coupon
3. Calculate tax & shipping
4. Select/add address
5. Choose payment method
6. Place order
7. Payment processing
8. Order confirmation

#### Feature 6: Order Tracking
**APIs:**
- `GET /api/orders/{id}`
- `GET /api/orders/{id}/track`
- `GET /api/orders/{id}/invoice`
- `GET /api/orders/{id}/cancel`
- `POST /api/orders/{id}/return`

#### Feature 7: Product Reviews
**APIs:**
- `POST /api/products/{id}/reviews`
- `GET /api/products/{id}/reviews?page=0`
- `PUT /api/products/reviews/{id}`
- `DELETE /api/products/reviews/{id}`

#### Feature 8: Product Search
**APIs:**
- `GET /api/products/search?q=diya&page=0`
- `GET /api/products/search/suggestions?q=di`
- `GET /api/products/search/filters?category=idols`

#### Feature 9: Order History
**APIs:**
- `GET /api/orders?page=0&status=DELIVERED`
- `GET /api/orders/{id}/download-invoice`

#### Feature 10: Payment for Orders
**APIs:**
- `POST /api/orders/payment/create`
- `POST /api/orders/payment/verify`
- `GET /api/orders/{id}/payment-status`

---

### MODULE 5: E-commerce Admin (8 Features)

#### Feature 1: Product Management
**APIs:**
- `GET /api/admin/products?page=0`
- `POST /api/admin/products`
- `PUT /api/admin/products/{id}`
- `DELETE /api/admin/products/{id}`
- `POST /api/admin/products/{id}/images`
- `DELETE /api/admin/products/images/{id}`
- `POST /api/admin/products/bulk-upload` (CSV)

#### Feature 2: Inventory Management
**APIs:**
- `GET /api/admin/inventory?lowStock=true`
- `PUT /api/admin/inventory/{id}/update-stock`
- `POST /api/admin/inventory/bulk-update`
- `GET /api/admin/inventory/alerts`

#### Feature 3: Order Management
**APIs:**
- `GET /api/admin/orders?page=0&status=PLACED`
- `GET /api/admin/orders/{id}`
- `PUT /api/admin/orders/{id}/status`
- `POST /api/admin/orders/{id}/assign-tracking`
- `PUT /api/admin/orders/{id}/cancel`
- `GET /api/admin/orders/{id}/refund`

#### Feature 4: Category Management
**APIs:**
- `GET /api/admin/categories`
- `POST /api/admin/categories`
- `PUT /api/admin/categories/{id}`
- `DELETE /api/admin/categories/{id}`

#### Feature 5: Discount & Coupon System
**APIs:**
- `GET /api/admin/coupons`
- `POST /api/admin/coupons`
- `PUT /api/admin/coupons/{id}`
- `DELETE /api/admin/coupons/{id}`
- `GET /api/admin/coupons/{id}/analytics`

#### Feature 6: Sales Reports
**APIs:**
- `GET /api/admin/reports/sales?from=&to=`
- `GET /api/admin/reports/best-sellers`
- `GET /api/admin/reports/revenue-by-category`
- `GET /api/admin/reports/abandoned-carts`
- `GET /api/admin/reports/download?type=SALES&format=EXCEL`

#### Feature 7: Vendor Management (Optional)
**APIs:**
- `GET /api/admin/vendors`
- `POST /api/admin/vendors`
- `PUT /api/admin/vendors/{id}`
- `GET /api/admin/vendors/{id}/products`
- `GET /api/admin/vendors/{id}/settlements`

#### Feature 8: Product Reviews Management
**APIs:**
- `GET /api/admin/reviews?pending=true`
- `PUT /api/admin/reviews/{id}/approve`
- `DELETE /api/admin/reviews/{id}`
- `POST /api/admin/reviews/{id}/reply`

---

## 6. API Endpoints Summary

### Total APIs: **120+**

#### By Module:
- **Authentication**: 8 APIs
- **Pandit Module**: 45 APIs
- **User Module**: 32 APIs
- **Admin Module**: 38 APIs
- **E-commerce User**: 25 APIs
- **E-commerce Admin**: 22 APIs

---

## 7. Security Architecture

### 7.1 Authentication Flow
```
1. User Registration
   ↓
2. Email/Phone Verification (OTP)
   ↓
3. Login (Email/Phone + Password)
   ↓
4. Generate JWT Tokens (Access + Refresh)
   ↓
5. Return tokens to client
   ↓
6. Client stores tokens (localStorage/SecureStorage)
   ↓
7. Include Access Token in header: Authorization: Bearer <token>
   ↓
8. Token expiry → Use Refresh Token to get new Access Token
```

### 7.2 JWT Configuration
```yaml
Access Token:
  - Expiry: 15 minutes
  - Contains: userId, role, email
  
Refresh Token:
  - Expiry: 7 days
  - Stored in database
  - Can be revoked
```

### 7.3 Authorization (RBAC)
```java
@PreAuthorize("hasRole('USER')")
@PreAuthorize("hasRole('PANDIT')")
@PreAuthorize("hasRole('ADMIN')")
@PreAuthorize("hasAnyRole('USER', 'PANDIT')")
```

### 7.4 Data Encryption
- Passwords: BCrypt (Spring Security)
- Sensitive data (Aadhar, Account numbers): AES-256
- Communication: HTTPS/TLS

### 7.5 Security Best Practices
- CORS configuration
- CSRF protection
- Rate limiting (Spring Rate Limiter)
- SQL injection prevention (JPA/Hibernate)
- XSS protection
- Input validation (@Valid annotations)
- File upload restrictions

---

## 8. File Management System

### 8.1 File Storage Strategy
```yaml
Option 1: Local Filesystem
  - Path: /uploads/{module}/{userId}/{filename}
  - Pros: Simple, no cost
  - Cons: Not scalable, hard to backup

Option 2: AWS S3 (Recommended)
  - Bucket structure: anytime-pooja/{env}/{module}/
  - Pros: Scalable, CDN support, backups
  - Cost: ~₹1000/month for 50GB

Option 3: CloudFlare R2
  - Similar to S3 but cheaper
  - No egress fees
```

### 8.2 File Types & Sizes
```yaml
Profile Images:
  - Max size: 2MB
  - Formats: JPG, PNG
  - Resolution: 800x800px

KYC Documents:
  - Max size: 5MB
  - Formats: JPG, PNG, PDF

Product Images:
  - Max size: 3MB
  - Multiple images per product
  - Thumbnail generation

Chat Attachments:
  - Max size: 10MB
  - Formats: Images, PDFs
```

### 8.3 Image Processing
```java
// Using Thumbnailator or ImageMagick
- Resize images
- Compress quality
- Generate thumbnails
- Watermark (optional)
```

---

## 9. Payment Integration

### 9.1 Razorpay Integration

#### Setup
```java
// Dependencies
implementation 'com.razorpay:razorpay-java:1.4.3'

// Configuration
razorpay.key_id=rzp_test_xxxxx
razorpay.key_secret=xxxxx
```

#### Booking Payment Flow
```
1. Create Order
   POST /api/payments/create-order
   Request: { bookingId, amount }
   Response: { orderId, amount, currency }

2. Client processes payment (Razorpay SDK)

3. Payment Success Callback
   POST /api/payments/verify
   Request: { razorpay_order_id, razorpay_payment_id, razorpay_signature }
   
4. Verify Signature & Update booking status

5. Send confirmation notifications
```

#### E-commerce Payment Flow
```
Similar flow but for Order entity
```

### 9.2 Refund Processing
```java
POST /api/payments/{id}/refund
- Full refund
- Partial refund
- Refund reasons
- Async processing
- Notification to user
```

---

## 10. Real-Time Features

### 10.1 WebSocket Configuration
```java
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    
    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic", "/queue");
        config.setApplicationDestinationPrefixes("/app");
    }
    
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws").setAllowedOrigins("*").withSockJS();
    }
}
```

### 10.2 Chat System
```java
// Send message
@MessageMapping("/chat.send")
@SendToUser("/queue/messages")
public ChatMessage sendMessage(ChatMessage message) {
    // Save to database
    // Send to recipient
    return chatService.sendMessage(message);
}

// Receive messages
Client subscribes to: /user/queue/messages
```

### 10.3 Real-Time Notifications
```java
// Booking status update
@SendTo("/topic/booking-{bookingId}")
public BookingUpdate sendBookingUpdate(BookingUpdate update) {
    return update;
}
```

---

## 11. Notification System

### 11.1 Multi-Channel Notifications

#### A. Email Notifications
```yaml
Events:
  - Registration (Welcome email)
  - Booking confirmation
  - Booking status updates
  - Payment receipt
  - KYC approval/rejection
  - Password reset
  - Order dispatch/delivery

Technology: Spring Mail + SMTP (Gmail/SendGrid)
Templates: Thymeleaf HTML templates
```

#### B. SMS Notifications
```yaml
Provider: Twilio / MSG91
Events:
  - OTP verification
  - Booking confirmation
  - Pandit on the way
  - Order dispatch

Cost: ~₹0.20 per SMS
```

#### C. Push Notifications
```yaml
Technology: Firebase Cloud Messaging (FCM)
Events:
  - New booking (for pandit)
  - Booking confirmed (for user)
  - Chat messages
  - Promotional offers

Implementation:
  - Store FCM tokens in users table
  - Send via Firebase Admin SDK
```

#### D. In-App Notifications
```yaml
Storage: notifications table
Display: Bell icon with badge count
Features:
  - Mark as read
  - Delete
  - Action buttons
```

---

## 12. Analytics & Reporting

### 12.1 Dashboard Metrics

#### Admin Dashboard
```java
- Total Revenue (Today, This Month, All Time)
- Total Bookings (Pending, Confirmed, Completed)
- Total Orders (Processing, Shipped, Delivered)
- Active Users, Pandits
- New Registrations (Graph)
- Revenue Trend (Line chart)
- Top Performing Pandits
- Best Selling Products
- Geographic Distribution (Map)
```

#### Pandit Dashboard
```java
- Today's Bookings
- Upcoming Bookings
- Total Earnings (This Month)
- Pending Payouts
- Average Rating
- Completion Rate
- Booking Trends (Graph)
```

#### User Dashboard
```java
- Upcoming Bookings
- Past Bookings
- Total Spent
- Saved Addresses
- Wishlist Count
```

### 12.2 Report Generation

#### Types of Reports
```yaml
1. Revenue Reports (PDF/Excel)
   - Date range
   - Module-wise breakdown
   - Payment method breakdown

2. Booking Reports
   - Status-wise summary
   - Pandit-wise summary
   - Service-wise summary

3. Product Sales Reports
   - Best sellers
   - Category-wise
   - Slow-moving items

4. User Activity Reports
   - Active users
   - Churned users
   - Registration trends

5. Payout Reports
   - Pending payouts
   - Processed payouts
   - Pandit-wise earnings
```

#### Implementation
```java
// Using Apache POI for Excel
// Using iText for PDF
// Using JasperReports (optional)

@GetMapping("/reports/download")
public ResponseEntity<Resource> downloadReport(
    @RequestParam ReportType type,
    @RequestParam String format,
    @RequestParam LocalDate from,
    @RequestParam LocalDate to
) {
    // Generate report
    // Return as downloadable file
}
```

---

## 13. Testing Strategy

### 13.1 Unit Tests
```java
// Using JUnit 5 + Mockito
- Service layer tests
- Repository tests
- Utility classes
- Coverage target: 70%+
```

### 13.2 Integration Tests
```java
// Using Spring Boot Test + TestContainers
- Controller tests (MockMvc)
- Database integration (Real MySQL)
- Security tests
- API tests
```

### 13.3 E2E Testing
```yaml
Tools: Postman/Newman
- Complete user flows
- Payment flow (test mode)
- File upload scenarios
```

---

## 14. Deployment Architecture

### 14.1 Server Requirements
```yaml
Development:
  - Local machine
  - MySQL local instance

Staging:
  - AWS EC2 t3.medium (2 vCPU, 4GB RAM)
  - MySQL RDS t3.small
  - Cost: ~₹3000/month

Production:
  - AWS EC2 t3.large (2 vCPU, 8GB RAM) x 2 (Load balanced)
  - MySQL RDS t3.medium (Multi-AZ)
  - S3 for file storage
  - CloudFront CDN
  - Cost: ~₹12,000/month
```

### 14.2 CI/CD Pipeline
```yaml
Tools: GitHub Actions / Jenkins

Pipeline:
  1. Code push to GitHub
  2. Run tests
  3. Build JAR
  4. Docker image creation
  5. Push to Docker Hub
  6. Deploy to EC2
  7. Health check
  8. Notification
```

### 14.3 Monitoring & Logging
```yaml
Monitoring:
  - Spring Boot Actuator
  - Prometheus + Grafana
  - AWS CloudWatch

Logging:
  - Logback configuration
  - Log aggregation (ELK stack optional)
  - Error tracking (Sentry optional)
```

---

## 15. Implementation Timeline

### Phase 1: Foundation (Week 1-2)
- [ ] Enhanced project setup
- [ ] Complete database schema
- [ ] Security configuration (JWT)
- [ ] Base controllers & services
- [ ] File upload system
- [ ] Email configuration

### Phase 2: Pandit Module (Week 3-4)
- [ ] Registration & KYC
- [ ] Profile management
- [ ] Service management
- [ ] Availability management
- [ ] Bank details
- [ ] Earnings dashboard

### Phase 3: User Module (Week 5-6)
- [ ] Registration & authentication
- [ ] Pandit search & filtering
- [ ] Booking system
- [ ] Payment integration
- [ ] Reviews & ratings
- [ ] Support tickets

### Phase 4: E-commerce (Week 7-8)
- [ ] Product management
- [ ] Cart & wishlist
- [ ] Checkout flow
- [ ] Order management
- [ ] Inventory system
- [ ] Product reviews

### Phase 5: Admin Panel (Week 9-10)
- [ ] Dashboard & analytics
- [ ] User/Pandit management
- [ ] KYC verification
- [ ] Payout processing
- [ ] Ticket management
- [ ] Reports generation

### Phase 6: Advanced Features (Week 11-12)
- [ ] WebSocket chat
- [ ] Push notifications
- [ ] Real-time tracking
- [ ] Advanced search
- [ ] Recommendation engine
- [ ] Performance optimization

### Phase 7: Testing & Deployment (Week 13-14)
- [ ] Unit tests
- [ ] Integration tests
- [ ] API documentation
- [ ] Server setup
- [ ] CI/CD pipeline
- [ ] Production deployment

---

## 16. Immediate Next Steps

### Step 1: Add Missing Dependencies
Update `pom.xml` with:
- JWT libraries
- Email support
- WebSocket
- File handling
- API documentation
- Payment SDK

### Step 2: Expand Models
Add missing entities:
- ChatConversation, ChatMessage
- Notification
- AdminUser
- AuditLog
- ServiceCategory
- ProductImage

### Step 3: Create Additional Controllers
- ChatController
- NotificationController
- ReportController
- AnalyticsController

### Step 4: Implement Core Services
- FileStorageService
- EmailService
- SmsService
- NotificationService
- PaymentService
- ReportService

### Step 5: Add Scheduled Jobs
- Payment reconciliation
- Booking reminders
- Payout processing
- Report generation

---

## 17. API Documentation

### Swagger/OpenAPI Setup
```java
// Add dependency
implementation 'org.springdoc:springdoc-openapi-starter-webmvc-ui:2.3.0'

// Access at: http://localhost:8080/swagger-ui.html
```

---

## Conclusion

This comprehensive plan provides a production-ready architecture for the Anytime Pooja platform. The implementation covers all 5 modules with 120+ APIs, 45+ database tables, and enterprise-grade features.

**Key Highlights:**
- ✅ Scalable architecture
- ✅ Secure (JWT + RBAC)
- ✅ Payment integration ready
- ✅ Real-time features
- ✅ Multi-channel notifications
- ✅ Comprehensive admin panel
- ✅ Analytics & reporting
- ✅ Production deployment ready

**Estimated Development Time:** 12-14 weeks
**Budget Utilization:** ₹65,000 (Backend + Frontend)

---

**Next:** Would you like me to start implementing these features? I can begin with:
1. Adding missing dependencies to pom.xml
2. Creating all database entities
3. Implementing JWT authentication
4. Building the core services
5. Creating comprehensive controllers

Please confirm to proceed!

