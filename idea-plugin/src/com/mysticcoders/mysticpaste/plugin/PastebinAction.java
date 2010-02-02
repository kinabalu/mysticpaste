/*
 * Created by IntelliJ IDEA.
 * User: kinabalu
 * Date: Mar 29, 2009
 * Time: 8:36:44 PM
 */
package com.mysticcoders.mysticpaste.plugin;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.actionSystem.DataKeys;
import com.intellij.openapi.actionSystem.ex.DataConstantsEx;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.SelectionModel;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.ide.CopyPasteManager;
import com.intellij.openapi.fileTypes.FileTypeManager;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.wm.WindowManager;
import com.intellij.openapi.wm.StatusBar;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.Application;
import com.intellij.openapi.ui.Messages;
import com.intellij.ui.LightColors;

import javax.swing.*;
import java.awt.datatransfer.StringSelection;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

/**
 * IDEA Action to paste contents of selection to http://mysticpaste.com
 * and add URL to clipboard
 */
public class PastebinAction extends AnAction {

    

    /**
     * Process the popup event for sending to mysticpaste
     *
     * @param event anActionEvent
     */
    public void actionPerformed(AnActionEvent event) {
        DataContext context = event.getDataContext();
        Editor editor = DataKeys.EDITOR.getData(context);

        String selectedText = null;
        SelectionModel selection = null;
        if (editor != null) {
            selection = editor.getSelectionModel();
            if (selection.hasSelection()) {
                selectedText = selection.getSelectedText();
            }

            if (selectedText != null && selectedText.trim().length() > 0) {

                Document doc = editor.getDocument();
                VirtualFile virtualFile = FileDocumentManager.getInstance().getFile(doc);
                String extension = null;
                if (virtualFile.getName().lastIndexOf(".") != -1) {
                    extension = virtualFile.getName().substring(virtualFile.getName().lastIndexOf(".") + 1, virtualFile.getName().length());
                }

                String url = sendPaste(selectedText, extension);

                if(url != null) {
                    pasteToClipboard(url);

                    Project project = DataKeys.PROJECT.getData(context);
                    MysticPasteIndicationComponent mpiComponent = project.getComponent(MysticPasteIndicationComponent.class);
                    mpiComponent.updateWithStatus("Paste successful!\n\nURL automatically copied to your clipboard");
                    
                }
            }
        }
    }

    /**
     * Use IDEA's Clipboard tools to paste URL
     *
     * @param text the text to paste to clipboard
     */
    private void pasteToClipboard(String text) {
        CopyPasteManager.getInstance().setContents(new StringSelection(text));
    }

    /**
     * Send the paste to http://mysticpaste.com and return the URL
     *
     * @param text the text to paste to the bin
     * @param extension the file we are pasting from's extension
     * @return the URL of the paste
     */
    private String sendPaste(String text, String extension) {

        String pasteNumber = null;

        try {
            // Construct data
            String data = URLEncoder.encode("content", "UTF-8") + "=" + URLEncoder.encode(text, "UTF-8");

            if(extension != null) {
                data += "&" + URLEncoder.encode("fileExt", "UTF-8") + "=" + URLEncoder.encode(extension, "UTF-8");
            }

            // Send data
            URL url = new URL("http://www.mysticpaste.com/servlet/plugin");
            URLConnection conn = url.openConnection();
            conn.setDoOutput(true);
            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
            wr.write(data);
            wr.flush();

            // Get the response
            BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = rd.readLine()) != null) {
                pasteNumber = line;
            }

            wr.close();
            rd.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return (pasteNumber != null ? "http://www.mysticpaste.com/view/" + pasteNumber : null);
    }
}