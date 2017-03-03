package com.diskscanner;
import org.jdesktop.swingx.treetable.AbstractTreeTableModel;

import javax.swing.*;
import javax.swing.tree.TreePath;

public class DiskTreeTable extends AbstractTreeTableModel {
	public static final int TREE_PATH_INDEX = 3;
	private DiskNode rootNode;
	private JLabel volumePanel;

	private TreePath treePath = null;

    private DiskScanner scanner;

	public DiskTreeTable() {
        scanner = new DiskScanner(size -> this.refreshStatus(size));
	}

    public void scanDir(String dir) {
        rootNode = new DiskNode.Builder().setName("root")
                .setDescription("Root of the tree")
                .setParent(null)
                .setAbsolutePath(dir)
                .build();
        this.treePath = new TreePath(rootNode);
        this.modelSupport.fireTreeStructureChanged(null);
        rootNode.getChildren().clear();
        scanner.nonRecursiveScan(rootNode);
    }

	public void stopScanning() {
	    scanner.stop();
	}

	public void refreshView(TreePath treePath) {
        this.modelSupport.fireTreeStructureChanged(treePath);
    }
	
	private void refreshStatus(Long scannedVolume) {
		if (this.volumePanel == null) {
			System.out.println("Volume panel is empty");
			return;
		}
		this.volumePanel.setText("Scanned volume: " + DiskSizeUtil.humanReadableSize(scannedVolume));
		try {
		    refreshView(treePath);
		}
		catch (ArrayIndexOutOfBoundsException e){
			e.printStackTrace();
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
	@Override
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


