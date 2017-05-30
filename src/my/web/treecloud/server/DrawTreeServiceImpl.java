package my.web.treecloud.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.URL;

import javax.servlet.ServletContext;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import my.web.treecloud.client.DrawTreeService;


public class DrawTreeServiceImpl extends RemoteServiceServlet implements DrawTreeService{
	
	public String drawTree(String [] params) {
		
		Tree t = new Tree();
		
		t.importedtext = params[0];
		t.importtype = params[1];
		t.locatetarget = params[2];
		t.formula = params[3];
		if(params[4].equals("")){
			t.removestopwords = false;
		}else{
			t.removestopwords = true;
			if(params[4].equals("English") | params[4].equals("French")) {
				t.stopwordsPath = getServletContext().getRealPath("/StoplistEnglishFrench.txt");
			}else if(params[4].equals("German")) {
				t.stopwordsPath = getServletContext().getRealPath("/StoplistGerman.txt");
			}else if(params[4].equals("Russian")) {
				t.stopwordsPath = getServletContext().getRealPath("/StoplistRussian.txt");
			}else if(params[4].equals("Portuguese")) {
				t.stopwordsPath = getServletContext().getRealPath("/StoplistPortuguese.txt");
			}
		}
		t.winSize = Integer.parseInt(params[5]);
		t.step = Integer.parseInt(params[6]);
		t.numberoftaxa = Integer.parseInt(params[7]);
		t.colormode = params[8];
		
		String path = getServletContext().getRealPath("/script.svg");
		
		
		try {
			t.setDistanceMatrix();
		} catch (IOException | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		t.performNJ();
		t.performEqualAngle();
		try {
			t.drawTree(path);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		TransformerFactory tf = TransformerFactory.newInstance();

		StringWriter writer = new StringWriter();
		try {
			Transformer tr = tf.newTransformer();
			tr.transform(new DOMSource(t.svgDoc),new StreamResult(writer));
		    tr.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
		} catch (TransformerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String output = writer.getBuffer().toString().replaceAll("\n|\r", "");
		return output;	
	}

}