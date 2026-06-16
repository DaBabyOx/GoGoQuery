package repository

import (
    "database/sql"
    "errors"

    "gogoquery/internal/models"
)

type CartRepository struct {
    db *sql.DB
}

func NewCartRepository(db *sql.DB) *CartRepository {
    return &CartRepository{db: db}
}

func (r *CartRepository) GetCart(userID int) ([]models.CartItemDetail, error) {
    query := `SELECT MsItem.ItemID, MsItem.ItemName, MsItem.ItemPrice, MsCart.Quantity 
              FROM MsItem 
              JOIN MsCart ON MsItem.ItemID = MsCart.ItemID 
              WHERE MsCart.UserID = ?`
    
    rows, err := r.db.Query(query, userID)
    if err != nil {
        return nil, err
    }
    defer rows.Close()
    
    var items []models.CartItemDetail
    for rows.Next() {
        var item models.CartItemDetail
        if err := rows.Scan(&item.ItemID, &item.Name, &item.Price, &item.Quantity); err != nil {
            return nil, err
        }
        item.UserID = userID
        items = append(items, item)
    }
    
    return items, nil
}

func (r *CartRepository) GetCartItem(userID, itemID int) (*models.CartItem, error) {
    query := "SELECT UserID, ItemID, Quantity FROM MsCart WHERE UserID = ? AND ItemID = ?"
    
    item := &models.CartItem{}
    err := r.db.QueryRow(query, userID, itemID).Scan(&item.UserID, &item.ItemID, &item.Quantity)
    if err != nil {
        if errors.Is(err, sql.ErrNoRows) {
            return nil, nil
        }
        return nil, err
    }
    
    return item, nil
}

func (r *CartRepository) AddToCart(userID, itemID, quantity int) (string, error) {
    // Check current stock
    stock, err := r.GetProductStock(itemID)
    if err != nil {
        return "", err
    }
    
    // Check if item already in cart
    existing, err := r.GetCartItem(userID, itemID)
    if err != nil {
        return "", err
    }
    
    if existing != nil {
        // Item exists, update quantity
        newQuantity := existing.Quantity + quantity
        if newQuantity > stock {
            newQuantity = stock
        }
        
        err = r.UpdateCartItem(userID, itemID, newQuantity)
        if err != nil {
            return "", err
        }
        
        if newQuantity == stock {
            return "not enough stock", nil
        }
        return "updated", nil
    } else {
        // New item, add to cart
        if quantity > stock {
            quantity = stock
        }
        
        query := "INSERT INTO MsCart (UserID, ItemID, Quantity) VALUES (?, ?, ?)"
        _, err := r.db.Exec(query, userID, itemID, quantity)
        if err != nil {
            return "", err
        }
        
        return "added", nil
    }
}

func (r *CartRepository) UpdateCartItem(userID, itemID, quantity int) error {
    if quantity == 0 {
        return r.RemoveFromCart(userID, itemID)
    }
    
    query := "UPDATE MsCart SET Quantity = ? WHERE UserID = ? AND ItemID = ?"
    result, err := r.db.Exec(query, quantity, userID, itemID)
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

func (r *CartRepository) RemoveFromCart(userID, itemID int) error {
    query := "DELETE FROM MsCart WHERE UserID = ? AND ItemID = ?"
    _, err := r.db.Exec(query, userID, itemID)
    return err
}

func (r *CartRepository) GetTotalCartPrice(userID int) (float64, error) {
    query := `SELECT SUM(p.ItemPrice * c.Quantity) AS total 
              FROM MsCart c 
              JOIN MsItem p ON c.ItemID = p.ItemID 
              WHERE c.UserID = ?`
    
    var total sql.NullFloat64
    err := r.db.QueryRow(query, userID).Scan(&total)
    if err != nil {
        return 0, err
    }
    
    if total.Valid {
        return total.Float64, nil
    }
    return 0, nil
}

func (r *CartRepository) ClearCart(userID int) error {
    query := "DELETE FROM MsCart WHERE UserID = ?"
    _, err := r.db.Exec(query, userID)
    return err
}

func (r *CartRepository) GetProductStock(itemID int) (int, error) {
    query := "SELECT ItemStock FROM MsItem WHERE ItemID = ?"
    
    var stock int
    err := r.db.QueryRow(query, itemID).Scan(&stock)
    if err != nil {
        return 0, err
    }
    
    return stock, nil
}
