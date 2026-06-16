package test;

import org.junit.jupiter.api.Test;
import src.Product;

import static org.junit.jupiter.api.Assertions.*;

class ProductTest {
    
    @Test
    void testProductConstructorAndGetters() {
        Product product = new Product(1, "Test Product", "Electronics", 99.99, "Test Description", 10);
        
        assertEquals(1, product.getId());
        assertEquals("Test Product", product.getName());
        assertEquals("Electronics", product.getCategory());
        assertEquals(99.99, product.getPrice(), 0.001);
        assertEquals("Test Description", product.getDesc());
        assertEquals(10, product.getStock());
    }
    
    @Test
    void testProductEquality() {
        Product product1 = new Product(1, "Product", "Category", 10.0, "Desc", 5);
        Product product2 = new Product(1, "Product", "Category", 10.0, "Desc", 5);
        Product product3 = new Product(2, "Different", "Category", 20.0, "Desc", 10);
        
        // Same ID should be considered equal for database purposes
        assertEquals(product1.getId(), product2.getId());
        assertNotEquals(product1.getId(), product3.getId());
    }
}
