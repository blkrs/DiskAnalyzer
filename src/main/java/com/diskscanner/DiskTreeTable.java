package com.diskscanner;
import java.io.File;
import java.nio.file.Path;
import java.util.*;

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
    private final NodeComparator comparator = new NodeComparator();

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
		rootNode.getChildren().clear();
		nonRecursiveScan(rootNode);
	}
	
	public void stopScanning()
	{
		scanning  = false;
	}
	
	private void refreshStatus(long scannedVolume) {
		if (this.volumePanel == null) {
			System.out.println("Volume panel is empty");
			return;
		}
		this.volumePanel.setText("Scanned volume: " + DiskSizeUtil.humanReadableSize(scannedVolume));
		try {
		    this.modelSupport.fireTreeStructureChanged(this.treePath);
		}
		catch (ArrayIndexOutOfBoundsException e){
			e.printStackTrace();
		}
	}

	public long nonRecursiveScan(DiskNode rootNode) {
		List<DiskNode> filesToScan = new LinkedList<>();
		int fileCounter = 0;
		filesToScan.add(rootNode);
		while (!filesToScan.isEmpty()) {
			if (!scanning) break;
			if (fileCounter > 100) {
				fileCounter = 0;
                refreshView(rootNode);
			}
			fileCounter++;

			DiskNode currentNode = filesToScan.remove(0);
			File file = new File(currentNode.getAbsolutePath());
			if (file.isDirectory()) {
				String[] subDirs = file.list();
				if (subDirs != null) {
                    Arrays.stream(subDirs).forEach( (fileName) -> {
                        File fileInFolder = new File(file, fileName);
                        DiskNode newNode = new DiskNode.Builder()
                                .setName(fileName)
                                .setParent(currentNode)
                                .setAbsolutePath(fileInFolder.getAbsoluteFile().toString())
                                .build();
                        currentNode.getChildren().add(newNode);
                        filesToScan.add(newNode);
                    });
				}
			} else {
				DuplicateFinder.getInstance().insert(new FileInfo(file.length()), file.getAbsolutePath());
				currentNode.increaseSize(file.length());
			}
		}
		Collections.sort(rootNode.getChildren(), comparator);
        refreshStatus(rootNode.getSize());
		return rootNode.getSize();
	}

    private void refreshView(DiskNode rootNode) {
        refreshStatus(rootNode.getSize());
        Collections.sort(rootNode.getChildren(), comparator);
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

	public class NodeComparator implements Comparator<DiskNode> {
		@Override
		public int compare(DiskNode o1, DiskNode o2) {
			if (o1.getSize() > o2.getSize() ) return -1;
			if (o1.getSize() < o2.getSize() ) return 1;
			return 0;
		}
	}
}


