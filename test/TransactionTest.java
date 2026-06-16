package test;

import org.junit.jupiter.api.Test;
import src.Transaction;

import static org.junit.jupiter.api.Assertions.*;

class TransactionTest {
    
    @Test
    void testTransactionConstructorAndGetters() {
        Transaction transaction = new Transaction(
            1, 100, "customer@email.com", "2024-01-15", 199.99, "In Queue"
        );
        
        assertEquals(1, transaction.getId());
        assertEquals(100, transaction.getCustomerId());
        assertEquals("customer@email.com", transaction.getCustomerEmail());
        assertEquals("2024-01-15", transaction.getDate());
        assertEquals(199.99, transaction.getAmount(), 0.001);
        assertEquals("In Queue", transaction.getStatus());
    }
}
