


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

public class App {
	
	
	
    private static final String ACCESS_TOKEN = "YVMLBbT9LfAAAAAAAAAACO8ieMv5s9sBa27LQ9CoA3NMoUF_s97z3DIyY_qWs-zu";

    
    
    public static void main(String args[]) throws DbxException, IOException {
    	
    	final JFrame mainframe = new JFrame("SecureStorage");
    	final JFrame loginframe = new JFrame("Login");
    	String userPath = "/home/simon/dropbox/";
    	String key = "qwerty";
        // Create Dropbox client
        DbxRequestConfig config = new DbxRequestConfig("dropbox/java-tutorial", "en_US");
        DbxClientV2 client = new DbxClientV2(config, ACCESS_TOKEN);
        
        String[] userPass = new String[2];
        loginJFrame(loginframe, userPass, key, client, mainframe, userPath);
        
        
        
    }
    
    public static void upDownJFrame(final Account acc, JFrame mainframe){
    	mainframe.getContentPane().setLayout(new FlowLayout(FlowLayout.CENTER, 50, 50));
    	JLabel dataLabel = new JLabel("User File Path");
    	dataLabel.setPreferredSize(new Dimension(150, 20));
    	mainframe.getContentPane().add(dataLabel);
    	final JTextField field1 = new JTextField("/home/users/");
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
    	acc.SecureUpload(s1.next(), s2.next());
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
        	acc.SecureDownload(s1.next(), s2.next());
        	s1.close();
        	s2.close();
    	}
    	});
    	mainframe.getContentPane().add(undoButton);
    	mainframe.pack();
    	mainframe.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    	mainframe.setVisible(true);
    	}
    
    public static void loginJFrame(final JFrame mainframe, final String[] userPass, final String key, final DbxClientV2 client, final JFrame frame, final String userPath){
    	mainframe.getContentPane().setLayout(new FlowLayout(FlowLayout.CENTER, 50, 50));
    	JLabel dataLabel = new JLabel("Username:");
    	dataLabel.setPreferredSize(new Dimension(80, 20));
    	mainframe.getContentPane().add(dataLabel);
    	final JTextField field1 = new JTextField("");
    	field1.setPreferredSize(new Dimension(130, 20));
    	
    	final JTextField field2 = new JTextField("");
    	field2.setPreferredSize(new Dimension(130, 20));
    	
    	mainframe.getContentPane().add(field1);
    	JLabel dataLabel2 = new JLabel("Password:");
    	dataLabel2.setPreferredSize(new Dimension(150, 20));
    	mainframe.getContentPane().add(dataLabel2);
    	mainframe.getContentPane().add(field2);
    	JButton okButton = new JButton("Login");
    	okButton.addActionListener(new ActionListener(){
    	public void actionPerformed(ActionEvent e){
    	Scanner s1 = new Scanner(field1.getText());
    	Scanner s2 = new Scanner(field2.getText());
    	userPass[0] = s1.next();
    	userPass[1] = s2.next();
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
    			continueLogin(userPass, key, client, userPath, frame);
    		}
    	};
    	mainframe.addWindowListener(wl);
    	mainframe.getContentPane().add(okButton);
    	mainframe.pack();
    	mainframe.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    	mainframe.setVisible(true);
    	}
    
    
    
    public static void continueLogin(String[] userPass, String key, DbxClientV2 client, String userPath, JFrame mainframe){
    	final Account userA = new Account(userPass[0], userPass[1], key, client, userPath);
        if(!userA.validate()){
        	System.out.println("username/password/key incorrect");
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
        
        
        upDownJFrame(userA, mainframe);
        // Upload "test.txt" to Dropbox
        //userA.SecureUpload("/home/simon/dropbox/userlist.txt", "/userList.txt");
        //userA.addUser("simonq", "qwerty");
        //userA.SecureDownload(userPath+"maven.zip", "/maven.txt");
    }
}

