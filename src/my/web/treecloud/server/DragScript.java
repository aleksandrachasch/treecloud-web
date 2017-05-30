
package my.web.treecloud.server;


import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.apache.batik.anim.dom.SAXSVGDocumentFactory;
import org.apache.batik.util.XMLResourceDescriptor;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.svg.SVGDocument;

public class DragScript {
	

	public static Node getScript(String path) throws IOException{
		
		System.out.println("DragScript path: " + path);
		String parser = XMLResourceDescriptor.getXMLParserClassName();
		SAXSVGDocumentFactory f = new SAXSVGDocumentFactory(parser);
		InputStream stream = new FileInputStream(path);
		//String path = MyResources.INSTANCE.dragScript().getSafeUri().asString();
		//InputStream stream = getResourceAsStream(path);
		Document doc = f.createDocument("http://www.w3.org/2000/svg", "svg" ,"http://www.w3.org/2000/svg", stream);
		System.out.println(doc.getDocumentElement().getTagName());
		SVGDocument svgDoc = (SVGDocument) doc;
		System.out.println("length: " + svgDoc.getElementsByTagName("script").getLength());
		return svgDoc.getElementsByTagName("script").item(0);
	
	}
	
}
