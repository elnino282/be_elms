package org.example.be_elms.util;

import org.example.be_elms.model.entity.Employee;

/**
 * Simple thread-local storage for current authenticated user
 * In production, this would be replaced with Spring Security context
 */
public class UserContext {
    private static final ThreadLocal<Employee> currentUser = new ThreadLocal<>();
    
    public static void setCurrentUser(Employee employee) {
        currentUser.set(employee);
    }
    
    public static Employee getCurrentUser() {
        return currentUser.get();
    }
    
    public static void clear() {
        currentUser.remove();
    }
}

