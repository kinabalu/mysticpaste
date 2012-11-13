package com.mysticcoders.mysticpaste.model;

import org.incava.util.diff.Diff;
import org.incava.util.diff.Difference;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author <a href="mailto:gcastro@mysticcoders.com">Guillermo Castro</a>
 * @author <a href="mailto:andrew@mysticcoders.com">Andrew Lombardi</a>
 * @version $Revision$ $Date$
 */
public class PasteItem implements Serializable {
    private static final long serialVersionUID = -6467870857777145137L;

    private static SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");

    protected String itemId;
    protected String content;
    protected String type;
    protected Date timestamp;
    protected boolean abuseFlag;
    protected boolean privateFlag;
    protected String parent;
    protected String clientIp;
    protected int viewCount;

    private String imageLocation;

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    /**
     * Calculate up to 5 lines for a preview and return it to end user
     *
     * @return up to 5 lines of paste, or empty string
     */
    public String getPreviewContent() {
        if (getContent() == null || getContent().length() == 0) return "";

        String[] contentLines = getContent().split("\n");

        if (contentLines == null) return "";

        StringBuffer previewContent = new StringBuffer();

        for (int i = 0; i < (contentLines.length < 5 ? contentLines.length : 5); i++) {
            previewContent.append(contentLines[i]).append("\n");
        }
        return previewContent.toString();
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isAbuseFlag() {
        return abuseFlag;
    }

    public void markAbuse() {
        this.abuseFlag = true;
    }

    public boolean isPrivate() {
        return privateFlag;
    }

    public void setPrivate(boolean privateFlag) {
        privateFlag = this.privateFlag;
    }

    public String getParent() {
        return parent;
    }

    public void setParent(String parent) {
        this.parent = parent;
    }

    public void setClientIp(String clientIp) {
        this.clientIp = clientIp;
    }

    public int getViewCount() {
        return viewCount;
    }

    public void increaseViewCount() {
        viewCount += 1;
    }

    public String getImageLocation() {
        return imageLocation;
    }

    public void setImageLocation(String imageLocation) {
        this.imageLocation = imageLocation;
    }

    public boolean hasImage() {
        return this.imageLocation != null && this.imageLocation.length() > 0;
    }

    public int getContentLineCount() {
        if (getContent() == null || getContent().length() == 0) return 0;

        String[] lines = getContent().split("\n");
        return lines != null ? lines.length : 0;
    }

    /**
     * Return a simplistic diff view of 2 pastes
     *
     * @param originalPaste
     * @param revisedPaste
     * @return
     */
    public static Object[] diffPastes(String originalPaste, String revisedPaste) {
        int new_line_count = 0;
        int old_line_count = 0;

        System.out.println("Original: " + (originalPaste != null ? originalPaste.length() : 0) + " line(s)");
        System.out.println("Revised: " + (revisedPaste != null ? revisedPaste.length() : 0) + " line(s)");

        List<String> original = Arrays.asList(originalPaste.split("\n"));
        List<String> revised = Arrays.asList(revisedPaste.split("\n"));

        List<Integer> changedLines = new ArrayList<Integer>();

        int lineIndex = 0;

        StringBuilder diffText = new StringBuilder();
        List<Difference> diffs = new Diff<String>(original, revised).diff();
        for (Difference diff : diffs) {
            int del_start = diff.getDeletedStart();
            int del_end = diff.getDeletedEnd();
            int add_start = diff.getAddedStart();
            int add_end = diff.getAddedEnd();

            if (del_end != Difference.NONE) {
                for (; old_line_count < del_start; ++old_line_count, ++new_line_count) {
                    diffText.append(original.get(old_line_count)).append("\n");
                    lineIndex++;
                }
                for (; old_line_count <= del_end; ++old_line_count) {
                    diffText.append("- ").append(original.get(old_line_count)).append("\n");
                    changedLines.add(lineIndex);
                    lineIndex++;
                }
            }

            if (add_end != Difference.NONE) {
                for (; new_line_count < add_start; ++new_line_count, ++old_line_count) {
                    diffText.append(revised.get(new_line_count)).append("\n");
                    lineIndex++;
                }

                for (; new_line_count <= add_end; ++new_line_count) {
                    diffText.append("+ ").append(revised.get(new_line_count)).append("\n");
                    changedLines.add(lineIndex);
                    lineIndex++;
                }

            }
        }

        for (; new_line_count < revised.size(); ++new_line_count, ++old_line_count) {
            diffText.append(revised.get(new_line_count));
        }

        return new Object[]{changedLines, diffText.toString()};

    }


    public static String getElapsedTimeSincePost(PasteItem pasteItem) {
        String returnString;

        Calendar today = Calendar.getInstance();
        Calendar postDate = Calendar.getInstance();
        postDate.setTime(pasteItem.getTimestamp());

        long time = today.getTimeInMillis() - postDate.getTimeInMillis();
        long mins = time / 1000 / 60;
        long hours = mins / 60;
        long days = hours / 24;

        if (days > 30) {
            // If it is more than 30 days old... just show the post date
            returnString = "Posted " + sdf.format(postDate.getTime());
        } else {
            if (days > 0) {
                // Then it is more than 1 day old but less than 30 days old... so show how many days old it is
                returnString = "Posted " + days + " day" + (days > 1 ? "s" : "") + " ago";
            } else {
                if (hours > 0) {
                    // It has been more than 1 hr and less than a day... so display hrs
                    returnString = "Posted " + hours + " hour" + (hours > 1 ? "s" : "") + " ago";
                } else {
                    if (mins > 0) {
                        // It has been more than 1 min and less than an hour... so display mins
                        returnString = "Posted " + mins + " minute" + (mins > 1 ? "s" : "") + " ago";
                    } else {
                        returnString = "Posted less than a minute ago";
                    }
                }
            }
        }

        return returnString;
    }

}