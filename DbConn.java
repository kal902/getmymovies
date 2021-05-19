/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.sql.Statement;
import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
/**
 *
 * @author mili
 */
public class DbConn {
    
    
    public static void main(String arg[]) throws FileNotFoundException, IOException{
        final String sql = "jdbc:mysql://localhost/getmymovie";
        final String user = "root";
        final String pass = "";
        Connection conn = null;
        Statement st = null;
        PreparedStatement stmt = null;
        try {
            conn = DriverManager.getConnection(sql,user,pass);
            System.out.println("connected");
            //st = conn.createStatement();
            //st.executeUpdate("CREATE DATABASE test");
            //st.executeUpdate("CREATE TABLE category (id INT PRIMARY KEY AUTO_INCREMENT,name TEXT, viewtype TEXT)");
            //st.executeUpdate("DROP TABLE IF EXISTS callStmtTbl");
            //st = conn.createStatement();
            //st.executeUpdate("INSERT INTO category ('id',name','viewtype') values (1,'eng','holl");
            //st.executeUpdate("INSERT INTO category VALUES (1,'eng','hol')");
            String cmd = "CREATE TABLE homepage(id INT PRIMARY KEY AUTO_INCREMENT, moviepath TEXT, image MEDIUMBLOB, movname TEXT)";
            
            //String cmd = "INSERT INTO homepage(name,img) VALUE(?,?)";
            File image = new File("C:\\Users\\mili\\Desktop\\posture\\446701041_489728.jpg");
            FileInputStream in = new FileInputStream(image);
            st = conn.createStatement();
            st.executeUpdate(cmd);
            //stmt = conn.prepareStatement(cmd);
            //stmt.setString(1, "amh");
            //stmt.setBinaryStream(2, (InputStream)in,(int)image.length());
            //stmt.executeUpdate();
            //HashMap<String,String> d = new HashMap<>();
            //d.put("rootdrive","D:");
            //FileOutputStream fout = new FileOutputStream(new File("settings.data"));
            //ObjectOutputStream oout = new ObjectOutputStream(fout);
            //oout.writeObject(d);
            //oout.close();
            //fout.close();
        } catch (SQLException ex) {
            Logger.getLogger(DbConn.class.getName()).log(Level.SEVERE, null, ex);
        }finally{
            if(st==null){
                try {
                    stmt.close();
                } catch (SQLException ex) {
                }
            }
            if(conn!=null){
                try{
                    conn.close();
                }catch(SQLException ex){}
            }
        }
    }
}
