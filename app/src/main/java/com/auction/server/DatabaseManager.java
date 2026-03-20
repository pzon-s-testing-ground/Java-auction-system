package com.auction.server;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseManager {
    // Duong dan toi file database (no se tu dong duoc tao trong thu muc goc cua project)
    private static final String URL = "jdbc:sqlite:auction.db";
    private static DatabaseManager instance;
    private Connection connection;

    private DatabaseManager() {
        connect();
        initializeDatabase();
    }

    public static synchronized DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }

   private void connect() {
        try {
            // Chu dong yeu cau Java tai SQLite Driver len bo nho
            Class.forName("org.sqlite.JDBC");
            
            connection = DriverManager.getConnection(URL);
            System.out.println("Connected to SQLite database successfully.");
            
        } catch (ClassNotFoundException e) {
            // Bat loi neu Gradle chua tai thu vien ve
            System.err.println("SQLite JDBC Driver not found in classpath! Please sync Gradle.");
        } catch (SQLException e) {
            System.err.println("Database connection error: " + e.getMessage());
        }
    }

    public Connection getConnection() {
        try {
            // Kiem tra neu ket noi bi dong thi mo lai
            if (connection == null || connection.isClosed()) {
                connect();
            }
        } catch (SQLException e) {
            System.err.println("Cannot establish database connection: " + e.getMessage());
        }
        return connection;
    }

    // Tu dong tao bang neu chua co
    private void initializeDatabase() {

        if (connection == null) {
            System.err.println("Skipping table initialization because connection is null.");
            return; 
        }

        String createTableSQL = "CREATE TABLE IF NOT EXISTS auctions ("
                + "id TEXT PRIMARY KEY,"
                + "item_name TEXT NOT NULL,"
                + "starting_price REAL NOT NULL,"
                + "current_bid REAL NOT NULL,"
                + "status TEXT NOT NULL"
                + ");";


        String createUsersTable = "CREATE TABLE IF NOT EXISTS users ("
                + "username TEXT PRIMARY KEY,"
                + "password TEXT NOT NULL"
                + ");";

    
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(createTableSQL);
            stmt.execute(createUsersTable);
            System.out.println("Database tables initialized.");
        } catch (SQLException e) {
            System.err.println("Error initializing tables: " + e.getMessage());
        }
    }
}