

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.JWindow;


public class Initialize {
    
    private String maindata, subdata, viewdata,pathdata, homepagedata, indexdata;
        private JWindow frame;
        private boolean ok[];
        private JTextArea board;
        public Initialize(JWindow context,JTextArea ta) {
            maindata = "data\\maincatdata.data";//serialized ArrayList for storing main categories
            subdata = "data\\subcatdata.data";//serialized ArrayList
            viewdata = "data\\viewdata.data";//serialized HashMap for storing selected viewtypes
            pathdata = "data\\pathdata.data";
            homepagedata = "data\\homepagedata.data";
            indexdata = "data\\lastindex.data";//indexs of homepagedata
            frame = context;
            ok = new boolean[2];
            board = ta;
            init();
            System.out.println("done initializing");
        }
        //check if the program is running for the first time
        private void init() {
            try {
                boolean isfirsttime = isfirsttime();
                if(isfirsttime==true){
                    board.setText("initializing...");
                    DatabaseManager.prepareDatabase();
                    setup();
                    ok[0]=true;
                }else{
                    ok[0]=true;
                    ok[1]=true;
                }
            } catch (FileNotFoundException ex) {
                JOptionPane.showMessageDialog(frame, "error! can not find the check.file");
                ok[1]=false;
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(frame, "error! can not initialize, database is offline.");
                ok[0]=false;
            }
        }
        public boolean isfirsttime() throws FileNotFoundException{
            Scanner in = new Scanner(new File("data\\check.file"));
            String check = in.nextLine().trim();
            if (check.contains("yes")) {//if true the program will assume that its running in this machine for the first time.
                in.close();
                  return true;
                }else{
                in.close();
                return false;
            }
        }
        //serialize the demo objects. reduces FileNotFoundException while
        //trying to read data from the input stream for the first time. b/c they are not
        //created on the first run of this program.
        private void setup() {
            PrintWriter print = null;
            try {
                ArrayList<String> temp = new ArrayList<>();
                HashMap<String, String> tempview = new HashMap<>();
                HashMap<String, String> temppathdata = new HashMap<>();
                HashMap<Integer, String[]> homepagedat = new HashMap<>();
                ArrayList<Integer> index = new ArrayList<>();
                
                print = new PrintWriter(new File("data\\check.file"));
                print.write("no");
                print.close();
                FileOutputStream out = new FileOutputStream(new File(maindata));
                ObjectOutputStream obo = new ObjectOutputStream(out);
                obo.writeObject(temp);
                obo.close();
                out.close();
                board.setText("maincat data created");
                out = new FileOutputStream(new File(subdata));
                obo = new ObjectOutputStream(out);
                obo.writeObject(temp);
                out.close();
                obo.close();
                board.setText("subcat data created");
                out = new FileOutputStream(new File(viewdata));
                obo = new ObjectOutputStream(out);
                obo.writeObject(tempview);
                obo.close();
                out.close();
                board.setText("view data created");
                out = new FileOutputStream(pathdata);
                obo = new ObjectOutputStream(out);
                obo.writeObject(temppathdata);
                obo.close();
                out.close();
                board.setText("path data created");
                out = new FileOutputStream(homepagedata);
                obo = new ObjectOutputStream(out);
                obo.writeObject(homepagedat);
                obo.close();
                out.close();
                board.setText("homepage data created");
                out = new FileOutputStream(indexdata);
                obo = new ObjectOutputStream(out);
                obo.writeObject(index);
                obo.close();
                out.close();
                board.setText("index data created");
                String drive = JOptionPane.showInputDialog(frame, "enter the Drive letter where\nthe movies are stored in.\ne.g C:\\");
                FileOutputStream fout = new FileOutputStream(new File("data\\settings.data"));
                ObjectOutputStream oout = new ObjectOutputStream(fout);
                HashMap<String,String> data = new HashMap<>();
                data.put("rootdrive",drive.toUpperCase());
                oout.writeObject(data);
                oout.close();
                fout.close();
                board.setText("rootdrive data saved");
                boolean result = setupwebpagedata();
                if(result==true){
                    ok[1]=true;
                }else{
                    ok[1]=false;
                }
                ok[1]=true;
            } catch (FileNotFoundException ex) {
                JOptionPane.showMessageDialog(frame, "error! can not find the data files");
                ok[1]=false;
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(frame, "error! can not find the data files");
                ok[1]=false;
            }
        }
        public boolean[] isdone(){
            return ok;
        }
        public boolean setupwebpagedata(){
            return true;
        }
}
