package uk.co.jakebreen.sendgridandroid;

import android.support.annotation.NonNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class SendGridMail {

    static final String EMPTY = "";
    static final String TYPE_PLAIN = "text/plain";
    static final String TYPE_HTML = "text/html";

    private final Map<String, String> to = new HashMap<>();
    private final Map<String, String> cc = new HashMap<>();
    private final Map<String, String> bcc = new HashMap<>();
    private String subject;
    private final Map<String, String> content = new HashMap<>();
    private final Map<String, String> from = new HashMap<>();
    private final Map<String, String> replyTo = new HashMap<>();
    private String templateId;
    private List<String> categories = new ArrayList<>();
    private int sendAt;

    public SendGridMail() { }

    public void addTo(String email, @Nullable String name) {
        if (name == null) name = EMPTY;
        to.put(email, name);
    }

    public void setFrom(String email, @Nullable String name) {
        if (name == null) name = EMPTY;
        from.put(email, name);
    }

    public void setReplyTo(String email, @Nullable String name) {
        if (name == null) name = EMPTY;
        from.put(email, name);
    }

    public void addCc(String email, @Nullable String name) {
        if (name == null) name = EMPTY;
        cc.put(email, name);
    }

    public void addBcc(String email, @Nullable String name) {
        if (name == null) name = EMPTY;
        bcc.put(email, name);
    }

    public void setSubject(@NonNull String subject) {
        if (subject.length() == 0) subject = " ";
        this.subject = subject;
    }

    public void setTemplateId(@NonNull String templateId) {
        this.templateId = templateId;
    }

    public void setContent(@NonNull String body) {
        if (body.length() == 0) body = " ";
        content.put(TYPE_PLAIN, body);
    }

    public void setHtmlContent(@NonNull String body) {
        if (body.length() == 0) body = " ";
        content.put(TYPE_HTML, body);
    }

    public void addCategory(String category) {
        if (categories.size() <= 10) {
            category = category.substring(0, Math.min(category.length(), 255));
            categories.add(category);
        }
    }

    public void setSendAt(int sendAt) {
        this.sendAt = sendAt;
    }

    Map<String, String> getTo() {
        return to;
    }

    Map<String, String> getCc() {
        return cc;
    }

    Map<String, String> getBcc() {
        return bcc;
    }

    String getSubject() {
        return subject;
    }

    Map<String, String> getContent() {
        return content;
    }

    Map<String, String> getFrom() {
        return from;
    }

    Map<String, String> getReplyTo() {
        return replyTo;
    }

    String getTemplateId() {
        return templateId;
    }

    List<String> getCategories() {
        return categories;
    }

    int getSendAt() {
        return sendAt;
    }
}
