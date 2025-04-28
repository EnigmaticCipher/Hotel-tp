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
public class DatabaseOperation {

    Connection conn = DataBaseConnection.connectTODB();
    PreparedStatement statement = null;
    ResultSet result = null;
//********************* i modify here */
    public void insertCustomer(UserInfo user) throws SQLException {
        // try {
        //     String insertQuery = "insert into userInfo"
        //             + "('" + "name" + "'," + "'" + "address" + "','" + "phone" + "','" + "type" + "')"
        //             + " values('"
        //             + user.getName()
        //             + "','" + user.getAddress() + "'"
        //             + ",'" + user.getPhoneNo() + "'"
        //             + ",'" + user.getType() + "'"
        //             + ")";

        //     statement = conn.prepareStatement(insertQuery);

        //     statement.execute();

        //     JOptionPane.showMessageDialog(null, "successfully inserted new Customer");

        // } catch (SQLException ex) {
        //     JOptionPane.showMessageDialog(null, ex.toString() + "\n" + "InsertQuery Failed");
        // }
        // finally
        // {
        //     flushStatmentOnly();
        // }
        String sql = ""
    + "INSERT INTO userInfo "
    + "(name, address, phone, type) "
    + "VALUES (?, ?, ?, ?)";

  try (
    Connection conn = DataBaseConnection.connectTODB();
    PreparedStatement pstmt = conn.prepareStatement(sql)
  ) {
    pstmt.setString(1, user.getName());
    pstmt.setString(2, user.getAddress());
    pstmt.setString(3, user.getPhoneNo());
    pstmt.setString(4, user.getType());

    int rows = pstmt.executeUpdate();
    JOptionPane.showMessageDialog(
      null, rows + " customer(s) added successfully!"
    );
  } catch (SQLException ex) {
    JOptionPane.showMessageDialog(
      null, "Error inserting customer: " + ex.getMessage()
    );
  }           
    }
    //********************************************************** */
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
    public void updateCustomer(UserInfo user) {
        String sql = ""
            + "UPDATE userInfo "
            + "SET name    = ?, "
            + "    address = ?, "
            + "    phone   = ?, "
            + "    type    = ? "
            + "WHERE user_id = ?";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, user.getName());
            stmt.setString(2, user.getAddress());
            stmt.setString(3, user.getPhoneNo());
            stmt.setString(4, user.getType());
            stmt.setInt(   5, user.getCustomerId());
            
            int rows = stmt.executeUpdate();
            if (rows > 0) {
                JOptionPane.showMessageDialog(null, "Successfully updated customer.");
            } else {
                JOptionPane.showMessageDialog(null, "No customer found with ID " 
                                                    + user.getCustomerId());
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(
                null,
                ex.toString() + "\nUpdate query failed"
            );
        }
    }
    

    public void deleteCustomer(int userId) {
        String sql = "DELETE FROM userInfo WHERE user_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userId);
            
            int rows = stmt.executeUpdate();
            if (rows > 0) {
                JOptionPane.showMessageDialog(null, "Deleted user with ID " + userId);
            } else {
                JOptionPane.showMessageDialog(null, "No user found with ID " + userId);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(
                null,
                ex.toString() + "\nDelete query failed"
            );
        }
    }
    

    public ResultSet getAllCustomer() {
        try {
            String query = "select * from userInfo";
            statement = conn.prepareStatement(query);
            result = statement.executeQuery();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, ex.toString() + "\n error coming from returning all customer DB Operation");
        }
        finally
        {
            flushAll();
        }

        return result;
    }

  
    /// ************************************************************************  SEARCH AND OTHERS ************************************************
    public ResultSet searchUser(String user) {
        try {
            String query = "SELECT user_id, name, address FROM userInfo WHERE name LIKE ?";
            statement = conn.prepareStatement(query);
            statement.setString(1, "%" + user + "%"); // Bind parameter safely
            result = statement.executeQuery();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, ex.toString() + "\nError in searchUser function");
        }
        return result;
    }    
    
    public ResultSet searchAnUser(int id) {
        try {
            String query = "SELECT * FROM userInfo WHERE user_id = ?";
            statement = conn.prepareStatement(query);
            statement.setInt(1, id); // Bind the id safely
            result = statement.executeQuery();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, ex.toString() + "\nError in searchAnUser function");
        }
        return result;
    }

    public ResultSet getAvailableRooms(long checkInTime) {
        ResultSet result = null;
        String query = "SELECT room_no FROM room LEFT OUTER JOIN booking ON room.room_no = booking.booking_room "
                     + "WHERE booking.booking_room IS NULL OR ? < booking.check_in OR booking.check_out < ? "
                     + "GROUP BY room.room_no ORDER BY room_no";
    
        try {
            PreparedStatement statement = conn.prepareStatement(query);
            statement.setLong(1, checkInTime);
            statement.setLong(2, checkInTime);
            result = statement.executeQuery();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, ex.toString() + "\n error coming from returning free rooms from getAvailable func.");
        }
        return result;
    }
    
    
    public ResultSet getBookingInfo(long startDate, long endDate, String roomNo) {
        ResultSet result = null;
        String query = "SELECT * FROM booking WHERE booking_room = ? AND ("
                     + "(check_in <= ? AND (check_out = 0 OR check_out <= ?)) OR "
                     + "(check_in > ? AND check_out < ?) OR "
                     + "(check_in <= ? AND (check_out = 0 OR check_out > ?))"
                     + ")";
        try {
            PreparedStatement statement = conn.prepareStatement(query);
            statement.setString(1, roomNo);
            statement.setLong(2, startDate);
            statement.setLong(3, endDate);
            statement.setLong(4, startDate);
            statement.setLong(5, endDate);
            statement.setLong(6, endDate);
            statement.setLong(7, endDate);
            result = statement.executeQuery();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, ex.toString() + "\n error coming from returning booking info between two specific days");
        }
        return result;
    }
    
    
    public int getCustomerId(UserInfo user) {
        int id = -1;
        String query = "SELECT user_id FROM userInfo WHERE name = ? AND phone = ?";
        
        try (PreparedStatement statement = conn.prepareStatement(query)) {
            statement.setString(1, user.getName());
            statement.setString(2, user.getPhoneNo());
            ResultSet result = statement.executeQuery();
            
            if (result.next()) {  // You forgot to move cursor to first row
                id = result.getInt("user_id");
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, ex.toString() + "\n error coming from returning user ID");
        }
        
        return id;
    }
    
    
    
    @SuppressWarnings("unused")
    private void flushStatmentOnly()
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
}
