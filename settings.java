

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;

class settings extends JFrame {

        JLabel lbl = new JLabel("select root drive");
        JTextField tf;
        JButton btn;
        public settings() {
            
            setLayout(new FlowLayout());
            add(lbl);
            super.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    System.out.println("exiting");
                    //System.exit(0);
                }
            });
            tf = new JTextField();
            tf.setPreferredSize(new Dimension(50,25));
            add(tf);
            btn = new JButton("add");
            btn.setPreferredSize(new Dimension(20,20));
            btn.addActionListener(new ActionListener(){
                @Override
                public void actionPerformed(ActionEvent ev){
                    DataStorage.setrootdrive(tf.getText().trim());
                }
            });
            add(btn);
            setSize(300, 300);
            setTitle("settings");
        }

        public void launch() {
            setVisible(true);
        }
    }
