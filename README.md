# 💳 Stripe Payment Integration System
  A production-ready microservices-based payment system built with **Spring Boot** and **Stripe API** for secure online payment processing with real-time webhook handling.

--------

## 🚀 Features

✅ Create Stripe checkout sessions with secure payment URLs  
✅ Retrieve payment session details and transaction status  
✅ Expire or cancel active payment sessions programmatically  
✅ Real-time webhook handling for payment status updates  
✅ RESTful APIs for complete payment lifecycle management  
✅ Microservices architecture with independent deployments  
✅ Support for test and live payment modes  

---

## 🛠️ Tech Stack

| Technology |    Purpose |
|------------|---------|
| **Java 17** | Backend programming language |
| **Spring Boot 3.x** | Framework for microservices |
| **Spring Web** | REST API development |
| **Stripe Java SDK** | Payment gateway integration |
| **Maven** | Build and dependency management |

------------------------------------------------------------------------
## 📁 Project Structure


stripe-payment-integration-system/
|
├── stripe-provider-create-session/
|
├── stripe-provider-get-session/
|
├── stripe-provider-expire-session/
|
└── README.md

> Each microservice follows a clean and maintainable **Spring Boot layered architecture**,  
> ensuring proper separation of concerns:
> - **Controller Layer** – Exposes REST APIs and handles HTTP requests/responses  
> - **Service Layer** – Contains core business logic and payment workflows  
> - **Helper Layer** – Encapsulates Stripe-specific processing and reusable logic  
> - **Exception Layer** – Centralized error handling with custom exceptions  
> - **Config Layer** – Application and Stripe configuration management  
> - **Util Layer** – Common utilities and JSON processing helpers

----------------------------------------------------------------------------------------------------||

## 🏗️ Microservices Architecture

┌─────────────────────────────────────────────────────────────┐
│ Client Application │
└───────────────────────────┬─────────────────────────────────┘
│
┌───────────────────┼───────────────────┐
│ │ │
▼ ▼ ▼
┌──────────────┐ ┌──────────────┐ ┌──────────────┐
│ CREATE │ │ GET │ │ EXPIRE │
│ SESSION │ │ SESSION │ │ SESSION │
│ Port: 8083 │ │ Port: 8084 │ │ Port: 8085 │
└──────┬───────┘ └──────┬───────┘ └──────┬───────┘
│ │ │
└───────────────────┼───────────────────┘
│
┌──────▼──────┐
│ Stripe API │
└─────────────┘

-------------------------------------------------------------------------------------------|
|     Service        | Port |              Responsibility                                  |
|--------------------|------|--------------------------------------------------------------|
| **Create Session** | 8083 | Creates new Stripe checkout session & returns payment URL    |
| **Get Session**    | 8084 | Retrieves session details, payment status & transaction info |
| **Expire Session** | 8085 | Cancels or expires active payment sessions                   |
--------------------------------------------------------------------------------------------

## 📋 Prerequisites

- ☕ **Java 17** 
- 📦 **Maven 3.6+**
- 💳 **Stripe Account** ([Sign up here](https://stripe.com))
- 🔧 **Postman** (for API testing)

---

## ⚡ Quick Start

### 1️⃣ Clone Repository
         git clone :
              https://github.com/deepanshupal8601/Stripe-Payment-Integration-System.git
              cd stripe-payment-integration-system

2️⃣ Configure Stripe API Keys
              Update application.properties in all three services:

-📁 stripe-provider-create-session/src/main/resources/application.properties

    server.port=8083
    stripe.api.secret-key=sk_test_YOUR_SECRET_KEY
    stripe.api.publishable-key=pk_test_YOUR_PUBLISHABLE_KEY
    stripe.webhook.secret=whsec_YOUR_WEBHOOK_SECRET
    
-📁 stripe-provider-get-session/src/main/resources/application.properties

    server.port=8084
    stripe.api.secret-key=sk_test_YOUR_SECRET_KEY
    
-📁 stripe-provider-expire-session/src/main/resources/application.properties

    server.port=8085
    stripe.api.secret-key=sk_test_YOUR_SECRET_KEY

-3️⃣ Build Project

    mvn clean install
-4️⃣ Run All Services

Terminal 1 - Create Session Service:

         cd stripe-provider-create-session
          mvn spring-boot:run
Terminal 2 - Get Session Service:

        cd stripe-provider-get-session
         mvn spring-boot:run
Terminal 3 - Expire Session Service:

        cd stripe-provider-expire-session
        mvn spring-boot:run
✅ All services will be running on ports 8083, 8084, and 8085
.............................................................................

📡 API Endpoints

1️⃣ Create Payment Session
       
       Request:  POST http://localhost:8083/payments
       Content-Type: application/json
          {
          "amount": 5000,
          "currency": "inr",
          "successUrl": "http://localhost:8083/payments/success",
           "cancelUrl": "http://localhost:8083/payments/cancel"
        }
        
     Response:

        json
          {
          "id": "cs_test_a1b2c3d4e5f6",
         "checkoutUrl": "https://checkout.stripe.com/c/pay/cs_test_a1b2c3d4e5f6",
         "status": "open",
         "amount": 5000,
         "currency": "inr"
        }


2️⃣ Get Session Details
               
               Request:
                    GET http://localhost:8084/payments/cs_test_a1b2c3d4e5f6
          Response:
        json
          {
            "id": "cs_test_a1b2c3d4e5f6",
            "paymentStatus": "paid",
            "amount": 5000,
            "currency": "inr",
            "customerEmail": "customer@example.com",
            "paymentIntentId": "pi_3abc123xyz"
       }

3️⃣ Expire Payment Session
              
              Request:

            POST http://localhost:8085/payments/cs_test_a1b2c3d4e5f6/expire
     Response:
         json
          {
           "id": "cs_test_a1b2c3d4e5f6",
           "status": "expired",
          "message": "Session expired successfully"
         }
4️⃣ Webhook Handler
                Endpoint:
POST http://localhost:8083/api/webhook/stripe
Supported Events:

✅ checkout.session.completed

✅ payment_intent.succeeded

❌ payment_intent.payment_failed
------------------------------------------------------------------------
🧪 Testing-
Stripe Test Cards

     Card Number	           Scenario               	CVV	Expiry
     
     4242 4242 4242 4242	✅ Success	Any 3 digits   	Any future date
     4000 0000 0000 0002	❌ Declined	Any 3 digits	  Any future date
     4000 0025 0000 3155	🔐 3D Secure	Any 3 digits	Any future date


Testing with Postman
Create Session → Copy checkoutUrl

Open URL in browser → Complete test payment

Get Session → Verify payment status

Local Webhook Testing
bash
# Install Stripe CLI
    stripe login

# Forward webhooks to local server
stripe listen --forward-to localhost:8083/api/webhook/stripe

# Trigger test events
stripe trigger checkout.session.completed
🔐 Security Best Practices
🔒 Never commit API keys to Git
🔒 Use environment variables for sensitive data
🔒 Validate webhook signatures
🔒 Enable HTTPS in production
🔒 Implement rate limiting
............................................................................................

🎯 Microservices Principles Demonstrated

✅ Service Independence - Each service runs on separate port (8083, 8084, 8085) independently

✅ Single Responsibility - Create Session handles only creation, Get Session only retrieval, Expire Session only cancellation

✅ Loose Coupling - Services communicate via REST APIs with no direct code dependencies

✅ Fault Isolation - Failure in one service doesn't crash others (e.g., Expire down, Create/Get still work)

✅ Independent Scalability - Scale individual services based on traffic load separately

✅ Independent Deployment - Deploy, update, or rollback services separately without affecting others

✅ Stateless Services - No local session storage, all state managed by Stripe

✅ API Gateway Ready - Architecture supports Spring Cloud Gateway integration as single entry point

✅ Externalized Configuration - API keys and ports in application.properties, not hardcoded

✅ Service Instance per Container - Each service runs as separate instance, ready for Docker

✅ RESTful Communication - Services expose REST endpoints following REST principles
...................................................................................................

👤 Contact

 Deepanshu Pal
 
📧 Email: deepanshupal7650@gmail.com

💼 LinkedIn: https://www.linkedin.com/in/deepanshu-pal-b28599205/

🐙 GitHub: https://github.com/deepanshupal8601


--------------------------------------------------------------------------------------------------------
Project Link: https://github.com/deepanshupal8601/Stripe-Payment-Integration-System

* Acknowledgments

Stripe API Documentation

Spring Boot Documentation

Stripe Java SDK

Stripe Webhook Guide



