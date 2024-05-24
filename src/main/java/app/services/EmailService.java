package app.services;
import com.azure.communication.email.EmailClient;
import com.azure.communication.email.EmailClientBuilder;
import com.azure.communication.email.models.EmailMessage;
import com.azure.communication.email.models.EmailSendResult;
import com.azure.communication.email.models.EmailSendStatus;
import com.azure.core.util.polling.LongRunningOperationStatus;
import com.azure.core.util.polling.PollResponse;
import com.azure.core.util.polling.SyncPoller;


public class EmailService {
    public static void SendEmail(String to, String fromEmail) {


        String connectionString = "endpoint=https://carport-acs.europe.communication.azure.com/;accesskey=1pCslwkVLQKgOiSbW8FHSVf3WNMXq1/T+rws8EtagEh3ccl2DJc+q94xs+XHHiTWhnr02ggLBcNdf6SlxMvvFg==>";

        EmailClient emailClient = new EmailClientBuilder()
                .connectionString(connectionString)
                .buildClient();

        EmailMessage message = new EmailMessage()
                .setSenderAddress("DoNotReply@5ad5fab0-6a37-413f-8bf0-ea00f8379112.azurecomm.net")
                .setToRecipients("Drassuil@gmail.com")
                .setSubject("Tak for din Bestilling Fog Carport")
                .setBodyPlainText("Tak for bestilling for til din nye Carport der vil sendes snarest muligt!. Vh Fog Carport");

        SyncPoller<EmailSendResult, EmailSendResult> poller = emailClient.beginSend(message, null);
        PollResponse<EmailSendResult> result = poller.waitForCompletion();
    }
}