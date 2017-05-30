package my.web.treecloud.server;

import java.util.ArrayList;

/**
 * Data structure for a node of a tree
 * @author Aleksandra Chashchina
 *
 */

public class TreeNode {
	
	public String name;
	public int id;
	public double length;
	public ArrayList<Integer> children = new ArrayList<Integer>();
	public ArrayList<TreeNode> childnodes = new ArrayList<TreeNode>();
	TreeNode parent;
	public boolean hasSisterLeaf = false;
	public double angle;
	public int descendants;
	public boolean isLeaf = false;
	public double startX;
	public double startY;
	public double endX;
	public double endY;
	public String pathattr;
	public boolean isRoot = false;
	public String fontcolor;
	public String fontsize;
	
	public void setName(String n){
		this.name = n;
	}
	
	public void setParent(TreeNode p){
		this.parent = p;
	}
	
	public void addChild(int ch){
		this.children.add(ch);
	}
	
	public void setLength(double l){
		this.length = l;
	}
	
	public void setID(int i){
		this.id = i;
	}
	
	public void setAngle(double a){
		this.angle = a;
	}
	
	public void setAsLeaf(){
		this.isLeaf = true;
	}
	
	public void setColor(String c){
		this.fontcolor = c;
	}
	
	public void setSize(String s){
		this.fontsize = s;
	}

}
