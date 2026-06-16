package service

import (
    "errors"

    "gogoquery/internal/models"
    "gogoquery/internal/repository"
)

type CartService struct {
    cartRepo *repository.CartRepository
    userRepo *repository.UserRepository
}

func NewCartService(cartRepo *repository.CartRepository, userRepo *repository.UserRepository) *CartService {
    return &CartService{cartRepo: cartRepo, userRepo: userRepo}
}

func (s *CartService) AddToCart(email string, req *models.AddToCartRequest) (string, error) {
    userID, err := s.userRepo.GetUserID(email)
    if err != nil {
        return "", err
    }
    
    result, err := s.cartRepo.AddToCart(userID, req.ItemID, req.Quantity)
    if err != nil {
        return "", err
    }
    
    return result, nil
}

func (s *CartService) GetCart(email string) ([]models.CartItemDetail, error) {
    userID, err := s.userRepo.GetUserID(email)
    if err != nil {
        return nil, err
    }
    
    return s.cartRepo.GetCart(userID)
}

func (s *CartService) UpdateCartItem(email string, itemID int, quantity int) error {
    userID, err := s.userRepo.GetUserID(email)
    if err != nil {
        return err
    }
    
    return s.cartRepo.UpdateCartItem(userID, itemID, quantity)
}

func (s *CartService) RemoveFromCart(email string, itemID int) error {
    userID, err := s.userRepo.GetUserID(email)
    if err != nil {
        return err
    }
    
    return s.cartRepo.RemoveFromCart(userID, itemID)
}

func (s *CartService) GetTotalPrice(email string) (float64, error) {
    userID, err := s.userRepo.GetUserID(email)
    if err != nil {
        return 0, err
    }
    
    return s.cartRepo.GetTotalCartPrice(userID)
}

func (s *CartService) Checkout(email string) error {
    userID, err := s.userRepo.GetUserID(email)
    if err != nil {
        return err
    }
    
    // Check if cart is empty
    cartItems, err := s.cartRepo.GetCart(userID)
    if err != nil {
        return err
    }
    
    if len(cartItems) == 0 {
        return errors.New("cart is empty")
    }
    
    return s.cartRepo.Checkout(userID)
}
