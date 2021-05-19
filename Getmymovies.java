
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
/**
 *
 * @author kaleab
 */
public class Getmymovies {
    private JWindow splashscreen;
    private JPanel mainpane;
    private JTextArea ta;
    private File splashicon;
    public Getmymovies(){
        splashicon = new File("res\\splash.jpg");
        splashscreen = new JWindow();
        splashscreen.setSize(240, 160);
        splashscreen.setLayout(new BorderLayout());
        splashscreen.setBackground(Color.white);
        JLabel imglbl = new JLabel(new ImageIcon(splashicon.getAbsolutePath()));
        splashscreen.add(imglbl,BorderLayout.CENTER);
        ta = new JTextArea();
        ta.setEditable(false);
        ta.setText("initializing...");
        splashscreen.add(ta,BorderLayout.SOUTH);
        Dimension screensize = Toolkit.getDefaultToolkit().getScreenSize();
        splashscreen.setLocation(screensize.width/3, screensize.height/3);
        
        Runnable waitrunner = new Runnable(){
            public void run(){
                try {
                    ta.setText("initializing");
                    Thread.sleep(4000);
                    Initialize init = new Initialize(splashscreen,ta);
                    boolean ok[] = init.isdone();//is all the intialization successfull?
                    if(ok[0]==true && ok[1]==true){
                        new Main();
                        splashscreen.setVisible(false);
                        splashscreen.dispose();
                    }else{
                        splashscreen.setVisible(false);
                        splashscreen.dispose();
                    }
                } catch (InterruptedException ex) {
                    //
                }
                
            }
        };
        splashscreen.setVisible(true);
        SwingUtilities.invokeLater(waitrunner);
    }
    public static void main(String arg[]){
        new Getmymovies();
    }
}
