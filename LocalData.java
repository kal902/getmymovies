
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
import java.util.HashMap;
import java.util.Scanner;

public class LocalData{
        private File datamaincat,datasubcat,datausr;
        private PrintWriter print;
        public LocalData(){
            datamaincat = new File("datmain.data");
            datasubcat = new File("subcat.data");
            datausr = new File("usrdata.data");
        }
        public void addmaincategory(String name){
            try {
                String old = read(datamaincat);
                print = new PrintWriter(datamaincat);
                print.println(old+"\n"+name);
            } catch (FileNotFoundException ex) {
                System.out.println("maincategory data not found");
            }finally{
                print.close();
            }
        }
        public boolean addsubCategory(String main,String sub){
            try {
                String old = read(datasubcat);
                print = new PrintWriter(datasubcat);
                print.println(old+"\n"+main+" "+sub);
                return true;
            } catch (FileNotFoundException ex) {
                System.out.println("subcategory data not found");
            }finally{
                print.close();
            }
            return false;
        }
        public String read(File file) throws FileNotFoundException{
            StringBuilder str = null;
            try{
                Scanner in = new Scanner(file);
                 str = new StringBuilder();
                while(in.hasNext()){
                    str.append(in.nextLine());
                }
                in.close();
                return str.toString();
            }
        catch(IOException ex){}
            return null;
        }
        public void setPassword(String pass){
            try {
                encryptpass dou = new encryptpass(pass);//for password encryption(Serializable class).
                FileOutputStream out = new FileOutputStream(datausr);
                ObjectOutputStream ob = new ObjectOutputStream(out);
                ob.writeObject(dou);
            } catch (Exception ex) {
                System.out.println("maincategory data not found");
            }finally{
                print.close();
            }
        }
        //reads the serialized class from the file
        public String getPassword(){
            try {
                FileInputStream in = new FileInputStream(datausr);
                ObjectInputStream obin = new ObjectInputStream(in);
                encryptpass passobj = (encryptpass)obin.readObject();
                String paswd = passobj.getpswd();
                return paswd;
            } catch (FileNotFoundException ex) {
            }catch(IOException ex){
                ex.printStackTrace();
            }catch(ClassNotFoundException ex){
                System.out.println("class not found"+ex);
            }
            return null;
        }
        public String getMainCategories(){
            Scanner in = null;
            try{
                in = new Scanner(datamaincat);
                StringBuilder data = new StringBuilder();
                while(in.hasNext()){
                    data.append(in.nextLine());
                }
                return data.toString();
            }catch(IOException ex){
                System.out.println("maincat.data not found");
            }finally{
            in.close();
        }
            return null;
        }
        //returns a full list of MainMovieCategories List with their relative subDirectories
        public String getfullCategoryData(){
            Scanner mcin = null;
            Scanner scin = null;
            try{
               mcin = new Scanner(datamaincat);
               scin = new Scanner(datasubcat);
               //get how many lines are in the maincatdata
               int lines = 0;
               while(mcin.hasNext()){
                   String acat = mcin.nextLine();
                   lines++;
               }
               mcin.close();
               String maincatlines[] = new String[lines];
               mcin = new Scanner(datamaincat);
               for(int i=0;i<lines;i++){
                   maincatlines[i] = mcin.nextLine();
               }
               mcin.close();
               int linessc = 0;
               while(scin.hasNext()){
                   String line = scin.nextLine();
                   linessc++;
               }
               scin.close();
               HashMap<String,ArrayList> cats = new HashMap();//maincatname,subcats[]
               for(int i=0;i<lines;i++){
                   scin = new Scanner(datasubcat);
                   ArrayList<String> cat = new ArrayList<String>();
                   for(int j=0;j<linessc;j++){
                       String temp = scin.nextLine();
                       String split[] = temp.split(" ");
                       String one = split[0];
                       String two = maincatlines[i];
                       if(one.contains(two)){
                           cat.add(split[1]);
                       }else{System.out.println("not equal");}
                   }
                   cats.put(maincatlines[i], cat);
                   scin.close();
               }
               StringBuilder completedlist = new StringBuilder();
               for(int i=0;i<lines;i++){
                   completedlist.append(maincatlines[i].toUpperCase()+":"+"\n");
                   ArrayList list = cats.get(maincatlines[i]);
                   for(int j=0;j<list.size();j++){
                       completedlist.append("      "+list.get(j)+"\n");
                   }
               }
               return completedlist.toString();
            }catch(IOException ex){
                ex.printStackTrace();
            }
            return null;
        }
        public class encryptpass implements Serializable{
        private String password = null;
        public encryptpass(String pass){
            password = pass;
        }
        public String getpswd(){
            return password;
        }
    }
    }
