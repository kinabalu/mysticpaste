package com.mysticcoders.mysticpaste.popup.actions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.eclipse.core.resources.IFile;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Monitor;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolTip;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.texteditor.ITextEditor;

/**
 * Implements an Action Delegate which responds to a context menu item click<br/>
 * <br/>
 * The delegate posts the selected text to the Mystic Paste webapplication, then places the url for the page
 * where the user can view their paste onto the clipboard.  A "Balloon Tip" is shown after a successful paste.
 * 
 * @author Craig Tataryn
 *
 */
public class MysticPasteAction implements IEditorActionDelegate {

	private String selectionText = null;
	private String fileExtension = null;
	private Shell shell;
	ResourceBundle bundle = null;
	
	public MysticPasteAction() {
		super();
		bundle = ResourceBundle.getBundle("plugin");	
	}
	
	public void setActiveEditor(IAction action, IEditorPart editorPart) {
		shell = editorPart.getSite().getShell();
		IFile file = (IFile) editorPart.getEditorInput().getAdapter(IFile.class);
		fileExtension = file.getFileExtension();
	}

	/**
	 * Code which executes when the menu item is clicked.  Code selected is pasted to the Mystic Paste
	 * web application, the view URL is put on the clipboard and an informational message is shown in a
	 * balloon tip.
	 */
	public void run(IAction action) {
		System.out.println("run called");
		if (this.selectionText != null && !this.selectionText.trim().equals("")) {
			//the action is setup in plugin.xml with an ID that ends in .<lang type>
			String type = action.getId().substring(action.getId().lastIndexOf('.') + 1);
			String url = submitPaste(this.selectionText, type);
			Clipboard cb = new Clipboard(this.shell.getDisplay());
			cb.setContents(new Object[] {url}, new Transfer[] {TextTransfer.getInstance()});
			cb.dispose();
			showUrlBox();
		}
	}

	/**
	 * Decides whether or not to enable the mystic paste menu item in the context menu
	 * depending on whether there is selected text.  Unfortunately, because of how eclipse
	 * lazy loads things, this method isn't fired until the menu item is clicked for the
	 * first time, so you can never grey out the item before it is clicked. 
	 */
	public void selectionChanged(IAction action, ISelection selection) {
		System.out.println("selectionChanged called");
		if (ITextSelection.class.isAssignableFrom(selection.getClass())) {
			ITextSelection txtSelection = (ITextSelection) selection;
			if (txtSelection == null || txtSelection.isEmpty() || txtSelection.getText().trim().equals("")) {
				action.setEnabled(false);
				this.selectionText = null;
			} else {
				action.setEnabled(true);
				this.selectionText = txtSelection.getText();
			}
			
		}

	}

	/**
	 * Submits the selected text to the Mystic Paste web application.  Web application
	 * urls and other information are stored in the plugin.properties file.<br/>
	 * <br/>
	 * The language type for the selected text is determined by the action's id as setup
	 * in plugin.xml.  For instance, the action for the Java editor will have an id that ends
	 * with .JAVA.  The xml editor's action ID ends in .XML, and so on.  The default is TEXT.
	 * @param content
	 * @param type will use this type if the Eclipse doesn't offer us a file extension for the current file
	 * @return
	 */
	private String submitPaste(String content, String type) {
		String url = bundle.getString("mysticpaste.url");
		String newPasteContext = bundle.getString("mysticpaste.new");
		String contentParam = bundle.getString("mysticpaste.content.param");
		String langParam = bundle.getString("mysticpaste.language.param");
		String fileExtParam = bundle.getString("mysticpaste.fileExt.param");
		
		String retString = null;
		
		HttpClient httpClient = new DefaultHttpClient();
		HttpPost post = new HttpPost(url + newPasteContext);
		List <NameValuePair> nvps = new ArrayList <NameValuePair>();
		if (this.fileExtension == null || this.fileExtension.trim().equals("")) {
			nvps.add(new BasicNameValuePair(langParam, type));
		} else {
			nvps.add(new BasicNameValuePair(fileExtParam, this.fileExtension));
		}
        nvps.add(new BasicNameValuePair(contentParam, content));

        try {
	        post.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));
	        HttpResponse response = httpClient.execute(post);
			HttpEntity entity = response.getEntity();
			retString = EntityUtils.toString(entity, HTTP.UTF_8);
			retString = url + bundle.getString("mysticpaste.view") + retString;
			System.out.println(retString);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
			MessageDialog.openInformation(
					shell,
					"MysticPaste Plug-in",
					"Sorry, we couldn't contact Mystic Paste");
		} catch (IOException e) {
			e.printStackTrace();
			MessageDialog.openInformation(
					shell,
					"MysticPaste Plug-in",
					"Sorry, we couldn't contact Mystic Paste");
		}
		
		return retString;
	}

	/**
	 * Shows an informational "balloon tip" at the bottom of the screen
	 */
	private void showUrlBox() {
		Rectangle bounds = shell.getDisplay().getPrimaryMonitor().getClientArea();
		ToolTip tip = new ToolTip(shell, SWT.BALLOON | SWT.ICON_INFORMATION);
		tip.setAutoHide(true);
		tip.setText("Your selection has been copied to to Mystic Paste");
		tip.setMessage("The Url is on your clipboard");
		tip.setLocation(bounds.width, bounds.height);
		tip.setVisible(true);
		
	}
}
