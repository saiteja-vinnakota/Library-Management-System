import java.sql.*;
import java.util.Scanner;

public class LibraryManagementSystem {

    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        while (true) {
            System.out.println("\nLibrary Management System");
            System.out.println("1. Add Book");
            System.out.println("2. List Books");
            System.out.println("3. Borrow Book");
            System.out.println("4. Return Book");
            System.out.println("5. Exit");

            System.out.print("Choose an option: ");
            int choice = scanner.nextInt();
            scanner.nextLine(); 

            switch (choice) {
                case 1:
                    addBook();
                    break;
                case 2:
                    listBooks();
                    break;
                case 3:
                    borrowBook();
                    break;
                case 4:
                    returnBook();
                    break;
                case 5:
                    System.exit(0);
                    break;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
    }

    private static void addBook() {
        System.out.print("Enter book title: ");
        String title = scanner.nextLine();
        System.out.print("Enter author name: ");
        String author = scanner.nextLine();
        System.out.print("Enter number of available copies: ");
        int available = scanner.nextInt();

        try (Connection connection = DatabaseConnection.connect()) {
            String query = "INSERT INTO Books (title, author, available) VALUES (?, ?, ?)";
            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setString(1, title);
                stmt.setString(2, author);
                stmt.setInt(3, available); 
                stmt.executeUpdate();
                System.out.println("Book added successfully.");
            }
        } catch (SQLException e) {
            System.out.println("Error adding book: " + e.getMessage());
        }
    }
    private static void listBooks() {
        try (Connection connection = DatabaseConnection.connect()) {
            String query = "SELECT * FROM Books WHERE available > 0"; 
            try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
                System.out.println("\nAvailable Books:");
                while (rs.next()) {
                    int id = rs.getInt("book_id");
                    String title = rs.getString("title");
                    String author = rs.getString("author");
                    int available = rs.getInt("available"); 
                    System.out.println("ID: " + id + " | Title: " + title + " | Author: " + author + " | Available: " + available);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error listing books: " + e.getMessage());
        }
    }

    private static void borrowBook() {
        System.out.print("Enter book ID to borrow: ");
        int bookId = scanner.nextInt();

        try (Connection connection = DatabaseConnection.connect()) {
            String query = "UPDATE Books SET available = available - 1 WHERE book_id = ? AND available > 0";
            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setInt(1, bookId);
                int rowsAffected = stmt.executeUpdate();
                if (rowsAffected > 0) {
                    System.out.println("Book borrowed successfully.");
                } else {
                    System.out.println("Book not available or does not exist.");
                }
            }
        } catch (SQLException e) {
            System.out.println("Error borrowing book: " + e.getMessage());
        }
    }

    private static void returnBook() {
        System.out.print("Enter book ID to return: ");
        int bookId = scanner.nextInt();

        try (Connection connection = DatabaseConnection.connect()) {
            String query = "UPDATE Books SET available = available + 1 WHERE book_id = ?";
            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setInt(1, bookId);
                int rowsAffected = stmt.executeUpdate();
                if (rowsAffected > 0) {
                    System.out.println("Book returned successfully.");
                } else {
                    System.out.println("Book not found.");
                }
            }
        } catch (SQLException e) {
            System.out.println("Error returning book: " + e.getMessage());
        }
    }
}
