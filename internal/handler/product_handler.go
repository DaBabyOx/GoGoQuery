package handler

import (
    "net/http"
    "strconv"

    "github.com/gin-gonic/gin"

    "gogoquery/internal/models"
    "gogoquery/internal/service"
)

type ProductHandler struct {
    productService *service.ProductService
}

func NewProductHandler(productService *service.ProductService) *ProductHandler {
    return &ProductHandler{productService: productService}
}

func (h *ProductHandler) GetCategories(c *gin.Context) {
    categories, err := h.productService.GetCategories()
    if err != nil {
        c.JSON(http.StatusInternalServerError, gin.H{"error": "Failed to fetch categories"})
        return
    }
    
    c.JSON(http.StatusOK, gin.H{"categories": categories})
}

func (h *ProductHandler) GetProducts(c *gin.Context) {
    category := c.Query("category")
    search := c.Query("search")
    
    var products []models.Product
    var err error
    
    if category != "" || search != "" {
        products, err = h.productService.FilterProducts(category, search)
    } else {
        products, err = h.productService.GetProducts()
    }
    
    if err != nil {
        c.JSON(http.StatusInternalServerError, gin.H{"error": "Failed to fetch products"})
        return
    }
    
    c.JSON(http.StatusOK, gin.H{"products": products, "count": len(products)})
}

func (h *ProductHandler) GetProduct(c *gin.Context) {
    idStr := c.Param("id")
    id, err := strconv.Atoi(idStr)
    if err != nil {
        c.JSON(http.StatusBadRequest, gin.H{"error": "Invalid product ID"})
        return
    }
    
    product, err := h.productService.GetProductByID(id)
    if err != nil {
        c.JSON(http.StatusNotFound, gin.H{"error": "Product not found"})
        return
    }
    
    c.JSON(http.StatusOK, gin.H{"product": product})
}

func (h *ProductHandler) CreateProduct(c *gin.Context) {
    // Check if user is manager
    role, exists := c.Get("role")
    if !exists || role != "Manager" {
        c.JSON(http.StatusForbidden, gin.H{"error": "Only managers can add products"})
        return
    }
    
    var req models.AddProductRequest
    if err := c.ShouldBindJSON(&req); err != nil {
        c.JSON(http.StatusBadRequest, gin.H{"error": err.Error()})
        return
    }
    
    product, err := h.productService.CreateProduct(&req)
    if err != nil {
        c.JSON(http.StatusBadRequest, gin.H{"error": err.Error()})
        return
    }
    
    c.JSON(http.StatusCreated, gin.H{
        "message": "Product added successfully",
        "product": product,
    })
}
