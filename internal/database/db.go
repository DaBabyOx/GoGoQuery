package database

import (
    "database/sql"
    "fmt"
    "log"
    "time"

    _ "github.com/go-sql-driver/mysql"
)

const (
    dbDriver   = "mysql"
    dbUser     = "root"
    dbPassword = "Ayasjago1@"
    dbName     = "gogoquery"
    dbHost     = "localhost"
    dbPort     = "3306"
)

var DB *sql.DB

func InitDB() error {
    dsn := fmt.Sprintf("%s:%s@tcp(%s:%s)/%s?parseTime=true", dbUser, dbPassword, dbHost, dbPort, dbName)
    
    var err error
    DB, err = sql.Open(dbDriver, dsn)
    if err != nil {
        return fmt.Errorf("failed to open database: %v", err)
    }
    
    DB.SetMaxOpenConns(25)
    DB.SetMaxIdleConns(25)
    DB.SetConnMaxLifetime(5 * time.Minute)
    
    if err = DB.Ping(); err != nil {
        return fmt.Errorf("failed to ping database: %v", err)
    }
    
    log.Println("Database connection established")
    return nil
}

func CloseDB() {
    if DB != nil {
        DB.Close()
    }
}
