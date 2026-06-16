package service

import (
    "errors"
    "regexp"
    "strings"
    "time"

    "gogoquery/internal/models"
    "gogoquery/internal/repository"
    "golang.org/x/crypto/bcrypt"
)

type AuthService struct {
    userRepo *repository.UserRepository
}

func NewAuthService(userRepo *repository.UserRepository) *AuthService {
    return &AuthService{userRepo: userRepo}
}

func (s *AuthService) ValidateRegistration(req *models.RegisterRequest) error {
    // Email domain validation
    if !strings.HasSuffix(req.Email, "@gomail.com") {
        return errors.New("email must end with '@gomail.com'")
    }
    
    // Password alphanumeric validation
    if !isAlphanumeric(req.Password) {
        return errors.New("password must be alphanumeric")
    }
    
    // Password match validation
    if req.Password != req.Confirm {
        return errors.New("passwords don't match")
    }
    
    // Age validation
    age := s.userRepo.CalculateAge(req.DOB)
    if age < 17 {
        return errors.New("you must be at least 17 years old")
    }
    
    // Gender validation
    if req.Gender != "Male" && req.Gender != "Female" {
        return errors.New("select your gender")
    }
    
    // Terms agreement
    if !req.Agree {
        return errors.New("you must agree to terms and conditions")
    }
    
    // Check email uniqueness
    exists, err := s.userRepo.CheckEmailExists(req.Email)
    if err != nil {
        return err
    }
    if exists {
        return errors.New("email already exists")
    }
    
    return nil
}

func (s *AuthService) Register(req *models.RegisterRequest) (*models.User, error) {
    // Hash password
    hashedPassword, err := bcrypt.GenerateFromPassword([]byte(req.Password), bcrypt.DefaultCost)
    if err != nil {
        return nil, err
    }
    
    user := &models.User{
        DOB:      req.DOB,
        Email:    req.Email,
        Password: string(hashedPassword),
        Gender:   req.Gender,
        Role:     req.Role,
    }
    
    err = s.userRepo.CreateUser(user)
    if err != nil {
        return nil, err
    }
    
    return user, nil
}

func (s *AuthService) Login(email, password string) (*models.User, error) {
    user, err := s.userRepo.GetUserByEmail(email)
    if err != nil {
        return nil, err
    }
    
    if user == nil {
        return nil, errors.New("invalid credentials")
    }
    
    // Verify password
    err = bcrypt.CompareHashAndPassword([]byte(user.Password), []byte(password))
    if err != nil {
        return nil, errors.New("invalid credentials")
    }
    
    return user, nil
}

func isAlphanumeric(s string) bool {
    hasLetter := false
    hasDigit := false
    
    for _, c := range s {
        if (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') {
            hasLetter = true
        } else if c >= '0' && c <= '9' {
            hasDigit = true
        }
        
        if hasLetter && hasDigit {
            return true
        }
    }
    
    return false
}

func (s *AuthService) ValidateEmail(email string) error {
    // Basic email format check
    emailRegex := regexp.MustCompile(`^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$`)
    if !emailRegex.MatchString(email) {
        return errors.New("invalid email format")
    }
    
    if !strings.HasSuffix(email, "@gomail.com") {
        return errors.New("email must end with '@gomail.com'")
    }
    
    return nil
}
