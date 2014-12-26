package com.diskscanner;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import javax.jnlp.*;

import org.jdesktop.swingx.JXTreeTable;

/*
 * http://www.informit.com/guides/content.aspx?g=java&seqNum=528
 */



public class DiskAnalyzer extends JFrame 
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -2129525315141277648L;

	private JTabbedPane tabs = new JTabbedPane();
	
	private DiskTreeTable diskTreeTable = new DiskTreeTable();
	private JXTreeTable treeTableView = new JXTreeTable( diskTreeTable );
	
	private JButton scanButton = new JButton("Scan");
	
	private JButton selectButton = new JButton("Choose folder");
	
	private JTextField dirNameField = new JTextField("C:\\");
	
	private JLabel scannedVolume = new JLabel("");
	
	JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
	
	JPanel treeTablePanel = new JPanel( new BorderLayout() );
	
	private boolean inProgress = false;
	
	JPanel header = null;
	
	JLabel progressLbl = null;
	
	public class ScanThread extends Thread {

	    public void run() {
	    	
	    	scanButton.setText("Stop");
	    	inProgress = true;
	    	
	    	showProgressGif();
	    	
	    	diskTreeTable.scanDir(dirNameField.getText());
	    	
	    	inProgress = false;
	    	
	    	treeTablePanel.repaint();
	    	treeTableView.repaint();
	    	split.repaint();
	    	
	    	disableProgressGif();
	    	
	    	scanButton.setText("Scan");
	    }
	}
	
	public Component getComponent()
	{
		return this;
	}
	
	
	public void disableProgressGif() {
		header.remove(progressLbl);
	}


	public void showProgressGif() {
		
		header.add(progressLbl, FlowLayout.RIGHT); 		
	}


	public DiskAnalyzer()
	{
		super( "Disk space analyzer" );
		
		
		
		header = new JPanel(new FlowLayout());
		
		ImageUtils imgUtils = new ImageUtils();
		ImageIcon progressIcon = imgUtils.createImageIcon("scan_in_progress.gif","Disk scan in progress");
		
		progressLbl = new JLabel(progressIcon);
		
		
		// Build the tree table panel
		treeTablePanel.add( new JScrollPane( treeTableView ) );
		
		tabs.addTab( "Folder tree", treeTablePanel );
		
		
		
		header.add(scanButton,FlowLayout.LEFT);		
		header.add(dirNameField,FlowLayout.LEFT);
		header.add(selectButton, FlowLayout.LEFT);
		
		header.add(scannedVolume, FlowLayout.RIGHT);
		
		dirNameField.setColumns(30);	
		diskTreeTable.setVolumePanel(scannedVolume);
		
		
		
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
		        int r = treeTableView.rowAtPoint(e.getPoint());
		        if (r >= 0 && r < treeTableView.getRowCount()) {
		        	treeTableView.setRowSelectionInterval(r, r);
		        } else {
		        	treeTableView.clearSelection();
		        }

		        int rowindex = treeTableView.getSelectedRow();
		        if (rowindex < 0)
		            return;
		        
		        if (e.isPopupTrigger() && e.getComponent() instanceof JXTreeTable ) {
		            JPopupMenu popup = new JPopupMenu();
		            popup.add("NEw string");
		            try {
						popup.add((String) treeTableView.getModel().getValueAt(r,DiskTreeTable.TREE_PATH_INDEX));
						
					} catch (SecurityException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
		            popup.show(e.getComponent(), e.getX(), e.getY());
		        }
		    }
		});
			
		//header.setBounds(new Rectangle(0,0,500,100));
		/*
		add( header, BorderLayout.PAGE_START);
		
		add( tabs, BorderLayout.);
		*/
		
		split.setTopComponent(header);
		split.setBottomComponent(tabs);
		split.setDividerLocation(0.1);
		split.setDividerSize(1);
		
		add( split );
		
		setSize( 1024, 768 );
		Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
		setLocation( d.width / 2 - 512, d.height/2 - 384 );
		setVisible( true );
		setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
	}
	
	
	public static void main( String[] args )
	{
		AppStarter starter = new AppStarter( args );
		SwingUtilities.invokeLater( starter );
	}
}

class AppStarter extends Thread
{
	private String[] args;
	
	public AppStarter( String[] args )
	{
		this.args = args;
	}
	
	public void run()
	{
		DiskAnalyzer example = new DiskAnalyzer();
	}
	
}