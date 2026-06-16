package repository

import (
    "database/sql"
    "strings"

    "gogoquery/internal/models"
)

type ProductRepository struct {
    db *sql.DB
}

func NewProductRepository(db *sql.DB) *ProductRepository {
    return &ProductRepository{db: db}
}

func (r *ProductRepository) GetCategories() ([]string, error) {
    query := "SELECT DISTINCT ItemCategory FROM MsItem"
    
    rows, err := r.db.Query(query)
    if err != nil {
        return nil, err
    }
    defer rows.Close()
    
    categories := []string{"Select a category"}
    for rows.Next() {
        var category string
        if err := rows.Scan(&category); err != nil {
            return nil, err
        }
        categories = append(categories, category)
    }
    
    return categories, nil
}

func (r *ProductRepository) GetProducts() ([]models.Product, error) {
    query := "SELECT ItemID, ItemName, ItemCategory, ItemPrice, ItemDesc, ItemStock FROM MsItem"
    
    rows, err := r.db.Query(query)
    if err != nil {
        return nil, err
    }
    defer rows.Close()
    
    var products []models.Product
    for rows.Next() {
        var p models.Product
        if err := rows.Scan(&p.ID, &p.Name, &p.Category, &p.Price, &p.Desc, &p.Stock); err != nil {
            return nil, err
        }
        products = append(products, p)
    }
    
    return products, nil
}

func (r *ProductRepository) GetProductByID(id int) (*models.Product, error) {
    query := "SELECT ItemID, ItemName, ItemCategory, ItemPrice, ItemDesc, ItemStock FROM MsItem WHERE ItemID = ?"
    
    p := &models.Product{}
    err := r.db.QueryRow(query, id).Scan(&p.ID, &p.Name, &p.Category, &p.Price, &p.Desc, &p.Stock)
    if err != nil {
        return nil, err
    }
    
    return p, nil
}

func (r *ProductRepository) GetProductStock(itemID int) (int, error) {
    query := "SELECT ItemStock FROM MsItem WHERE ItemID = ?"
    
    var stock int
    err := r.db.QueryRow(query, itemID).Scan(&stock)
    if err != nil {
        return 0, err
    }
    
    return stock, nil
}

func (r *ProductRepository) FilterProducts(category, searchText string) ([]models.Product, error) {
    query := "SELECT ItemID, ItemName, ItemCategory, ItemPrice, ItemDesc, ItemStock FROM MsItem WHERE ItemStock > 0"
    var args []interface{}
    
    if category != "" && category != "Select a category" {
        query += " AND ItemCategory = ?"
        args = append(args, category)
    }
    
    if searchText != "" {
        query += " AND LOWER(ItemName) LIKE ?"
        args = append(args, "%"+strings.ToLower(searchText)+"%")
    }
    
    rows, err := r.db.Query(query, args...)
    if err != nil {
        return nil, err
    }
    defer rows.Close()
    
    var products []models.Product
    for rows.Next() {
        var p models.Product
        if err := rows.Scan(&p.ID, &p.Name, &p.Category, &p.Price, &p.Desc, &p.Stock); err != nil {
            return nil, err
        }
        products = append(products, p)
    }
    
    return products, nil
}

func (r *ProductRepository) CreateProduct(p *models.Product) error {
    query := `INSERT INTO MsItem (ItemName, ItemCategory, ItemPrice, ItemDesc, ItemStock) 
              VALUES (?, ?, ?, ?, ?)`
    
    result, err := r.db.Exec(query, p.Name, p.Category, p.Price, p.Desc, p.Stock)
    if err != nil {
        return err
    }
    
    id, err := result.LastInsertId()
    if err != nil {
        return err
    }
    
    p.ID = int(id)
    return nil
}

func (r *ProductRepository) UpdateStock(itemID, quantity int) error {
    query := "UPDATE MsItem SET ItemStock = ItemStock - ? WHERE ItemID = ? AND ItemStock >= ?"
    
    result, err := r.db.Exec(query, quantity, itemID, quantity)
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
