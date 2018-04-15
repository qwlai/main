# chanyikwai
###### \java\seedu\address\email\Email.java
``` java
/**
 * Email component and relevant API
 */
public interface Email {

    /**
     * Logins to Email Component with given login information
     *
     * @throws EmailLoginInvalidException when login fails
     */
    void loginEmailAccount(String[] loginInformation) throws EmailLoginInvalidException;

    /**
     * Returns true if user is logged in
     */
    boolean isUserLoggedIn();

    /**
     * Creates a draft email template with a given message
     */
    void composeEmail(MessageDraft messageDraft);

    /**
     * Views Email Draft
     */
    ReadOnlyMessageDraft getEmailDraft();

    /**
     * Views the Email Sent Status
     */
    String getEmailStatus();

    /**
     * Clears the Email Draft content
     */
    void clearEmailDraft();

    /**
     * Sends the Email Draft to all users
     *
     * @throws EmailLoginInvalidException if login details is empty
     * @throws EmailMessageEmptyException if message is empty
     * @throws EmailRecipientsEmptyException if recipients list is empty
     * @throws AuthenticationFailedException if gmail account can't be logged in
     */
    void sendEmail() throws EmailLoginInvalidException, EmailMessageEmptyException,
            EmailRecipientsEmptyException, AuthenticationFailedException;
}
```
###### \java\seedu\address\email\EmailCompose.java
``` java
/**
 * Holds a list of messageDraft
 * Handles how messageDraft are created and edited
 */
public class EmailCompose {
    private DraftList emailDraftsList;

    /** Creates an EmailCompose with an empty draft list **/
    public EmailCompose() {
        emailDraftsList = new DraftList();
    }

    /**
     * Compose an Email or edit the current one
     *
     * @param message
     */
    public void composeEmail(MessageDraft message) {
        emailDraftsList.composeEmail(message);
    }

    public ReadOnlyMessageDraft getMessage() {
        return emailDraftsList.getMessage(0);
    }

    /**
     * Resets the existing data of this {@code emailDraftsList} with an empty draft list
     */
    public void resetData() {
        emailDraftsList = new DraftList();
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof EmailCompose // instanceof handles nulls
                && this.emailDraftsList.equals(((EmailCompose) other).emailDraftsList));
    }
}
```
###### \java\seedu\address\email\EmailFunction.java
``` java
/**
 * Keeps track of called Email Function
 */
public class EmailFunction {
    public static final String EMAIL_FUNCTION_SEND = "send";
    public static final String EMAIL_FUNCTION_CLEAR = "clear";

    private String emailFunction;

    public EmailFunction() {
        emailFunction = "";
    }

    public EmailFunction(String emailFunction) {
        this.emailFunction = emailFunction;
    }

    public String getEmailFunction() {
        return emailFunction;
    }

    public void setEmailFunction(String emailFunction) {
        this.emailFunction = emailFunction;
    }

    /**
     * Returns true if task is valid
     */
    public boolean isValid() {
        switch (emailFunction) {
        case EMAIL_FUNCTION_SEND:
        case EMAIL_FUNCTION_CLEAR:
            return true;
        default:
            return false;
        }
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof EmailFunction // instanceof handles nulls
                && this.emailFunction.equals(((EmailFunction) other).emailFunction));
    }
}
```
###### \java\seedu\address\email\EmailLogin.java
``` java
/**
 * Handles how user logs into email
 */
public class EmailLogin {
    private static final Pattern GMAIL_FORMAT = Pattern.compile("^[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(@gmail.com)$");

    private String[] loginDetails;

    /** Creates an EmailLogin with an empty login detail */
    public EmailLogin() {
        loginDetails = new String[0];
    }

    /**
     * Saves user's login details
     *
     * @param loginDetails login email and password
     * @throws EmailLoginInvalidException if loginDetails is in wrong format
     */
    public void loginEmail(String[] loginDetails) throws EmailLoginInvalidException {
        //replace login details and ignore if login details is omitted.
        if (loginDetails.length != 0 && loginDetails.length == 2) {
            if (wrongUserEmailFormat(loginDetails)) {
                throw new EmailLoginInvalidException();
            } else {
                this.loginDetails = loginDetails;
            }
        }
    }

    /**
     * Checks if user's login details have been stored
     *
     * @return true if loginDetails is available
     */
    public boolean isUserLoggedIn() {
        if (loginDetails.length != 2) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * Verifies if the user is using a gmail account
     *
     * @return true if gmail account, false for everything else
     */
    private boolean wrongUserEmailFormat(String [] loginDetails) {
        if (loginDetails.length == 2) {
            final Matcher matcher = GMAIL_FORMAT.matcher(loginDetails[0].trim());
            if (!matcher.matches()) {
                return true;
            }
        }
        return false;
    }

    /** Returns user's login email */
    public String getEmailLogin() {
        if (loginDetails.length == 2) {
            return loginDetails[0];
        } else {
            return "";
        }
    }

    /** Returns user's login password */
    public String getPassword() {
        if (loginDetails.length == 2) {
            return loginDetails[1];
        } else {
            return "";
        }
    }

    /**
     * Resets the existing data of this {@code loginDetails} with an empty login
     */
    public void resetData() {
        loginDetails = new String[0];
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof EmailLogin // instanceof handles nulls
                && this.loginDetailsEquals(((EmailLogin) other).loginDetails));
    }

    /** Returns true if both have the same loginDetails */
    private boolean loginDetailsEquals(String [] other) {
        if (loginDetails.length == other.length) {
            for (int i = 0; i < loginDetails.length; i++) {
                if (loginDetails[i] != other[i]) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }
}
```
###### \java\seedu\address\email\EmailManager.java
``` java
/**
 * Handles the process of sending out email via MVP
 */
public class EmailManager extends ComponentManager implements Email {
    private static final Logger logger = LogsCenter.getLogger(EmailManager.class);

    private static final String STATUS_CLEARED = "cleared.";
    private static final String STATUS_DRAFTED = "drafted.\n";
    private static final String STATUS_SENT = "sent ";
    private static final String STATUS_LOGIN_FAIL = "You are not logged in to a Gmail account.";
    private static final String STATUS_LOGIN_SENT = "using %1$s";
    private static final String STATUS_LOGIN_SUCCESS = "You are logged in to %1$s";

    private final EmailLogin emailLogin;
    private final EmailCompose emailCompose;
    private final EmailSend emailSend;

    private String emailStatus;
    private String emailLoginStatus;

    /**
     * Initialize EmailManager with EmailLogin, EmailCompose and EmailSend
     */
    public EmailManager() {
        logger.fine("Initializing Default Email component");

        emailLogin = new EmailLogin();
        emailCompose = new EmailCompose();
        emailSend = new EmailSend();
        emailStatus = "";
        emailLoginStatus = STATUS_LOGIN_FAIL;
    }

    @Override
    public void composeEmail(MessageDraft message) {
        emailCompose.composeEmail(message);
        emailStatus = STATUS_DRAFTED;
    }

    @Override
    public ReadOnlyMessageDraft getEmailDraft() {
        return emailCompose.getMessage();
    }

    @Override
    public String getEmailStatus() {
        return emailStatus + emailLoginStatus;
    }

    @Override
    public void sendEmail() throws EmailLoginInvalidException, EmailMessageEmptyException,
            EmailRecipientsEmptyException, AuthenticationFailedException {
        logger.info("-------------------[Sending Email] ");

        emailSend.sendEmail(emailCompose, emailLogin);

        //reset the email draft after email have been sent
        emailStatus = STATUS_SENT;
        emailLoginStatus = String.format(STATUS_LOGIN_SENT, emailLogin.getEmailLogin());
        resetData();
    }

    @Override
    public void loginEmailAccount(String [] emailLoginDetails) throws EmailLoginInvalidException {
        emailLogin.loginEmail(emailLoginDetails);
        if (emailLogin.isUserLoggedIn()) {
            emailLoginStatus = String.format(STATUS_LOGIN_SUCCESS, emailLogin.getEmailLogin());
        } else {
            emailLoginStatus = STATUS_LOGIN_FAIL;
        }
    }

    /**
     * Returns true if the emailLogin contains user's login details
     */
    @Override
    public boolean isUserLoggedIn() {
        return emailLogin.isUserLoggedIn();
    }


    @Override
    public void clearEmailDraft() {
        resetData();
        emailStatus = STATUS_CLEARED;
        emailLoginStatus = "";
    }

    /**
     * Resets the existing data of this {@code emailCompose} and this {@code emailLogin}
     */
    private void resetData() {
        emailCompose.resetData();
        emailLogin.resetData();
    }

    @Override
    public boolean equals(Object obj) {

        // short circuit if same object
        if (obj == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(obj instanceof EmailManager)) {
            return false;
        }

        // state check
        EmailManager other = (EmailManager) obj;
        return this.emailCompose.equals(((EmailManager) obj).emailCompose)
                && this.emailLogin.equals(((EmailManager) obj).emailLogin);
    }
}
```
###### \java\seedu\address\email\EmailSend.java
``` java
/**
 * Handles the process of sending email via JavaAPI
 */
public class EmailSend {
    private Properties props;

    /**
     * Creates an EmailSend with an default properties
     */
    public EmailSend() {
        setUpEmailProperties();
    }

    /**
     * Sets up the default email properties
     */
    private void setUpEmailProperties() {
        props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.socketFactory.port", "465");
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.port", "465");
    }

    /**
     * Handles sending email process
     *
     * @param emailCompose contains message to be send
     * @param emailLogin contains login details of user
     * @throws EmailLoginInvalidException if login details is empty
     * @throws EmailMessageEmptyException if message is empty
     * @throws EmailRecipientsEmptyException if recipients list is empty
     * @throws AuthenticationFailedException if gmail account can't be logged in
     */
    public void sendEmail(EmailCompose emailCompose, EmailLogin emailLogin) throws EmailLoginInvalidException,
            EmailMessageEmptyException, EmailRecipientsEmptyException, AuthenticationFailedException {

        // Verify that the email draft consists of message and subject
        if (!emailCompose.getMessage().containsContent()) {
            throw new EmailMessageEmptyException();
        }

        // Verify that the user is logged in strictly using only Gmail account.
        if (!emailLogin.isUserLoggedIn()) {
            throw new EmailLoginInvalidException();
        }

        // Verify that Recipient field is not empty
        if (emailCompose.getMessage().getRecipient().isEmpty()) {
            throw new EmailRecipientsEmptyException();
        }

        // Send out email using JavaMail API
        sendingEmail(emailLogin.getEmailLogin(), emailLogin.getPassword(), emailCompose.getMessage());
    }

    /**
     * Sends email out using JavaMail API
     *
     * @param login email login account
     * @param pass email login password
     * @param message message to send
     * @throws AuthenticationFailedException if gmail account can't be logged in
     */
    private void sendingEmail(String login, String pass, ReadOnlyMessageDraft message)
            throws AuthenticationFailedException {
        final String username = login;
        final String password = pass;

        Session session = Session.getInstance(props, new javax.mail.Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        try {
            Message newMessage = new MimeMessage(session);
            newMessage.setFrom(new InternetAddress(username));
            InternetAddress recipientEmail = new InternetAddress(message.getRecipient());
            newMessage.setRecipient(Message.RecipientType.TO, recipientEmail);
            newMessage.setSubject(message.getSubject());
            if (message.getRelativeFilePath().isEmpty()) {
                newMessage.setText(message.getMessage());
            } else {
                BodyPart messageBodyPart = new MimeBodyPart();
                messageBodyPart.setText(message.getMessage());
                Multipart multipart = new MimeMultipart();
                multipart.addBodyPart(messageBodyPart);
                messageBodyPart = new MimeBodyPart();
                DataSource source = new FileDataSource(message.getRelativeFilePath());
                messageBodyPart.setDataHandler(new DataHandler(source));
                messageBodyPart.setFileName(message.getRelativeFilePath());
                multipart.addBodyPart(messageBodyPart);
                newMessage.setContent(multipart);
            }

            Transport.send(newMessage);
        } catch (AuthenticationFailedException e) {
            throw new AuthenticationFailedException();
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }
}
```
###### \java\seedu\address\email\exceptions\EmailLoginInvalidException.java
``` java
/**
 * Prompts user that email failed to send due to invalid login information
 */
public class EmailLoginInvalidException extends Exception{
}
```
###### \java\seedu\address\email\message\DraftList.java
``` java
/**
 * Contains a list of Email Message Drafts
 */
public class DraftList {
    private MessageDraft[] messages = new MessageDraft[1];

    public DraftList() {
        messages[0] = new MessageDraft();
    }

    /**
     * Compose a new email or edit the current one
     *
     * @param newMessage new email
     */
    public void composeEmail(MessageDraft newMessage) {
        MessageDraft message = messages[0];
        if (newMessage.getSubject().isEmpty()) {
            newMessage.setSubject(message.getSubject());
        }
        if (newMessage.getMessage().isEmpty()) {
            newMessage.setMessage(message.getMessage());
        }
        if (newMessage.getRecipient().isEmpty()) {
            newMessage.setRecipients(message.getRecipient());
        }
        if (newMessage.getRelativeFilePath().isEmpty()) {
            newMessage.setRelativeFilePath(message.getRelativeFilePath());
        }
        messages[0] = newMessage;
    }

    /**
     * Returns draft at requested index
     *
     * @param i index of message in draftlist
     * @return Unmodifiable message draft
     */
    public ReadOnlyMessageDraft getMessage(int i) {
        return messages[i];
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof DraftList // instanceof handles nulls
                && this.draftListEquals(((DraftList) other).messages));
    }

    /** Returns true if both have the same draft list */
    private boolean draftListEquals(MessageDraft [] other) {
        if (other.length == this.messages.length) {
            for (int i = 0; i < this.messages.length; i++) {
                if (!this.messages[i].equals(other[i])) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }
}
```
###### \java\seedu\address\email\message\MessageDraft.java
``` java
/**
 * Represents a Email Draft Message in MVP.
 */
public class MessageDraft implements ReadOnlyMessageDraft {

    private String message;
    private String subject;
    private String recipient;
    private String relativeFilePath;

    public MessageDraft() {
        message = "";
        subject = "";
        recipient = "";
        relativeFilePath = "";
    }

    public MessageDraft(String message, String subject, String recipient) {
        this.message = message;
        this.subject = subject;
        this.recipient = recipient;
        this.relativeFilePath = "";
    }

    public MessageDraft(String message, String subject, String recipient, String relativeFilePath) {
        this.message = message;
        this.subject = subject;
        this.recipient = recipient;
        this.relativeFilePath = relativeFilePath;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    @Override
    public String getRecipient() {
        return recipient;
    }

    public void setRecipients(String recipient) {
        this.recipient = recipient;
    }

    @Override
    public String getRelativeFilePath() {
        return relativeFilePath;
    }

    public void setRelativeFilePath(String relativeFilePath) {
        this.relativeFilePath = relativeFilePath;
    }

    @Override
    public boolean containsContent() {
        if (message.isEmpty() || subject.isEmpty() || recipient.isEmpty()) {
            return false;
        } else {
            return true;
        }
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof ReadOnlyMessageDraft // instanceof handles nulls
                && this.isSameStateAs((ReadOnlyMessageDraft) other));
    }
}
```
###### \java\seedu\address\email\message\ReadOnlyMessageDraft.java
``` java
/**
 * A read-only immutable interface for a email message in the addressbook.
 * Implementations should guarantee: details are present and not null, field values are validated.
 */
public interface ReadOnlyMessageDraft {
    String getMessage();
    String getSubject();
    String getRecipient();
    String getRelativeFilePath();

    /**
     * Returns true if message and subject is not empty
     */
    boolean containsContent();

    /**
     * Returns true if both have the same state. (interfaces cannot override .equals)
     */
    default boolean isSameStateAs(ReadOnlyMessageDraft other) {

        return other == this // short circuit if same object
                || (other != null // this is first to avoid NPE below
                && other.getMessage().equals(this.getMessage()) // state checks here onwards
                && other.getSubject().equals(this.getSubject())
                && other.getRecipient().equals(this.getRecipient())
                && other.getRelativeFilePath().equals(this.getRelativeFilePath()));
    }
}
```
###### \java\seedu\address\logic\commands\EmailCommand.java
``` java
/**
 * Composes an email draft or sends the draft out using gmail account
 */
public class EmailCommand extends Command {

    public static final String COMMAND_WORD = "email";
    public static final String COMMAND_ALIAS = "eml";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Emails all contacts in the last displayed list\n"
            + "Parameters: "
            + "email msg/MESSAGE subj/SUBJECT lgn/cineManager@gmail.com:password "
            + "recp/recipient@gmail.com func/<send|clear> [ attc/docs/example/file.txt ]\n"
            + "Examples:\n"
            + "email msg/message subj/subject lgn/test@gmail.com:password recp/contacts@gv.com "
            + "attc/docs/images/Architecture.png func/send";

    public static final String MESSAGE_SUCCESS = "Email have been %1$s";
    public static final String MESSAGE_LOGIN_INVALID = "You must be logged in with a gmail account to send an email.\n"
            + "Command: email lgn/<username@gmail.com>:<password>";
    public static final String MESSAGE_EMPTY_INVALID = "Your message and subject fields must not be empty when "
            + "sending an email.\n"
            + "Command: email msg/<messages> sub/<subjects>";
    public static final String MESSAGE_RECIPIENT_INVALID = "You must have at least 1 recipient to send the email to.";
    public static final String MESSAGE_AUTHENTICATION_FAIL = "MVP is unable to log in to your gmail account. Please "
            + "check the following:\n"
            + "1) You have entered the correct email address and password.\n"
            + "2) You have enabled 'Allow less secure app' in your gmail account settings.";
    public static final String MESSAGE_FAIL_UNKNOWN = "An unexpected error have occurred. Please try again later.";

    private final MessageDraft messageDraft;
    private final String[] emailLoginDetails;
    private final EmailFunction emailFunction;

    public EmailCommand(
            String message, String subject, String recipient, String fileRelativePath,
            String [] emailLoginDetails, EmailFunction emailFunction) {
        this.messageDraft = new MessageDraft(message, subject, recipient, fileRelativePath);
        this.emailFunction = emailFunction;
        this.emailLoginDetails = emailLoginDetails;
    }

    /**
     * Identifies the Email Command Execution Task purpose
     *
     * @throws EmailLoginInvalidException if email login details is empty
     * @throws EmailMessageEmptyException if email message is empty
     * @throws EmailRecipientsEmptyException if recipients list is empty
     * @throws AuthenticationFailedException if gmail account can't be logged in
     */
    private void identifyEmailTask() throws EmailLoginInvalidException, EmailMessageEmptyException,
            EmailRecipientsEmptyException, AuthenticationFailedException {
        switch (emailFunction.getEmailFunction()) {
        case EmailFunction.EMAIL_FUNCTION_SEND:
            model.sendEmail(messageDraft);
            break;
        case EmailFunction.EMAIL_FUNCTION_CLEAR:
            model.clearEmailDraft();
            break;
        default:
            model.draftEmail(messageDraft);
            break;
        }
    }

    @Override
    public CommandResult execute() throws CommandException {
        requireNonNull(model);

        try {
            model.loginEmailAccount(emailLoginDetails);
            identifyEmailTask();
            return new CommandResult(String.format(MESSAGE_SUCCESS, model.getEmailStatus()));
        } catch (EmailLoginInvalidException e) {
            throw new CommandException(MESSAGE_LOGIN_INVALID);
        } catch (EmailMessageEmptyException e) {
            throw new CommandException(MESSAGE_EMPTY_INVALID);
        } catch (EmailRecipientsEmptyException e) {
            throw new CommandException(MESSAGE_RECIPIENT_INVALID);
        } catch (AuthenticationFailedException e) {
            throw new CommandException(MESSAGE_AUTHENTICATION_FAIL);
        } catch (RuntimeException e) {
            throw new CommandException(MESSAGE_FAIL_UNKNOWN);
        }
    }

    /**
     * Extracts Email from input recipient {@code recipient} into an InternetAddresss[] for sending email
     *
     * @param: recipient
     * @return: list of internet email address
     */
    public InternetAddress[] extractEmailFromContacts(String recipient) throws AddressException {
        InternetAddress[] recipientsEmail = new InternetAddress[1];
        try {
            recipientsEmail[0] = new InternetAddress(recipient);
        } catch (AddressException e) {
            throw new AddressException();
        }
        return recipientsEmail;
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof EmailCommand // instanceof handles nulls
                && ((EmailCommand) other).messageDraft.equals(this.messageDraft)
                && ((EmailCommand) other).loginDetailsEquals(this.emailLoginDetails)
                && ((EmailCommand) other).emailFunction.equals(this.emailFunction));
    }

    /**
     * Returns true for correct login details.
     */
    private boolean loginDetailsEquals(String [] other) {
        if (this.emailLoginDetails.length == other.length) {
            for (int i = 0; i < this.emailLoginDetails.length; i++) {
                if (this.emailLoginDetails[i] != other[i]) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }
}
```
###### \java\seedu\address\logic\parser\EmailCommandParser.java
``` java
/**
 * Parses input arguments and creates a new EmailCommand object
 */
public class EmailCommandParser implements Parser<EmailCommand> {

    /**
     * Parses the given {@code String} of arguments in the context of the EmailCommand
     * and returns an EmailCommand object for execution.
     *
     * @throws ParseException if the user input does not conform the expected format
     */
    public EmailCommand parse(String args) throws ParseException {
        requireNonNull(args);
        ArgumentMultimap argMultimap =
                ArgumentTokenizer.tokenize(args, PREFIX_EMAIL_MESSAGE, PREFIX_EMAIL_SUBJECT, PREFIX_EMAIL_LOGIN,
                        PREFIX_EMAIL_FUNCTION, PREFIX_EMAIL_RECIPIENT, PREFIX_EMAIL_ATTACHMENT);

        EmailFunction emailFunction = new EmailFunction();
        String message;
        String subject;
        String recipient;
        String fileRelativePath;
        String[] loginDetails;

        try {
            message = getArgumentMessage(argMultimap);
            subject = getArgumentSubject(argMultimap);
            recipient = getArgumentRecipient(argMultimap);
            fileRelativePath = getArgumentFileRelativePath(argMultimap);
            loginDetails = getArgumentLoginDetails(argMultimap);
            emailFunction = getArgumentEmailFunction(argMultimap, emailFunction);

            if (message.isEmpty() && subject.isEmpty() && recipient.isEmpty() &&  (
                    loginDetails.length == 0) && !emailFunction.isValid()) {
                throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, EmailCommand.MESSAGE_USAGE));
            }

        } catch (IllegalValueException e) {
            throw new ParseException(e.getMessage(), e);
        }

        return new EmailCommand(message, subject, recipient, fileRelativePath, loginDetails, emailFunction);
    }

    /**
     * Returns argument message values if available
     *
     * @param argMultimap
     * @return argument message values
     * @throws IllegalValueException if value is empty
     */
    private String getArgumentMessage(ArgumentMultimap argMultimap) throws IllegalValueException {
        String message = "";

        if (argMultimap.getValue(PREFIX_EMAIL_MESSAGE).isPresent()) {
            message = ParserUtil.parseEmailMessage(argMultimap.getValue(PREFIX_EMAIL_MESSAGE)).trim();
            if (message.isEmpty()) {
                throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, EmailCommand.MESSAGE_USAGE));
            }
        }
        return message;
    }

    /**
     * Returns argument subject values if available
     *
     * @param argMultimap
     * @return argument subject values
     * @throws IllegalValueException if value is empty
     */
    private String getArgumentSubject(ArgumentMultimap argMultimap) throws IllegalValueException {
        String subject = "";

        if (argMultimap.getValue(PREFIX_EMAIL_SUBJECT).isPresent()) {
            subject = ParserUtil.parseEmailMessage(argMultimap.getValue(PREFIX_EMAIL_SUBJECT)).trim();
            if (subject.isEmpty()) {
                throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, EmailCommand.MESSAGE_USAGE));
            }
        }
        return subject;
    }

    /**
     * Returns argument fileRelativePath values if available
     *
     * @param argMultimap
     * @return argument fileRelativePath values
     * @throws IllegalValueException if value is empty
     */
    private String getArgumentFileRelativePath(ArgumentMultimap argMultimap) throws IllegalValueException {
        String fileRelativePath = "";

        if (argMultimap.getValue(PREFIX_EMAIL_ATTACHMENT).isPresent()) {
            fileRelativePath = ParserUtil.parseEmailMessage(argMultimap.getValue(PREFIX_EMAIL_ATTACHMENT)).trim();
            if (fileRelativePath.isEmpty()) {
                throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, EmailCommand.MESSAGE_USAGE));
            }
        }
        return fileRelativePath;
    }

    /**
     * Returns argument login values if available
     *
     * @param argMultimap
     * @return argument login values
     * @throws IllegalValueException if value is empty
     */
    private String [] getArgumentLoginDetails(ArgumentMultimap argMultimap) throws IllegalValueException {
        String login = "";
        String [] loginDetails = new String[0];

        if (argMultimap.getValue(PREFIX_EMAIL_LOGIN).isPresent()) {
            login = ParserUtil.parseEmailLoginDetails(argMultimap.getValue(PREFIX_EMAIL_LOGIN)).trim();
            loginDetails = login.split(":");
            if (loginDetails.length != 2) {
                throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, EmailCommand.MESSAGE_USAGE));
            }
        }
        return loginDetails;
    }

    /**
     * Returns argument emailFunction values if available
     *
     * @param argMultimap
     * @param emailFunction new EmailFunction object
     * @return emailFunction with argument emailFunction values
     * @throws IllegalValueException if emailFunction is not "send" or "clear"
     */
    private EmailFunction getArgumentEmailFunction(
            ArgumentMultimap argMultimap, EmailFunction emailFunction) throws IllegalValueException {
        if (argMultimap.getValue(PREFIX_EMAIL_FUNCTION).isPresent()) {
            emailFunction.setEmailFunction(
                    ParserUtil.parseEmailTask(argMultimap.getValue(PREFIX_EMAIL_FUNCTION)).trim());
            if (!emailFunction.isValid()) {
                throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, EmailCommand.MESSAGE_USAGE));
            }
        }
        return emailFunction;
    }

    /**
     * Returns argument recipient values if available
     *
     * @param argMultimap
     * @return argument recipient values
     * @throws IllegalValueException if value is empty
     */
    private String getArgumentRecipient(ArgumentMultimap argMultimap) throws IllegalValueException {
        String recipient = "";

        if (argMultimap.getValue(PREFIX_EMAIL_RECIPIENT).isPresent()) {
            recipient = ParserUtil.parseEmailMessage(argMultimap.getValue(PREFIX_EMAIL_RECIPIENT)).trim();
            if (recipient.isEmpty()) {
                throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, EmailCommand.MESSAGE_USAGE));
            }
        }
        return recipient;
    }
}
```
###### \java\seedu\address\ui\CommandBox.java
``` java
    /**
     * Split text in text field into respective components for processing
     */
    private void checkChangesInCommandBoxInput() {
        String textFromTextField = commandTextField.getText();
        String[] allWordsFromText = textFromTextField.split(" ");
        String commandKeyWord = "";
        int index = 0;

        while (allWordsFromText.length > 0 && index < allWordsFromText.length && allWordsFromText[index].equals("")) {
            if (index < allWordsFromText.length) {
                index++;
            } else {
                break;
            }
            if (index < allWordsFromText.length && (!allWordsFromText[index].equals(""))) {
                break;
            }
        }

        if (index < allWordsFromText.length && allWordsFromText.length > 0) {
            commandKeyWord = allWordsFromText[index];
        }

        makeKeywordLabelNonVisible();

        if (isValidCommandKeyword(commandKeyWord)) {
            makeKeywordLabelVisible(commandKeyWord, index);
        }
        commandTextField.setStyle(defaultFontSize);
    }

```
###### \java\seedu\address\ui\CommandBox.java
``` java
    /**
     * Creates a label to replace the command keyword
     */
    private void makeKeywordLabelVisible(String commandKeyword, int offset) {
        keywordLabel.setId("commandKeywordLabel");
        keywordLabel.setText(commandKeyword);
        keywordLabel.setVisible(true);
        keywordLabel.getStyleClass().clear();
        Insets textFieldOffsets = new Insets(0, 0, 0, (offset * OFFSET_MULTIPLIER) + DEFAULT_TAG_OFFSET_VALUE);

        stackPane.setAlignment(keywordLabel, Pos.CENTER_LEFT);
        stackPane.setMargin(keywordLabel, textFieldOffsets);

        String keywordTextColor = keywordColorCode.get(commandKeyword);
        keywordLabel.setStyle("-fx-text-fill: " + keywordTextColor + ";\n"
            + "-fx-background-radius: 2;\n"
            + "-fx-font-size: " + KEYWORD_LABEL_FONT_SIZE + ";\n"
            + "-fx-background-color: " + KEYWORD_LABEL_BACKGROUND_COLOR + ";");
        keywordLabel.toFront();
    }

    /**
     * Set the keyword label to be non-visible
     */
    private void makeKeywordLabelNonVisible() {
        keywordLabel.setVisible(false);
    }

    /**
     * Check if input keyword is a valid command keyword
     * @param commandKeyword
     */
    private boolean isValidCommandKeyword(String commandKeyword) {
        return keywordColorCode.containsKey(commandKeyword);
    }

    /**
     * Updates the text field with the previous input in {@code historySnapshot},
     * if there exists a previous input in {@code historySnapshot}
     */
    private void navigateToPreviousInput() {
        assert historySnapshot != null;
        if (!historySnapshot.hasPrevious()) {
            return;
        }

        replaceText(historySnapshot.previous());
    }

    /**
     * Updates the text field with the next input in {@code historySnapshot},
     * if there exists a next input in {@code historySnapshot}
     */
    private void navigateToNextInput() {
        assert historySnapshot != null;
        if (!historySnapshot.hasNext()) {
            return;
        }

        replaceText(historySnapshot.next());
    }

    /**
     * Sets {@code CommandBox}'s text field with {@code text} and
     * positions the caret to the end of the {@code text}.
     */
    private void replaceText(String text) {
        commandTextField.setText(text);
        commandTextField.positionCaret(commandTextField.getText().length());
    }

    /**
     * Handles the Enter button pressed event.
     */
    @FXML
    private void handleCommandInputChanged() {
        try {
            CommandResult commandResult = logic.execute(commandTextField.getText());
            initHistory();
            historySnapshot.next();
            // process result of the command
            commandTextField.setText("");
            logger.info("Result: " + commandResult.feedbackToUser);
            raise(new NewResultAvailableEvent(commandResult.feedbackToUser));
            makeKeywordLabelNonVisible();
        } catch (CommandException | ParseException e) {
            initHistory();
            // handle command failure
            setStyleToIndicateCommandFailure();
            logger.info("Invalid command: " + commandTextField.getText());
            raise(new NewResultAvailableEvent(e.getMessage()));
        }
    }

    /**
     * Initializes the history snapshot.
     */
    private void initHistory() {
        historySnapshot = logic.getHistorySnapshot();
        // add an empty string to represent the most-recent end of historySnapshot, to be shown to
        // the user if she tries to navigate past the most-recent end of the historySnapshot.
        historySnapshot.add("");
    }

    /**
     * Sets the command box style to use the default style.
     */
    private void setStyleToDefault() {
        commandTextField.getStyleClass().remove(ERROR_STYLE_CLASS);
    }

    /**
     * Sets the command box style to indicate a failed command.
     */
    private void setStyleToIndicateCommandFailure() {
        ObservableList<String> styleClass = commandTextField.getStyleClass();

        if (styleClass.contains(ERROR_STYLE_CLASS)) {
            return;
        }

        styleClass.add(ERROR_STYLE_CLASS);
    }

```
###### \java\seedu\address\ui\CommandBox.java
``` java
    /**
     * Assign keywords with color coding
     */
    public HashMap<String, String> initializeKeywordColorCoding() {
        HashMap<String, String> keywordColorCode = new HashMap<>();
        keywordColorCode.put(AddCommand.COMMAND_WORD, DEFAULT_KEYWORD_COLOR);
        keywordColorCode.put(AddCommand.COMMAND_ALIAS, DEFAULT_KEYWORD_COLOR);
        keywordColorCode.put(AddMovieCommand.COMMAND_WORD, DEFAULT_KEYWORD_COLOR);
        keywordColorCode.put(AddMovieCommand.COMMAND_ALIAS, DEFAULT_KEYWORD_COLOR);
        keywordColorCode.put(AddTheaterCommand.COMMAND_WORD, DEFAULT_KEYWORD_COLOR);
        keywordColorCode.put(AddTheaterCommand.COMMAND_ALIAS, DEFAULT_KEYWORD_COLOR);
        keywordColorCode.put(AddScreeningCommand.COMMAND_WORD, DEFAULT_KEYWORD_COLOR);
        keywordColorCode.put(AddScreeningCommand.COMMAND_ALIAS, DEFAULT_KEYWORD_COLOR);
        keywordColorCode.put(ClearCommand.COMMAND_WORD, DEFAULT_KEYWORD_COLOR);
        keywordColorCode.put(ClearCommand.COMMAND_ALIAS, DEFAULT_KEYWORD_COLOR);
        keywordColorCode.put(DecryptCommand.COMMAND_WORD, DEFAULT_KEYWORD_COLOR);
        keywordColorCode.put(DecryptCommand.COMMAND_ALIAS, DEFAULT_KEYWORD_COLOR);
        keywordColorCode.put(DeleteCommand.COMMAND_WORD, DEFAULT_KEYWORD_COLOR);
        keywordColorCode.put(DeleteCommand.COMMAND_ALIAS, DEFAULT_KEYWORD_COLOR);
        keywordColorCode.put(DeleteMovieCommand.COMMAND_WORD, DEFAULT_KEYWORD_COLOR);
        keywordColorCode.put(DeleteMovieCommand.COMMAND_ALIAS, DEFAULT_KEYWORD_COLOR);
        keywordColorCode.put(DeleteTheaterCommand.COMMAND_WORD, DEFAULT_KEYWORD_COLOR);
        keywordColorCode.put(DeleteTheaterCommand.COMMAND_ALIAS, DEFAULT_KEYWORD_COLOR);
        keywordColorCode.put(DeleteScreeningCommand.COMMAND_WORD, DEFAULT_KEYWORD_COLOR);
        keywordColorCode.put(DeleteScreeningCommand.COMMAND_ALIAS, DEFAULT_KEYWORD_COLOR);
        keywordColorCode.put(EditCommand.COMMAND_WORD, DEFAULT_KEYWORD_COLOR);
        keywordColorCode.put(EditCommand.COMMAND_ALIAS, DEFAULT_KEYWORD_COLOR);
        keywordColorCode.put(EmailCommand.COMMAND_WORD, DEFAULT_KEYWORD_COLOR);
        keywordColorCode.put(EmailCommand.COMMAND_ALIAS, DEFAULT_KEYWORD_COLOR);
        keywordColorCode.put(EncryptCommand.COMMAND_WORD, DEFAULT_KEYWORD_COLOR);
        keywordColorCode.put(EncryptCommand.COMMAND_ALIAS, DEFAULT_KEYWORD_COLOR);
        keywordColorCode.put(ExitCommand.COMMAND_WORD, DEFAULT_KEYWORD_COLOR);
        keywordColorCode.put(FindCommand.COMMAND_WORD, DEFAULT_KEYWORD_COLOR);
        keywordColorCode.put(FindCommand.COMMAND_ALIAS, DEFAULT_KEYWORD_COLOR);
        keywordColorCode.put(FindMovieCommand.COMMAND_WORD, DEFAULT_KEYWORD_COLOR);
        keywordColorCode.put(FindMovieCommand.COMMAND_ALIAS, DEFAULT_KEYWORD_COLOR);
        keywordColorCode.put(HelpCommand.COMMAND_WORD, DEFAULT_KEYWORD_COLOR);
        keywordColorCode.put(HelpCommand.COMMAND_ALIAS, DEFAULT_KEYWORD_COLOR);
        keywordColorCode.put(HistoryCommand.COMMAND_WORD, DEFAULT_KEYWORD_COLOR);
        keywordColorCode.put(HistoryCommand.COMMAND_ALIAS, DEFAULT_KEYWORD_COLOR);
        keywordColorCode.put(JumpCommand.COMMAND_WORD, DEFAULT_KEYWORD_COLOR);
        keywordColorCode.put(JumpCommand.COMMAND_ALIAS, DEFAULT_KEYWORD_COLOR);
        keywordColorCode.put(ListCommand.COMMAND_WORD, DEFAULT_KEYWORD_COLOR);
        keywordColorCode.put(ListCommand.COMMAND_ALIAS, DEFAULT_KEYWORD_COLOR);
        keywordColorCode.put(RedoCommand.COMMAND_WORD, DEFAULT_KEYWORD_COLOR);
        keywordColorCode.put(RedoCommand.COMMAND_ALIAS, DEFAULT_KEYWORD_COLOR);
        keywordColorCode.put(SelectCommand.COMMAND_WORD, DEFAULT_KEYWORD_COLOR);
        keywordColorCode.put(SelectCommand.COMMAND_ALIAS, DEFAULT_KEYWORD_COLOR);
        keywordColorCode.put(UndoCommand.COMMAND_WORD, DEFAULT_KEYWORD_COLOR);
        keywordColorCode.put(UndoCommand.COMMAND_ALIAS, DEFAULT_KEYWORD_COLOR);
        return keywordColorCode;
    }

}
```
###### \java\seedu\address\ui\EmailMessagePanel.java
``` java
/**
 * A ui for the display of the current email draft
 */
public class EmailMessagePanel extends UiPart<Region> {
    private static final Logger logger = LogsCenter.getLogger(ResultDisplay.class);
    private static final String FXML = "EmailMessagePanel.fxml";

    private final StringProperty messageDisplay = new SimpleStringProperty("");
    private final StringProperty recipientsDisplay = new SimpleStringProperty("");
    private final StringProperty subjectDisplay = new SimpleStringProperty("");

    @FXML
    private TextArea messageArea;
    @FXML
    private TextArea recipientsArea;
    @FXML
    private TextArea subjectArea;


    public EmailMessagePanel() {
        super(FXML);

        messageArea.setWrapText(true);
        recipientsArea.setWrapText(true);
        subjectArea.setWrapText(true);

        messageArea.textProperty().bind(messageDisplay);
        recipientsArea.textProperty().bind(recipientsDisplay);
        subjectArea.textProperty().bind(subjectDisplay);
        registerAsAnEventHandler(this);
    }

    @Subscribe
    private void handleEmailDraftChangedEvent(EmailDraftChangedEvent event) {
        logger.info(LogsCenter.getEventHandlingLogMessage(event));
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                messageDisplay.setValue(event.message.getMessage());
                recipientsDisplay.setValue(event.message.getRecipient());
                subjectDisplay.setValue(event.message.getSubject());
            }
        });
    }
}
```
###### \java\seedu\address\ui\MainWindow.java
``` java
    /**
     * Toggle to next tab in Main Window.
     */
    @FXML
    public void handleToggleNextTab() {
        tabsPanel.toggleTabs(TOGGLE_TO_NEXT_TAB);
    }

    void show() {
        primaryStage.show();
    }

    /**
     * Closes the application.
     */
    @FXML
    private void handleExit() {
        raise(new ExitAppRequestEvent());
    }

    @Subscribe
    private void handleShowHelpEvent(ShowHelpRequestEvent event) {
        logger.info(LogsCenter.getEventHandlingLogMessage(event));
        handleHelp();
    }
}
```
