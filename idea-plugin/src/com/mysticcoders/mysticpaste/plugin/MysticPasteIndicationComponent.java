/*
 * Created by IntelliJ IDEA.
 * User: kinabalu
 * Date: Apr 17, 2009
 * Time: 2:52:57 PM
 */
package com.mysticcoders.mysticpaste.plugin;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.StatusBar;
import com.intellij.openapi.wm.WindowManager;
import com.intellij.ui.LightColors;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class MysticPasteIndicationComponent implements ProjectComponent {

    private Project project;

    public MysticPasteIndicationComponent(Project inProject) {
        project = inProject;
    }

    public void projectOpened() {
    }

    public void projectClosed() {
    }

    @NotNull
    public String getComponentName() {
        return "MysticPastePlugin.MysticPasteIndicationComponent";
    }

    protected void updateWithStatus(final String statusMessage) {

        /**
         * Don't make Swing angry.  You won't like it when its angry
         *
         */
        ApplicationManager.getApplication().invokeLater(
                new Runnable() {
                    public void run() {
                        JTextArea area = new JTextArea();
                        area.setOpaque(false);
                        area.setEditable(false);
                        StringBuffer notification = new StringBuffer(statusMessage);
                        area.setText(notification.toString());
                        StatusBar statusBar = WindowManager.getInstance().getStatusBar(project);
                        if (statusBar != null)

                        {
                            statusBar.fireNotificationPopup(area, LightColors.GREEN);
                        }
                    }

                }
        );

    }

    public void initComponent() {
    }

    public void disposeComponent() {
    }
}