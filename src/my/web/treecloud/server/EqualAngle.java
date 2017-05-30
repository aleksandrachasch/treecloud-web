package my.web.treecloud.server;

import java.util.ArrayList;

/**
 * Implementation of the EqualAngle algorithm
 * @author Aleksandra Chashchina
 *
 */


public class EqualAngle {
	
	/*
	 * Share is value assigned to each node in radians;
	 * Share=2pi/numberofleaves
	 */
	double share;
	/*
	 * count is used for EqualAngle computation
	 */
	double count = 0.0;
	
/**
 * compute angle between axis X and an edge
 * @param node root node of the tree
 */
	
	public void computeAngles(TreeNode node){
		for(TreeNode child : node.childnodes){
			if(child.isLeaf){
				child.setAngle(count + share/2);
				count += share;
			}else{
				child.setAngle(count + child.descendants*share/2);
			}
			computeAngles(child);
		}
	}
	
	/**
	 * Compute coordinates of each node on Cartesian coordinate system
	 * @param node root node of the tree
	 */
	public void computeCoordinates(TreeNode node){
		/*
		 * By default the tree starts from the point (300;300)
		 */
		if(node.isRoot){
			node.startX = 300.0;
			node.startY = 300.0;
			node.endX = 300.0;
			node.endY = 300.0;
		}
		for(TreeNode child: node.childnodes){
			child.startX = node.endX;
			child.startY = node.endY;
			child.endX = child.startX + 30.0*Math.cos(child.angle);
			child.endY = child.startY + 30.0*Math.sin(child.angle);
			computeCoordinates(child);
		}
	}
	
	/**
	 * Generate attribute of the "path" element in SVG file;
	 * Defined by coordinates of the start and end points of the path
	 * @param node root node of the tree
	 */
	public void  makeNodePath(TreeNode node){
		for(TreeNode child: node.childnodes){
			String p = "M " + child.startX + " " + child.startY
					+ " L " + child.endX + " " + child.endY;
			child.pathattr = p;
			makeNodePath(child);
		}
	}
	
	/**
	 * Compute EqualAngle algorithm for a tree
	 * @param nodes ArrayList of nodes of a tree
	 * @param ntaxa Number of leaves in the tree
	 */
	public void doEqualAngle(ArrayList<TreeNode> nodes, int ntaxa){
		
		share = 2*Math.PI/ntaxa;
		computeAngles(nodes.get(nodes.size()-1));
		computeCoordinates(nodes.get(nodes.size()-1));
		makeNodePath(nodes.get(nodes.size()-1));
	}
	
}