
import java.awt.TextArea;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import java.io.PrintWriter;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
/**
 *
 * @author kaleab
 */
public class PhpJavaConnectionService {
    
    private boolean connectionOn = true;
    private JTextArea dashboard;
    private static int NEWUSER = 0;
    private static int OLDUSER = 1;
    private ServerSocket sv;
    private Socket aclient;
    public PhpJavaConnectionService(JTextArea board){
        dashboard = board;
    }
    public void startService(){
        new Thread(new Runnable(){
            public void run(){
                try {
                    sv = new ServerSocket(56321);
                    SwingUtilities.invokeLater(new Runnable(){
                            public void run(){
                                dashboard.append("Service started\n");
                                dashboard.append("waiting\n");
                            }
                        });
                    int ids = 1;
                    while(connectionOn){
                        aclient = sv.accept();
                        BufferedReader in = new BufferedReader(new InputStreamReader((aclient.getInputStream())));
                        String r = in.readLine();
                        System.out.println(r);
                        if(r.contains("getmymovie")){//if 0 the client is new
                           new Thread(new ServerConnection(aclient,dashboard,"ID: "+ids,NEWUSER)).start();
                           ids = ids+1; 
                        }else{
                            new Thread(new ServerConnection(aclient,dashboard,r,OLDUSER)).start();
                        }
                    }
                } catch (IOException ex) {
                        ex.printStackTrace();
                  }
            }
        }).start();
    }
    public boolean stopService(){
        try{
            connectionOn = false;
            if(aclient!=null){
                aclient.close();
            }
            sv.close();
            return true;
        }catch(Exception ex){ex.printStackTrace();}
        return false;
    }
    private class ServerConnection implements Runnable{
        private JTextArea dashboard;
        private Socket client;
        private String clientId;
        private int clientconstatus;
        private PrintStream m;
        ServerConnection(Socket sock,JTextArea ta,String id,int constate){
            dashboard = ta;
            client = sock;
            clientId = id;
            clientconstatus = constate;
        }
        @Override
        public void run(){
            try{
                //writer = new DataOutputStream(client.getOutputStream());
                m = new PrintStream(client.getOutputStream());
                if(clientconstatus==PhpJavaConnectionService.NEWUSER){
                        m.append(clientId+"\n");//send the client its id.
                        m.flush();
                        SwingUtilities.invokeLater(new Runnable(){
                            public void run(){
                                dashboard.append("\nClient Connected: id:- "+clientId+"\n\n");
                            }
                        });
                        m.close();
                        client.close();
                }else{
                       SwingUtilities.invokeLater(new Runnable(){
                            public void run(){
                                dashboard.append(clientId+"\n");
                            }
                        });
                       client.close();
                    }
            }catch(IOException ex){}
        }
    }    
}
