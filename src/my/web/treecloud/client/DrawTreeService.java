package my.web.treecloud.client;

import com.google.gwt.dom.client.Document;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import java.io.Serializable;

import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;

@RemoteServiceRelativePath("draw")
public interface DrawTreeService extends RemoteService {
	
	public String drawTree(String [] file);

}
