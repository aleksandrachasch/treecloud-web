package my.web.treecloud.client;

import com.google.gwt.dom.client.Document;

import com.google.gwt.user.client.rpc.AsyncCallback;

interface DrawTreeServiceAsync {
	public void drawTree(String [] s, AsyncCallback<String> callback);

}
