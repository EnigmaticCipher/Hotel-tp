/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hotel.databaseOperation;

import hotel.classes.UserInfo;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JOptionPane;

/**
 *
 * @author Faysal Ahmed
 */
public class CustomerDb {
    Connection conn;
    PreparedStatement statement = null;
    ResultSet result = null;
    
    public CustomerDb()
    {
        conn = DataBaseConnection.connectTODB();
    }
    //***************************and here */
      public void insertCustomer(UserInfo user)  {
        try {
            String insertQuery = ""
                + "INSERT INTO userInfo "
                + "(name, address, phone, type) "
                + "VALUES (?, ?, ?, ?)";
        
            statement = conn.prepareStatement(insertQuery);
        
            statement.setString(1, user.getName());
            statement.setString(2, user.getAddress());
            statement.setString(3, user.getPhoneNo());
            statement.setString(4, user.getType());
        
            statement.executeUpdate(); // Use executeUpdate for inserts
        
            JOptionPane.showMessageDialog(null, "Successfully inserted new Customer!");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, ex.toString() + "\nInsert query Failed");
        } finally {
            flushStatementOnly();
        }
                
    }
  //************************** i modify here  */  
    public void updateCustomer(UserInfo user) {
        try {
            String updateQuery = ""
                + "UPDATE userInfo "
                + "SET name = ?, "
                + "address = ?, "
                + "phone = ?, "
                + "type = ? "
                + "WHERE user_id = ?";
        
            statement = conn.prepareStatement(updateQuery);
        
            statement.setString(1, user.getName());
            statement.setString(2, user.getAddress());
            statement.setString(3, user.getPhoneNo());
            statement.setString(4, user.getType());
            statement.setInt(5, user.getCustomerId());
        
            statement.executeUpdate(); // Use executeUpdate for update/insert/delete
        
            JOptionPane.showMessageDialog(null, "Successfully updated Customer!");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, ex.toString() + "\nUpdate query Failed");
        } finally {
            flushStatementOnly();
        }

    }
//********************************and here ****************************************** */
    public void deleteCustomer(int userId) throws SQLException {
        String sql = "DELETE FROM userInfo WHERE customer_id = ?";

  try (
    Connection conn = DataBaseConnection.connectTODB();
    PreparedStatement pstmt = conn.prepareStatement(sql)
  ) {
    pstmt.setInt(1, userId);

    int rows = pstmt.executeUpdate();
    JOptionPane.showMessageDialog(
      null, rows + " customer(s) deleted successfully!"
    );
  } catch (SQLException ex) {
    JOptionPane.showMessageDialog(
      null, "Error deleting customer: " + ex.getMessage()
    );
  }

    }
//****************************************************************** */
    public ResultSet getAllCustomer() {
        try {
            String query = "select * from userInfo";
            statement = conn.prepareStatement(query);
            result = statement.executeQuery();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, ex.toString() + "\n error coming from returning all customer DB Operation");
        }
        

        return result;
    }
     private void flushStatementOnly()
    {
        {
                        try
                        {
                            statement.close();
                        }
                        catch(SQLException ex)
                        {System.err.print(ex.toString()+" >> CLOSING DB");}
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
