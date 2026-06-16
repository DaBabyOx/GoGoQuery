package models

import (
    "time"
)

type User struct {
    ID       int       `json:"id"`
    DOB      time.Time `json:"dob"`
    Email    string    `json:"email"`
    Password string    `json:"-"`
    Gender   string    `json:"gender"`
    Role     string    `json:"role"`
}

type Product struct {
    ID       int     `json:"id"`
    Name     string  `json:"name"`
    Category string  `json:"category"`
    Price    float64 `json:"price"`
    Desc     string  `json:"desc"`
    Stock    int     `json:"stock"`
}

type CartItem struct {
    UserID   int `json:"user_id"`
    ItemID   int `json:"item_id"`
    Quantity int `json:"quantity"`
}

type CartItemDetail struct {
    CartItem
    Name  string  `json:"name"`
    Price float64 `json:"price"`
}

type TransactionHeader struct {
    ID          int       `json:"id"`
    UserID      int       `json:"user_id"`
    DateCreated time.Time `json:"date_created"`
    Status      string    `json:"status"`
}

type TransactionDetail struct {
    TransactionID int `json:"transaction_id"`
    ItemID       int `json:"item_id"`
    Quantity     int `json:"quantity"`
}

type TransactionView struct {
    ID            int     `json:"id"`
    CustomerID    int     `json:"customer_id"`
    CustomerEmail string  `json:"customer_email"`
    Date          string  `json:"date"`
    Amount        float64 `json:"amount"`
    Status        string  `json:"status"`
}

type LoginRequest struct {
    Email    string `json:"email" binding:"required,email"`
    Password string `json:"password" binding:"required"`
}

type RegisterRequest struct {
    Email     string    `json:"email" binding:"required,email"`
    Password  string    `json:"password" binding:"required"`
    Confirm   string    `json:"confirm" binding:"required"`
    DOB       time.Time `json:"dob" binding:"required"`
    Gender    string    `json:"gender" binding:"required,oneof=Male Female"`
    Role      string    `json:"role" binding:"required,oneof=Shopper Manager"`
    Agree     bool      `json:"agree" binding:"required"`
}

type AddToCartRequest struct {
    ItemID   int `json:"item_id" binding:"required,min=1"`
    Quantity int `json:"quantity" binding:"required,min=1"`
}

type UpdateCartRequest struct {
    Quantity int `json:"quantity" binding:"required,min=0"`
}

type AddProductRequest struct {
    Name     string  `json:"name" binding:"required,min=5,max=70"`
    Category string  `json:"category" binding:"required"`
    Price    float64 `json:"price" binding:"required,min=0.5,max=900000"`
    Desc     string  `json:"desc" binding:"required,min=10,max=255"`
    Stock    int     `json:"stock" binding:"required,min=1"`
}

type UpdateStatusRequest struct {
    Status string `json:"status" binding:"required,oneof=Sent"`
}
