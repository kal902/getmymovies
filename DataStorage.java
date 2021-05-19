
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.JOptionPane;
/*
@author kaleab
*/
    public class DataStorage{

        private String maindata, subdata, viewdata,pathdata, homepagedata, indexdata;
        private JFrame frame;
        public DataStorage(JFrame context) {
            maindata = "data\\maincatdata.data";//serialized ArrayList for storing main categories
            subdata = "data\\subcatdata.data";//serialized ArrayList
            viewdata = "data\\viewdata.data";//serialized HashMap for storing selected viewtypes
            pathdata = "data\\pathdata.data";
            homepagedata = "data\\homepagedata.data";
            indexdata = "data\\lastindex.data";//indexs of homepagedata
            frame = context;
        }
        /*
        HOMEPAGE DATA
        */
        public boolean addHomepageData(String movname,String imgpath, String moviepath){
            boolean ok = false;
            try {
                DatabaseManager dbm = new DatabaseManager(frame);
                ok = dbm.addHomepageData(moviepath,getInputStream(imgpath),movname);
            } catch (SQLException ex) {
                SwingUtilities.invokeLater(new Runnable(){
                        public void run(){
                            JOptionPane.showMessageDialog(frame,"error! Database Server is offline\n"+ex.getMessage());
                        }
                    });
            }
            if(ok!=false){
                HashMap<Integer, String[]> homepagedata = gethomepagedata();
                ArrayList<Integer> indexdata = getindexdata();
                int freeindex = indexdata.size();
                String datatoadd[] = {movname,imgpath, moviepath};
                homepagedata.put(freeindex, datatoadd);
                indexdata.add(freeindex);
                saveindexdata(indexdata);
                savehomepagedata(homepagedata);
                return true;
            }else{
                return false;
            }
        }
        public boolean removeHomepageData(String name){
            boolean isremovedfromdb = false;
            try {
                //remove from database
                DatabaseManager dbm = new DatabaseManager(frame);
                isremovedfromdb = dbm.removeHomepageData(name);
                if(isremovedfromdb){
                    //remove from the hashmap
                    HashMap<Integer, String[]> homepagedata = gethomepagedata();
                    for(int i=0;i<homepagedata.size();i++){
                        String data[] = homepagedata.get(i);
                        if(data[0].contains(name)){
                            if(data[0].length()==name.length()){
                                homepagedata.remove(i);
                                //remove from index data
                                ArrayList<Integer> indexdata = getindexdata();
                                indexdata.remove(i);
                                saveindexdata(indexdata);
                                break;
                            }
                        }
                    }
                    savehomepagedata(homepagedata);
                    return true;
                }
            } catch (SQLException ex) {
                Logger.getLogger(DataStorage.class.getName()).log(Level.SEVERE, null, ex);
            }
            return false;
        }
        private InputStream getInputStream(String imagepath){
            File file = new File(imagepath);
            try {
                FileInputStream stream = new FileInputStream(file);
                InputStream inputstream = (InputStream)stream;
                return inputstream;
            } catch (FileNotFoundException ex) {
            }
            return null;
        }
        private HashMap gethomepagedata(){
            FileInputStream fin;
            ObjectInputStream oin;
            HashMap<Integer,String[]> data;
            try {
                fin = new FileInputStream(homepagedata);
                oin = new ObjectInputStream(fin);
                data = (HashMap<Integer,String[]>) oin.readObject();
                oin.close();
                fin.close();
                return data;
            } catch (FileNotFoundException ex) {
                JOptionPane.showMessageDialog(frame, "error! can not find the homepage datafile");
            } catch (IOException ex) {
                Logger.getLogger(DataStorage.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(DataStorage.class.getName()).log(Level.SEVERE, null, ex);
            }
            return null;
        }
        public String[] gethomepagesname(){
            HashMap<Integer, String[]> data = gethomepagedata();
            String nameslist[] = new String[data.size()];
            for(int i=0;i<nameslist.length;i++){
                String lists[] = data.get(i);
                nameslist[i] = lists[0];
            }
            return nameslist;
        }
        private void savehomepagedata(HashMap data){
            FileOutputStream fout;
            ObjectOutputStream oout;
            try {
                fout = new FileOutputStream(homepagedata);
                oout = new ObjectOutputStream(fout);
                oout.writeObject(data);
                oout.close();
                fout.close();
            } catch (FileNotFoundException ex) {
                JOptionPane.showMessageDialog(frame, "error! can not find the homepage data file");
            } catch (IOException ex) {
                Logger.getLogger(DataStorage.class.getName()).log(Level.SEVERE, null, ex);
            }
            
        }
        private ArrayList getindexdata(){
            FileInputStream fin;
            ObjectInputStream oin;
            try {
                fin = new FileInputStream(indexdata);
                oin = new ObjectInputStream(fin);
                ArrayList<Integer> data = (ArrayList<Integer>)oin.readObject();
                oin.close();
                fin.close();
                return data;
            } catch (FileNotFoundException ex) {
                JOptionPane.showMessageDialog(frame, "error! can not find the indexdata");
            } catch (IOException ex) {
                Logger.getLogger(DataStorage.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(DataStorage.class.getName()).log(Level.SEVERE, null, ex);
            }
            return null;
        }
        private void saveindexdata(ArrayList data){
            FileOutputStream fout;
            ObjectOutputStream oout;
            try {
                fout = new FileOutputStream(indexdata);
                oout = new ObjectOutputStream(fout);
                oout.writeObject(data);
                oout.close();
                fout.close();
            } catch (FileNotFoundException ex) {
                Logger.getLogger(DataStorage.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(DataStorage.class.getName()).log(Level.SEVERE, null, ex);
            }
            
        }
        /*
        MAIN CATEGORY DATA
        */
        public boolean addMainCategory(String main) {
            DatabaseManager dbm;
            boolean ok = false;
            try {
                dbm = new DatabaseManager(frame);
                String viewtype = getView(main);
                if(viewtype.contains("Gridview")){
                    ok = dbm.addMainCategory(main, DatabaseManager.GRIDVIEW);
                }else{
                    ok = dbm.addMainCategory(main, DatabaseManager.LISTVIEW);
                }
                if(dbm.isConnected()){
                    dbm.close();
                }
            } catch (SQLException ex) {
                    SwingUtilities.invokeLater(new Runnable(){
                        public void run(){
                            JOptionPane.showMessageDialog(frame,"error! Database Server is offline\n"+ex.getMessage());
                        }
                    });
              }
            // save to local storage
            if(ok==true){
                ArrayList<String> olddata = getMainCat();
                olddata.add(main);
                saveMainData(olddata);
                return true;
            }else{
                System.out.println("not added");
                return false;
            }
        }
        public String[] getMainCategories() {
            ArrayList<String> dat = getMainCat();
            int size = dat.size();
            String list[] = new String[size];
            for (int i = 0; i < size; i++) {
                list[i] = dat.get(i);
            }
            return list;
        }

        private ArrayList getMainCat() {
            FileInputStream fin = null;
            ObjectInputStream obin = null;
            try {
                fin = new FileInputStream(new File(maindata));
                obin = new ObjectInputStream(fin);
                ArrayList<String> mdata = (ArrayList) obin.readObject();
                return mdata;
            } catch (FileNotFoundException ex) {
                ex.printStackTrace();
            } catch (IOException ex) {
                ex.printStackTrace();
            } catch (ClassNotFoundException ex) {
                ex.printStackTrace();
            } finally {
                try {
                    obin.close();
                    fin.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
            return null;
        }
        private boolean saveMainData(ArrayList list) {
            FileOutputStream fout = null;
            ObjectOutputStream obout = null;
            try {
                fout = new FileOutputStream(new File(maindata));
                obout = new ObjectOutputStream(fout);
                obout.writeObject(list);
                return true;
            } catch (FileNotFoundException ex) {
                ex.printStackTrace();
            } catch (IOException ex) {
                ex.printStackTrace();
            } finally {
                try {
                    obout.close();
                    fout.close();
                } catch (IOException ex) {
                    ex.printStackTrace();;
                }
            }
            return false;
        }
        
        public int removeMainCategoy(String name){
            try{
                DatabaseManager dbm = new DatabaseManager(frame);
                dbm.removeMainCategory(name);
            }catch(Exception ex){ex.printStackTrace();}
            ArrayList<String> maincatdata = getMainCat();
            maincatdata.remove(name);
            ArrayList<String> subcatdata = getSubCat();
            //remove the names containing the MainCategory name from the subcategory data
            for(int i=0;i<subcatdata.size();i++){
                String temp = subcatdata.get(i);
                String split[] = temp.split(" ");
                if(split[0].contains(name)){
                    subcatdata.remove(temp);
                }
            }
            //viewdata
            HashMap<String,String> viewdata = this.getViews();
            viewdata.remove(name);
            //save the datas
            saveView(viewdata);
            saveSubData(subcatdata);
            boolean issaved = saveMainData(maincatdata);
            if(issaved==false){
                return 1;
            }
            return 0;
        }
        /*
        SUBCATEGORY DATA
        */
        //posture path could be null if list view
        public boolean addsubCategory(String main, String sub,String path,String posturepath) {
            DatabaseManager dbm;
            boolean resultdb = false;
            try {
                dbm = new DatabaseManager(frame);
                Scan scan = new Scan(path);
                scan.startScan();
                String[][] listofmovies;
                listofmovies = (String[][])scan.getScanResults();
                String viewtype = getView(main);
                if(viewtype.contains("Gridview")){
                    resultdb = dbm.addSubcategoryWithGridview(main, sub, listofmovies, getInputStream(posturepath));
                    if(dbm.isConnected()){
                        dbm.close();
                    }
                }else{
                    resultdb = dbm.addSubCategoryWithListview(main, sub, listofmovies);
                    if(dbm.isConnected()){
                        dbm.close();
                    }
                }
            } catch (SQLException ex) {
                Logger.getLogger(DataStorage.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            //local storage
            ArrayList<String> olddata = getSubCat();
            olddata.add(main + " " + sub);
            boolean resultld;
            resultld = saveSubData(olddata);
            //save path data
            //boolean pathsaved = addPath(sub,path);
            return resultdb && resultld;
        }
        public ArrayList getSubCat() {
            FileInputStream fin = null;
            ObjectInputStream obin = null;
            try {
                fin = new FileInputStream(new File(subdata));
                obin = new ObjectInputStream(fin);
                ArrayList<String> data = (ArrayList<String>) obin.readObject();
                obin.close();
                fin.close();
                return data;
            } catch (FileNotFoundException ex) {
                ex.printStackTrace();
            } catch (IOException ex) {
                ex.printStackTrace();
            } catch (ClassNotFoundException ex) {
                ex.printStackTrace();
            }
            return null;
        }
        
        private boolean saveSubData(ArrayList list) {
            FileOutputStream fout = null;
            ObjectOutputStream obout = null;
            try {
                fout = new FileOutputStream(new File(subdata));
                obout = new ObjectOutputStream(fout);
                obout.writeObject(list);
                return true;
            } catch (FileNotFoundException ex) {
                ex.printStackTrace();
            } catch (IOException ex) {
                ex.printStackTrace();
            } finally {
                try {
                    obout.close();
                    fout.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
            return false;
        }
        
        public String[] getSubCatFor(String main) {
            ArrayList<String> subcats = getSubCat();
            ArrayList<String> choosedcats = new ArrayList<String>();
            for (int i = 0; i < subcats.size(); i++) {
                String temp[] = subcats.get(i).split(" ");
                if (temp[0].contains(main)) {
                    choosedcats.add(temp[1]);
                }
            }
            String list[] = new String[choosedcats.size()];
            for (int i = 0; i < list.length; i++) {
                list[i] = choosedcats.get(i);
            }
            return list;
        }
        
        public boolean removeSubCategory(String main,String name){
            DatabaseManager dbm;
            try {
                dbm = new DatabaseManager(frame);
                try {
                    dbm.removeSubcat(main, name);
                } catch (Exception ex) {
                    return false;
                }
            } catch (SQLException ex) {
                Logger.getLogger(DataStorage.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            ArrayList<String> subdata = getSubCat();
            for(int i=0;i<subdata.size();i++){
                String temp = subdata.get(i);
                String split[] = temp.split(" ");
                if(split[1].contains(name)){
                    subdata.remove(temp);
                }
            }
            boolean issaved = saveSubData(subdata);
            return issaved==true;
        }
        /*
        PATHDATA
        */
        /*
        added path will be used to scan the directory for new updated movies on startup or manually.
        so that the admin dont have to add the subdirectories again whenever a movie is added.
        @Param1 subcategoryname(key)
        @Param2 the path
        */
        private boolean addPath(String subname,String path){
            HashMap<String,String> pathdata;
            pathdata = (HashMap<String,String>)getPathdata();
            pathdata.put(subname,path);
            savePathdata(pathdata);
            return true;
        }
        private HashMap getPathdata(){
            FileInputStream fin;
            ObjectInputStream oin;
            try {
                fin = new FileInputStream(pathdata);
                oin = new ObjectInputStream(fin);
                HashMap<String,String> data = (HashMap<String,String>)oin.readObject();
                return data;
            } catch (FileNotFoundException ex) {
                ex.printStackTrace();
            } catch (IOException ex) {
                ex.printStackTrace();
            } catch (ClassNotFoundException ex) {
                ex.printStackTrace();
            }
            return null;
        }
        private void savePathdata(HashMap data){
            FileOutputStream fout;
            ObjectOutputStream oout;
            try {
                fout = new FileOutputStream(pathdata);
                oout = new ObjectOutputStream(fout);
                oout.writeObject(data);
                oout.close();
                fout.close();
            } catch (FileNotFoundException ex) {
                ex.printStackTrace();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            
        }
        public String getPath(String key){
            HashMap<String,String> pathdata = getPathdata();
            String path = pathdata.get(key);
            return path;
        }
        /*
        VIEW DATA
        */
        // Gridview or Listview
        public boolean setView(String main, String cmd) {
            HashMap<String, String> olddata = getViews();
            if (olddata.containsKey(main)) {
                olddata.remove(main);
            }
            olddata.put(main, cmd);
            saveView(olddata);
            return false;
        }

        public String getView(String name) {
            HashMap<String, String> data = getViews();
            String viewcmd = data.get(name);
            return viewcmd;
        }

        private HashMap getViews() {
            FileInputStream fin = null;
            ObjectInputStream obin = null;
            try {
                fin = new FileInputStream(new File(viewdata));
                obin = new ObjectInputStream(fin);
                HashMap<String, String> data = (HashMap<String, String>) obin.readObject();
                obin.close();
                fin.close();
                return data;
            } catch (FileNotFoundException ex) {
                //
            } catch (IOException ex) {
                //
            } catch (ClassNotFoundException ex) {
                //
            }
            return null;
        }

        private void saveView(HashMap viewdat) {
            FileOutputStream fout = null;
            ObjectOutputStream obout = null;
            try {
                fout = new FileOutputStream(new File(viewdata));
                obout = new ObjectOutputStream(fout);
                obout.writeObject(viewdat);
                obout.close();
                fout.close();
            } catch (FileNotFoundException ex) {
                ///
            } catch (IOException ex) {
                //
            }

        }

        public String getCategoriesData() {
            ArrayList<String> maincat = getMainCat();
            ArrayList<String> subcat = getSubCat();
            StringBuilder fulldata = new StringBuilder();
            for (int i = 0; i < maincat.size(); i++) {
                fulldata.append(maincat.get(i) + ":\n");
                for (int j = 0; j < subcat.size(); j++) {
                    String temp = subcat.get(j);
                    String split[] = temp.split(" ");
                    if (split[0].contains(maincat.get(i).trim())) {
                        fulldata.append("     " + split[1] + "\n");
                    }
                }
            }
            return fulldata.toString();
        }
        //root dir where the movies are. web page datas will be copied to the selected drives root
        public static void setrootdrive(String drive){
            HashMap<String, String> setting = getsetting();
            setting.remove("rootdrive");
            setting.put("rootdrive", drive.toUpperCase());
            savesetting(setting);
        }
        public static String getrootdrive(){
            try {
                HashMap<String, String> setting = getsetting();
                String rootdir = setting.get("rootdrive");
                return rootdir;
            }catch(Exception e){}
            return null;
        }
       public static HashMap getsetting(){
           try {
                FileInputStream fin = new FileInputStream(new File("data\\settings.data"));
                ObjectInputStream oin = new ObjectInputStream(fin);
                HashMap<String, String> setting = (HashMap<String, String>)oin.readObject();
                oin.close();
                fin.close();
                return setting;
            } catch (FileNotFoundException ex) {
                Logger.getLogger(DataStorage.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(DataStorage.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(DataStorage.class.getName()).log(Level.SEVERE, null, ex);
            }
           return null;
       }
       private static void savesetting(HashMap<String, String> data){
           try {
                FileOutputStream fout = new FileOutputStream(new File("settings.data"));
                ObjectOutputStream oout = new ObjectOutputStream(fout);
                oout.writeObject(data);
                oout.close();
                fout.close();
            } catch (FileNotFoundException ex) {
                Logger.getLogger(DataStorage.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(DataStorage.class.getName()).log(Level.SEVERE, null, ex);
            }
       }
        
        
        
        /*
            scans a given path for movies
        */
        public class Scan {

        private boolean ISPATHSET = false;
        private String map[][];
        private String path;
        private String rootdir;
        public Scan(String dirpath) {
            rootdir = DataStorage.getrootdrive();
            path = dirpath;
            ISPATHSET = true;
        }

        public void startScan() {
            if (ISPATHSET == true) {
                start();
            }
        }
        private boolean start() {
                File f = new File(path);
                File files[] = f.listFiles();
                int size = files.length;
                map = new String[size][2];
                //iterate in the files array and set the files name and absolute path to the hashmap
                for (int i = 0; i < size; i++) {
                    if (files[i].isFile()) {
                        String filename = files[i].getName();
                        String filepath = files[i].getAbsolutePath();
                        String downloadablepath = filepath.replace(rootdir, "");
                        map[i][0] = filename;//print(filename);
                        map[i][1] = downloadablepath;//print(filepath);
                    }
                }
                print(map.length);
            return true;
        }

        public String[][] getScanResults() {
            return map;
        }
        private void print(int test){
            System.out.println(test);
        }
    }

  }
