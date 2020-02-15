package uk.co.jakebreen.sendgridandroid;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static uk.co.jakebreen.sendgridandroid.FileEncoder.encodeFileToBase64;

public class SendGridMail {

    static final String EMPTY = "";
    static final String TYPE_PLAIN = "text/plain";
    static final String TYPE_HTML = "text/html";

    private final Map<String, String> to = new HashMap<>();
    private String subject;
    private final Map<String, String> content = new HashMap<>();
    private final Map<String, String> from = new HashMap<>();
    private final Map<String, String> replyTo = new HashMap<>();
    private String templateId;
    private int sendAt;
    private List<Attachment> attachments = new ArrayList<>();

    public SendGridMail() { }

    /**
     * Add a recipient up to a maximum of 1000 recipients.
     * Email address must be specified and an optional name of person or company
     * that is receiving this mail.
     *
     * @param email the recipient's email address
     * @param name name of person or company that is receiving this mail
     *
     */
    public void addTo(@NonNull String email, @Nullable String name) {
        if (to.size() >= 1000) return;

        if (name == null) name = EMPTY;
        to.put(email, name);
    }

    /**
     * Provide the senders email address and optional name or company that is
     * sending this mail.
     *
     * @param email email of person or company that is sending this mail
     * @param name name of person or company that is sending this mail
     */
    public void setFrom(@NonNull String email, @Nullable String name) {
        if (name == null)
            name = EMPTY;
        from.put(email, name);
    }

    /**
     * The name of the person or company that is sending the email.
     *
     * @param email email of person or company that is sending this mail
     * @param name name of person or company that is sending this mail
     */
    public void setReplyTo(@NonNull String email, @Nullable String name) {
        if (name == null)
            name = EMPTY;
        from.put(email, name);
    }

    /**
     * The subject matter of your email.
     *
     * @param subject subject of your email
     */
    public void setSubject(@NonNull String subject) {
        if (subject.length() == 0)
            subject = " ";
        this.subject = subject;
    }

    /**
     * The id of a template that you would like to use.
     *
     * @param templateId the id of your designated template
     */
    public void setTemplateId(@NonNull String templateId) {
        this.templateId = templateId;
    }

    /**
     * Add a plain text body to your email using Content-Type: "text/plain".
     *
     * @param body the body of your email
     */
    public void setContent(@NonNull String body) {
        if (body.length() == 0)
            body = " ";
        content.put(TYPE_PLAIN, body);
    }

    /**
     * Add a HTML body to your email using Content-Type: "text/html".
     *
     * @param body the body of your email
     */
    public void setHtmlContent(@NonNull String body) {
        if (body.length() == 0)
            body = " ";
        content.put(TYPE_HTML, body);
    }

    /**
     * A unix timestamp allowing you to specify when you want your email to be delivered.
     *
     * @param sendAt the unix timestamp of when your email should be sent
     */
    public void setSendAt(int sendAt) {
        if (sendAt > System.currentTimeMillis() / 1000L)
            this.sendAt = sendAt;
    }

    /**
     * Add an attachment to the email, up to a maximum of 10. Provide the File
     * of the desired content to be attached.
     *
     * @param file the content to be attached
     */
    public void addAttachment(@NonNull File file) {
        if (attachments.size() >= 10)
            return;
        if (file.canRead() && file.exists() && file.isFile())
            attachments.add(new Attachment(file));
    }

    /**
     * Returns a list of attached file names.
     *
     * @return list of file names
     */
    public List<String> getAttachments() {
        final List<String> fileNames = new ArrayList<>();
        for (Attachment a : attachments) {
            fileNames.add(a.filename);
        }
        return fileNames;
    }

    Map<String, String> getTo() {
        return to;
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

    int getSendAt() {
        return sendAt;
    }

    List<Attachment> getFileAttachments() {
        return attachments;
    }

    class Attachment {
        private final String content;
        private final String filename;

        Attachment(File file) {
            this.content = encodeFileToBase64(file);
            this.filename = file.getName();
        }

        String getContent() {
            return content;
        }

        String getFilename() {
            return filename;
        }
    }
}
