package com.makeurpicks.service;

import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

	@Autowired(required = false)
	private JavaMailSender mailSender;

	@Value("${spring.mail.username:noreply@makeurpicks.com}")
	private String fromEmail;

	private String getSanitizedFromEmail() {
		if (fromEmail == null || fromEmail.trim().isEmpty() || fromEmail.equals("''") || fromEmail.equals("\"\"") || fromEmail.contains("${")) {
			return "noreply@makeurpicks.com";
		}
		String sanitized = fromEmail.trim();
		if (sanitized.startsWith("'") && sanitized.endsWith("'")) {
			sanitized = sanitized.substring(1, sanitized.length() - 1);
		} else if (sanitized.startsWith("\"") && sanitized.endsWith("\"")) {
			sanitized = sanitized.substring(1, sanitized.length() - 1);
		}
		return sanitized.trim().isEmpty() ? "noreply@makeurpicks.com" : sanitized.trim();
	}

	@Async
	public void sendPasswordResetEmail(String recipientEmail, String displayName, String resetLink) {
		if (mailSender == null) {
			System.err.println("SMTP is not configured. Password reset link: " + resetLink);
			return;
		}

		try {
			MimeMessage mimeMessage = mailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

			helper.setFrom(getSanitizedFromEmail());
			helper.setTo(recipientEmail);
			helper.setSubject("Reset Your Password - Make Your Picks");

			String htmlContent = "<div style=\"background-color: #121212; color: #ececec; font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; padding: 40px 20px; text-align: center; min-height: 100%;\">"
					+ "  <div style=\"max-width: 500px; margin: 0 auto; background: #1e1e1e; border: 1px solid rgba(255, 255, 255, 0.05); border-radius: 12px; overflow: hidden; box-shadow: 0 10px 30px rgba(0,0,0,0.5);\">"
					+ "    <div style=\"background: #181818; padding: 30px; border-bottom: 2px solid #FFB415;\">"
					+ "      <h1 style=\"color: #FFB415; margin: 0; font-size: 24px; font-weight: 800; letter-spacing: 1px;\">MAKE YOUR PICKS</h1>"
					+ "    </div>"
					+ "    <div style=\"padding: 40px 30px;\">"
					+ "      <p style=\"font-size: 16px; line-height: 1.6; margin: 0 0 10px 0; color: #ffffff;\">Hello, <strong>" + displayName + "</strong>!</p>"
					+ "      <p style=\"font-size: 14px; line-height: 1.6; color: #aaaaaa; margin: 0 0 25px 0;\">We received a request to reset your password. Click the golden action button below to set up a new password:</p>"
					+ "      <a href=\"" + resetLink + "\" style=\"display: inline-block; background: linear-gradient(135deg, #FFB415, #f39c12); color: #000000; font-weight: 700; text-decoration: none; padding: 14px 35px; border-radius: 6px; font-size: 14px; text-transform: uppercase; letter-spacing: 1px; box-shadow: 0 4px 15px rgba(243, 156, 18, 0.3); margin-bottom: 25px; transition: transform 0.2s;\">Reset Password</a>"
					+ "      <p style=\"font-size: 12.5px; line-height: 1.6; color: #777777; margin: 0;\">If you didn't request a password reset, you can safely ignore this email.</p>"
					+ "      <p style=\"font-size: 11px; line-height: 1.6; color: #555555; margin: 20px 0 0 0; word-break: break-all;\">Button not working? Copy & paste this URL:<br/>" + resetLink + "</p>"
					+ "    </div>"
					+ "    <div style=\"background: #151515; padding: 20px 30px; border-top: 1px solid rgba(255,255,255,0.03);\">"
					+ "      <p style=\"font-size: 11px; color: #555555; margin: 0;\">&copy; 2026 Make Your Picks. Hosted on Basement Server.</p>"
					+ "    </div>"
					+ "  </div>"
					+ "</div>";

			helper.setText(htmlContent, true);
			mailSender.send(mimeMessage);
		} catch (Exception e) {
			System.err.println("Failed to send password reset email to: " + recipientEmail);
			e.printStackTrace();
		}
	}

	@Async
	public void sendBroadcastEmail(String recipientEmail, String displayName, String subject, String body) {
		if (mailSender == null) {
			System.err.println("SMTP is not configured. Broadcast not sent to: " + recipientEmail);
			return;
		}

		try {
			MimeMessage mimeMessage = mailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

			helper.setFrom(getSanitizedFromEmail());
			helper.setTo(recipientEmail);
			helper.setSubject(subject);

			// Replace newlines with <br/> tags in the body
			String htmlBody = body.replace("\n", "<br/>");

			String htmlContent = "<div style=\"background-color: #121212; color: #ececec; font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; padding: 40px 20px; text-align: left; min-height: 100%;\">"
					+ "  <div style=\"max-width: 600px; margin: 0 auto; background: #1e1e1e; border: 1px solid rgba(255, 255, 255, 0.05); border-radius: 12px; overflow: hidden; box-shadow: 0 10px 30px rgba(0,0,0,0.5);\">"
					+ "    <div style=\"background: #181818; padding: 30px; border-bottom: 2px solid #FFB415; text-align: center;\">"
					+ "      <h1 style=\"color: #FFB415; margin: 0; font-size: 24px; font-weight: 800; letter-spacing: 1px;\">MAKE YOUR PICKS</h1>"
					+ "    </div>"
					+ "    <div style=\"padding: 40px 30px;\">"
					+ "      <p style=\"font-size: 16px; line-height: 1.6; margin: 0 0 15px 0; color: #ffffff;\">Hello, <strong>" + displayName + "</strong>,</p>"
					+ "      <div style=\"font-size: 14.5px; line-height: 1.7; color: #cccccc; margin: 0 0 25px 0;\">" + htmlBody + "</div>"
					+ "    </div>"
					+ "    <div style=\"background: #151515; padding: 20px 30px; border-top: 1px solid rgba(255,255,255,0.03); text-align: center;\">"
					+ "      <p style=\"font-size: 11px; color: #555555; margin: 0;\">&copy; 2026 Make Your Picks. This broadcast was sent by your league administrator.</p>"
					+ "    </div>"
					+ "  </div>"
					+ "</div>";

			helper.setText(htmlContent, true);
			mailSender.send(mimeMessage);
		} catch (Exception e) {
			System.err.println("Failed to send broadcast email to: " + recipientEmail);
			e.printStackTrace();
		}
	}

	@Async
	public void sendWeeklySetupEmail(String recipientEmail, String displayName, int weekNumber) {
		if (mailSender == null) {
			System.err.println("SMTP is not configured. Weekly notification not sent to: " + recipientEmail);
			return;
		}

		try {
			MimeMessage mimeMessage = mailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

			helper.setFrom(getSanitizedFromEmail());
			helper.setTo(recipientEmail);
			helper.setSubject("Week " + weekNumber + " is Ready! - Make Your Picks");

			String htmlContent = "<div style=\"background-color: #121212; color: #ececec; font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; padding: 40px 20px; text-align: center; min-height: 100%;\">"
					+ "  <div style=\"max-width: 500px; margin: 0 auto; background: #1e1e1e; border: 1px solid rgba(255, 255, 255, 0.05); border-radius: 12px; overflow: hidden; box-shadow: 0 10px 30px rgba(0,0,0,0.5);\">"
					+ "    <div style=\"background: #181818; padding: 30px; border-bottom: 2px solid #FFB415;\">"
					+ "      <h1 style=\"color: #FFB415; margin: 0; font-size: 24px; font-weight: 800; letter-spacing: 1px;\">MAKE YOUR PICKS</h1>"
					+ "    </div>"
					+ "    <div style=\"padding: 40px 30px;\">"
					+ "      <div style=\"background: rgba(255, 180, 21, 0.05); border: 1px solid rgba(255, 180, 21, 0.2); border-radius: 8px; padding: 15px; margin-bottom: 25px;\">"
					+ "        <span style=\"color: #FFB415; font-size: 13px; text-transform: uppercase; font-weight: 700; letter-spacing: 1px; display: block; margin-bottom: 4px;\">Announcement</span>"
					+ "        <strong style=\"color: #ffffff; font-size: 20px;\">Week " + weekNumber + " is now open!</strong>"
					+ "      </div>"
					+ "      <p style=\"font-size: 15px; line-height: 1.6; margin: 0 0 10px 0; color: #ffffff;\">Hello, <strong>" + displayName + "</strong>!</p>"
					+ "      <p style=\"font-size: 14px; line-height: 1.6; color: #aaaaaa; margin: 0 0 25px 0;\">The administrator has successfully set up the matchups and games for <strong>Week " + weekNumber + "</strong>. You can now log in and submit your picks before kickoff!</p>"
					+ "      <a href=\"http://localhost:8080/\" style=\"display: inline-block; background: linear-gradient(135deg, #FFB415, #f39c12); color: #000000; font-weight: 700; text-decoration: none; padding: 14px 35px; border-radius: 6px; font-size: 13px; text-transform: uppercase; letter-spacing: 1px; box-shadow: 0 4px 15px rgba(243, 156, 18, 0.3); margin-bottom: 10px;\">Submit Your Picks</a>"
					+ "    </div>"
					+ "    <div style=\"background: #151515; padding: 20px 30px; border-top: 1px solid rgba(255,255,255,0.03);\">"
					+ "      <p style=\"font-size: 11px; color: #555555; margin: 0;\">&copy; 2026 Make Your Picks. Hosted on Basement Server.</p>"
					+ "    </div>"
					+ "  </div>"
					+ "</div>";

			helper.setText(htmlContent, true);
			mailSender.send(mimeMessage);
		} catch (Exception e) {
			System.err.println("Failed to send weekly setup email to: " + recipientEmail);
			e.printStackTrace();
		}
	}
}
