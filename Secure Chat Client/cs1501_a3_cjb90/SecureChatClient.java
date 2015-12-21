import java.util.*;
import java.io.*;
import java.net.*;
import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.math.*;

public class SecureChatClient extends JFrame implements Runnable, ActionListener {

	public static final int PORT = 8765;

	ObjectInputStream myReader;
	ObjectOutputStream myWriter;
	JTextArea outputArea;
	JLabel prompt;
	JTextField inputField;
	String myName, serverName;
	Socket connection;
	private BigInteger E, N;
	private String cipherType;
	private SymCipher myCipher;

	public SecureChatClient()
	{
		try {

		serverName = JOptionPane.showInputDialog(this, "Enter the server name: ");
		InetAddress addr = InetAddress.getByName(serverName);
		connection = new Socket(addr, PORT);   // Connect to server with new
											   // Socket
											   
		myWriter = new ObjectOutputStream(connection.getOutputStream());
		myWriter.flush();
		myReader = new ObjectInputStream(connection.getInputStream());
	
		
		//Gets Servers Public Keys and Symmetric Encryption Type
		E = (BigInteger)myReader.readObject();
		N = (BigInteger)myReader.readObject();
		cipherType = (String)myReader.readObject();
		
		//Uses the symmetric cipher the server chose
		if(cipherType.equals("Add")){
			System.out.println("Symmetric Cipher Type: Add128");
			myCipher = new Add128();
		}
		else{
			System.out.println("Symmetric Cipher Type: Substitute");
			myCipher = new Substitute();	
		}
		
		BigInteger symmetricKey = new BigInteger(1, myCipher.getKey());
		System.out.println("Decrypted Symmetric Cipher Key: " + symmetricKey);
		
		//RSA ENCRYPTION
		BigInteger encryptedSymmetricKey = symmetricKey.modPow(E, N);
		
		System.out.println("Encrypted Symmetric Cipher Key: " + encryptedSymmetricKey);
  		
  		myWriter.writeObject(encryptedSymmetricKey);
  		myWriter.flush();
  		
  		// Prompt user for name and send encrypted name to Server.
  		myName = JOptionPane.showInputDialog(this, "Enter your user name: ");
  		myWriter.writeObject(myCipher.encode(myName));
		myWriter.flush();

		this.setTitle(myName);	  // Set title to identify chatter

		Box b = Box.createHorizontalBox();  // Set up graphical environment for
		outputArea = new JTextArea(8, 30);  // user
		outputArea.setEditable(false);
		b.add(new JScrollPane(outputArea));

		outputArea.append("Welcome to the Chat Group, " + myName + "\n");

		inputField = new JTextField("");  // This is where user will type input
		inputField.addActionListener(this);

		prompt = new JLabel("Type your messages below:");
		Container c = getContentPane();

		c.add(b, BorderLayout.NORTH);
		c.add(prompt, BorderLayout.CENTER);
		c.add(inputField, BorderLayout.SOUTH);

		Thread outputThread = new Thread(this);  // Thread is to receive strings
		outputThread.start();					// from Server

		addWindowListener(
				new WindowAdapter()
				{
					public void windowClosing(WindowEvent e){
						try	{myWriter.writeObject(myCipher.encode("CLIENT CLOSING"));}
						catch (IOException ex){System.out.println("Output error " + e);}
					  	System.exit(0);
					 }
				}
			);

		setSize(500, 200);
		setVisible(true);

		}
		catch (Exception e)
		{
			System.out.println("Problem starting client!");
			System.out.println(e +  ", closing client!");
			System.exit(0);
		}
	}

	public void run()
	{
		while (true)
		{
			 try
			 {
			 	byte[] encryptedMsg = (byte[])myReader.readObject();
			 	BigInteger encryptedMsgBytes = new BigInteger(encryptedMsg);
			 	System.out.println("Encrypted Array of Bytes Received: " + encryptedMsgBytes);
			 	
			 	String currMsg = myCipher.decode(encryptedMsg);
			 	byte[] decryptedMsg = currMsg.getBytes();
				BigInteger decryptedMsgBytes = new BigInteger(decryptedMsg);
				System.out.println("Decrypted Array of Bytes Received: " + decryptedMsgBytes);
				
				System.out.println("String Received: " + currMsg);
				outputArea.append(currMsg+"\n");
			 }
			 catch (Exception e)
			 {
				System.out.println(e +  ", closing client!");
				break;
			 }
		}
		System.exit(0);
	}

	public void actionPerformed(ActionEvent e)
	{
		String currMsg = e.getActionCommand();	  // Get input value
		inputField.setText("");
		
		String currMsgApp = new String(myName + ":" + currMsg);
		byte[] decryptedMsg = currMsgApp.getBytes();
		byte[] encryptedMsg = myCipher.encode(currMsgApp);
		BigInteger decryptedMsgBytes = new BigInteger(decryptedMsg);
		BigInteger encryptedMsgBytes = new BigInteger(encryptedMsg);
		
		System.out.println("String Sent: " + currMsgApp);
		System.out.println("Decrypted Array of Bytes Sent: " + decryptedMsgBytes);
		System.out.println("Encrypted Array of Bytes Sent: " + encryptedMsgBytes);
		
		try{
			myWriter.writeObject(encryptedMsg);
			myWriter.flush();
		}
		catch (IOException ex){System.out.println("Output error " + e);}
		
	}											 	

	public static void main(String [] args)
	{
		 SecureChatClient JR = new SecureChatClient();
		 JR.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
	}
}


