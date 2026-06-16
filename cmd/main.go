package main

import (
    "log"
    "os"

    "github.com/gin-gonic/gin"
    "gogoquery/internal/database"
    "gogoquery/internal/handler"
    "gogoquery/internal/middleware"
    "gogoquery/internal/repository"
    "gogoquery/internal/service"
)

func main() {
    // Initialize database
    if err := database.InitDB(); err != nil {
        log.Fatal("Failed to connect to database:", err)
    }
    defer database.CloseDB()
    
    // Initialize repositories
    userRepo := repository.NewUserRepository(database.DB)
    productRepo := repository.NewProductRepository(database.DB)
    cartRepo := repository.NewCartRepository(database.DB)
    transRepo := repository.NewTransactionRepository(database.DB)
    
    // Initialize services
    authService := service.NewAuthService(userRepo)
    productService := service.NewProductService(productRepo)
    cartService := service.NewCartService(cartRepo, userRepo)
    transService := service.NewTransactionService(transRepo)
    
    // Initialize handlers
    jwtSecret := os.Getenv("JWT_SECRET")
    if jwtSecret == "" {
        jwtSecret = "your-secret-key-change-in-production"
    }
    
    authHandler := handler.NewAuthHandler(authService, jwtSecret)
    productHandler := handler.NewProductHandler(productService)
    cartHandler := handler.NewCartHandler(cartService)
    transHandler := handler.NewTransactionHandler(transService)
    
    // Setup router
    router := gin.Default()
    
    // Public routes
    public := router.Group("/api")
    {
        public.POST("/login", authHandler.Login)
        public.POST("/register", authHandler.Register)
        public.GET("/check-email", authHandler.CheckEmail)
        public.GET("/products", productHandler.GetProducts)
        public.GET("/products/:id", productHandler.GetProduct)
        public.GET("/categories", productHandler.GetCategories)
    }
    
    // Protected routes
    protected := router.Group("/api")
    protected.Use(middleware.AuthMiddleware(jwtSecret))
    {
        // Shopper routes
        shopper := protected.Group("")
        shopper.Use(middleware.RoleMiddleware("Shopper"))
        {
            shopper.GET("/cart", cartHandler.GetCart)
            shopper.POST("/cart", cartHandler.AddToCart)
            shopper.PUT("/cart/:itemId", cartHandler.UpdateCartItem)
            shopper.DELETE("/cart/:itemId", cartHandler.RemoveFromCart)
            shopper.POST("/checkout", cartHandler.Checkout)
        }
        
        // Manager routes
        manager := protected.Group("")
        manager.Use(middleware.RoleMiddleware("Manager"))
        {
            manager.POST("/products", productHandler.CreateProduct)
            manager.GET("/transactions", transHandler.GetAllTransactions)
            manager.PUT("/transactions/:id/status", transHandler.UpdateStatus)
        }
    }
    
    // Start server
    port := os.Getenv("PORT")
    if port == "" {
        port = "8080"
    }
    
    log.Printf("Server starting on port %s", port)
    if err := router.Run(":" + port); err != nil {
        log.Fatal("Failed to start server:", err)
    }
}
