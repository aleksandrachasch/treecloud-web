package my.web.treecloud.client;


import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

import org.vectomatic.dom.svg.OMSVGElement;
import org.vectomatic.dom.svg.utils.OMSVGParser;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Treecloud_web implements EntryPoint {
	/**
	 * The message displayed to the user when the server cannot be reached or
	 * returns an error.
	 */
	private static final String SERVER_ERROR = "An error occurred while "
			+ "attempting to contact the server. Please check your network " + "connection and try again.";

	private final DrawTreeServiceAsync drawTreeService = (DrawTreeServiceAsync) GWT.create(DrawTreeService.class);
	
	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {
		
		final HTML resPage = new HTML();
		final VerticalPanel panel = new VerticalPanel();
		panel.setSpacing(10);
		
		Anchor anchor = new Anchor();
		anchor.setHref("Manual.md.html");
		anchor.setText("Help");
		panel.add(anchor);
		
		Label textLabel = new Label("Paste a text or Concordance here: ");
		panel.add(textLabel);
		final TextArea txt = new TextArea();
		txt.setName("importedText");
		txt.setCharacterWidth(100);
		txt.setVisibleLines(10);
		panel.add(txt);
		
		final RadioButton text = new RadioButton("importType" , "Plain Text");
		final RadioButton conc = new RadioButton("importType" , "Concordance");
		text.setValue(true);
		
		Label importTypeLabel = new Label("Type of the imported Text: ");
		
		HorizontalPanel hp = new HorizontalPanel();
		hp.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		hp.add(importTypeLabel);
		hp.add(text);
		hp.add(conc);
		
		final TextBox target = new TextBox();
		target.setName("targetWord");
		Label targetLabel = new Label("Target word: ");
		HorizontalPanel targetPanel = new HorizontalPanel();
		targetPanel.setSpacing(10);
		targetPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		targetPanel.add(targetLabel);
		targetPanel.add(target);
		
		HorizontalPanel formulaPanel = new HorizontalPanel();
		formulaPanel.setSpacing(10);
		formulaPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		Label formulaLabel = new Label("Select formula: ");
		RadioButton chisquared = new RadioButton("formulaGroup" , "Chi-squared");
		chisquared.setName("chisquared");
		chisquared.setValue(true);
		RadioButton pmi = new RadioButton("formulaGroup" , "PMI");
		pmi.setName("pmi");
		RadioButton oddsratio = new RadioButton("formulaGroup" , "Odds ratio");
		oddsratio.setName("oddsRatio");
		RadioButton zscore = new RadioButton("formulaGroup" , "Z-score");
		zscore.setName("zscore");
		RadioButton log = new RadioButton("formulaGroup", "Log-likelihood");
		log.setName("log");
		RadioButton jaccard = new RadioButton("formulaGroup" , "Jaccard");
		jaccard.setName("jaccard");
		formulaPanel.add(formulaLabel);
		final ListBox langs = new ListBox();
		Label langLabel = new Label("Choose the stopwords list: ");
		final ListBox formulas = new ListBox();
		formulas.setVisibleItemCount(1);
		formulas.addItem("Chi-squared");
		formulas.addItem("PMI");
		formulas.addItem("Odds ratio");
		formulas.addItem("Z-score");
		formulas.addItem("Log-likelihood");
		formulas.addItem("Jaccard");
		formulaPanel.add(formulas);
		
		HorizontalPanel stopPanel = new HorizontalPanel();
		stopPanel.setSpacing(10);
		stopPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		final CheckBox removeStopwords = new CheckBox("Remove Stopwords ");
		removeStopwords.setName("removeStopwords");
		removeStopwords.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent e) {
				if(removeStopwords.getValue()) {
					langs.setEnabled(true);
				}else{
					langs.setItemSelected(0, true);
					langs.setEnabled(false);
				}
			}
		});
		
		
		langs.setName("language");
		langs.addItem("");
		langs.addItem("Russian");
		langs.addItem("English");
		langs.addItem("French");
		langs.addItem("Germal");
		langs.addItem("Spanish");
		langs.addItem("Portuguese");
		langs.setVisibleItemCount(1);
		langs.setEnabled(false);
		stopPanel.add(removeStopwords);
		stopPanel.add(langLabel);
		stopPanel.add(langs);
		
		HorizontalPanel paramPanel = new HorizontalPanel();
		paramPanel.setSpacing(10);
		paramPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		Label windowLabel = new Label("Window size: ");
		final TextBox windowBox = new TextBox();
		windowBox.setName("windowSize");
		windowBox.setText("10");
		Label stepLabel = new Label("Step size: ");
		final TextBox stepBox = new TextBox();
		stepBox.setName("stepSize");
		stepBox.setText("2");
		Label nbLabel = new Label("Number of words on the tree: ");
		final TextBox nbBox = new TextBox();
		nbBox.setName("numberOfWords");
		nbBox.setText("30");
		Label colorLabel = new Label("Color mode: ");
		final ListBox colorBox = new ListBox();
		colorBox.addItem("Red & blue");
		colorBox.addItem("Grayscale");
		colorBox.addItem("Target");
		colorBox.setVisibleItemCount(1);
		paramPanel.add(windowLabel);
		paramPanel.add(windowBox);
		paramPanel.add(stepLabel);
		paramPanel.add(stepBox);
		paramPanel.add(nbLabel);
		paramPanel.add(nbBox);
		paramPanel.add(colorLabel);
		paramPanel.add(colorBox);
		
		final CheckBox compare = new CheckBox("Compare the trees");
		
		panel.add(hp);
		panel.add(targetPanel);
		panel.add(formulaPanel);
		panel.add(stopPanel);
		panel.add(paramPanel);
		panel.add(compare);
		
		Button sendButton = new Button ("Draw TreeCloud!");
		sendButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				String[] res = new String [8];
				String tmp = txt.getText().replaceAll("\r\n|\n", "  aaaaaaaaa  ");
				
				if (text.getValue()) {
					res[0] = txt.getText();
					res[1] = "text";
				} else if (conc.getValue()) {
					res[0] = tmp;
					res[1] =  "concordance";
				}
				
				res[2] = target.getText();
				res[3] = formulas.getSelectedItemText();
				if (removeStopwords.getValue()) {
					res[4] = langs.getSelectedItemText();
				} else {
					res[4] = "";
				}
				
				res[5] = windowBox.getText();
				res[6] = stepBox.getText();
				res[7] = nbBox.getText();
				String color = colorBox.getSelectedItemText();
				if (color.equals("Target") && res[2].equals("")){
					Window.alert("You need to specify the Target word for using 'Target' color mode");
				}
				res[8] = color;
				
				final AsyncCallback<String> callback = new AsyncCallback<String>() {
				      public void onFailure(Throwable caught) {
				    	  
				    	  Window.alert("Could not build the tree :( Please try again with different parameters.");
				          // TODO: Do something with errors.
				        }

				        public void onSuccess(String result) {
				        	OMSVGElement svgEl = OMSVGParser.parse(result, true);
				        	Element div = resPage.getElement();
				        	if(!compare.getValue()){
				        		if(div.hasChildNodes()){
				        			div.removeAllChildren();
				        		}
				        	}
				        	
				        	div.appendChild(svgEl.getElement());
				        }
				        
				      };
				drawTreeService.drawTree(res, callback);;
			}
				
		});
		

		
		
		panel.add(sendButton);
		panel.add(resPage);
		RootPanel.get().add(panel);
	}
	
	private HorizontalPanel createDownloadPanel() {
		Label downloadLabel = new Label("Download the tree as: ");
		ListBox downloadType = new ListBox();
		downloadType.addItem("SVG");
		downloadType.addItem("JPEG");
		downloadType.addItem("PDF");
		downloadType.addItem("Newick");
		downloadType.setVisibleItemCount(1);
		
		HorizontalPanel result = new HorizontalPanel();
		result.add(downloadLabel);
		result.add(downloadType);
		return result;
	}
	
}
