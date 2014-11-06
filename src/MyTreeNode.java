import java.util.ArrayList;
import java.util.List;

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