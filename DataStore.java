
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DataStore{
        
    private ArrayList<String> maincat;
    private ArrayList<String> subcat;
    private EncryptData crypt;
    private ObjectOutputStream obo;
    private ObjectInputStream obin;
    private FileOutputStream fo;
    private FileInputStream fin;
    private File data,check;
    public DataStore(){
        check = new File("check.file");
        data = new File("SettingsData.data");
        maincat = new ArrayList<>();
        subcat = new ArrayList<>();
        boolean isfirsttime = init();
        if(isfirsttime==true){
            setup();
        }
    }
    private void setup(){
        EncryptData dat = new EncryptData();
        dat.addMain("main categories");
        dat.addSubcat("sub categories");
        saveData(dat);
    }
    private void checkNotFirst(){
        try {
            PrintWriter out = new PrintWriter(check);
            out.write("no");
            out.close();
        } catch (FileNotFoundException ex) {
            ///
        }
    }
    private boolean init(){
        try {
            Scanner in = new Scanner(check);
            String str = in.nextLine().trim();
            if(str.contains("yes")){
                return true;
            }else{
                return false;
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }
    public boolean addMainCategory(String name){
        EncryptData ob;
        ob = (EncryptData)getObject();
        ArrayList md = ob.getMainCat();
        md.add(name);
        ob.setMainCat(md);
        saveData(ob);
        return true;
    }
    public boolean addSubCategory(String main,String sub){
        EncryptData ob;
        ob = getObject();
        ArrayList<String> sb = ob.getSubCat();
        sb.add(main+"  "+sub);
        ob.setSubCat(sb);
        saveData(ob);
        return true;
    }
    public void setPassword(String pass){
        EncryptData ob;
        ob = getObject();
        ob.setPswd(pass);
        saveData(ob);
    }
    public String getPassword(){
        EncryptData ob;
        ob = getObject();
        String pass = ob.getPswd();
        return pass;
    }
    public String getCategoriesData(){
        EncryptData obj;
        obj = getObject();
        ArrayList<String> mc = obj.getMainCat();
        int mcsize = mc.size();
        ArrayList<String> sc = obj.getSubCat();
        int scsize = sc.size();
        StringBuilder listdata = new StringBuilder();
        for(int i=0;i<mcsize;i++){
            listdata.append(mc.get(i)+":"+"\n");
            for(int j=0;j<scsize;j++){
                String temp = sc.get(j);
                String ms[] = temp.split("  ");
                if(ms[0].contains(mc.get(i))){
                    listdata.append("     "+ms[1]+"\n");
                }
            }
        }
        return listdata.toString();
    }
    public EncryptData getObject(){
        try {
            fin = new FileInputStream(data);
            obin = new ObjectInputStream(fin);
            EncryptData obj = (EncryptData)obin.readObject();
            obin.close();
            fin.close();
            return obj;
        } catch (ClassNotFoundException ex) {
            System.out.println("enctyptdata class not found!");
        }catch(IOException ex){
            System.out.println("setting data not found!");
        }
        return null;
    }
    public boolean saveData(EncryptData obj){
        try {
            fo = new FileOutputStream(data);
            obo = new ObjectOutputStream(fo);
            obo.writeObject(obj);
            obo.close();
            fo.close();
            return true;
        } catch (FileNotFoundException ex) {
            ///
        } catch (IOException ex) {
            ///
        }
        return false;
    }
    public class EncryptData implements Serializable{
        ArrayList<String> maincat;
        private ArrayList<String> subcat;
        private String  password;
        public EncryptData(){
            maincat = new ArrayList();
            subcat = new ArrayList();
        }
        public ArrayList getMainCat(){
            return maincat;
        }
        public ArrayList getSubCat(){
            return subcat;
        }
        public void setMainCat(ArrayList data){
            maincat = data;
        }
        public void setSubCat(ArrayList data){
            subcat = data;
        }
        public String getPswd(){
            return password;
        }
        public void setPswd(String pswd){
            password = pswd;
        }
        public void addMain(String str){
           maincat.add(str); 
        }
        public void addSubcat(String str){
            subcat.add(str);
        }
}
  
}
