package hotel.databaseOperation;

import hotel.classes.Booking;
import hotel.classes.Order;
import hotel.classes.Room;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Arrays;
import javax.swing.JOptionPane;

public class BookingDb {
   

    Connection conn;
    PreparedStatement statement = null;
    ResultSet result = null;

    public BookingDb() {
        conn = DataBaseConnection.connectTODB();
    }
//************************i modifay this (chak) ************* */
     public void insertBooking(Booking booking) {

        String sql = 
    "INSERT INTO booking "
  + "(customer_id, booking_room, guests, check_in, check_out, booking_type, has_checked_out) "
  + "VALUES (?, ?, ?, ?, ?, ?, ?)";
        // try-with-resources will auto-close the Connection & PreparedStatement
        try (
  Connection conn = DataBaseConnection.connectTODB();
  PreparedStatement pstmt = conn.prepareStatement(sql)
) {
  for (Room room : booking.getRooms()) {
    pstmt.setInt(1, booking.getCustomer().getCustomerId());
    pstmt.setString(2, room.getRoomNo());
    pstmt.setInt(3, booking.getPerson());

    // <-- use Timestamp(long):
    pstmt.setTimestamp(4, new Timestamp(booking.getCheckInDateTime()));
    pstmt.setTimestamp(5, new Timestamp(booking.getCheckOutDateTime()));

    pstmt.setString(6, booking.getBookingType());
    pstmt.setBoolean(7, false);  // has_checked_out

    pstmt.addBatch();
  }

  int[] results = pstmt.executeBatch();
  int inserted = Arrays.stream(results).sum();
  JOptionPane.showMessageDialog(null, inserted + " booking(s) inserted!");
} catch (SQLException ex) {
  JOptionPane.showMessageDialog(null, "Insert failed: " + ex.getMessage());
}
    }
    //************************************************************************************************ */

    public ResultSet getBookingInformation() {
        try {
            String query = "select * from booking";
            statement = conn.prepareStatement(query);
            result = statement.executeQuery();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, ex.toString() + "\n error coming from returning all booking DB Operation");
        }

        return result;
    }

    public ResultSet getABooking(int bookingId) {
        try {
            String query = "SELECT * FROM booking WHERE booking_id = ?";
            PreparedStatement statement = conn.prepareStatement(query);
            statement.setInt(1, bookingId); // Set the bookingId safely
            return statement.executeQuery();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, ex.toString() + 
                "\nError coming from returning a booking DB operation");
            return null;
        }
    }

    public ResultSet bookingsReadyForOrder(String roomName) {
        ResultSet result = null;
        try {
            String query = 
                "SELECT booking_id, booking_room_name " +
                "FROM booking " +
                "JOIN userInfo ON booking.customer_id = userInfo.user_id " +
                "WHERE booking_room LIKE ? AND has_checked_out = 0 " +
                "ORDER BY booking_id DESC";
            
            PreparedStatement statement = conn.prepareStatement(query);
    
            statement.setString(1, "%" + roomName + "%");
            
            result = statement.executeQuery();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(
                null,
                ex.toString() + "\nError in bookingsReadyForOrder method, BookingDb"
            );
        }
        return result;
    }
    

    public void updateCheckOut(int bookingId, long checkOutTime) {
        try {
            String updateSql =
                "UPDATE booking " +
                "SET has_checked_out = 1, check_out = ? " +
                "WHERE booking_id = ?";
            
            PreparedStatement statement = conn.prepareStatement(updateSql);
            
            statement.setLong(1, checkOutTime);
            statement.setInt(2, bookingId);
            
            int rows = statement.executeUpdate();
            if (rows > 0) {
                JOptionPane.showMessageDialog(null, "Successfully updated check-out.");
            } else {
                JOptionPane.showMessageDialog(null, "No booking found with ID: " + bookingId);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(
                null,
                ex.toString() + "\nupdateCheckOut of BookingDb failed"
            );
        } finally {
            flushStatementOnly();
        }
    }
    
    public int getRoomPrice(int bookingId) {
        int price = -1;
        
        try (PreparedStatement statement = conn.prepareStatement(
                "SELECT r.price FROM booking b " +
                "JOIN room r ON b.booking_room = r.room_no " +
                "JOIN roomType rt ON r.type = rt.room_class " +
                "WHERE b.booking_id = ?")) {
            
            
            statement.setInt(1, bookingId);
            
            try (ResultSet result = statement.executeQuery()) {
                if (result.next()) {
                    price = result.getInt("price");
                    System.out.println(price);
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, 
                "Error retrieving room price: " + ex.getMessage() + 
                "\nMethod: getRoomPrice, bookingDB");
        }
        return price;
    }
    
    public void insertOrder(Order order) {
        try {
            String sql = ""
                + "INSERT INTO orderItem "
                + "(booking_id, item_food, price, quantity, total) "
                + "VALUES (?, ?, ?, ?, ?)";
            
            PreparedStatement stmt = conn.prepareStatement(sql);
       
            stmt.setInt(1, order.getBookingId());
            stmt.setString(2, order.getFoodItem());
            stmt.setDouble(3, order.getPrice());
            stmt.setInt(4, order.getQuantity());
            stmt.setDouble(5, order.getTotal());
            
            int rows = stmt.executeUpdate();
            if (rows > 0) {
                JOptionPane.showMessageDialog(null, "Successfully inserted a new order.");
            } else {
                JOptionPane.showMessageDialog(null, "Insert failed, no rows affected.");
            }
            
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, ex.toString() + "\nOrder insert failed");
        } finally {
            flushStatementOnly();
        }
    }
    
    public ResultSet getAllPaymentInfo(int bookingId) {
        ResultSet result = null;
        try {
            String sql = "SELECT * FROM orderItem WHERE booking_id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, bookingId);      
            System.out.println("Executing: " + sql.replace("?", String.valueOf(bookingId)));
            result = stmt.executeQuery();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(
                null,
                ex.toString() 
                + "\nError in getAllPaymentInfo, BookingDb"
            );
        }
        return result;
    }
    

    public void flushAll() {
        {
            try {
                statement.close();
                result.close();
            } catch (SQLException ex) {
                System.err.print(ex.toString() + " >> CLOSING DB");
            }
        }
    }

    public void flushStatementOnly() {
        {
            try {
                statement.close();
                
            } catch (SQLException ex) {
                System.err.print(ex.toString() + " >> CLOSING DB");
            }
        }
    }

}
