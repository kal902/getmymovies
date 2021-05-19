
/*
@author Kaleab
*/
import java.io.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.ButtonGroup;
import javax.swing.border.*;
import javax.swing.SwingUtilities;

public class Main{

    private JFrame frame;
    private JMenuBar mb;
    private JMenu options;
    private JMenuItem setting, help, about, exit, rescan, categories, minimize;
    public String posturepath;
    private boolean postureview = false;
    private float opacity = 0.5f;

    public Main() {
        frame = new JFrame("getMyMovies");
        frame.setLayout(new BorderLayout());
        frame.setSize(832, 557);
        //frame.setResizable(false);
        //frame.setUndecorated(true);
        //frame.setOpacity(opacity);
        frame.setBackground(new Color(179, 77, 128));
        frame.setContentPane(new MainPane());
        frame.addMouseMotionListener(new MouseMotionListener() {
            Dimension d;

            @Override
            public void mouseDragged(MouseEvent e) {
                d = frame.getSize();
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                d = frame.getSize();
            }
        });
        
        //menubars
        mb = new JMenuBar();
        options = new JMenu("  options");
        setting = new JMenuItem("  settings");
        setting.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                settings setting = new settings();
                setting.launch();
            }
        });
        help = new JMenuItem("  help");
        help.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(null, "CATEGORIES EDITOR help:\n"
                        + "to add a sub category a main category must be added,\n"
                        + "the main category names will appear in the navigation\n"
                        + "panel of the web page, when clicked the page will redirect\n"
                        + "to a subCategories list that is relative to the main category\n"
                        + "when adding a subCategory a main category name is required\n"
                        + "and the name must be valid(added before)\n"
                        + "when adding a subcategory there is option for adding a postureview\n"
                        + "if posture view is enabled an image file must be selected,else an\n"
                        + "error will occur.\n"
                        + "CONTROLPANEL:\n"
                        + "the top layout lists connected users list(their id)\n"
                        + "when selecting a list(an id),the downloaded movies by\n"
                        + "that user will be listed below.\n"
                        + "the added categories will be added to a database.\n"
                        + "a new table is created for each subcategory in the database.\n"
                        + "\n\n"
                        + "if an error occured:\n"
                        + "check if the web server service is running.\n"
                        + "check if the mysql server is up.");
            }
        });
        about = new JMenuItem("  about");
        exit = new JMenuItem("  quit");
        exit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ev) {
                int result = JOptionPane.showConfirmDialog(frame, "exit GetMyMovies", "getmymovies", JOptionPane.YES_NO_OPTION);
                if (result == 0) {
                    frame.setVisible(false);
                    System.exit(0);
                }
            }
        });
        rescan = new JMenuItem("  rescan");
        rescan.setToolTipText("scan the disk for new(updated) movies. only the added Subcategory dir will be scanned");
        minimize = new JMenuItem("  minimize");
        minimize.setToolTipText("hide the frame");
        minimize.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ev) {
                frame.setState(JFrame.ICONIFIED);
            }
        });
  
        options.addSeparator();
        options.add(setting);
        options.addSeparator();
        options.add(help);
        options.addSeparator();
        options.add(rescan);
        options.addSeparator();
        options.add(about);
        options.addSeparator();
        options.add(minimize);
        options.addSeparator();
        options.add(exit);
        
        mb.add(options);
        
        frame.setJMenuBar(mb);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationByPlatform(true);
        frame.setVisible(true);
    }

    private class MainPane extends JPanel {

        private JPanel catedit,controlpanel;
        private JTabbedPane tabbedpane;
        private File icon;
        public MainPane() {
            setBackground(new Color(64, 128, 191));
            icon = new File("res\\edit.png");
            catedit = new CategoryEditor();
            controlpanel = new Controlpanel();
            tabbedpane = new JTabbedPane(JTabbedPane.TOP);
            tabbedpane.addTab("CategoryEditor", catedit);
            tabbedpane.addTab("Monitor", controlpanel);
            add(tabbedpane);
        }
    }

    //panel that holds the categorie editor,categories list and the category delete section.
    private class CategoryEditor extends JPanel {

        // rightpane,delcat and addedCategories class need to access each other, to update their data.

        private JPanel leftpane;
        public RightPane rightpane;
        public deleteCat delcat;
        public Homepagepane homepagepane;
        public addedCategories listpane;
        private JTabbedPane tabbedpane;
        public CategoryEditor() {
            super.setBackground(Color.white);
            setLayout(new BorderLayout());
            leftpane = new JPanel();//left pane holds categories list and categories delete pane
            leftpane.setPreferredSize(new Dimension(250, 430));
            leftpane.setBackground(new Color(64, 128, 191));

            rightpane = new RightPane(this);
            delcat = new deleteCat(this);
            listpane = new addedCategories();
            homepagepane = new Homepagepane();
            
            leftpane.add(listpane);
            leftpane.add(delcat);

            add(rightpane, BorderLayout.EAST);//add rightpane to super pane
            add(leftpane, BorderLayout.CENTER);//add left pane to super pane
            add(homepagepane,BorderLayout.WEST);
        }
    }

    private class deleteCat extends JPanel {

        private JComboBox sub;
        public JComboBox main;
        private JButton maindel, subdel;
        public CategoryEditor context;

        public deleteCat(CategoryEditor context) {
            this.context = context;
            setPreferredSize(new Dimension(230, 150));
            FlowLayout layout = new FlowLayout();
            layout.setHgap(30);
            layout.setVgap(20);
            setLayout(layout);
            setBackground(new Color(64, 128, 191));
            TitledBorder border = new TitledBorder("delete Categories");
            setBorder(border);

            DataStorage store = new DataStorage(frame);
            sub = new JComboBox();
            sub.setPreferredSize(new Dimension(70, 20));
            main = new JComboBox(store.getMainCategories());
            main.setPreferredSize(new Dimension(70, 20));
            main.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    String selected = main.getSelectedItem().toString();
                    String subcatformain[] = store.getSubCatFor(selected);
                    sub.removeAllItems();//refresh the subcategories list
                    for (int i = 0; i < subcatformain.length; i++) {
                        sub.addItem(subcatformain[i]);
                    }
                }

            });
            File icon = new File("res\\delete.png");
            maindel = new JButton();
            maindel.setIcon(new ImageIcon(icon.getAbsolutePath()));
            maindel.setPreferredSize(new Dimension(30, 20));
            maindel.setToolTipText("delete selected MainCategory, if main category is deleted all it subcategories will be deleted!");
            maindel.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent ev) {
                    DataStorage store;
                    String selected = main.getSelectedItem().toString();
                    int result = JOptionPane.showConfirmDialog(frame, selected + " and all its subcategories wil removed.\n"
                            + "continue?");
                    if (result == 0) {
                        store = new DataStorage(frame);
                        int resultdel = store.removeMainCategoy(selected);
                        if (resultdel == 0) {
                            JOptionPane.showMessageDialog(frame, "deleted successfully");
                            sub.removeAllItems();
                            context.listpane.refreshList();
                            try {
                                main.removeItem(selected);
                            } catch (NullPointerException ex) {
                                //raises error if the removed item is the last item
                            }
                            try {
                                context.rightpane.mlist.removeItem(selected);
                            } catch (NullPointerException ex) {
                                //raises error if the removed item is the last item
                            }
                        }
                    }
                }
            });
            subdel = new JButton();
            subdel.setPreferredSize(new Dimension(30, 20));
            subdel.setIcon(new ImageIcon(icon.getAbsolutePath()));
            subdel.setToolTipText("delete selected subCategory");
            subdel.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent ev) {
                    DataStorage store;
                    int result = JOptionPane.showConfirmDialog(frame, sub.getSelectedItem().toString() + "  wil removed.\n"
                            + "continue?");
                    if (result == 0) {
                        store = new DataStorage(frame);
                        boolean resultdel = store.removeSubCategory(main.getSelectedItem().toString(),sub.getSelectedItem().toString());
                        if (resultdel == true) {
                            JOptionPane.showMessageDialog(frame, "deleted successfully");
                            sub.removeItem(sub.getSelectedItem().toString());
                            context.listpane.refreshList();
                        }
                    }
                }
            });
            add(main);
            add(maindel);
            add(sub);
            add(subdel);

        }
    }

    //panel to display added categories
    public class addedCategories extends JPanel {

        private JTextArea ta;
        private JScrollPane scroll;

        public addedCategories() {
            setBackground(new Color(64, 128, 191));
            setPreferredSize(new Dimension(230, 290));
            TitledBorder border = new TitledBorder("added Categories");
            setBorder(border);
            ta = new JTextArea();
            ta.setEditable(false);
            ta.setPreferredSize(new Dimension(200, 250));
            scroll = new JScrollPane();
            scroll.add(ta);
            DataStorage dat = new DataStorage(frame);
            ta.append(dat.getCategoriesData());
            add(ta);
        }

        public void refreshList() {
            DataStorage store = new DataStorage(frame);
            ta.setText(store.getCategoriesData());
        }
    }

    //category editor panel(right side).
    private class RightPane extends JPanel {

        private JButton btnaddcategory, btnaddsubcategory, choosefile, choosedir, refreshlist;
        private JTextField tfcategoryname, tfsubcategoryname,iconpath,filepath;
        private JTextField tfmaincatname;//to add a subcategory, its main category is needed.
        private JRadioButton btnon, btnoff;
        private ButtonGroup group;
        private Color colr;
        private boolean gridview;
        private String list[];
        public JComboBox mlist;
        private boolean isimageselected = false;
        private CategoryEditor context;
        private String dirpath;

        public RightPane(CategoryEditor con) {
            this.context = con;
            colr = new Color(51, 128, 204);
            FlowLayout layout = new FlowLayout();
            layout.setHgap(15);
            layout.setVgap(10);
            setLayout(layout);
            setBackground(colr);
            TitledBorder border = new TitledBorder("Category Editor");
            border.setTitleColor(Color.DARK_GRAY);
            super.setBorder(border);
            super.setPreferredSize(new Dimension(250, 450));
            //top pane(main categogy)
            JPanel toppane = new JPanel();
            toppane.setBackground(colr);
            toppane.setPreferredSize(new Dimension(230, 180));
            FlowLayout fl = new FlowLayout();
            fl.setHgap(15);
            fl.setVgap(10);
            toppane.setLayout(fl);
            TitledBorder bordermain = new TitledBorder("Main Category");
            bordermain.setTitleColor(Color.BLACK);
            toppane.setBorder(bordermain);
            JLabel emptyspace = new JLabel("                                                                 ");
            JLabel lbl;
            lbl = new JLabel("Enter Main Category Name");
            toppane.add(lbl);
            tfcategoryname = new JTextField();
            tfcategoryname.setToolTipText("enter a new name for your mainMovieCategory");
            tfcategoryname.setPreferredSize(new Dimension(150, 30));
            toppane.add(tfcategoryname);
            btnon = new JRadioButton("on");
            btnon.addItemListener(new ItemListener() {
                public void itemStateChanged(ItemEvent e) {
                    gridview = true;
                }
            });
            btnoff = new JRadioButton("off");
            btnoff.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    gridview = false;
                }
            });
            lbl = new JLabel("      GridView:-");
            ButtonGroup group = new ButtonGroup();
            btnoff.setSelected(true);
            group.add(btnon);
            group.add(btnoff);
            toppane.add(lbl);
            toppane.add(btnon);
            toppane.add(btnoff);
            File icon = new File("res\\add.png");
            btnaddcategory = new JButton();
            btnaddcategory.setPreferredSize(new Dimension(30,20));
            btnaddcategory.setToolTipText("Add Main category");
            btnaddcategory.setIcon(new ImageIcon(icon.getAbsolutePath()));
            btnaddcategory.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    String categoryname = tfcategoryname.getText();
                    int caret = 0;
                    caret = tfcategoryname.getCaretPosition();//cursor position,if 0 means no text is entered
                    if (caret != 0) {
                        DataStorage store = new DataStorage(frame);
                        if (gridview == true) {
                            store.setView(tfcategoryname.getText(), "Gridview");
                        } else {
                            store.setView(tfcategoryname.getText(), "Listview");
                        }
                        boolean ok=store.addMainCategory(tfcategoryname.getText());
                        if(ok==true){
                            mlist.addItem(tfcategoryname.getText());//update the combobox
                            context.delcat.main.addItem(tfcategoryname.getText().trim());//update the combobox for delete cat section
                            context.listpane.refreshList();
                            JOptionPane.showMessageDialog(frame, "MainMovie Categorie added successfully");
                        }
                    } else {
                        JOptionPane.showMessageDialog(frame, "please enter a name for the category");
                    }
                }
            });
            toppane.add(btnaddcategory);
            add(toppane);
            //sub category section
            GridBagLayout gb = new GridBagLayout();
            GridBagConstraints gbc = new GridBagConstraints();
            //gbc.insets = new Insets(5,5,5,5);
            JPanel panebtm = new JPanel(gb);
            JLabel view = new JLabel("Listview");
            DataStorage data = new DataStorage(frame);
            list = data.getMainCategories();
            mlist = new JComboBox(list);
            mlist.setPreferredSize(new Dimension(70, 20));
            mlist.addItemListener(new ItemListener() {
                @Override
                public void itemStateChanged(ItemEvent e) {
                    DataStorage data = new DataStorage(frame);
                    String selected = (String) mlist.getSelectedItem();
                    String viewcmd = data.getView(selected);
                    view.setText(viewcmd);
                    if(selected!=null){
                        if (viewcmd.contains("Gridview")) {
                        choosefile.setEnabled(true);//  if grid view admin must select an image file.
                    } else {
                        choosefile.setEnabled(false);
                    }
                    }
                }

            });

            panebtm.setBackground(colr);
            panebtm.setPreferredSize(new Dimension(230, 225));
            TitledBorder bordersub = new TitledBorder("Sub Categories");
            bordersub.setTitleColor(Color.BLACK);
            panebtm.setBorder(bordersub);
            gbc.gridx = 0;
            gbc.gridy = 0;
            panebtm.add(mlist,gbc);
            gbc.gridx = 1;
            gbc.gridy = 0;
            panebtm.add(view, gbc);
            gbc.insets = new Insets(5,5,5,5);
            tfsubcategoryname = new JTextField();
            tfsubcategoryname.setPreferredSize(new Dimension(150, 25));
            gbc.gridx = 0;
            gbc.gridy = 2;
            panebtm.add(tfsubcategoryname,gbc);
            JLabel lblname = new JLabel(": name");
            gbc.gridx = 1;
            gbc.gridy = 2;
            panebtm.add(lblname,gbc);
            iconpath = new JTextField("image path");
            iconpath.setPreferredSize(new Dimension(150, 25));
            gbc.gridx = 0;
            gbc.gridy = 3;
            panebtm.add(iconpath,gbc);
            choosefile = new JButton("...");
            choosefile.setPreferredSize(new Dimension(30,20));
            choosefile.setToolTipText("choose an image file to set as a posture for the subcategory");
            choosefile.setEnabled(false);
            choosefile.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    FileDialog fd = new FileDialog(frame);
                    fd.setName("choose an image");
                    fd.setVisible(true);
                    String path = fd.getDirectory();
                    String name = fd.getFile();
                    if(path!=null&&name!=null){
                        File temppath = new File(path+"\\"+name);
                        String imgpath = temppath.getAbsolutePath();
                        iconpath.setText(imgpath);
                        isimageselected = true;
                        System.out.println(imgpath);
                        JOptionPane.showMessageDialog(frame, "image selected");
                    }
                }
            });
            gbc.gridx = 1;
            gbc.gridy = 3;
            panebtm.add(choosefile,gbc);
            filepath = new JTextField("dir path");
            filepath.setPreferredSize(new Dimension(150, 25));
            gbc.gridx = 0;
            gbc.gridy = 4;
            panebtm.add(filepath,gbc);
            choosedir = new JButton("..");
            choosedir.setPreferredSize(new Dimension(30,20));
            choosedir.setToolTipText("select a directory as a main path for the subCategory,the selected dir will be scanned for movies.\n"
                    + " Note: please select directory from the rootdrive you have set during first intialization");
            choosedir.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    FileDialog fd = new FileDialog(frame);
                    fd.setDirectory(DataStorage.getrootdrive());
                    fd.setName("select a folder containing the movies");
                    fd.setVisible(true);
                    String path = fd.getDirectory();
                    String name = fd.getFile();
                    if(path!=null&&name!=null){
                        filepath.setText(path);
                        if (view.getText().contains("Gridview") && isimageselected == true) {//enable the add button if the the view is grid view and image(posture) is selected.
                            btnaddsubcategory.setEnabled(true);
                        } else if (view.getText().contains("Listview")) {
                            btnaddsubcategory.setEnabled(true);
                        }
                    } 
                }
            });
            gbc.gridx = 1;
            gbc.gridy = 4;
            panebtm.add(choosedir,gbc);
            btnaddsubcategory = new JButton();
            btnaddsubcategory.setToolTipText("add Subcategory");
            btnaddsubcategory.setIcon(new ImageIcon(icon.getAbsolutePath()));
            btnaddsubcategory.setPreferredSize(new Dimension(30,20));
            btnaddsubcategory.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent ev) {
                    int caretsub = tfsubcategoryname.getCaretPosition();
                    String maincat;
                    if (caretsub != 0) {
                        String subname = tfsubcategoryname.getText();
                        DataStorage ld = new DataStorage(frame);
                        maincat = mlist.getSelectedItem().toString();

                        if (choosefile.isEnabled() == false) {// if listview
                            boolean result = ld.addsubCategory(maincat, subname, filepath.getText().toString(), null);
                            context.listpane.refreshList();
                            if (result == true) {
                                JOptionPane.showMessageDialog(frame, "sub category added");
                            } else {
                                JOptionPane.showMessageDialog(frame, "adding subCategory failed!");
                            }
                        } else {// gridview
                            boolean result = ld.addsubCategory(maincat, subname, filepath.getText().toString(), iconpath.getText().toString());
                            if (result == true) {
                                context.listpane.refreshList();
                                JOptionPane.showMessageDialog(frame, "sub category added");
                            } else {
                                JOptionPane.showMessageDialog(frame, "adding subCategory failed!");
                            }
                        }
                    } else {
                        JOptionPane.showMessageDialog(frame, "please fill the full form");
                    }

                }
            });
            btnaddsubcategory.setToolTipText("choose the directory for the subcategory the add the data");
            btnaddsubcategory.setEnabled(false);
            gbc.gridx = 0;
            gbc.gridy = 5;
            panebtm.add(btnaddsubcategory,gbc);
            add(panebtm);
        }
    }
    //left upper panel, for listing connected users
    public class connectedUsersList extends JPanel {

        JList connectedusrs;
        private JScrollPane scroll;

        public connectedUsersList() {
            super.setPreferredSize(new Dimension(370, 280));
            setBackground(Color.white);
            connectedusrs = new JList();
            connectedusrs.setPreferredSize(new Dimension(300, 300));
            scroll = new JScrollPane();
            scroll.add(connectedusrs);
            add(connectedusrs);
        }
        //@Override
        //public void paintComponent(Graphics g){super.paintComponent(g);}
    
    }
    
    public class Homepagepane extends JPanel{
        JComboBox homepageslist;
        JPanel editpane,statuspane;
        JTextField movname,imgpath,movpath;
        JButton choosemovpath,chooseimgpath,add,remove;
        private DataStorage store;
        private Dimension tfsize;
        private boolean ISMOVPATHSET = false;
        private boolean ISIMGPATHSET = false;
        public Homepagepane(){
            setBackground(new Color(51, 128, 204));
            tfsize = new Dimension(180,25);
            setPreferredSize(new Dimension(300,430));
            setLayout(new BorderLayout());
            //editpane
            store = new DataStorage(frame);
            GridBagLayout layout = new GridBagLayout();
            GridBagConstraints gbc = new GridBagConstraints();
            TitledBorder border = new TitledBorder("Homepage");
            editpane = new JPanel(layout);
            editpane.setBorder(border);
            editpane.setBackground(new Color(51, 128, 204));
            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.insets = new Insets(10,5,5,5);
            movpath = new JTextField("movie path");
            movpath.setPreferredSize(tfsize);
            editpane.add(movpath,gbc);
            
            gbc.gridx = 1;
            gbc.gridy = 0;
            choosemovpath = new JButton("...");
            choosemovpath.setPreferredSize(new Dimension(30,20));
            choosemovpath.setToolTipText("Select a movie. \nNote: please select directory from the rootdrive you have set during first intialization");
            choosemovpath.addActionListener(new ActionListener(){
                @Override
                public void actionPerformed(ActionEvent event){
                    FileDialog fd = new FileDialog(frame);
                    fd.setVisible(true);
                    String dir = fd.getDirectory();
                    String file = fd.getFile();
                    String path = dir+file;
                    if(path!=null){
                        String rootdrive = DataStorage.getrootdrive();
                        String downloadablepath = path.replace(rootdrive, "");
                        movpath.setText(downloadablepath);
                        ISMOVPATHSET = true;
                    }
                }
            });
            editpane.add(choosemovpath,gbc);
            
            gbc.gridx = 0;
            gbc.gridy = 1;
            imgpath = new JTextField("image path");
            imgpath.setPreferredSize(tfsize);
            editpane.add(imgpath,gbc);
            
            gbc.gridx = 1;
            gbc.gridy = 1;
            chooseimgpath = new JButton("...");
            chooseimgpath.setPreferredSize(new Dimension(30,20));
            chooseimgpath.setToolTipText("Select an image file.");
            chooseimgpath.addActionListener(new ActionListener(){
                @Override
                public void actionPerformed(ActionEvent event){
                    FileDialog fd = new FileDialog(frame);
                    fd.setVisible(true);
                    String dir = fd.getDirectory();
                    String file = fd.getFile();
                    String path = dir+file;
                    if(file!=null&&path!=null){
                        imgpath.setText(path);
                        ISIMGPATHSET = true;
                    }
                }
            });
            editpane.add(chooseimgpath,gbc);
            
            gbc.gridx = 0;
            gbc.gridy = 2;
            movname = new JTextField();
            movname.setPreferredSize(tfsize);
            editpane.add(movname,gbc);
            gbc.gridx = 1;
            gbc.gridy = 2;
            editpane.add(new JLabel(": Movie name"),gbc);
            
            gbc.gridx = 0;
            gbc.gridy = 3;
            add = new JButton("add");
            add.addActionListener(new ActionListener(){
                @Override
                public void actionPerformed(ActionEvent event){
                    if(movname.getCaretPosition()!=0){
                        DataStorage store = new DataStorage(frame);
                        String imagepath = imgpath.getText();
                        String moviepath = movpath.getText();
                        String moviename = movname.getText();
                        boolean ok = store.addHomepageData(moviename, imagepath, moviepath);
                        if(ok==true){
                            homepageslist.addItem(moviename);
                            JOptionPane.showMessageDialog(frame, "homepage data added successfully.");
                        }
                       }
                }
            });
            editpane.add(add,gbc);
            
            gbc.gridx = 0;
            gbc.gridy = 4;
            homepageslist = new JComboBox(store.gethomepagesname());
            homepageslist.setPreferredSize(tfsize);
            editpane.add(homepageslist,gbc);
            remove = new JButton();
            remove.setPreferredSize(new Dimension(30, 20));
            File icon = new File("res\\delete.png");
            remove.setIcon(new ImageIcon(icon.getAbsolutePath()));
            remove.addActionListener(new ActionListener(){
                @Override
                public void actionPerformed(ActionEvent event){
                    DataStorage store = new DataStorage(frame);
                    String selecteditem = (String)homepageslist.getSelectedItem();
                    boolean isremoved = store.removeHomepageData(selecteditem);
                    if(isremoved){
                        homepageslist.removeItem(selecteditem);
                        JOptionPane.showMessageDialog(frame, "homepage data removed successfully.");
                    }
                }
            });
            gbc.gridx = 1;
            gbc.gridy = 4;
            editpane.add(remove,gbc);
            
            add(editpane,BorderLayout.NORTH);
        }
    }
}
