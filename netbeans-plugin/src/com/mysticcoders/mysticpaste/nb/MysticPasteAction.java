package com.mysticcoders.mysticpaste.nb;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import javax.swing.JEditorPane;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.cookies.EditorCookie;
import org.openide.filesystems.FileObject;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.actions.CookieAction;
import org.openide.util.datatransfer.ExClipboard;

public final class MysticPasteAction extends CookieAction {

  private static final String NOTIFICATION_MESSAGE =
          "Selection copied to clipboard and sent to MysticPaste!";
  private static final String ERROR_MESSAGE =
          "Error encountered while sending to MysticPaste - selection not copied";

  protected MysticPastePost createPost() {
    MysticPastePost mppost = new MysticPastePost();
    mppost.setBaseUrl(NbBundle.getMessage(MysticPasteAction.class, "mysticpaste.url"));
    mppost.setNewContext(NbBundle.getMessage(MysticPasteAction.class, "mysticpaste.new"));
    mppost.setViewContext(NbBundle.getMessage(MysticPasteAction.class, "mysticpaste.view"));
    mppost.setContentParam(NbBundle.getMessage(MysticPasteAction.class, "mysticpaste.content.param"));
    mppost.setLangParam(NbBundle.getMessage(MysticPasteAction.class, "mysticpaste.language.param"));
    return mppost;
  }

  protected void performAction(Node[] activatedNodes) {
    try {
      EditorCookie editorCookie = activatedNodes[0].getLookup().lookup(EditorCookie.class);
      FileObject fileobj = activatedNodes[0].getLookup().lookup(FileObject.class);

      JEditorPane focusedPane = editorCookie.getOpenedPanes()[0];
      String selectedText = focusedPane.getSelectedText();

      MysticPastePost mppost = createPost();
      String viewUrl =
              mppost.sendPaste(selectedText, fileobj.getExt().toUpperCase());

      Clipboard clipboard = Lookup.getDefault().lookup(ExClipboard.class);
      if (clipboard == null) {
        clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
      }
      clipboard.setContents(new StringSelection(viewUrl), null);

      NotifyDescriptor d;
      if (viewUrl == null) {
        d = new NotifyDescriptor.Message(ERROR_MESSAGE, NotifyDescriptor.ERROR_MESSAGE);
        DialogDisplayer.getDefault().notify(d);
      }
    }
    catch (RuntimeException e) {
      NotifyDescriptor d = new NotifyDescriptor.Exception(e, NotifyDescriptor.ERROR_MESSAGE);
    }
  }

  protected int mode() {
    return CookieAction.MODE_EXACTLY_ONE;
  }

  public String getName() {
    return NbBundle.getMessage(MysticPasteAction.class, "CTL_MysticPasteAction");
  }

  protected Class[] cookieClasses() {
    return new Class[]{EditorCookie.class};
  }

  @Override
  protected String iconResource() {
    return "com/mysticcoders/mysticpaste/nb/mystic16.png";
  }

  public HelpCtx getHelpCtx() {
    return HelpCtx.DEFAULT_HELP;
  }

  @Override
  protected boolean asynchronous() {
    return false;
  }
}
