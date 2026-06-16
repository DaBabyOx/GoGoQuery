package handler

import (
    "net/http"
    "strconv"

    "github.com/gin-gonic/gin"

    "gogoquery/internal/models"
    "gogoquery/internal/service"
)

type TransactionHandler struct {
    transService *service.TransactionService
}

func NewTransactionHandler(transService *service.TransactionService) *TransactionHandler {
    return &TransactionHandler{transService: transService}
}

func (h *TransactionHandler) GetAllTransactions(c *gin.Context) {
    // Check if user is manager
    role, exists := c.Get("role")
    if !exists || role != "Manager" {
        c.JSON(http.StatusForbidden, gin.H{"error": "Only managers can view transactions"})
        return
    }
    
    transactions, err := h.transService.GetAllTransactions()
    if err != nil {
        c.JSON(http.StatusInternalServerError, gin.H{"error": "Failed to fetch transactions"})
        return
    }
    
    c.JSON(http.StatusOK, gin.H{"transactions": transactions})
}

func (h *TransactionHandler) UpdateStatus(c *gin.Context) {
    // Check if user is manager
    role, exists := c.Get("role")
    if !exists || role != "Manager" {
        c.JSON(http.StatusForbidden, gin.H{"error": "Only managers can update transaction status"})
        return
    }
    
    idStr := c.Param("id")
    id, err := strconv.Atoi(idStr)
    if err != nil {
        c.JSON(http.StatusBadRequest, gin.H{"error": "Invalid transaction ID"})
        return
    }
    
    var req models.UpdateStatusRequest
    if err := c.ShouldBindJSON(&req); err != nil {
        c.JSON(http.StatusBadRequest, gin.H{"error": err.Error()})
        return
    }
    
    err = h.transService.UpdateStatus(id, req.Status)
    if err != nil {
        c.JSON(http.StatusBadRequest, gin.H{"error": err.Error()})
        return
    }
    
    c.JSON(http.StatusOK, gin.H{"message": "Transaction status updated"})
}
