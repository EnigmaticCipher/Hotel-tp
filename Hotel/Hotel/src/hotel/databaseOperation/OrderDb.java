package hotel.databaseOperation;

import hotel.classes.Order;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JOptionPane;

/**
 *
 * @author Faysal
 */


/// ######                       DORKAR NAI EI DB ER , ETA PORE BAD DIYE DIBO
public class OrderDb {

    Connection conn = DataBaseConnection.connectTODB();
    PreparedStatement statement = null;
    ResultSet result = null;

    public void insertOrder(Order order) {
        String sql = ""
          + "INSERT INTO orderItem "
          + "(booking_id, item_food, price, quantity, total) "
          + "VALUES (?, ?, ?, ?, ?)";
    
        try {
            statement = conn.prepareStatement(sql);
            statement.setInt(1, order.getBookingId());
            statement.setString(2, order.getFoodItem());
            statement.setDouble(3, order.getPrice());
            statement.setInt(4, order.getQuantity());
            statement.setDouble(5, order.getTotal());
    
            int rows = statement.executeUpdate();
            JOptionPane.showMessageDialog(
              null,
              rows + " order(s) inserted successfully!"
            );
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(
              null,
              "Error inserting order: " + ex.getMessage()
            );
        } finally {
            flushStatmentOnly();
        }
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

    private void flushStatmentOnly() {
        {
            try {
                statement.close();
            } catch (SQLException ex) {
                System.err.print(ex.toString() + " >> CLOSING DB");
            }
        }
    }

}
