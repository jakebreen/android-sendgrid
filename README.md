[![](https://jitpack.io/v/jakebreen/android-sendgrid.svg)](https://jitpack.io/#jakebreen/android-sendgrid)

![header](https://github.com/Jakebreen/android-sendgrid/blob/master/images/heading-image.jpg)

# android-sendgrid
A simplified Android email library utilising SendGrid's v3 API that targets minSdkVersion 16.

Use with JitPack and implement in your app
```
allprojects {
    repositories {
        ...
        maven { url "https://jitpack.io" }
    }
}

dependencies {
     implementation 'com.github.jakebreen:android-sendgrid:1.3.0'
}
```

# How to use
Create an instance of the SendGrid library tied to your API key.
```
SendGrid sendGrid = SendGrid.create(@NonNull String apiKey)
```

Create a SendGridMail and provide the required attributes.
```
SendGridMail mail = new SendGridMail()
mail.addRecipient(@NonNull String email, @Nullable String name)
mail.setFrom(@NonNull String email, @Nullable String name)
mail.setSubject(@NonNull String subject)
mail.setContent(@NonNull String body)
```
Set a plain text or a html text content body, when using a template neither of these content variables need to be set.
```
mail.setContent(@NonNull String body)
mail.setHtmlContent(@NonNull String body)
```

Send a mail with the library's SendTask.
```
SendTask task = new SendTask(sendGrid, mail);
SendGridResponse response = task.execute().get();
```

Send a mail with RxJava.
```
Single.fromCallable(sendGrid.send(mail))
    .subscribeOn(Schedulers.io())
    .subscribe(response -> {
            if (response.isSuccessful()) {
                // ...
            }
        }
    );
```

Send requests return a SendGridResponse that contains the success state of the request and the associated HTTP response code.
A failed request will contain the error message from the API.
```
response.isSuccessful()
response.getCode()
response.getErrorMessage()
```

Additional SendGridMail methods that aren't required to send an email include templates, tracking, carbon copies, attachments and others
```
mail.addAttachment(@NonNull File file)
mail.addAttachment(@NonNull Uri uri)
mail.addRecipientCarbonCopy(@NonNull String email, @Nullable String name)
mail.addRecipientBlindCarbonCopy(@NonNull String email, @Nullable String name)
mail.setReplyTo(@NonNull String email, @Nullable String name)
mail.setSendAt(@NonNull int sendAt)
mail.setClickTrackingEnabled(@NonNull Boolean enabled)
mail.setOpenTrackingEnabled(@NonNull Boolean enabled)
mail.setSubscriptionTrackingEnabled(@NonNull Boolean enabled)
```

Transactional email templates and custom data can be applied to the SendGridMail in the form of a JSONObject structured into key/value pairs
```
mail.setTemplateId(@NonNull String templateId)
mail.setDynamicTemplateData(@NonNull JSONObject jsonObject)

// JSONObject structured into key/value pairs
final JSONObject templateData = new JSONObject();
templateData.put("forename", "Jane");
templateData.put("surname", "Doe");
templateData.put("message", "Hello, This is my email");

mail.setDynamicTemplateData(templateData)
```

# TestApp
A test app included when cloning the library to test the library with file attachments, uses RxJava2. Feel free utilise this app.
