
import java.awt.BorderLayout;
import java.awt.Color;
import javax.swing.*;
/**
 *
 * @author kaleab
 */
public class Controlpanel extends JPanel{
    public Controlpanel(){
        setSize(800,430);
        setLayout(new BorderLayout());
        setBackground(new Color(64, 128, 191));
        add(new Dashboard(),BorderLayout.WEST);
    }
}
