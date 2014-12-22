import java.util.ArrayList;
import java.util.List;

class DiskNode {
	private String name;
	private String description;
	private List<DiskNode> children = new ArrayList<DiskNode>();
	
	DiskNode myParent;
	
	private long size;

	private DiskNode() {
	}

	public DiskNode(String name, String description, DiskNode parent) {
		this.name = name;
		this.description = description;
		this.myParent = parent;
	}
	
	public DiskNode getParent()
	{
		return myParent;
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

	public List<DiskNode> getChildren() {
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