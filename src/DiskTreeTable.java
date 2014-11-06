import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.tree.TreePath;

import org.jdesktop.swingx.treetable.AbstractTreeTableModel;

public class DiskTreeTable extends AbstractTreeTableModel {
	private MyTreeNode myroot;
	private JLabel volumePanel;

	public DiskTreeTable() {
		myroot = new MyTreeNode("root", "Root of the tree");
	}
	
	public void scanDir(String dir)
	{
		this.treePath = new TreePath(dir);
		this.modelSupport.fireTreeStructureChanged(null);
		totalSize = 0;
		myroot.getChildren().clear();
		recursiveScan(new File(dir), myroot);
		refreshStatus();
	}

	private void refreshStatus() {
		if (this.volumePanel == null) {
			System.out.println("Volume panel is empty");
			return;
		}
		this.volumePanel.setText("Scanned volume: " + DiskSizeUtil.humanReadableSize(totalSize));
		this.modelSupport.fireTreeStructureChanged(this.treePath);
	}

	long totalSize = 0;
	int counter = 0;
	
	TreePath treePath = null;
	
	public class NodeComparator implements Comparator<MyTreeNode> {
		@Override
		public int compare(MyTreeNode o1, MyTreeNode o2) {
			if (o1.getSize() > o2.getSize() ) return -1;
			if (o1.getSize() < o2.getSize() ) return 1;
			return 0;
		}
	}
	
	public long recursiveScan(File file, MyTreeNode treeNode) {

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
				long my_size = recursiveScan(new File(file, filename), newNode);
				newNode.setDescription(DiskSizeUtil.humanReadableSize(my_size));
				newNode.setSize(my_size);
				total_size += my_size;
			}
			
			Collections.sort(treeNode.getChildren(), new NodeComparator());

			return total_size;
		}
		else 
			{
				totalSize += file.length();
				return file.length();
			}
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


