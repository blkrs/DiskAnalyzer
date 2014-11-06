import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

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
	private JXTreeTable treeTable = new JXTreeTable( diskTreeTable );
	
	private JButton scanButton = new JButton("Scan");
	
	private JTextField dirNameField = new JTextField("C:\\");
	
	private JLabel scannedVolume = new JLabel("");
	
	JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
	
	JPanel treeTablePanel = new JPanel( new BorderLayout() );
	
	public class ScanThread extends Thread {

	    public void run() {
	    	diskTreeTable.scanDir(dirNameField.getText());
	    	treeTablePanel.repaint();
	    	treeTable.repaint();
	    	split.repaint();
	    }
	}
	
	
	public DiskAnalyzer()
	{
		super( "Disk space analyzer" );
		
		JPanel header = new JPanel(new FlowLayout());
		
		// Build the tree table panel
		treeTablePanel.add( new JScrollPane( treeTable ) );
		
		tabs.addTab( "Folder tree", treeTablePanel );
		
		header.add(new JFileChooser(), FlowLayout.LEFT);
		
		header.add(scanButton,FlowLayout.LEFT);		
		header.add(dirNameField,FlowLayout.LEFT);
		header.add(scannedVolume, FlowLayout.RIGHT);
		
		
		
		dirNameField.setColumns(30);
		
		diskTreeTable.setVolumePanel(scannedVolume);
		
		
		
		scanButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {	
				(new ScanThread()).start();
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