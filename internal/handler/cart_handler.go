package handler

import (
    "net/http"
    "strconv"

    "github.com/gin-gonic/gin"

    "gogoquery/internal/models"
    "gogoquery/internal/service"
)

type CartHandler struct {
    cartService *service.CartService
}

func NewCartHandler(cartService *service.CartService) *CartHandler {
    return &CartHandler{cartService: cartService}
}

func (h *CartHandler) GetCart(c *gin.Context) {
    email, _ := c.Get("email")
    
    cartItems, err := h.cartService.GetCart(email.(string))
    if err != nil {
        c.JSON(http.StatusInternalServerError, gin.H{"error": "Failed to fetch cart"})
        return
    }
    
    total, err := h.cartService.GetTotalPrice(email.(string))
    if err != nil {
        c.JSON(http.StatusInternalServerError, gin.H{"error": "Failed to calculate total"})
        return
    }
    
    c.JSON(http.StatusOK, gin.H{
        "items": cartItems,
        "count": len(cartItems),
        "total": total,
    })
}

func (h *CartHandler) AddToCart(c *gin.Context) {
    email, _ := c.Get("email")
    
    var req models.AddToCartRequest
    if err := c.ShouldBindJSON(&req); err != nil {
        c.JSON(http.StatusBadRequest, gin.H{"error": err.Error()})
        return
    }
    
    result, err := h.cartService.AddToCart(email.(string), &req)
    if err != nil {
        c.JSON(http.StatusInternalServerError, gin.H{"error": "Failed to add item to cart"})
        return
    }
    
    response := gin.H{"message": "Item added to cart"}
    switch result {
    case "updated":
        response["message"] = "Quantity updated"
    case "not enough stock":
        response["message"] = "Quantity adjusted to available stock"
    }
    
    c.JSON(http.StatusOK, response)
}

func (h *CartHandler) UpdateCartItem(c *gin.Context) {
    email, _ := c.Get("email")
    
    itemIDStr := c.Param("itemId")
    itemID, err := strconv.Atoi(itemIDStr)
    if err != nil {
        c.JSON(http.StatusBadRequest, gin.H{"error": "Invalid item ID"})
        return
    }
    
    var req models.UpdateCartRequest
    if err := c.ShouldBindJSON(&req); err != nil {
        c.JSON(http.StatusBadRequest, gin.H{"error": err.Error()})
        return
    }
    
    err = h.cartService.UpdateCartItem(email.(string), itemID, req.Quantity)
    if err != nil {
        c.JSON(http.StatusInternalServerError, gin.H{"error": "Failed to update cart item"})
        return
    }
    
    c.JSON(http.StatusOK, gin.H{"message": "Cart updated successfully"})
}

func (h *CartHandler) RemoveFromCart(c *gin.Context) {
    email, _ := c.Get("email")
    
    itemIDStr := c.Param("itemId")
    itemID, err := strconv.Atoi(itemIDStr)
    if err != nil {
        c.JSON(http.StatusBadRequest, gin.H{"error": "Invalid item ID"})
        return
    }
    
    err = h.cartService.RemoveFromCart(email.(string), itemID)
    if err != nil {
        c.JSON(http.StatusInternalServerError, gin.H{"error": "Failed to remove item from cart"})
        return
    }
    
    c.JSON(http.StatusOK, gin.H{"message": "Item removed from cart"})
}

func (h *CartHandler) Checkout(c *gin.Context) {
    email, _ := c.Get("email")
    
    err := h.cartService.Checkout(email.(string))
    if err != nil {
        c.JSON(http.StatusBadRequest, gin.H{"error": err.Error()})
        return
    }
    
    c.JSON(http.StatusOK, gin.H{"message": "Checkout successful"})
}
