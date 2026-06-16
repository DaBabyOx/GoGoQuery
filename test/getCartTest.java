package test;

import org.junit.jupiter.api.Test;
import src.getCart;

import static org.junit.jupiter.api.Assertions.*;

class getCartTest {
    
    @Test
    void testGetCartConstructorAndGetters() {
        getCart cartItem = new getCart(100, 200, "Test Item", 29.99, 3);
        
        assertEquals(100, cartItem.getUid());
        assertEquals(200, cartItem.getPid());
        assertEquals("Test Item", cartItem.getName());
        assertEquals(29.99, cartItem.getPrice(), 0.001);
        assertEquals(3, cartItem.getQty());
    }
}
