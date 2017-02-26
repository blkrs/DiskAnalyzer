package com.diskscanner;
import java.io.File;
import java.util.Collections;
import java.util.Comparator;

import javax.swing.JLabel;
import javax.swing.tree.TreePath;

import com.diskscanner.duplicates.DuplicateFinder;
import com.diskscanner.duplicates.FileInfo;
import org.jdesktop.swingx.treetable.AbstractTreeTableModel;

public class DiskTreeTable extends AbstractTreeTableModel {
	public static final int TREE_PATH_INDEX = 3;
	private DiskNode rootNode;
	private JLabel volumePanel;
	private boolean scanning = false;
	private long totalSize = 0;
	private int counter = 0;

	private TreePath treePath = null;

	public DiskTreeTable() {

	}
	
	public void scanDir(String dir)
	{
		scanning = true;

		rootNode = new DiskNode.Builder().setName("root")
					.setDescription("Root of the tree")
					.setParent(null)
					.setAbsolutePath(dir)
					.build();

		this.treePath = new TreePath(rootNode);
		this.modelSupport.fireTreeStructureChanged(null);
		
		totalSize = 0;
		rootNode.getChildren().clear();
		
		
		recursiveScan(new File(dir), rootNode);
		refreshStatus();
	}
	
	public void stopScanning()
	{
		scanning  = false;
	}
	
	private void refreshStatus() {
		if (this.volumePanel == null) {
			System.out.println("Volume panel is empty");
			return;
		}
		this.volumePanel.setText("Scanned volume: " + DiskSizeUtil.humanReadableSize(totalSize));
		
		try {
		this.modelSupport.fireTreeStructureChanged(this.treePath);
		}
		catch (ArrayIndexOutOfBoundsException e){
			
		}
	}


	
	public class NodeComparator implements Comparator<DiskNode> {
		@Override
		public int compare(DiskNode o1, DiskNode o2) {
			if (o1.getSize() > o2.getSize() ) return -1;
			if (o1.getSize() < o2.getSize() ) return 1;
			return 0;
		}
	}
	
	public long recursiveScan(File file, DiskNode treeNode) {
		if (!scanning) return 0;
		//System.out.println(file.getAbsoluteFile());
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
			for (String fileName : subDirs) {
				File newFile = new File(file, fileName);
				DiskNode newNode = new DiskNode.Builder()
						             .setName(fileName)
									.setParent(treeNode)
									.setAbsolutePath(newFile.getAbsoluteFile().toString())
						            .build();
				treeNode.getChildren().add(newNode);
				long my_size = recursiveScan(newFile, newNode);
				newNode.setDescription(DiskSizeUtil.humanReadableSize(my_size));
				newNode.setSize(my_size);
				total_size += my_size;
			}
			Collections.sort(treeNode.getChildren(), new NodeComparator());
			return total_size;
		}
		else 
			{
				DuplicateFinder.getInstance().insert(new FileInfo(file.length()),file.getAbsolutePath());
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
			return "Children";
		default:
			return "Unknown";
		}
	}

	@Override
	public Object getValueAt(Object node, int column) {
		
		DiskNode treenode = (DiskNode) node;
		switch (column) {
		case 0:
			return treenode.getName();
		case 1:
			return treenode.getDescription();
		case 2:
			return treenode.getChildren().size();
		case TREE_PATH_INDEX:
			return treenode.getAbsolutePath();
		default:
			return "Unknown";
		}
	}

	@Override
	public Object getChild(Object node, int index) {
		DiskNode treenode = (DiskNode) node;
		return treenode.getChildren().get(index);
	}

	@Override
	public int getChildCount(Object parent) {
		DiskNode treenode = (DiskNode) parent;
		return treenode.getChildren().size();
	}

	@Override
	public int getIndexOfChild(Object parent, Object child) {
		DiskNode treenode = (DiskNode) parent;
		for (int i = 0; i > treenode.getChildren().size(); i++) {
			if (treenode.getChildren().get(i) == child) {
				return i;
			}
		}
		return 0;
	}

	public boolean isLeaf(Object node) {
		DiskNode treenode = (DiskNode) node;
		if (treenode.getChildren().size() > 0) {
			return false;
		}
		return true;
	}

	@Override
	public Object getRoot() {
		return rootNode;
	}

	public void setVolumePanel(JLabel scannedVolume) {
		this.volumePanel = scannedVolume;
		
	}
}


