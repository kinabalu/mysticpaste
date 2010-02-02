package com.mysticcoders.mysticpaste.nb;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

public class MysticPastePost {

  String baseUrl;
  String newContext;
  String viewContext;
  String contentParam;
  String langParam;

  public String getNewContext() {
    return newContext;
  }

  public void setNewContext(String newContext) {
    this.newContext = newContext;
  }

  public String getViewContext() {
    return viewContext;
  }

  public void setViewContext(String viewContext) {
    this.viewContext = viewContext;
  }

  public void setBaseUrl(String baseUrl) {
    this.baseUrl = baseUrl;
  }

  public void setContentParam(String contentParam) {
    this.contentParam = contentParam;
  }

  public void setLangParam(String langParam) {
    this.langParam = langParam;
  }

  public String getBaseUrl() {
    return baseUrl;
  }

  public String getContentParam() {
    return contentParam;
  }

  public String getLangParam() {
    return langParam;
  }

  public String sendPaste(String text, String extension) {
    String pasteNumber = null;

    try {
      // Construct data
      String data = URLEncoder.encode(this.contentParam, "UTF-8") + "=" + URLEncoder.encode(text, "UTF-8");

      if (extension != null) {
        data += "&" + URLEncoder.encode(this.langParam, "UTF-8") + "=" + URLEncoder.encode(extension, "UTF-8");
      }

      // Send data
      URL url = new URL(this.baseUrl + this.newContext);
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

    return (pasteNumber != null ? this.baseUrl + this.viewContext + pasteNumber : null);
  }
}