package rfe.bsu.SikolonkoMa.laba4;

import java.awt.BorderLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.io.*;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;

@SuppressWarnings("serial")
public class MainFrame extends JFrame {
    private static final int WIDTH = 800;
    private static final int HEIGHT = 600;
    private JFileChooser fileChooser = null;
    private JCheckBoxMenuItem showAxisMenuItem;
    private JCheckBoxMenuItem showMarkersMenuItem;
    private JCheckBoxMenuItem showAreaMenuItem;
    private JCheckBoxMenuItem RotateMenuItem;
    private GraphicsDisplay display = new GraphicsDisplay();
    private boolean fileLoaded = false;
    public MainFrame() {
        super("Build Graphics");
                setSize(WIDTH, HEIGHT);
        Toolkit kit = Toolkit.getDefaultToolkit();
        setLocation((kit.getScreenSize().width - WIDTH)/2,
                (kit.getScreenSize().height - HEIGHT)/2);
        setExtendedState(MAXIMIZED_BOTH);
        JMenuBar menuBar = new JMenuBar();
        setJMenuBar(menuBar);
        JMenu fileMenu = new JMenu("File");
        menuBar.add(fileMenu);
        Action openGraphicsAction = new AbstractAction("Open file with graphics data") {
        public void actionPerformed(ActionEvent event) {
            if (fileChooser==null) {
                fileChooser = new JFileChooser();
                fileChooser.setCurrentDirectory(new File("."));
            }
            if (fileChooser.showOpenDialog(MainFrame.this) ==
                    JFileChooser.APPROVE_OPTION)
                openGraphics(fileChooser.getSelectedFile());
        }
    };
    fileMenu.add(openGraphicsAction);
    JMenu graphicsMenu = new JMenu("Graphic");
    menuBar.add(graphicsMenu);
    Action showAxisAction = new AbstractAction("Show axis") {
    public void actionPerformed(ActionEvent event) {
        display.setShowAxis(showAxisMenuItem.isSelected());
    }
};
        showAxisMenuItem = new JCheckBoxMenuItem(showAxisAction);
        graphicsMenu.add(showAxisMenuItem);
        showAxisMenuItem.setSelected(true);

        Action showAreaAction = new AbstractAction("Show area") {
            public void actionPerformed(ActionEvent event) {
                display.setShowArea(showAreaMenuItem.isSelected());
            }
        };
        showAreaMenuItem = new JCheckBoxMenuItem(showAreaAction);
        graphicsMenu.add(showAreaMenuItem);
        showAreaMenuItem.setSelected(false);
        showAreaMenuItem.setEnabled(false);

        Action RotateAction = new AbstractAction("Rotate at left") {
            public void actionPerformed(ActionEvent event) {
                display.setRotate(RotateMenuItem.isSelected());
            }
        };
        RotateMenuItem = new JCheckBoxMenuItem(RotateAction);
        graphicsMenu.add(RotateMenuItem);
        RotateMenuItem.setSelected(false);
        RotateMenuItem.setEnabled(false);

        Action showMarkersAction = new AbstractAction("Show spots markeres") {
            public void actionPerformed(ActionEvent event) {
                display.setShowMarkers(showMarkersMenuItem.isSelected());
            }
        };
        showMarkersMenuItem = new JCheckBoxMenuItem(showMarkersAction);
        graphicsMenu.add(showMarkersMenuItem);
        showMarkersMenuItem.setSelected(true);
        graphicsMenu.addMenuListener(new GraphicsMenuListener());
        getContentPane().add(display, BorderLayout.CENTER);
        }

protected void openGraphics(File selectedFile) {
        try {
            DataInputStream in = new DataInputStream(new FileInputStream(selectedFile));
            Double[][] graphicsData = new
            Double[in.available()/(Double.SIZE/8)/2][];
            int i = 0;
            Double val = 0.1;
            while (in.available()>0) {
            Double x = in.readDouble();
            Double y = in.readDouble();
            graphicsData[i++] = new Double[]{x, y};
        }
        if (graphicsData!=null && graphicsData.length>0) {
            fileLoaded = true;
            display.showGraphics(graphicsData);
        }
            in.close();
        } catch (FileNotFoundException ex) {
        JOptionPane.showMessageDialog(MainFrame.this, "File doesn't found",
                "Cannot upload data",
                JOptionPane.WARNING_MESSAGE);
            showAreaMenuItem.setEnabled(false);
            RotateMenuItem.setEnabled(false);
        return;
        } catch (IOException ex) {
        JOptionPane.showMessageDialog(MainFrame.this, "Error in reading data from the file",
                "Cannot upload data",
        JOptionPane.WARNING_MESSAGE);
            showAreaMenuItem.setEnabled(false);
            RotateMenuItem.setEnabled(false);
        return;
        }
        showAreaMenuItem.setEnabled(true);
        RotateMenuItem.setEnabled(true);
        }
public static void main(String[] args) {




        MainFrame frame = new MainFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
        }
private class GraphicsMenuListener implements MenuListener {
    public void menuSelected(MenuEvent e) {
        showAxisMenuItem.setEnabled(fileLoaded);
        showMarkersMenuItem.setEnabled(fileLoaded);
    }
    public void menuDeselected(MenuEvent e) {
    }
    public void menuCanceled(MenuEvent e) {
    }
}
}

