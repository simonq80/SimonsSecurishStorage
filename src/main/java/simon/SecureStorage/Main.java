


package simon.SecureStorage;


import com.dropbox.core.DbxDownloader;
import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v1.DbxEntry;
import com.dropbox.core.v1.DbxClientV1;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.DownloadErrorException;
import com.dropbox.core.v2.files.FileMetadata;
import com.dropbox.core.v2.files.ListFolderResult;
import com.dropbox.core.v2.files.Metadata;
import com.dropbox.core.v2.files.UploadErrorException;
import com.dropbox.core.v2.users.FullAccount;

import javax.swing.*;

import java.util.Date;
import java.util.List;
import java.util.Scanner;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

import org.jasypt.util.binary.*;

public class Main {
	
	
	//token to access dropbox account
    private static final String ACCESS_TOKEN = "YVMLBbT9LfAAAAAAAAAACO8ieMv5s9sBa27LQ9CoA3NMoUF_s97z3DIyY_qWs-zu";

    
    
    public static void main(String args[]) throws DbxException, IOException {
    	
    	final JFrame mainframe = new JFrame("SecureStorage");
    	final JFrame loginframe = new JFrame("Login");
    	
    	String userPath = null;
    	String key = "qwerty";
        // Create Dropbox client
        DbxRequestConfig config = new DbxRequestConfig("dropbox/java-tutorial", "en_US");
        DbxClientV2 client = new DbxClientV2(config, ACCESS_TOKEN);
        
        String[] userPass = new String[2];
        loginJFrame(loginframe, userPass, key, client, mainframe, userPath);
        
        
        
    }
    
    
    //JFrame allowing the upload/download of files from given paths
    public static void upDownJFrame(final Account acc, JFrame mainframe, String userPath){
    	mainframe.getContentPane().setLayout(new FlowLayout(FlowLayout.CENTER, 50, 50));
    	JLabel dataLabel = new JLabel("User File Path");
    	dataLabel.setPreferredSize(new Dimension(150, 20));
    	mainframe.getContentPane().add(dataLabel);
    	final JTextField field1 = new JTextField(userPath);
    	field1.setPreferredSize(new Dimension(200, 20));
    	
    	final JTextField field2 = new JTextField("/");
    	field2.setPreferredSize(new Dimension(200, 20));
    	
    	mainframe.getContentPane().add(field1);
    	JLabel dataLabel2 = new JLabel("Server File Path");
    	dataLabel2.setPreferredSize(new Dimension(150, 20));
    	mainframe.getContentPane().add(dataLabel2);
    	mainframe.getContentPane().add(field2);
    	JButton okButton = new JButton("Upload");
    	okButton.addActionListener(new ActionListener(){
    	public void actionPerformed(ActionEvent e){
    	Scanner s1 = new Scanner(field1.getText());
    	Scanner s2 = new Scanner(field2.getText());
    	acc.SecureUpload(s1.next(), s2.next(), false);
    	s1.close();
    	s2.close();
    	}
    	});
    	mainframe.getContentPane().add(okButton);
    	JButton undoButton = new JButton("Download");
    	undoButton.addActionListener(new ActionListener(){
    	public void actionPerformed(ActionEvent e){
    		Scanner s1 = new Scanner(field1.getText());
        	Scanner s2 = new Scanner(field2.getText());
        	acc.SecureDownload(s1.next(), s2.next(), false);
        	s1.close();
        	s2.close();
    	}
    	});
    	mainframe.getContentPane().add(undoButton);
    	mainframe.pack();
    	mainframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    	mainframe.setVisible(true);
    	}
    
    
    //allows input of username, password, and directory to use
    public static void loginJFrame(final JFrame mainframe, final String[] userPass, final String key, final DbxClientV2 client, final JFrame frame, final String userPath){
    	final boolean[] admin = new boolean[1];
    	final String[] path = new String[1];
    	mainframe.getContentPane().setLayout(new FlowLayout(FlowLayout.CENTER, 50, 50));
    	
    	JLabel dataLabel = new JLabel("Username:");
    	dataLabel.setPreferredSize(new Dimension(80, 20));
    	mainframe.getContentPane().add(dataLabel);
    	
    	final JTextField field1 = new JTextField("");
    	field1.setPreferredSize(new Dimension(130, 20));
    	mainframe.getContentPane().add(field1);
    	
    	JLabel dataLabel2 = new JLabel("Password:");
    	dataLabel2.setPreferredSize(new Dimension(150, 20));
    	mainframe.getContentPane().add(dataLabel2);
    	
    	final JPasswordField field2 = new JPasswordField("");
    	field2.setPreferredSize(new Dimension(130, 20));
    	mainframe.getContentPane().add(field2);
    	
    	JLabel dataLabel3 = new JLabel("Directory to Use");
    	dataLabel.setPreferredSize(new Dimension(80, 20));
    	mainframe.getContentPane().add(dataLabel3);
    	
    	final JTextField field3 = new JTextField("/");
    	field3.setPreferredSize(new Dimension(130, 20));
    	mainframe.getContentPane().add(field3);
    	
    	JButton okButton = new JButton("Login");
    	JButton adminButton = new JButton("Admin");
    	okButton.addActionListener(new ActionListener(){
	    	public void actionPerformed(ActionEvent e){
		    	Scanner s1 = new Scanner(field1.getText());
		    	Scanner s2 = new Scanner(field3.getText());
		    	admin[0] = false;
		    	userPass[0] = s1.next();
		    	userPass[1] = String.valueOf(field2.getPassword());
		    	path[0] = s2.next();
		    	if((path[0].charAt(path[0].length()-1) != '/'))path[0]+="/";
		    	System.out.println(userPass[0]);
		    	System.out.println(userPass[1]);
		    	s1.close();
		    	s2.close();
		    	mainframe.dispose();
	    	}
    	});
    	adminButton.addActionListener(new ActionListener(){
	    	public void actionPerformed(ActionEvent e){
		    	Scanner s1 = new Scanner(field1.getText());
		    	Scanner s2 = new Scanner(field3.getText());
		    	admin[0] = true;
		    	userPass[0] = s1.next();
		    	userPass[1] = String.valueOf(field2.getPassword());
		    	path[0] = s2.next();
		    	System.out.println(userPass[0]);
		    	System.out.println(userPass[1]);
		    	s1.close();
		    	s2.close();
		    	mainframe.dispose();
	    	}
    	});
    	
    	WindowListener wl = new WindowAdapter(){
    		@Override
    		public void windowClosed(WindowEvent e){
    			//moves forward with login when login is pressed
    			continueLogin(userPass, key, client, path[0], frame, admin[0]);
    		}
    	};
    	mainframe.addWindowListener(wl);
    	mainframe.getContentPane().add(okButton);
    	mainframe.getContentPane().add(adminButton);
    	mainframe.pack();
    	mainframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    	mainframe.setVisible(true);
    	}
    
    
    //JFrame window for admin functions, add/remove user/admin
    public static void adminJFrame(final Account acc, JFrame mainframe){
    	mainframe.getContentPane().setLayout(new FlowLayout(FlowLayout.CENTER, 50, 50));
    	JLabel dataLabel = new JLabel("Username");
    	dataLabel.setPreferredSize(new Dimension(150, 20));
    	mainframe.getContentPane().add(dataLabel);
    	final JTextField field1 = new JTextField("");
    	field1.setPreferredSize(new Dimension(200, 20));
    	
    	final JTextField field2 = new JTextField("");
    	field2.setPreferredSize(new Dimension(200, 20));
    	
    	mainframe.getContentPane().add(field1);
    	JLabel dataLabel2 = new JLabel("Password");
    	dataLabel2.setPreferredSize(new Dimension(150, 20));
    	mainframe.getContentPane().add(dataLabel2);
    	mainframe.getContentPane().add(field2);
    	JButton okButton = new JButton("Add User");
    	okButton.addActionListener(new ActionListener(){
	    	public void actionPerformed(ActionEvent e){
		    	Scanner s1 = new Scanner(field1.getText());
		    	Scanner s2 = new Scanner(field2.getText());
		    	acc.addUser(s1.next(), s2.next());
		    	s1.close();
		    	s2.close();
	    	}
    	});
    	mainframe.getContentPane().add(okButton);
    	JButton undoButton = new JButton("Remove User");
    	undoButton.addActionListener(new ActionListener(){
    	public void actionPerformed(ActionEvent e){
    		Scanner s1 = new Scanner(field1.getText());
        	acc.removeUser(s1.next());
        	s1.close();
    	}
    	});
    	mainframe.getContentPane().add(undoButton);
    	JButton adminButton = new JButton("Add Admin");
    	adminButton.addActionListener(new ActionListener(){
    	public void actionPerformed(ActionEvent e){
    		Scanner s1 = new Scanner(field1.getText());
        	acc.addAdmin(s1.next());
        	s1.close();
    	}
    	});
    	mainframe.getContentPane().add(adminButton);
    	JButton removeAdminButton = new JButton("Remove Admin");
    	removeAdminButton.addActionListener(new ActionListener(){
	    	public void actionPerformed(ActionEvent e){
	    		Scanner s1 = new Scanner(field1.getText());
	        	acc.removeAdmin(s1.next());
	        	s1.close();
	    	}
    	});
    	mainframe.getContentPane().add(removeAdminButton);
    	mainframe.pack();
    	mainframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    	mainframe.setVisible(true);
    	}
    
    
    //called after login is pressed
    public static void continueLogin(String[] userPass, String key, DbxClientV2 client, String userPath, JFrame mainframe, boolean admin){
    	//System.out.println(userPath);
    	final Account userA = new Account(userPass[0], userPass[1], key, client, userPath);
    	//check username/password
        if(!userA.validate()){
        	System.out.println("username/password/key incorrect");
        	System.exit(0);
        }
        //check account is admin if accessing admin functions
        if(admin && !userA.adminValidate()){
        	System.out.println("not admin");
        	System.exit(0);
        }
        // Get current account info
        //FullAccount account = userA.client.users().getCurrentAccount();
        //System.out.println(account.getName().getDisplayName());

        // Get files and folder metadata from Dropbox root directory
        ListFolderResult result = null;
        try{
        	result = client.files().listFolder("");
        }catch(DbxException e){System.out.println("ListError");}
        
        
        

        while (true) {
            for (Metadata metadata : result.getEntries()) {
                System.out.println(metadata.getPathLower());
            }
            

            if (!result.getHasMore()) {
                break;
            }
            try{
            	result = client.files().listFolderContinue(result.getCursor());
            }catch(DbxException e){System.out.println("ListError2");}
        }
        
        if(admin){
        	adminJFrame(userA, mainframe);
        }
        else{
        	upDownJFrame(userA, mainframe, userPath);
        }
        // Upload "test.txt" to Dropbox
        //userA.SecureUpload("/home/simon/dropbox/userlist.txt", "/userList.txt");
        //userA.addUser("simonq", "qwerty");
        //userA.SecureDownload(userPath+"maven.zip", "/maven.txt");
    }
}
