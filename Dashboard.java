
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import javax.swing.*;
import javax.swing.border.TitledBorder;

public class Dashboard extends JPanel{
    public JTextArea dashboard;
    public JScrollPane scroll;
    private PhpJavaConnectionService connservice;
    public Dashboard(){
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(280,430));
        setBackground(new Color(64, 128, 191));
        TitledBorder border = new TitledBorder("Client Activities");
        setBorder(border);
        
        dashboard = new JTextArea();
        dashboard.setEditable(false);
        scroll = new JScrollPane(dashboard);
        add(scroll,BorderLayout.CENTER);
        
        connservice = new PhpJavaConnectionService(dashboard);
        connservice.startService();
    }
}
