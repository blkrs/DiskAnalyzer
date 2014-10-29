import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import javax.swing.JLabel;

import org.jdesktop.swingx.treetable.AbstractTreeTableModel;

public class DiskTreeTable extends AbstractTreeTableModel {
	private MyTreeNode myroot;
	private JLabel volumePanel;

	public DiskTreeTable() {
		myroot = new MyTreeNode("root", "Root of the tree");
	}
	
	public void scanDir(String dir)
	{
		this.modelSupport.fireTreeStructureChanged(null);
		totalSize = 0;
		myroot.getChildren().clear();
		displayIt(new File(dir), myroot);
		refreshStatus();
	}

	private void refreshStatus() {
		if (this.volumePanel == null) {
			System.out.println("Volume panel is empty");
			return;
		}
		this.volumePanel.setText("Scanned volume: " + humanReadableSize(totalSize));
		this.modelSupport.fireTreeStructureChanged(null);
	}

	long totalSize = 0;
	
	int counter = 0;
	
	public long displayIt(File file, MyTreeNode treeNode) {

		System.out.println(file.getAbsoluteFile());
		
		if (counter > 100)
		{
			counter = 0;
			refreshStatus();
		}
		counter ++;
			

		if (file.isDirectory()) {
			long total_size = 0;
			String[] subDirs = file.list();
			if (subDirs == null){ return total_size; };
			for (String filename : subDirs) {
				MyTreeNode newNode = new MyTreeNode(filename,"");
				treeNode.getChildren().add(newNode);
				long my_size = displayIt(new File(file, filename), newNode);
				newNode.setDescription(humanReadableSize(my_size));
				newNode.setSize(my_size);
				total_size += my_size;
			}
			
			treeNode.getChildren().sort(new Comparator<MyTreeNode>() {

				@Override
				public int compare(MyTreeNode o1, MyTreeNode o2) {
					if (o1.getSize() > o2.getSize() ) return -1;
					if (o1.getSize() < o2.getSize() ) return 1;
					return 0;
				}
			});
			return total_size;
		}
		else 
			{
				totalSize += file.length();
				return file.length();
			}
	}
	
	static long kilo = 1024;
	static long mega = kilo*kilo;
	static long giga = mega * kilo;
	static long tera = giga * kilo;

	private String humanReadableSize(long my_size) {
		String txt = "";
		
		
		if (my_size < kilo)
		{
			txt = my_size + " bytes";
		} else if (my_size < mega)
		{
			txt = (long)(my_size/kilo) + "KB"; 
		}else if (my_size < giga)
		{
			txt = (long)(my_size/mega) + "MB"; 
		}else if (my_size < tera)
		{
			txt = (long)(my_size/giga) + "GB"; 
		}else 
		{
			txt = (long)(my_size/tera) + "TB"; 
		}
			
		return txt;
	}

	@Override
	public int getColumnCount() {
		return 3;
	}

	@Override
	public String getColumnName(int column) {
		switch (column) {
		case 0:
			return "FileName";
		case 1:
			return "Size";
		case 2:
			return "Subfolders";
		default:
			return "Unknown";
		}
	}

	@Override
	public Object getValueAt(Object node, int column) {
		
		MyTreeNode treenode = (MyTreeNode) node;
		switch (column) {
		case 0:
			return treenode.getName();
		case 1:
			return treenode.getDescription();
		case 2:
			return treenode.getChildren().size();
		default:
			return "Unknown";
		}
	}

	@Override
	public Object getChild(Object node, int index) {
		MyTreeNode treenode = (MyTreeNode) node;
		return treenode.getChildren().get(index);
	}

	@Override
	public int getChildCount(Object parent) {
		MyTreeNode treenode = (MyTreeNode) parent;
		return treenode.getChildren().size();
	}

	@Override
	public int getIndexOfChild(Object parent, Object child) {
		MyTreeNode treenode = (MyTreeNode) parent;
		for (int i = 0; i > treenode.getChildren().size(); i++) {
			if (treenode.getChildren().get(i) == child) {
				return i;
			}
		}

		// TODO Auto-generated method stub
		return 0;
	}

	public boolean isLeaf(Object node) {
		MyTreeNode treenode = (MyTreeNode) node;
		if (treenode.getChildren().size() > 0) {
			return false;
		}
		return true;
	}

	@Override
	public Object getRoot() {
		return myroot;
	}

	public void setVolumePanel(JLabel scannedVolume) {
		this.volumePanel = scannedVolume;
		
	}
}

class MyTreeNode {
	private String name;
	private String description;
	private List<MyTreeNode> children = new ArrayList<MyTreeNode>();
	
	private long size;

	public MyTreeNode() {
	}

	public MyTreeNode(String name, String description) {
		this.name = name;
		this.description = description;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public List<MyTreeNode> getChildren() {
		return children;
	}

	public String toString() {
		return "MyTreeNode: " + name + ", " + description;
	}

	public long getSize() {
		return size;
	}

	public void setSize(long size) {
		this.size = size;
	}
}
