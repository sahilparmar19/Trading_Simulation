package model;

import java.sql.Timestamp;

public class User {
    private int userId;
    private String username;
    private String passwordHash;
    private String name;
    private double balance;
    private boolean isAdmin;
    private Timestamp createdAt;

    public User(int userId, String username, String passwordHash, String name, double balance, boolean isAdmin, Timestamp createdAt) {
        this.userId = userId;
        this.username = username;
        this.passwordHash = passwordHash;
        this.name = name;
        this.balance = balance;
        this.isAdmin = isAdmin;
        this.createdAt = createdAt;
    }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public double getBalance() { return balance; }
    public void setBalance(double balance) { this.balance = balance; }

    public boolean isAdmin() { return isAdmin; }
    public void setAdmin(boolean admin) { isAdmin = admin; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
}
