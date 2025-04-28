package hotel.databaseOperation;

import hotel.classes.Food;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JOptionPane;

/**
 *
 * @author Faysal Ahmed
 */
public class FoodDb {
    
    Connection conn = DataBaseConnection.connectTODB();
    PreparedStatement statement = null;
    ResultSet result = null;
    
     public void insertFood(Food food) {

      String sql = ""
    + "INSERT INTO food (name, price) "
    + "VALUES (?, ?)";
  try (
    Connection conn = DataBaseConnection.connectTODB();
    PreparedStatement pstmt = conn.prepareStatement(sql)
  ) {
    pstmt.setString(1, food.getName());
    pstmt.setBigDecimal(2, BigDecimal.valueOf(food.getPrice()));

    pstmt.executeUpdate();
    JOptionPane.showMessageDialog(null, "Food added!");
  } catch (SQLException ex) {
    JOptionPane.showMessageDialog(
      null, "Error inserting food: " + ex.getMessage()
    );
  }
    }

    public ResultSet getFoods() {
        try {
            String query = "select * from food";
            statement = conn.prepareStatement(query);
            result = statement.executeQuery();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, ex.toString() + "\n error coming from returning all food DB Operation");
        }
        

        return result;
    }

    public void updateFood(Food food) {
        String sql = ""
          + "UPDATE food "
          + "SET name = ?, price = ? "
          + "WHERE food_id = ?";
    
        // try-with-resources will auto-close only the PreparedStatement—
        // your existing conn field stays open until you choose to close it elsewhere
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, food.getName());
            // if price is a double:
            pstmt.setDouble(2, food.getPrice());
            // or, if you prefer BigDecimal:
            // pstmt.setBigDecimal(2, BigDecimal.valueOf(food.getPrice()));
            pstmt.setInt(3, food.getFoodId());
    
            int rows = pstmt.executeUpdate();
            JOptionPane.showMessageDialog(
              null,
              rows + " food item(s) updated successfully!"
            );
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(
              null,
              "Error updating food: " + ex.getMessage()
            );
        }
    }
    
    public void deleteFood(int foodId) {
        String sql = "DELETE FROM food WHERE food_id = ?";
    
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, foodId);
            int rows = pstmt.executeUpdate();
            JOptionPane.showMessageDialog(
              null,
              rows + " food item(s) deleted successfully!"
            );
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(
              null,
              "Error deleting food: " + ex.getMessage()
            );
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
