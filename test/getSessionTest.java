package test;

import org.junit.jupiter.api.Test;
import src.getSession;

import static org.junit.jupiter.api.Assertions.*;

class getSessionTest {
    
    @Test
    void testSingletonPattern() {
        getSession instance1 = getSession.getInstance();
        getSession instance2 = getSession.getInstance();
        
        assertSame(instance1, instance2, "Should return the same instance");
    }
    
    @Test
    void testEmailSetterAndGetter() {
        getSession session = getSession.getInstance();
        
        session.setEmail("test@example.com");
        assertEquals("test@example.com", session.getEmail());
        
        session.setEmail("another@example.com");
        assertEquals("another@example.com", session.getEmail());
    }
    
    @Test
    void testSessionPersistence() {
        getSession session1 = getSession.getInstance();
        session1.setEmail("persistent@example.com");
        
        getSession session2 = getSession.getInstance();
        assertEquals("persistent@example.com", session2.getEmail());
    }
}
