package com.diskscanner;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;

import javax.swing.*;


import com.diskscanner.duplicates.DuplicateFinder;
import org.jdesktop.swingx.JXTreeTable;


public class DiskAnalyzerFrame extends JFrame {
	// 
	//73B2E8
	private static final Color SCAN_BUTTON_COLOR = new Color(0x73,0xb2,0xe8);
	// 98FFD5
	private static final Color HEADER_BG_COLOR = new Color(0x98,0xFF,0xD5);
	// B6B2E8
	private static final Color CHOOSE_BUTTON_COLOR = new Color(0xb6,0xb2,0xe8);
	// E8674A
	private static final Color STOP_BUTTON_COLOR = new Color(0xe8,0x67,0x4A);

	private static final String STOP_BUTTON_ACTIVE = "Stop scanning";
	private static final String SCAN_BUTTON_ACTIVE = "Scan selected folder";
	private static final long serialVersionUID = -2129525315141277648L;
	private JTabbedPane tabs = new JTabbedPane();
	private DiskTreeTable diskTreeTable = new DiskTreeTable();
	private JXTreeTable treeTableView = new JXTreeTable( diskTreeTable );
	private JButton scanButton = new JButton(DiskAnalyzerFrame.SCAN_BUTTON_ACTIVE);
	private JButton selectButton = new JButton("Choose folder");
	private JTextField dirNameField = new JTextField("C:\\");
	private String currentScannedDir = "";
	private JLabel scannedVolume = new JLabel("");
	private JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
	private JPanel treeTablePanel = new JPanel( new BorderLayout() );
	private boolean inProgress = false;
	private JPanel header = null;
	private JLabel progressLbl = null;
	private JPanel helpPanel = null;

	DiskAnalyzerFrame()
	{
		super( "Disk Space Scanner" );
		createMainWindowLayout();
		setupActionListeners();
	}

    public static void main( String[] args )
    {
        AppStarter starter = new AppStarter( args );
        SwingUtilities.invokeLater( starter );
    }
	
	private class ScanThread extends Thread {

		public void run() {
	    	
	    	scanButton.setText(STOP_BUTTON_ACTIVE);
	    	scanButton.setBackground(STOP_BUTTON_COLOR);
	    	inProgress = true;
	    	
	    	showProgressGif();
	    	
	    	currentScannedDir = dirNameField.getText();
	    	diskTreeTable.scanDir(currentScannedDir);
	    	
	    	inProgress = false;
	    	
	    	treeTablePanel.repaint();
	    	treeTableView.repaint();
	    	split.repaint();
	    	
	    	disableProgressGif();
	    	
	    	scanButton.setText(SCAN_BUTTON_ACTIVE);
	    	scanButton.setBackground(SCAN_BUTTON_COLOR);

			DuplicateFinder.getInstance().countDuplicates();
			DuplicateFinder.getInstance().clear();
	    }
	}
	
	private Component getComponent()
	{
		return this;
	}
	
	
	private void disableProgressGif() {
		header.remove(progressLbl);
	}


	private void showProgressGif() {
		
		header.add(progressLbl, FlowLayout.RIGHT); 		
	}

	private void createMainWindowLayout() {
		header = new JPanel(new FlowLayout());
		header.setBackground(HEADER_BG_COLOR);
		scanButton.setBackground(SCAN_BUTTON_COLOR);
		selectButton.setBackground(CHOOSE_BUTTON_COLOR);

		ImageUtils imgUtils = new ImageUtils();
		ImageIcon progressIcon = imgUtils.createImageIcon("scan_in_progress.gif", "Disk scan in progress");

		progressLbl = new JLabel(progressIcon);

		// Build the tree table panel
		treeTablePanel.add( new JScrollPane( treeTableView ) );

		tabs.addTab( "Folder tree", treeTablePanel );

		header.add(scanButton,FlowLayout.LEFT);
		header.add(dirNameField,FlowLayout.LEFT);
		header.add(selectButton, FlowLayout.LEFT);
		header.add(scannedVolume, FlowLayout.RIGHT);
		this.helpPanel = new JPanel(new FlowLayout());

		this.helpPanel.add(new JLabel("This simple utility will scan your folder or entire drive in order to show summary disk space for each file and folder")
				,FlowLayout.LEFT);

		dirNameField.setColumns(30);
		diskTreeTable.setVolumePanel(scannedVolume);

		split.setTopComponent(header);
		split.setBottomComponent(tabs);
		split.setDividerLocation(0.1);
		split.setDividerSize(1);

		add( split );

		setSize( 1024, 768 );
		Dimension d = Toolkit.getDefaultToolkit().getScreenSize();

        // magic numbers - dont' ask ;)
		setLocation( d.width / 2 - 512, d.height/2 - 384 );
		setVisible( true );
		setDefaultCloseOperation( WindowConstants.EXIT_ON_CLOSE );
	}

	private void setupActionListeners() {
		scanButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				if (!inProgress)
				{
					(new ScanThread()).start();
				}
				else {
					diskTreeTable.stopScanning();
				}
			}
		});

		selectButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				final JFileChooser chooser = new JFileChooser();

				chooser.setCurrentDirectory(
                        chooser.getFileSystemView().getParentDirectory(
                            new File("C:\\")));

			    chooser.setDialogTitle("Select folder to scan");
			    chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				chooser.showOpenDialog(getComponent());

				dirNameField.setText(chooser.getSelectedFile().getAbsolutePath());

			}
		});

		treeTableView.addMouseListener(new MouseAdapter() {
		    @Override
		    public void mouseReleased(MouseEvent e) {
		        int row = treeTableView.rowAtPoint(e.getPoint());
		        if (row >= 0 && row < treeTableView.getRowCount()) {
		        	treeTableView.setRowSelectionInterval(row, row);
		        } else {
		        	treeTableView.clearSelection();
		        }
		        int rowindex = treeTableView.getSelectedRow();
		        if (rowindex < 0)
		            return;
		        if (e.isPopupTrigger() && e.getComponent() instanceof JXTreeTable) {
		            RightClickMenu popup = new RightClickMenu(
		            						(String) treeTableView.getModel().getValueAt(row, DiskTreeTable.TREE_PATH_INDEX));
		            popup.show(e.getComponent(), e.getX(), e.getY());
		        }
		    }
		});
	}
}

class AppStarter extends Thread
{
	private String[] args;
	
	public AppStarter( String[] args ) {
		this.args = args;

	}
	
	public void run() {
		DiskAnalyzerFrame app = new DiskAnalyzerFrame();
	}
	
}
