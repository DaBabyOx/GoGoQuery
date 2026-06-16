package repository

import (
    "database/sql"

    "gogoquery/internal/models"
)

type TransactionRepository struct {
    db *sql.DB
}

func NewTransactionRepository(db *sql.DB) *TransactionRepository {
    return &TransactionRepository{db: db}
}

func (r *TransactionRepository) Checkout(userID int) error {
    tx, err := r.db.Begin()
    if err != nil {
        return err
    }
    defer func() {
        if err != nil {
            tx.Rollback()
        }
    }()
    
    // 1. Create transaction header
    query := "INSERT INTO TransactionHeader (UserID, DateCreated, Status) VALUES (?, CURDATE(), 'In Queue')"
    result, err := tx.Exec(query, userID)
    if err != nil {
        return err
    }
    
    transactionID, err := result.LastInsertId()
    if err != nil {
        return err
    }
    
    // 2. Transfer cart items to transaction details
    query = `INSERT INTO TransactionDetail (TransactionID, ItemID, Quantity) 
             SELECT ?, ItemID, Quantity FROM MsCart WHERE UserID = ?`
    _, err = tx.Exec(query, transactionID, userID)
    if err != nil {
        return err
    }
    
    // 3. Update stock (atomic operation)
    query = `UPDATE MsItem 
             SET ItemStock = ItemStock - (
                 SELECT Quantity FROM MsCart 
                 WHERE MsCart.ItemID = MsItem.ItemID AND MsCart.UserID = ?
             ) 
             WHERE EXISTS (
                 SELECT 1 FROM MsCart 
                 WHERE MsCart.ItemID = MsItem.ItemID AND MsCart.UserID = ?
             )`
    _, err = tx.Exec(query, userID, userID)
    if err != nil {
        return err
    }
    
    // 4. Clear cart
    query = "DELETE FROM MsCart WHERE UserID = ?"
    _, err = tx.Exec(query, userID)
    if err != nil {
        return err
    }
    
    return tx.Commit()
}

func (r *TransactionRepository) GetAllTransactions() ([]models.TransactionView, error) {
    query := `SELECT 
                th.TransactionID AS id,
                th.UserID AS customerId,
                mu.UserEmail AS customerEmail,
                th.DateCreated AS date,
                (SELECT SUM(td.Quantity * mi.ItemPrice) 
                 FROM TransactionDetail td 
                 JOIN MsItem mi ON td.ItemID = mi.ItemID 
                 WHERE td.TransactionID = th.TransactionID) AS amount,
                th.Status AS status
              FROM TransactionHeader th 
              JOIN MsUser mu ON th.UserID = mu.UserID`
    
    rows, err := r.db.Query(query)
    if err != nil {
        return nil, err
    }
    defer rows.Close()
    
    var transactions []models.TransactionView
    for rows.Next() {
        var t models.TransactionView
        var amount sql.NullFloat64
        if err := rows.Scan(&t.ID, &t.CustomerID, &t.CustomerEmail, &t.Date, &amount, &t.Status); err != nil {
            return nil, err
        }
        if amount.Valid {
            t.Amount = amount.Float64
        }
        transactions = append(transactions, t)
    }
    
    return transactions, nil
}

func (r *TransactionRepository) UpdateStatus(transactionID int, status string) error {
    query := "UPDATE TransactionHeader SET Status = ? WHERE TransactionID = ?"
    result, err := r.db.Exec(query, status, transactionID)
    if err != nil {
        return err
    }
    
    rows, err := result.RowsAffected()
    if err != nil {
        return err
    }
    
    if rows == 0 {
        return sql.ErrNoRows
    }
    
    return nil
}

func (r *TransactionRepository) GetTransactionStatus(transactionID int) (string, error) {
    query := "SELECT Status FROM TransactionHeader WHERE TransactionID = ?"
    
    var status string
    err := r.db.QueryRow(query, transactionID).Scan(&status)
    if err != nil {
        return "", err
    }
    
    return status, nil
}
