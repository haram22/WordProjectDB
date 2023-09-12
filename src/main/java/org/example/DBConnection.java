package org.example;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.DriverManager;
// DB에 접속해서 데이터를 가져온다.
public class DBConnection {
    private static Connection conn = null;
    // function getConnection함수를 통해 data를 불러올 수 있다.
    public static Connection getConnection(){
        // conn은 private이므로 외부에서 접근할 수 없기 때문에 함수를 통해 접근.
        // conn이 null일 때만 데이터를 불러와야 하므로 조건문 사용
        if (conn == null) {
            try {
                Class.forName("org.sqlite.JDBC");
                conn = DriverManager.getConnection("jdbc:sqlite:myWordList.db");
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            } catch (SQLException e){
                e.printStackTrace();
            }
        }
        return conn;
    }
    public static void closeConnection(){
        if(conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
