# GoGoQuery Backend API

This is the Go backend implementation of the GoGoQuery e-commerce system, preserving all business logic from the original Java application.

## Features

- **User Authentication & Registration** with business rules:
  - Email must end with `@gomail.com`
  - Password must be alphanumeric
  - Minimum age: 17 years
  - Email uniqueness validation
- **Role-Based Access Control** (Shopper/Manager)
- **Product Management** with inventory rules
- **Shopping Cart** with stock validation
- **Checkout System** with atomic transactions
- **Order Queue Management** for managers

## Business Logic Preserved

### 1. Registration Constraints
- Email domain validation (`@gomail.com`)
- Password alphanumeric requirement
- Age restriction (17+ years)
- Email uniqueness check
- Terms agreement requirement

### 2. Product Management
- Products with zero stock are filtered out
- Manager product creation constraints:
  - Name: 5-70 characters
  - Description: 10-255 characters
  - Price: $0.50 - $900,000
  - Quantity > 0

### 3. Shopping Cart Logic
- Stock validation on add to cart
- Quantity capping at available stock
- Cart item updates with stock checks
- Atomic checkout transaction

### 4. Checkout Workflow
1. Create transaction header with "In Queue" status
2. Transfer cart items to transaction details
3. Deduct stock from inventory (atomic)
4. Clear user's cart

### 5. Order Management
- Managers can view all transactions
- Status can only be updated to "Sent" from "In Queue"

## API Endpoints

### Authentication
- `POST /api/login` - User login
- `POST /api/register` - User registration
- `GET /api/check-email` - Check email availability

### Products (Public)
- `GET /api/products` - List products (with optional category/search filters)
- `GET /api/products/:id` - Get product details
- `GET /api/categories` - Get product categories

### Cart (Shopper only)
- `GET /api/cart` - Get cart items
- `POST /api/cart` - Add item to cart
- `PUT /api/cart/:itemId` - Update cart item quantity
- `DELETE /api/cart/:itemId` - Remove item from cart
- `POST /api/checkout` - Checkout cart

### Management (Manager only)
- `POST /api/products` - Add new product
- `GET /api/transactions` - Get all transactions
- `PUT /api/transactions/:id/status` - Update transaction status

## Setup

1. **Database Setup:**
