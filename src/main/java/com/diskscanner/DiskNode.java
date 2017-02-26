package com.diskscanner;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
class DiskNode {
	private String name;
	private String description;
	private List<DiskNode> children = new ArrayList<DiskNode>();
	private DiskNode myParent;
	private long size = 0;
	private String absolutePath;

	private DiskNode(String name, String description, DiskNode parent, String absolutePath) {
		this.name = name;
		this.description = description;
		this.myParent = parent;
		this.absolutePath = absolutePath;
	}

	public void increaseSize(long addedSize) {
		this.size += addedSize;
		setDescription(DiskSizeUtil.humanReadableSize(this.size));
		if (myParent != null) {
			myParent.increaseSize(addedSize);
		}
	}

	public String toString() {
		return "MyTreeNode: " + name + ", " + description;
	}

	@Override
	public int hashCode() {
		return absolutePath.hashCode();
	}

	public static class Builder {
		private String name;
		private String description;
		private DiskNode parent;
		private String absolutePath;

		public DiskNode build() {
			return new DiskNode(name,description,parent,absolutePath);
		}

		public Builder setName(String name) {
			this.name = name;
			return this;
		}

		public Builder setDescription(String description) {
			this.description = description;
			return this;
		}

		public Builder setParent(DiskNode parent) {
			this.parent = parent;
			return this;
		}

		public Builder setAbsolutePath(String path) {
			this.absolutePath = path;
			return this;
		}
	}

}