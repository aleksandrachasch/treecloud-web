package my.web.treecloud.server;

import java.util.ArrayList;
import java.util.Collections;


/**
 * This class is used to compute a tree
 * from a distance matrix using NeighborJoining algorithm
 * (Saitou, Nei 1987)
 * 
 * @author Aleksandra Chashchina
 *
 */

public class NeighborJoining {
	
	public ArrayList<TreeNode> allnodes = new ArrayList<TreeNode>();
	
	public NeighborJoining(ArrayList<TreeNode> leaves){
		this.allnodes = leaves;
	}
	
	/**
	 * Compute a QMatrix from distance matrix
	 * @param distancematrix
	 * @return Lower triangular QMatrix; 
	 *         diagonal and higher triangle are filled with zeros
	 */
	
	
	private ArrayList<ArrayList<Double>> computeQMatrix(ArrayList<ArrayList<Double>> distancematrix){
		ArrayList<ArrayList<Double>> qmatrix = new ArrayList<ArrayList<Double>>();
		/*
		 * compute QMatrix row by row
		 */
		for(int i=0; i<distancematrix.size(); i++){
			ArrayList<Double> row = new ArrayList<Double>();
			/*
			 * fill higher triangle and diagonal with extremely high value
			 */
			for(int j=0; j<distancematrix.size();j++){
				if(i<=j){
					row.add(10000.0);}
				/*
				 * calculate Q-value for each distance using special formula
				 */
				else{
					double qvalue = (distancematrix.size()-2)*(distancematrix.get(i).get(j))
							- getDistanceSum(distancematrix , i) 
							- getDistanceSum(distancematrix , j);
					row.add(qvalue);}
			}
			qmatrix.add(row);
		}
		
		return qmatrix; 
	}
	
	/**
	 * Get the index of the element with 
	 * minimum Q-value from QMatrix
	 * @param qmatrix
	 * @return an array of two elements: 
	 * 1: index i of element(row in matrix);
	 * 2:index j of element(column in matrix)
	 */
	
	private int [] getMinQValue(ArrayList<ArrayList<Double>> qmatrix){
		int [] res = new int [2];
		ArrayList<Double> mins = new ArrayList<Double>();
		/*
		 * Search for the minimum value in every row and add it to an array
		 */
		for(int i=0; i<qmatrix.size(); i++){
			mins.add(Collections.min(qmatrix.get(i)));
				}
		/*
		 * Search for the minimum value in the array of minimums
		 */
		double minQ = Collections.min(mins);
		/*
		 * Index of the minimum value in the array of minimums
		 * equals to the index of the row in the matrix
		 * (equals index i)
		 */
		res[0] = mins.indexOf(minQ);
		/*
		 * Search for the second index in the row
		 * (index j)
		 */
		for(double v : qmatrix.get(res[0])){
			if(v==minQ){
				res[1] = qmatrix.get(res[0]).indexOf(v);
			}
		}		
		return res;
	}
	
	/**
	 * Compute sum of distances of one row in a matrix
	 * (used in computing QMatrix, distance from the first of the joined
	 * taxa to the new node)
	 * @param distancematrix
	 * @param i - index of a row
	 * @return sum
	 */
	private Double getDistanceSum(ArrayList<ArrayList<Double>> distancematrix , int i){
		
		double sum = 0.0;
		
		for(int k=0; k<distancematrix.size(); k++){
			sum += distancematrix.get(i).get(k);
		}
		return sum;
	}
	
	/**
	 * Compute distance from the new node to one
	 * of two joined taxa using special formula.
	 * @param distancematrix
	 * @param i - index of the first joined taxon (index of row in matrix);
	 * @param j - index of the second joined taxon (index of column in matrix)
	 * @return double
	 */
	
	private Double computeDistNew(ArrayList<ArrayList<Double>> distancematrix, int i, int j){
		
		return (0.5*distancematrix.get(i).get(j)) 
				+ (1D/(2*(distancematrix.size()-2))
				*(getDistanceSum(distancematrix, i)
				- getDistanceSum(distancematrix, j)));
	}
	
	/**
	 * Compute distance from a taxon to the new node using special formula
	 * @param distancematrix
	 * @param f - index of the first joined taxon (index of row in matrix);
	 * @param g - index of the second joined taxon (index of column in matrix);
	 * @param k - index of the taxon of interest
	 * @return
	 */
	
	private Double computeDistOld(ArrayList<ArrayList<Double>> distancematrix, int f, int g , int k){
		
		return 0.5*(distancematrix.get(f).get(k)
				+ distancematrix.get(g).get(k)
				- distancematrix.get(f).get(g));
	}
	
	/**
	 * Execute recursively NJ-algorithm on given
	 * distance matrix. End when the tree is fully
	 * resolved (i.e., when there are two taxa going out from the
	 * "parent" node)
	 * Compute a tree from distance matrix.
	 * @param distancematrix
	 * @param taxanames - a list of taxa(words)
	 * @return Newick string 
	 */
	
	public String computeNJTree(ArrayList<ArrayList<Double>> distancematrix , ArrayList<String> taxanames){
		//System.out.println("New step...");
		//System.out.println("Matrix size: " + distancematrix.size());
		//System.out.println("words: " + taxanames);
		/**
		 * If the size of distance matrix is 2 (i.e., only 2 taxa comes from the "parent"
		 * node) => the tree is fully resolved.
		 * Stop the algorithm and return the Newick string
		 */
		if(distancematrix.size()==2){
			
			/*
			 * At this stage we have distance matrix that looks like
			 *   |a |b
			 * a | 0|d
			 * b |d |0
			 * 
			 * Where d is some distance between a and b;
			 * b actually is a node from which all other joined taxa come from;
			 * it is the result of previous algorithm steps. That is, on this stage
			 * the tree is binary (i.e., fully resolved), so we don't need to do any more 
			 * steps. The only thing is we need to write the distance between the taxa a and b.
			 */
			
			String finaltaxanames = "(" + taxanames.get(1) + ":" + distancematrix.get(0).get(1) + "," + taxanames.get(0) 
			 + ":0.0);" ;
			
			TreeNode finalnode = new TreeNode();
			finalnode.setName(finaltaxanames);
			finalnode.setAngle(2*Math.PI);
			for(TreeNode n : allnodes){
				if(n.name.equals(taxanames.get(1))){
					finalnode.addChild(allnodes.indexOf(n));
					finalnode.childnodes.add(n);
					if(n.isLeaf){
						finalnode.descendants += 1;
					}else{
						finalnode.descendants += n.descendants;
					}
				}else if(n.name.equals(taxanames.get(0))){
					finalnode.addChild(allnodes.indexOf(n));
					finalnode.childnodes.add(n);
					if(n.isLeaf){
						finalnode.descendants += 1;
					}else{
						finalnode.descendants += n.descendants;
					}
				}
				
			}
			
			allnodes.add(finalnode);
			finalnode.setID(allnodes.indexOf(finalnode));
			finalnode.isRoot = true;
			return finaltaxanames;
		}
		/*
		 * Get QMatrix for the current distance matrix
		 */
		ArrayList<ArrayList<Double>> qmatrix = computeQMatrix(distancematrix);
		/*
		 * Get the index of the taxa with the smallest Q-value
		 */
		int taxaA = getMinQValue(qmatrix)[0];
		int taxaB = getMinQValue(qmatrix)[1];
		/*
		 * Initialize new list of taxa
		 */
		ArrayList<String> newtaxanames = new ArrayList<String>(taxanames.size()-2);
		
		/*
		 * Join two taxa with the smallest Q-value: add distance from each of the joined
		 * taxa to the new node
		 */
		
		String newtaxa = "(" + taxanames.get(taxaA) + ":" + computeDistNew(distancematrix, taxaA, taxaB) 
		+ "," + taxanames.get(taxaB) + ":" 
		+ (distancematrix.get(taxaA).get(taxaB)-computeDistNew(distancematrix, taxaA, taxaB)) + ")";
		
		
		TreeNode newnode = new TreeNode();
		
		newnode.setName(newtaxa);
		//System.out.println("taxaA: " + taxanames.get(taxaA));
		//System.out.println("taxaB: " + taxanames.get(taxaB));
		for(TreeNode n : allnodes){
			if(n.name.equals(taxanames.get(taxaA))){
				newnode.addChild(allnodes.indexOf(n));
				newnode.childnodes.add(n);
				if(n.isLeaf){
					newnode.descendants += 1;
				}else{
					newnode.descendants += n.descendants;
				}
				
			}else if(n.name.equals(taxanames.get(taxaB))){
				newnode.addChild(allnodes.indexOf(n));
				newnode.childnodes.add(n);
				if(n.isLeaf){
					newnode.descendants += 1;
				}else{
					newnode.descendants += n.descendants;
				}
			}
			
		}
		
		allnodes.add(newnode);
		newnode.setID(allnodes.indexOf(newnode));
		if(newnode.childnodes.get(0).isLeaf && newnode.childnodes.get(1).isLeaf){
			newnode.childnodes.get(0).hasSisterLeaf = true;
		}
		
	
		
		/*
		 * Make new list of taxa
		 */
		
		for(String w : taxanames){
			if(taxanames.indexOf(w)!=taxaA & taxanames.indexOf(w)!=taxaB){
				newtaxanames.add(w);
			}else if(taxanames.indexOf(w) == taxaA){
				newtaxanames.add(newtaxa);
			}
		}
		/*
		 * Compute distance matrix for new list of taxanames
		 */
		ArrayList<Double> row = new ArrayList<Double>();
		/*
		 * Initialize new distance matrix
		 */
		ArrayList<ArrayList<Double>> newdistancematrix = new ArrayList<ArrayList<Double>>(Collections.nCopies(newtaxanames.size(), row));
		/*
		 * Firstly, we make a row with the distances from the new node to all other nodes
		 */
		ArrayList<Double> newrow = new ArrayList<Double>(Collections.nCopies(newtaxanames.size(), 0.0));
		/*
		 * Get the index of the new node (two joined taxa) from the new list of taxanames
		 */
		int newindex = newtaxanames.indexOf(newtaxa);
		/*
		 * Compute distance from the new node to all other old taxa
		 */
		for(int k=0; k<newtaxanames.size(); k++){
			if(k == newindex){
				newrow.set(k, 0.0);
			}else{
			double newdist = computeDistOld(distancematrix,taxaA,taxaB,taxanames.indexOf(newtaxanames.get(k)));
			
			newrow.set(k, newdist);
			}
		}
		/*
		 * Set the row to the new distance matrix
		 */
		newdistancematrix.set(newindex, newrow);
		//System.out.println(newdistancematrix);
		
		/*
		 * Compute all the rest values of the new distance matrix
		 */

		for(int i=0; i<newtaxanames.size(); i++){
			/*
			 * If the index of the row equals the index of the new node, it is skipped
			 * because we have just computed its distances above
			 */
			if(i==newindex){
				continue;
			}
			/*
			 * Compute distance marix row by row
			 */
			ArrayList<Double> rows = new ArrayList<Double>(Collections.nCopies(newtaxanames.size(), 0.0));
			for(int j=0; j<newtaxanames.size(); j++){
				/*
				 * if taxon is not a new node (two old joined taxa), then
				 * retrieve the distance from the old distance matrix and
				 * add it to the new distance matrix
				 */
				if(taxanames.contains(newtaxanames.get(i)) && taxanames.contains(newtaxanames.get(j))){
					int oldI = taxanames.indexOf(newtaxanames.get(i));
					int oldJ = taxanames.indexOf(newtaxanames.get(j));
					double dist = distancematrix.get(oldI).get(oldJ);
					rows.set(j, dist);
				}
				/*
				 * If the column index equals to the index of the new node, copy
				 * the distance from the row of the new node
				 */
				else if(j== newindex){
					rows.set(j, newdistancematrix.get(j).get(i));
				}
				}

		/*
		 * Set the row to its position in the new distance matrix
		 */
		newdistancematrix.set(i,rows);	
		}
		/*
		 * Perform new step of the algorithm using new distance matrix and new list
		 * of taxanames
		 */
		return computeNJTree(newdistancematrix , newtaxanames);
	}
	
	
	}
