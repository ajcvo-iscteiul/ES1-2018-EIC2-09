package channels;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.mail.Address;
import javax.mail.BodyPart;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import gui.MainWindows;

/**
 * 
 * 
 * 
 * 
 * This class is used by the application so that the user can send and see his
 * daily email's and as also some other methods that allow the manipulation of
 * the XML file "config.xml".
 * 
 * @author jhmsa-iscteiul
 * @since 2018
 */

public class email {
	private Store store;
	private static File file = new File("config.xml");
	private static DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	private String username;
	private String password;
	public MainWindows mainWindow;
//	public ArrayList<Notification> receivedEmail;


	public email(String username, String password) {
		this.username = username;
		this.password = password;
//		 addCredentials();
		 
		connect();
//		searchEmail();
//		send("testees1111@outlook.pt", "username","subject","sdfjg");
		System.out.println(getDate());
	}

	
	/**
	 * 
	 * 
	 * This method does the connection to the Outlook servers and id the user doens't 
	 * have already his credentials saved in the XML file "config.xml" the method invokes 
	 * the method that saves the credentials in that XML file.
	 
	 * @exception MessagingException
	 */

	protected void connect(/* String username, String password */) {
		Properties properties = new Properties();

		properties.put("mail.imap.host", "imap-mail.outlook.com");
		properties.put("mail.imap.port", 993);

		properties.setProperty("mail.imap.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
		properties.setProperty("mail.imap.socketFactory.fallback", "false");
		properties.setProperty("mail.imap.socketFactory.port", String.valueOf(993));

		Session session = Session.getDefaultInstance(properties);

		try {
			/* Connect to the message Store */

			store = session.getStore("imap");
			store.connect(username, password);
			System.out.println("Conectou");

			if (!isRegistered(/* username, password */))
				addCredentials(/* username, password */);
		} catch (MessagingException e) {
//			e.printStackTrace();
			System.out.println("not connected");
		}

	}

	/**
	 * 
	 * This method searches all users email's from his IXBOX folder and the INBOX folder isn't empty
	 * this method invokes the method that stores the email's into an ArrayList. 
	 * 
	 * 
	 * @param username
	 *            User email address
	 * @param password
	 *            Password from the user email address
	 * @return 
	 * 
	 * @exception MessagingException
	 * 
	 */

	public ArrayList<Notification> searchEmail(/* String username, String password */) {
		ArrayList<Notification> receivedEmail = new ArrayList<>();
				
		try {

			/* open Inbox folder */
			Folder folderInbox = store.getFolder("INBOX");
			folderInbox.open(Folder.READ_ONLY);

			Message[] foundMessages = folderInbox.getMessages();
			if (foundMessages.length == 0) {
				System.out.println("Your Inbox is empty!");
			} 
			else {
				System.out.println("zete");
				
				receivedEmail = addEmailList(foundMessages, receivedEmail);
				System.out.println(receivedEmail.size());
//				showInbox(foundMessages);
			}

			folderInbox.close(false);
			// store.close();

		} catch (MessagingException e) {
			e.printStackTrace();
		}
		System.out.println(receivedEmail);
		return receivedEmail;
	}
//	private void showInbox(Message[] foundMessages) {
//		for (int i = foundMessages.length - 1; i >= 0; i--) {
//			Message message = foundMessages[i];
//			try {
//				System.out.println("From: " + message.getFrom()[0]);
//				System.out.println("Subject: " + message.getSubject());
//
//				System.out.println("Date: " + message.getSentDate());
//
//				System.out.println("");
//				System.out.println("------------: " + i + ":---------------");
//			} catch (MessagingException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//
//		}
//
//	}
//	
	/**
	 * 
	 * 
	 * This method is used to add an email to the main window of the application and Store that email
	 * into an ArrayList of Notification
	 * 
	 * @param foundMessage - Array with all of the received emails
	 * 
	 * @return String with the source of an email received
	 * 
	 */
	private ArrayList<Notification> addEmailList(Message[] foundMessages, ArrayList<Notification> receivedEmail) {
		try {
			for (int i = foundMessages.length - 1; i >= 0; i--) {
				if (foundMessages[i].getReceivedDate().after(getDate())) {
					System.out.println("emtra?");
					Date date = foundMessages[i].getReceivedDate();
					String source = foundMessages[i].getFrom()[0].toString().split("<")[1].split(">")[0];/*getSource(foundMessages[i])/* foundMessages[i].getFrom()[0] */
					String subject = foundMessages[i].getSubject();
					String text = getTextFromMessage(foundMessages[i]);
					Notification notification = new Notification("E-mail", date, source, subject, text);
					System.out.println(notification.getSubject());
					receivedEmail.add(notification);
//					mainWindow.addNotification(/* date, "email", source, subject */notification);
				
				}else {
					break;					
				}
				System.out.println(i);
			}
		} catch (MessagingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("chega");
		return receivedEmail;
	}
	/**
	 * 
	 * 
	 * This method is used to convert an array with all the senders froma received email
	 * into a String to show in the application
	 * 
	 * @param Message message email receiver
	 * 
	 * @return String with the source of an email received
	 * 
	 */
	
	private String getSource(Message message) {
		String source = "";
		try {
			for (int i = 0; i < message.getFrom().length; i++) {
				source += message.getFrom()[i];
			}
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return source;
	}

//	/**
//	 * 
//	 * O m�todo showInbox() imprime na consola o emissor do email, o Assunto e a
//	 * data de envio
//	 *
//	 * @param foundMessages
//	 *            Array com todas as Mensagens da pasta Inbox do utilizador
//	 * @exception MessagingException
//	 *
//	 */
//	private void showInbox(Message[] foundMessages) {
//		for (int i = foundMessages.length - 1; i >= 0; i--) {
//			Message message = foundMessages[i];
//			try {
//				System.out.println("From: " + message.getFrom()[0]);
//				System.out.println("Subject: " + message.getSubject());
//
//				System.out.println("Date: " + message.getSentDate());
//
//				System.out.println("");
//				System.out.println("------------: " + i + ":---------------");
//
//			} catch (MessagingException e) {
//				e.printStackTrace();
//			}
//
//		}
//
//	}

	/**
	 * 
	 * 
	 * This method is used to send an email to other user. To send the email firstly the method
	 * authenticates the user and then sends the email
	 * 
	 * @param receiverEmail Recipient email addredd
	 *            
	 * @param username
	 *            Sender email address
	 * @param password
	 *            Password from the user email address
	 * @param subject
	 *            subject to be sent in the email
	 * @param msg
	 *            body text to be sent in the email
	 * 
	 * @exception MessagingException
	 * 
	 */

	public void send(String receiverEmail, String username, /* String password, */ String subject, String msg) {

		// ************************************
		// ********** Properties **************
		// ************************************

		Properties props = new Properties();

		props.put("mail.smtp.host", "smtp-mail.outlook.com");
		props.put("mail.smtp.port", "587");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.auth", "true");

		// ************************************
		// ********** Authentication **********
		// ************************************
		Session session = Session.getInstance(props, new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(username, password);
			}
		});

		try {

			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(username));
			message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(receiverEmail));
			message.setSubject(subject);

			message.setContent(msg, "text/html");

			Transport transport = session.getTransport("smtp");
			transport.connect("smtp-mail.outlook.com", 587, username, password);
			transport.sendMessage(message, message.getAllRecipients());
			System.out.println("Mail sent successfully at: " + receiverEmail);
			transport.close();

		} catch (MessagingException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 
	 * This method is used to add credentials from an user to the XML file "config.xml"
	 * 
	 * @param username
	 *            User email address
	 * @param password
	 *            Password from the user email address
	 *
	 * @exception SAXException
	 * @exception IOException
	 * @exception ParserConfigurationException
	 * @exception TransformerConfigurationException
	 * @exception TransformerException
	 * 
	 */

	protected void addCredentials(/* String username, String password */) {
		if (!isRegistered(/* username, password */)) {
			try {
				DocumentBuilder builder = factory.newDocumentBuilder();
				Document xmlDoc = builder.parse(file);

				NodeList root = xmlDoc.getElementsByTagName("Credentials");

				Element credential = xmlDoc.createElement("credential");
				credential.setAttribute("Username", username);
				credential.setAttribute("Password", password);
				root.item(0).appendChild(credential);

				DOMSource source = new DOMSource(xmlDoc);

				TransformerFactory transformerFactory = TransformerFactory.newInstance();
				Transformer transformer = transformerFactory.newTransformer();
				transformer.setOutputProperty(OutputKeys.INDENT, "yes");
				transformer.setOutputProperty(OutputKeys.METHOD, "xml");
				StreamResult result = new StreamResult("config.xml");
				transformer.transform(source, result);
			} catch (ParserConfigurationException e) {
				e.printStackTrace();
			} catch (SAXException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (TransformerConfigurationException e) {
				e.printStackTrace();
			} catch (TransformerException e) {
				e.printStackTrace();
			}

		}
	}

	/**
	 * 
	 * This method verifies if the user is already registered in XML file "config.xml"
	 * 
	 * 
	 * @param username
	 *            User email address
	 * @param password
	 *            Password from the user email address
	 *            
	 * @exception SAXException
	 * @exception IOException
	 * 
	 */
	public boolean isRegistered(/* String username, String password */) {

		try {

			DocumentBuilder builder = factory.newDocumentBuilder();

			Document xmlDoc = builder.parse(file);

			NodeList credentialList = xmlDoc.getElementsByTagName("credential");

			if (credentialList.getLength() == 0)
				return false;

			for (int i = 0; i < credentialList.getLength(); i++) {
				NamedNodeMap nodeAttributtes = credentialList.item(i).getAttributes();
				if (nodeAttributtes.item(1).getNodeValue().equals(username)) {
					if (nodeAttributtes.item(0).getNodeValue().equals(password)) {
						return true;
					}
				}
			}
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * This method is used to delete user credentials from the XML file "config.xml"
	 * 
	 * 
	 * @param username -
	 *            User email address
	 * @param password -
	 *            Password from the user email address
	 *
	 * @exception SAXException
	 * @exception IOException
	 * @exception ParserConfigurationException
	 * @exception TransformerConfigurationException
	 * @exception TransformerException
	 * 
	 */
	protected void deleteCredentials(/* String username, String password */) {
		if (isRegistered(/* username, password */)) {
			try {
				DocumentBuilder builder = factory.newDocumentBuilder();
				Document xmlDoc = builder.parse(file);

				NodeList credentialList = xmlDoc.getElementsByTagName("credential");

				for (int i = 0; i < credentialList.getLength(); i++) {
					NamedNodeMap nodeAttributtes = credentialList.item(i).getAttributes();
					if (nodeAttributtes.item(1).getNodeValue().equals(username)) {
						Element toDelete = (Element) credentialList.item(i);
						toDelete.getParentNode().removeChild(toDelete);
						i = credentialList.getLength();
					}
				}
				DOMSource source = new DOMSource(xmlDoc);

				TransformerFactory transformerFactory = TransformerFactory.newInstance();
				Transformer transformer = transformerFactory.newTransformer();
				transformer.setOutputProperty(OutputKeys.INDENT, "yes");
				transformer.setOutputProperty(OutputKeys.METHOD, "xml");
				StreamResult result = new StreamResult("config.xml");
				transformer.transform(source, result);

			} catch (SAXException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ParserConfigurationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (TransformerConfigurationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (TransformerException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	/**
	 * 
	 * This method is a getter for the attribute store
	 * 
	 * @return Store
	 */

	public Store getStore() {
		return store;
	}
	
	/**
	 * 
	 * This method is a getter for the attribute username
	 * 
	 * @return Store
	 */
	public String getUsername() {
		return username;
	}
	
	/**
	 * 
	 * This method is a getter for the attribute password
	 * 
	 * @return Store
	 */
	public String getPassword() {
		return password;
	}
	
	
	
	/**
	 * This method is used to get the Text from an email in a String format. If the message is multipart
	 * the method invokes other method to translate the parameter from Message to String
	 * 
	 * @param Message - message to be "translated" from Message to String 
	 * @return email content in a String format
	 */

	private String getTextFromMessage(Message message) throws MessagingException, IOException {
		String text = "";
		if (message.isMimeType("text/plain")) {
			text = message.getContent().toString();
		} else if (message.isMimeType("multipart/*")) {
			MimeMultipart mimeMultipart = (MimeMultipart) message.getContent();
			text = getTextFromMimeMultipart(mimeMultipart);
		}
		return text;
	}


	/**
	 * This method is used to get the Text from an email in a String format. This method is invoked
	 * by the method getTextFromMessage.
	 * 
	 * @param MimeMultipart - message to be translated from MimeMultipart to String 
	 * 
	 * @return email content in a String format
	 */
	
	private String getTextFromMimeMultipart(MimeMultipart mimeMultipart) throws MessagingException, IOException {
		String text = "";
		int count = mimeMultipart.getCount();
		for (int i = 0; i < count; i++) {
			BodyPart bodyPart = mimeMultipart.getBodyPart(i);
			if (bodyPart.isMimeType("text/plain")) {
				text = text + "\n" + bodyPart.getContent();
				break; //necess�rio para n�o aparecer texto repetido

			} else if (bodyPart.getContent() instanceof MimeMultipart) {
				text = text + getTextFromMimeMultipart((MimeMultipart) bodyPart.getContent());
			}
		}
		return text;
	}
	
	/**
	 * This method is a setter for the class attributte mainWindow
	 * 
	 * @param MainWindows - this class main application window
	 * 
	 */
	public void setMainWindow(MainWindows mainWindow) {
		this.mainWindow = mainWindow;
	}
	
	/**
	 * This method is used to get the precise date at midnight
	 * this method is used to check which email's were received in the day the application is being used.
	 * 
	 * @return the date at midnight that this method is run
	 * 
	 */
	private Date getDate() {
		Date today = new Date();
		@SuppressWarnings("deprecation")
		Date today2 = new Date(today.getYear(),today.getMonth(), today.getDate(),0,0,0);
		return today2;
	}

	/**
	 * 
	 *	 The main method is only created to run some testing before the class is united with
	 * the application GUI
	 * 
	 * @param args
	 */

	public static void main(String[] args) {

		String username = "testees1111@outlook.pt";
		String password = "mailTESTE123";
		email e = new email(username, password);
		List<Notification>n= e.searchEmail();
		System.out.println(n.get(0));
		// Scanner in = new Scanner(System.in);
		//
		// System.out.println("Email: ");
		// String username = in.nextLine();
		//
		// System.out.println("Pass: ");
		// String password = in.nextLine();

//		new email(username, password);

		// email.connect(username, password);

		// email.addCredentials(username, password);
		// email.isRegistred(username, password);
		// email.searchEmail(username, password);
		// email.deleteCredentials(username, password);

	}

}
