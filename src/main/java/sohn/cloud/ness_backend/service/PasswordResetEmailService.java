package sohn.cloud.ness_backend.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sesv2.SesV2Client;
import software.amazon.awssdk.services.sesv2.model.*;

@Service
public class PasswordResetEmailService {

    private final SesV2Client sesClient;

    @Value("${ses.from-address}")
    private String fromAddress; // e.g. noreply@sohn.cloud

    public PasswordResetEmailService(SesV2Client sesClient) {
        this.sesClient = sesClient;
    }

    public void sendResetEmail(String toEmail, String name, String resetLink) {
        String html = buildHtml(name, resetLink);
        String text = buildText(name, resetLink);

        EmailContent content = EmailContent.builder()
            .simple(Message.builder()
                .subject(ContentBuilder("Reset your Ness password"))
                .body(Body.builder()
                    .html(ContentBuilder(html))
                    .text(ContentBuilder(text))
                    .build())
                .build())
            .build();

        SendEmailRequest request = SendEmailRequest.builder()
            .fromEmailAddress(fromAddress)
            .destination(Destination.builder().toAddresses(toEmail).build())
            .content(content)
            .build();

        SendEmailResponse response = sesClient.sendEmail(request);
    }

    private Content ContentBuilder(String data) {
        return Content.builder().data(data).charset("UTF-8").build();
    }

    private String buildHtml(String name, String resetLink) {
        return """
            <html>
              <body style="font-family: sans-serif; color: #1e293b;">
                <h2>Reset your password</h2>
                <p>Hi %s,</p>
                <p>Click below to reset your password. This link expires in 30 minutes.</p>
                <p><a href="%s" style="color: #06b6d4;">Reset Password</a></p>
                <p style="font-size: 12px; color: #64748b;">If you did not request this, ignore this email.</p>
              </body>
            </html>
            """.formatted(name, resetLink);
    }

    private String buildText(String name, String resetLink) {
        return "Hi %s, reset your password here: %s (expires in 30 minutes). If you did not request this, ignore this email."
            .formatted(name, resetLink);
    }
}