package my.web.treecloud.server;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import org.w3c.dom.Document;


public class Tree {
	
	public String corpuspath;
	int numberoftaxa = 30;
	public int winSize = 10;
	public int step = 2;
	public String language;
	public ArrayList<String> words = new ArrayList<String>();
	public ArrayList<ArrayList<Double>> distancematrix = new ArrayList<ArrayList<Double>>();
	public String newick;
	public ArrayList<TreeNode> nodes = new ArrayList<TreeNode>();
	private ArrayList<TreeNode> leaves = new ArrayList<TreeNode>();
	public String importedtext;
	public String alphabet;
	public String importtype;
	public String statsoutput;
	public String stopwordsPath;
	public boolean removestopwords;
	public String locatetarget;
	public String formula;
	public String servletpath;
	int minNmbOfOccur;
	public String colormode = "Red & blue";
	String edgecolor;
	Document svgDoc; 
	
	public void setCorpusPath(String p){
		this.corpuspath = p;
	}
	
	public void setSvgDoc(Document svg){
		this.svgDoc = svg;
	}
	
	public void setAlphabetPath(String a){
		this.alphabet = a;
	}
	
	public void setLanguage(String lang){
		this.language = lang;
	}
	
	public void setMinNbOccur(int n){
		this.minNmbOfOccur = n;
	}
	
	public void setNumberOfTaxa(int n){
		this.numberoftaxa = n;
	}
	
	public void setLocateTarget(String l){
		this.locatetarget = l;
	}
	
	public void setColorMode(String color){
		this.colormode = color;
	}
	
	public ArrayList<TreeNode> getNodeList(){
		return this.nodes;
	}
	
	public String getNewickTree(){
		return this.newick;
	}
	
	public ArrayList<String> getTreeWords(){
		return this.words;
	}
	
	public int getNumberOfTaxa(){
		return this.numberoftaxa;
	}
	
	public Document getSvgDoc(){
		return this.svgDoc;
	}
	
	
	public InputStream getStopWordsFile() throws FileNotFoundException{
		
			InputStream stream = new FileInputStream(stopwordsPath);
			return stream;
	}
		

	public void setDistanceMatrix() throws IOException, InterruptedException{
		
		System.out.println("importtype: " + importtype);
		System.out.println("winsize: " + winSize);
		System.out.println("stepSize: " + step);
		System.out.println("nbwords: " + numberoftaxa);
		System.out.println("Computing distance matrix...");
        ConcordanceText txt = new ConcordanceText();
        txt.locatetarget = locatetarget;
        if(importtype.equals("text")) {
        	if(removestopwords){
        			leaves = txt.computeMatrixText(importedtext, getStopWordsFile(), numberoftaxa, colormode, winSize, step, formula);
        	}else{
        		leaves = txt.computeMatrixText(importedtext, null, numberoftaxa, colormode, winSize, step, formula);
        	}
        }else if(importtype.equals("Unitex")){
        	if(removestopwords){
        		leaves = txt.computeMatrixNoStats(importedtext, getStopWordsFile(), numberoftaxa, colormode, formula);
        	}else{
        		leaves = txt.computeMatrixNoStats(importedtext, null, numberoftaxa, colormode, formula);
        	}
        }else if(importtype.equals("concordance")){
        	if(removestopwords){
        		leaves = txt.computeMatrixConcordance(importedtext, getStopWordsFile(), numberoftaxa, colormode, formula);
        	}else{
        		leaves = txt.computeMatrixConcordance(importedtext, null, numberoftaxa, colormode, formula);
        	}
        }
		distancematrix = txt.getMatrix();
		edgecolor = txt.getEdgeColor();
		words = txt.getLabelList();
	}
	
	public void performNJ(){
		System.out.println("Performing NeighborJoining algorithm...");
		NeighborJoining nj = new NeighborJoining(leaves);
		newick = nj.computeNJTree(distancematrix, words);
		nodes = nj.allnodes;
	}
	
	public void performEqualAngle(){
		System.out.println("Performing EqualAngle algorithm...");
		EqualAngle ea = new EqualAngle();
		ea.doEqualAngle(nodes, numberoftaxa);
	}
	
	public void drawTree(String stream) throws IOException{
		System.out.println("Drawing the tree...");
		setSvgDoc(TreeSVG.drawTreeCloud(nodes, edgecolor, stream));
	}

}