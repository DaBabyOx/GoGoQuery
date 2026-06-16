package service

import (
    "errors"

    "gogoquery/internal/models"
    "gogoquery/internal/repository"
)

type TransactionService struct {
    transRepo *repository.TransactionRepository
}

func NewTransactionService(transRepo *repository.TransactionRepository) *TransactionService {
    return &TransactionService{transRepo: transRepo}
}

func (s *TransactionService) GetAllTransactions() ([]models.TransactionView, error) {
    return s.transRepo.GetAllTransactions()
}

func (s *TransactionService) UpdateStatus(transactionID int, newStatus string) error {
    // Check current status
    currentStatus, err := s.transRepo.GetTransactionStatus(transactionID)
    if err != nil {
        return err
    }
    
    // Business rule: Can only update if status is "In Queue"
    if currentStatus != "In Queue" {
        return errors.New("can only update transactions with 'In Queue' status")
    }
    
    // Business rule: Can only update to "Sent"
    if newStatus != "Sent" {
        return errors.New("can only update status to 'Sent'")
    }
    
    return s.transRepo.UpdateStatus(transactionID, newStatus)
}
