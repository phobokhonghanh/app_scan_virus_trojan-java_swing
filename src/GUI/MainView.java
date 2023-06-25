package GUI;

import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseListener;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import virusanalyzer.VirusAnalyzer;

public class MainView extends JFrame {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    JButton btn_scan_all,
            btn_scan_simple,
            btn_view, btn_update,
            btn_quit, btn_deltete,
            btn_search, btn_auto_update,
            cancelButton;
    JCheckBox checkbox;
    JTextField field_search;
    JTable table_view;
    JScrollPane sp;
    JMenuItem jMenuItem_file, jMenuItem_quit;
    JDialog progressDialog;
    JProgressBar progressBar;
    JLabel jLabel_date, lb_all;
    JLabel progressLabel;
    VirusAnalyzer model;
    DefaultTableModel table_model;


    public MainView() {
        model = new VirusAnalyzer();
        init();
    }

    public void init() {
        this.setTitle("Simple Anti-Virus");
        this.setSize(700, 500);
        this.setLocationRelativeTo(null);
        this.setLayout(new BorderLayout());

        ActionListener ac = new Controller(this);

        //Menu
        JMenuBar jMenuBar = new JMenuBar();
        JMenu jMenu_file = new JMenu("File");
        JMenu jMenu_help = new JMenu("Help");
        jMenuItem_file = new JMenuItem("File");
        jMenuItem_quit = new JMenuItem("Quit");
        jMenuItem_quit.addActionListener(ac);

        jMenu_file.add(jMenuItem_file);
        jMenu_file.add(jMenuItem_quit);
        jMenuBar.add(jMenu_file);
        jMenuBar.add(jMenu_help);

        this.setJMenuBar(jMenuBar);

        // Panel
        JPanel jPanel_top = new JPanel();
        jPanel_top.setLayout(new FlowLayout());
        jPanel_top.setPreferredSize(new Dimension(700, 90));
        jPanel_top.setBorder(new TitledBorder("Scan for system"));

        btn_scan_all = new JButton("Scan all");
        btn_scan_all.setBounds(new Rectangle(new Dimension(200, 200)));
        btn_scan_all.addActionListener(ac);
        ImageIcon icon_all = new ImageIcon("Icon/scan_all.png");
        Image image = icon_all.getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH);
        icon_all.setImage(image);
        btn_scan_all.setIcon(icon_all);

        jPanel_top.add(btn_scan_all);

        btn_scan_simple = new JButton("Scan");
        ImageIcon icon_scan = new ImageIcon("Icon/scan.png");
        Image image_scan = icon_scan.getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH);
        icon_scan.setImage(image_scan);
        btn_scan_simple.setIcon(icon_scan);
        btn_scan_simple.setBounds(new Rectangle(new Dimension(200, 200)));

        btn_scan_simple.addActionListener(ac);
        jPanel_top.add(btn_scan_simple);

        JPanel jPanel_left = new JPanel();
        jPanel_left.setPreferredSize(new Dimension(120, 500));
        jPanel_left.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 15));
        btn_view = new JButton("View result");
        btn_view.setPreferredSize(new Dimension(100, 30));
        btn_view.addActionListener(ac);
        btn_update = new JButton("Update");
        btn_update.setPreferredSize(new Dimension(100, 30));
        btn_update.addActionListener(ac);

        btn_auto_update = new JButton("Auto Update");
        btn_auto_update.setPreferredSize(new Dimension(100, 30));
        btn_auto_update.addActionListener(ac);

        btn_quit = new JButton("Quit");
        btn_quit.setPreferredSize(new Dimension(100, 30));
        btn_quit.addActionListener(ac);
        jPanel_left.add(btn_view);
        jPanel_left.add(btn_update);
        jPanel_left.add(btn_auto_update);
        jPanel_left.add(btn_quit);

        JLabel jLabel_name = new JLabel("Simple Anti-Virus");
        jLabel_date = new JLabel("14/4/2023-v0.1");
        jPanel_left.add(jLabel_name);
        jPanel_left.add(jLabel_date);

        JPanel jPanel_center = new JPanel();
        jPanel_center.setPreferredSize(new Dimension(680, 300));
        jPanel_center.setBorder(new TitledBorder("List"));


        Object[] title_table = {"Path", "Virus Type", "Virus Name", "Action"};

        table_view = new JTable();

        table_model = new DefaultTableModel() {
            /**
             *
             */
            private static final long serialVersionUID = 1L;

            public Class<?> getColumnClass(int column) {
                switch (column) {
                    case 0:
                        return String.class;
                    case 1:
                        return String.class;
                    case 2:
                        return String.class;
                    case 3:
                        return Boolean.class;
                    default:
                        return String.class;
                }
            }

            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 3;
            }
        };
        table_model.setColumnIdentifiers(title_table);
        table_view.setModel(table_model);
        table_view.addMouseListener((MouseListener) ac); // 20130340
        TableColumnModel columnModel = table_view.getColumnModel();
        TableColumn column = columnModel.getColumn(0);
        column.setPreferredWidth(300);

        sp = new JScrollPane();
        sp.setPreferredSize(new Dimension(550, 255));
        sp.setViewportView(table_view);
        // 20130340
        JPanel pan = new JPanel();
        pan.setPreferredSize(new Dimension(100, 30));
        lb_all = new JLabel("Select all");
        checkbox = new JCheckBox();
        checkbox.addActionListener(ac);
        pan.add(checkbox);
        pan.add(lb_all);

        jPanel_center.setLayout(new BorderLayout());
        jPanel_center.add(sp, BorderLayout.NORTH);
        jPanel_center.add(pan, BorderLayout.EAST);

        JPanel jPanel_bottom = new JPanel();
        jPanel_bottom.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 1));
        jPanel_bottom.setPreferredSize(new Dimension(90, 50));
        jPanel_bottom.setBorder(new TitledBorder("Action"));

        // *20130340
        field_search = new JTextField(20);
        btn_search = new JButton("Search");
        btn_search.addActionListener(ac);

        JPanel searchPanel = new JPanel();
        searchPanel.add(field_search);
        searchPanel.add(btn_search);
        // 20130340*
        btn_deltete = new JButton("Delete");
        btn_deltete.addActionListener(ac);

        jPanel_bottom.add(searchPanel); // 20130340
        jPanel_bottom.add(btn_deltete);
        // Add Component
        this.add(jPanel_left, BorderLayout.WEST);
        this.add(jPanel_top, BorderLayout.NORTH);
        this.add(jPanel_center, BorderLayout.CENTER);
        this.add(jPanel_bottom, BorderLayout.SOUTH);
        this.setResizable(false);

        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);
    }

    public void jProgress() {
        progressDialog = new JDialog(this, "Scanning...", true);
        progressBar = new JProgressBar(0, 100);
        progressLabel = new JLabel("Please wait...");
        cancelButton = new JButton("Cancel");

        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout());
        panel.add(BorderLayout.CENTER, progressBar);
        panel.add(BorderLayout.NORTH, progressLabel);
        panel.add(BorderLayout.SOUTH, cancelButton);

        progressDialog.add(panel);
        progressDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        progressDialog.setSize(new Dimension(250, 85));
        progressDialog.setLocationRelativeTo(this);
    }

    public static void main(String[] args) throws ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException {
        UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new MainView();
            }
        });

    }
}
