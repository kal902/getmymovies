/*
 manages database
 */


import java.io.InputStream;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.sql.ResultSet;
import java.util.HashMap;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
/*
 @author kaleab
 */

public class DatabaseManager implements DatabaseExceptionListener {

    private Connection conn = null;
    private Statement stmt = null;
    private PreparedStatement pstmt = null;
    final String sql = "jdbc:mysql://localhost/getmymovie";
    static final String user = "root";
    static final String password = "";//no password is used
    public static final int GRIDVIEW = 1;
    public static final int LISTVIEW = 0;
    private JFrame mainui;

    public DatabaseManager(JFrame frame) throws SQLException {
        conn = DriverManager.getConnection(sql, user, password);
        mainui = frame;
    }

    /*
     add a main movie category
     @param name of the category
     */
    public boolean addMainCategory(String name, int viewtype) {
        try {
            String sql = "INSERT INTO category(name,viewtype) VALUE(?,?)";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, name);
            switch (viewtype) {
                case GRIDVIEW:
                    pstmt.setString(2, "Gridview");
                    break;
                case LISTVIEW:
                    pstmt.setString(2, "Listview");
                    break;
                default:
                    return false;
            }
            pstmt.executeUpdate();
            pstmt.close();
            if (viewtype == LISTVIEW) {
                stmt = conn.createStatement();
                // create an other table for storing its subcategories.
                // Table name = subcats_maincatname
                String fulltablename = "subcats_" + name.trim();
                String fullcreatestatement = "CREATE TABLE " + fulltablename + " (id INT PRIMARY KEY AUTO_INCREMENT,name TEXT)";
                stmt.executeUpdate(fullcreatestatement);
                stmt.close();
            } else {
                stmt = conn.createStatement();
                String fulltablename = "subcats_" + name.trim();
                String fullcreatestatement = "CREATE TABLE " + fulltablename + " (id INT PRIMARY KEY AUTO_INCREMENT,name TEXT,image MEDIUMBLOB)";
                stmt.executeUpdate(fullcreatestatement);
                stmt.close();
            }
            return true;
        } catch (SQLException ex) {
            notify("failed to add category", ex.getMessage());
        } catch (NullPointerException ex) {
            notify("failed to add category!", "Database Service is offline!");
            return false;
        }
        return false;
    }
    /*  
     add a list view subcategory
     @param1 the maincategory to add to.
     @param2 name of the subCategory.
     */

    public boolean addSubCategoryWithListview(String itsmaincategoryname, String subcategoryname, String[][] data) {
        String subcatname = itsmaincategoryname + subcategoryname.trim();
        String maincatname = itsmaincategoryname.trim();
        String movielist[][];
        movielist = (String[][]) data;
        try {
            String tablename = "subcats_" + maincatname;
            String statement = "INSERT INTO " + tablename + "(name) VALUE(?)";
            pstmt = conn.prepareStatement(statement);
            pstmt.setString(1, subcategoryname);
            pstmt.executeUpdate();
            pstmt.close();
            /* 
             create new table to store the scanned movies 
             tablename = subcategoryname.
             */
            statement = "CREATE TABLE " + subcatname + "(id INT PRIMARY KEY AUTO_INCREMENT,moviename TEXT, path TEXT)";
            stmt = conn.createStatement();
            stmt.executeUpdate(statement);
            stmt.close();
            String insertcmd;
            insertcmd = "INSERT INTO " + subcatname + "(moviename,path) VALUE(?,?)";
            //store the scanned movie list in the table
            for (String[] data1 : movielist) {
                pstmt = conn.prepareStatement(insertcmd);
                pstmt.setString(1, data1[0]);
                pstmt.setString(2, data1[1]);
                pstmt.executeUpdate();
                if (pstmt != null) {
                    pstmt.close();
                }
            }

            return true;
        } catch (SQLException ex) {

        } finally {
            if (pstmt != null) {
                try {
                    pstmt.close();
                } catch (SQLException ex) {
                    //
                }
            }
        }
        return false;
    }

    public boolean addSubcategoryWithGridview(String maincat, String subcat, String[][] data, InputStream image) {
        String tablename = "subcats_" + maincat.trim();
        String statement = "INSERT INTO " + tablename + "(name,image) VALUE(?,?)";
        try {
            pstmt = conn.prepareStatement(statement);
            pstmt.setString(1, subcat);
            pstmt.setBinaryStream(2, image);
            pstmt.executeUpdate();
            pstmt.close();
            String datatable = maincat + subcat.trim();
            statement = "CREATE TABLE " + datatable + "(id INT PRIMARY KEY AUTO_INCREMENT,moviename TEXT, path TEXT)";
            stmt = conn.createStatement();
            stmt.executeUpdate(statement);
            stmt.close();
            statement = "INSERT INTO " + datatable + "(moviename,path) VALUE(?,?)";
            //store the scanned movie list in the table
            for (String[] data1 : data) {
                pstmt = conn.prepareStatement(statement);
                pstmt.setString(1, data1[0]);
                pstmt.setString(2, data1[1]);
                pstmt.executeUpdate();
                if (pstmt != null) {
                    pstmt.close();
                }
            }
            return true;
        } catch (SQLException ex) {
            notify("add subcategory with grid view failed", ex.getMessage());
        }

        return false;
    }

    public boolean addHomepageData(String movpath, InputStream image, String movname) {
        String tablename = "homepage";
        String statement = "INSERT INTO " + tablename + "(moviepath,image,movname) VALUE(?,?,?)";
        try {
            PreparedStatement pstmt = conn.prepareStatement(statement);
            pstmt.setString(1, movpath);
            pstmt.setBinaryStream(2, image);
            pstmt.setString(3, movname);
            pstmt.executeUpdate();
            if (pstmt != null) {
                pstmt.close();
            }
            return true;
        } catch (SQLException ex) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    JOptionPane.showMessageDialog(mainui, "error adding homepage data!\n" + ex.getMessage());
                }
            });
        }
        return false;
    }

    public boolean removeMainCategory(String name) {
        try {
            stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM category");
            while (rs.next()) {
                String catname = rs.getString("name");
                if (catname.contains(name)) {
                    if (catname.length() == name.length()) {
                        int id = rs.getInt("id");
                        String st = "DELETE FROM getmymovie.category WHERE category.id = " + id;
                        Statement delstatement = conn.createStatement();
                        delstatement.executeUpdate(st);// remove the subcat name from the category table
                        delstatement.close();

                        String subcatstable = "subcats_" + name;
                        Statement stmt2 = conn.createStatement();
                        String stmt2cmd = "SELECT * FROM " + subcatstable;
                        ResultSet rs2 = stmt2.executeQuery(stmt2cmd);// list the datas(subcats) in the subcategories tabel i.e subcats_maincatname
                        while (rs2.next()) {
                            String colmn = rs2.getString("name");
                            int id2 = rs2.getInt("id");
                            String str2 = "DELETE FROM getmymovie." + subcatstable + " WHERE " + subcatstable + ".id = " + id2;
                            Statement stmt3 = conn.createStatement();
                            stmt3.executeUpdate(str2);// remove the list int the subcategories folder
                            stmt3.close();

                            String datatable = name + colmn;
                            System.out.println(datatable);
                            str2 = "DROP TABLE " + datatable;
                            stmt3 = conn.createStatement();
                            stmt3.executeUpdate(str2);// remove the table that holds the list of the scanned movies
                            stmt3.close();

                        }
                        stmt2.close();
                        stmt2 = conn.createStatement();
                        stmt2.executeUpdate("DROP TABLE " + subcatstable);
                        stmt2.close();
                        return true;
                    }
                }
            }
        } catch (SQLException ex) {
            System.out.println(ex);
        }
        return false;
    }

    public boolean removeSubcat(String main, String subcat) {
        String tblname = "subcats_" + main;
        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM " + tblname);
            while (rs.next()) {//look for the subcat name in the subcats_maincatname table
                String name = rs.getString("name");
                int id = rs.getInt("id");
                if (name.contains(subcat)) {
                    Statement stmt2 = conn.createStatement();
                    String stmtdel = "DELETE FROM getmymovie." + tblname + " WHERE " + tblname + ".id = " + id;
                    stmt2.executeUpdate(stmtdel);//remove the subcat from the subcats_maincatname table
                    if (stmt2 != null) {
                        stmt2.close();
                    }
                    String tbl = main + name;
                    stmtdel = "DROP TABLE " + tbl;
                    stmt2 = conn.createStatement();
                    stmt2.executeUpdate(stmtdel);//remove the table that holds the scanned movies
                    if (stmt2 != null) {
                        stmt2.close();
                    }
                }
            }
            if (rs != null) {
                rs.close();
            }
            if (stmt != null) {
                stmt.close();
            }
            return true;
        } catch (SQLException ex) {
            // handle error
        }
        return false;
    }
    public boolean removeHomepageData(String name){
        try {
            stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM homepage");
            while(rs.next()){
                String movname = rs.getString("movname");
                if(movname.contains(name)){
                    if(movname.length()==name.length()){
                        int id = rs.getInt("id");
                        String stmtdel = "DELETE FROM getmymovie.homepage WHERE homepage.id = " + id;
                        stmt.executeUpdate(stmtdel);
                        stmt.close();
                        if(rs.isClosed()==false){
                            rs.close();
                        }
                        stmt.close();
                        conn.close();
                        return true;
                    }
                }
            }
        } catch (SQLException ex) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    JOptionPane.showMessageDialog(mainui, "error, could not remove homepage data!\n" + ex.getMessage());
                }
            });
        }
        return false;
    }

    public void close() {
        try {
            conn.close();
        } catch (SQLException ex) {
        }
    }

    public boolean isConnected() {
        if (conn != null) {
            return true;
        }
        return false;
    }

    public static void prepareDatabase() throws SQLException {
        String sql = "jdbc:mysql://localhost";
        Connection conn = DriverManager.getConnection(sql, user, password);
        Statement stmt = conn.createStatement();
        stmt.executeUpdate("CREATE DATABASE getmymovie");
        stmt.close();
        conn.close();

        sql = "jdbc:mysql://localhost/getmymovie";
        conn = DriverManager.getConnection(sql, user, password);
        stmt = conn.createStatement();
        stmt.executeUpdate("CREATE TABLE category (id INT PRIMARY KEY AUTO_INCREMENT,name TEXT, viewtype TEXT)");
        stmt.close();
        stmt = conn.createStatement();
        stmt.execute("CREATE TABLE homepage(id INT PRIMARY KEY AUTO_INCREMENT, moviepath TEXT, image MEDIUMBLOB,movname TEXT)");
        stmt.close();
        conn.close();
    }
}
