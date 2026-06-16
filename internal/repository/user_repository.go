package repository

import (
    "database/sql"
    "errors"
    "time"

    "gogoquery/internal/models"
)

type UserRepository struct {
    db *sql.DB
}

func NewUserRepository(db *sql.DB) *UserRepository {
    return &UserRepository{db: db}
}

func (r *UserRepository) ValidateLogin(email, password string) (bool, error) {
    query := "SELECT COUNT(*) FROM MsUser WHERE UserEmail = ? AND UserPassword = ?"
    
    var count int
    err := r.db.QueryRow(query, email, password).Scan(&count)
    if err != nil {
        return false, err
    }
    
    return count > 0, nil
}

func (r *UserRepository) CheckEmailExists(email string) (bool, error) {
    query := "SELECT COUNT(*) FROM MsUser WHERE UserEmail = ?"
    
    var count int
    err := r.db.QueryRow(query, email).Scan(&count)
    if err != nil {
        return false, err
    }
    
    return count > 0, nil
}

func (r *UserRepository) CreateUser(user *models.User) error {
    query := `INSERT INTO MsUser (UserDOB, UserEmail, UserPassword, UserGender, UserRole) 
              VALUES (?, ?, ?, ?, ?)`
    
    result, err := r.db.Exec(query, user.DOB, user.Email, user.Password, user.Gender, user.Role)
    if err != nil {
        return err
    }
    
    id, err := result.LastInsertId()
    if err != nil {
        return err
    }
    
    user.ID = int(id)
    return nil
}

func (r *UserRepository) GetUserByEmail(email string) (*models.User, error) {
    query := "SELECT UserID, UserDOB, UserEmail, UserPassword, UserGender, UserRole FROM MsUser WHERE UserEmail = ?"
    
    user := &models.User{}
    err := r.db.QueryRow(query, email).Scan(
        &user.ID,
        &user.DOB,
        &user.Email,
        &user.Password,
        &user.Gender,
        &user.Role,
    )
    
    if err != nil {
        if errors.Is(err, sql.ErrNoRows) {
            return nil, nil
        }
        return nil, err
    }
    
    return user, nil
}

func (r *UserRepository) GetUserID(email string) (int, error) {
    query := "SELECT UserID FROM MsUser WHERE UserEmail = ?"
    
    var userID int
    err := r.db.QueryRow(query, email).Scan(&userID)
    if err != nil {
        return 0, err
    }
    
    return userID, nil
}

func (r *UserRepository) CalculateAge(dob time.Time) int {
    now := time.Now()
    years := now.Year() - dob.Year()
    
    if now.YearDay() < dob.YearDay() {
        years--
    }
    
    return years
}
