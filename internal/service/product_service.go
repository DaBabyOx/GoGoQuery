package service

import (
    "errors"

    "gogoquery/internal/models"
    "gogoquery/internal/repository"
)

type ProductService struct {
    productRepo *repository.ProductRepository
}

func NewProductService(productRepo *repository.ProductRepository) *ProductService {
    return &ProductService{productRepo: productRepo}
}

func (s *ProductService) GetCategories() ([]string, error) {
    return s.productRepo.GetCategories()
}

func (s *ProductService) GetProducts() ([]models.Product, error) {
    products, err := s.productRepo.GetProducts()
    if err != nil {
        return nil, err
    }
    
    // Filter out products with zero stock
    var availableProducts []models.Product
    for _, p := range products {
        if p.Stock > 0 {
            availableProducts = append(availableProducts, p)
        }
    }
    
    return availableProducts, nil
}

func (s *ProductService) GetProductByID(id int) (*models.Product, error) {
    return s.productRepo.GetProductByID(id)
}

func (s *ProductService) FilterProducts(category, searchText string) ([]models.Product, error) {
    return s.productRepo.FilterProducts(category, searchText)
}

func (s *ProductService) CreateProduct(req *models.AddProductRequest) (*models.Product, error) {
    // Business rule validations
    if len(req.Name) < 5 || len(req.Name) > 70 {
        return nil, errors.New("item name must be between 5 and 70 characters")
    }
    
    if len(req.Desc) < 10 || len(req.Desc) > 255 {
        return nil, errors.New("item description must be between 10 and 255 characters")
    }
    
    if req.Price < 0.50 || req.Price > 900000 {
        return nil, errors.New("item price must be between $0.50 and $900,000")
    }
    
    if req.Stock <= 0 {
        return nil, errors.New("quantity must be greater than 0")
    }
    
    product := &models.Product{
        Name:     req.Name,
        Category: req.Category,
        Price:    req.Price,
        Desc:     req.Desc,
        Stock:    req.Stock,
    }
    
    err := s.productRepo.CreateProduct(product)
    if err != nil {
        return nil, err
    }
    
    return product, nil
}
