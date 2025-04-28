package hotel.databaseOperation;

import hotel.classes.Room;
import hotel.classes.RoomFare;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JOptionPane;

/**
 *
 * @author Faysal Ahmed
 */
public class RoomDb {
    Connection conn = DataBaseConnection.connectTODB();
    PreparedStatement statement = null;
    ResultSet result = null;

    public void insertRoom(Room room) {
        String sql = ""
          + "INSERT INTO room "
          + "(room_no, bed_number, tv, wifi, gizer, phone, room_class) "
          + "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try {
            statement = conn.prepareStatement(sql);
            statement.setString(1, room.getRoomNo());
            statement.setInt(2, room.getBedNumber());
            statement.setBoolean(3, room.isHasTV());
            statement.setBoolean(4, room.isHasWIFI());
            statement.setBoolean(5, room.isHasGizer());
            statement.setBoolean(6, room.isHasPhone());
            statement.setString(7, room.getRoomClass().getRoomType());
    
            int rows = statement.executeUpdate();
            JOptionPane.showMessageDialog(null, rows + " room(s) inserted successfully!");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(
              null, "Error inserting room: " + ex.getMessage()
            );
        } finally {
            flushStatmentOnly();
        }
    }
    
    

  // 1) getRooms
public ResultSet getRooms() {
    try {
        String sql = "SELECT * FROM room";
        statement = conn.prepareStatement(sql);
        result = statement.executeQuery();
    } catch (SQLException ex) {
        JOptionPane.showMessageDialog(
          null,
          ex.toString() + "\nError fetching all rooms"
        );
    }
    return result;
}

// 2) getNoOfRooms
public int getNoOfRooms() {
    int rooms = -1;
    try {
        String sql = "SELECT COUNT(room_no) AS noRoom FROM room";
        statement = conn.prepareStatement(sql);
        result = statement.executeQuery();
        if (result.next()) {
            rooms = result.getInt("noRoom");
        }
    } catch (SQLException ex) {
        JOptionPane.showMessageDialog(
          null,
          ex.toString() + "\nError counting rooms"
        );
    }
    return rooms;
}

// 3) getAllRoomNames
public ResultSet getAllRoomNames() {
    try {
        String sql = "SELECT room_no FROM room";
        statement = conn.prepareStatement(sql);
        result = statement.executeQuery();
    } catch (SQLException ex) {
        JOptionPane.showMessageDialog(
          null,
          ex.toString() + "\nError fetching room numbers"
        );
    }
    return result;
}

// 4) deleteRoom
public void deleteRoom(int roomId) {
    try {
        String sql = "DELETE FROM room WHERE room_id = ?";
        statement = conn.prepareStatement(sql);
        statement.setInt(1, roomId);
        statement.executeUpdate();
        JOptionPane.showMessageDialog(null, "Deleted room");
    } catch (SQLException ex) {
        JOptionPane.showMessageDialog(
          null,
          ex.toString() + "\nDelete room failed"
        );
    } finally {
        flushStatmentOnly();
    }
}

// 5) updateRoom
public void updateRoom(Room room) {
    try {
        String sql = ""
          + "UPDATE room SET "
          + "room_no    = ?, "
          + "bed_number = ?, "
          + "tv         = ?, "
          + "wifi       = ?, "
          + "gizer      = ?, "
          + "phone      = ?, "
          + "room_class = ? "
          + "WHERE room_id = ?";
        statement = conn.prepareStatement(sql);
        statement.setString(1, room.getRoomNo());
        statement.setInt(2, room.getBedNumber());
        statement.setBoolean(3, room.isHasTV());
        statement.setBoolean(4, room.isHasWIFI());
        statement.setBoolean(5, room.isHasGizer());
        statement.setBoolean(6, room.isHasPhone());
        statement.setString(7, room.getRoomClass().getRoomType());
        statement.setInt(8, room.getRoomId());   // assumes Room has getRoomId()
        statement.executeUpdate();
        JOptionPane.showMessageDialog(null, "Successfully updated room");
    } catch (SQLException ex) {
        JOptionPane.showMessageDialog(
          null,
          ex.toString() + "\nUpdate room failed"
        );
    } finally {
        flushStatmentOnly();
    }
}

// 6) boolToString (unchanged)
public String boolToString(boolean value) {
    return value ? "true" : "false";
}

// 7) insertRoomType
public void insertRoomType(RoomFare roomType) {
    try {
        String sql = "INSERT INTO roomType (type, price) VALUES (?, ?)";
        statement = conn.prepareStatement(sql);
        statement.setString(1, roomType.getRoomType());
        statement.setDouble(2, roomType.getPricePerDay());
        statement.executeUpdate();
        JOptionPane.showMessageDialog(null, "Successfully inserted a new Room Type");
    } catch (SQLException ex) {
        JOptionPane.showMessageDialog(
          null,
          ex.toString() + "\nInsert room type failed"
        );
    } finally {
        flushStatmentOnly();
    }
}

// 8) getRoomType
public ResultSet getRoomType() {
    try {
        String sql = "SELECT * FROM roomType";
        statement = conn.prepareStatement(sql);
        result = statement.executeQuery();
    } catch (SQLException ex) {
        JOptionPane.showMessageDialog(
          null,
          ex.toString() + "\nError fetching room types"
        );
    }
    return result;
}

// 9) updateRoomType
public void updateRoomType(RoomFare roomType) {
    try {
        String sql = "UPDATE roomType SET price = ? WHERE type = ?";
        statement = conn.prepareStatement(sql);
        statement.setDouble(1, roomType.getPricePerDay());
        statement.setString(2, roomType.getRoomType());
        statement.executeUpdate();
        JOptionPane.showMessageDialog(null, "Successfully updated Room Type");
    } catch (SQLException ex) {
        JOptionPane.showMessageDialog(
          null,
          ex.toString() + "\nUpdate room type failed"
        );
    } finally {
        flushStatmentOnly();
    }
}

// 10) flushAll (unchanged)
public void flushAll() {
    try {
        if (statement != null && !statement.isClosed()) statement.close();
        if (result    != null && !result.isClosed())    result.close();
    } catch (SQLException ex) {
        System.err.print(ex.toString() + " >> CLOSING DB");
    }
}

// 11) flushStatmentOnly (unchanged)
private void flushStatmentOnly() {
    try {
        if (statement != null && !statement.isClosed()) statement.close();
    } catch (SQLException ex) {
        System.err.print(ex.toString() + " >> CLOSING DB");
    }
}

}
