/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hotel.databaseOperation;

import hotel.classes.Item;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JOptionPane;

/**
 *
 * @author Faysal Ahmed
 */

public class ItemDb {
    private final Connection conn;
    private PreparedStatement statement;
    private ResultSet result;

    public ItemDb() {
        conn = DataBaseConnection.connectTODB();
    }

    // 1) INSERT
    public void insertItem(Item item) {
        String sql = "INSERT INTO item (name, description, price) VALUES (?, ?, ?)";
        try {
            statement = conn.prepareStatement(sql);
            statement.setString(1, item.getItemName());
            statement.setString(2, item.getDescription());
            statement.setDouble(3, item.getPrice());
            statement.executeUpdate();
            JOptionPane.showMessageDialog(null, "Successfully inserted item");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error inserting item: " + ex.getMessage());
        } finally {
            flushStatementOnly();
        }
    }

    // 2) UPDATE
    public void updateItem(Item item) {
        String sql = "UPDATE item SET name = ?, description = ?, price = ? WHERE item_id = ?";
        try {
            statement = conn.prepareStatement(sql);
            statement.setString(1, item.getItemName());
            statement.setString(2, item.getDescription());
            statement.setDouble(3, item.getPrice());
            statement.setInt(4, item.getItemId());
            statement.executeUpdate();
            JOptionPane.showMessageDialog(null, "Successfully updated item");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error updating item: " + ex.getMessage());
        } finally {
            flushStatementOnly();
        }
    }

    // 3) SELECT ALL → returns ResultSet as you wanted
    public ResultSet getItems() {
        String sql = "SELECT * FROM item";
        try {
            statement = conn.prepareStatement(sql);
            result = statement.executeQuery();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error fetching items: " + ex.getMessage());
        }
        return result;
    }

    // 4) DELETE
    public void deleteItem(int itemId) {
        String sql = "DELETE FROM item WHERE item_id = ?";
        try {
            statement = conn.prepareStatement(sql);
            statement.setInt(1, itemId);
            statement.executeUpdate();
            JOptionPane.showMessageDialog(null, "Successfully deleted item");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error deleting item: " + ex.getMessage());
        } finally {
            flushStatementOnly();
        }
    }

    // your existing cleanup helper
    private void flushStatementOnly() {
        try {
            if (statement != null && !statement.isClosed()) {
                statement.close();
            }
        } catch (SQLException ex) {
            System.err.println("Error closing statement: " + ex.getMessage());
        }
    }

    public void flushAll()
    {
        {
                        try
                        {
                            statement.close();
                            result.close();
                        }
                        catch(SQLException ex)
                        {System.err.print(ex.toString()+" >> CLOSING DB");}
                    }
    } 
}
