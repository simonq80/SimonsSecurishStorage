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
import com.dropbox.core.v2.files.WriteMode;

import java.util.Date;
import java.util.List;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.IOException;
import java.io.FileWriter;

import org.apache.commons.io.FileUtils;

import org.jasypt.util.binary.*;
import org.jasypt.util.password.*;
public class Account {
	public DbxClientV2 client;
	private String username;
	private String password;
	private BasicPasswordEncryptor pe = new BasicPasswordEncryptor(); //password encryptor for userlist.txt
	// userlist.txt in form username:encrypted password , new line for each entry
	//adminlist.txt in form username , new line for each entry
	//both can only be upload to from the admin JFrame
	private BasicBinaryEncryptor be = new BasicBinaryEncryptor(); //file encryptor
	private String userPath;
	public Account(String username, String password, String key, DbxClientV2 client, String userPath){
		this.username = username;
		this.password = password;
		be.setPassword(key);
		this.client = client;
		this.userPath = userPath;
	}
	public void setPath(String path){
		userPath = path;
	}
	
	//checks username and password given matches entry in userlist.txt on dropbox
	public boolean validate(){
		SecureDownload(userPath+"userlist.txt", "/userlist.txt", true);
		List<String> users = null;
		try{
			users = Files.readAllLines(Paths.get(userPath+"userlist.txt"));
		}catch(IOException e){System.out.println("readException");}
		
		String currentLine;
		String[] userPass;
		
		for(int i = 0; i < users.size(); i++){
			currentLine = users.get(i);
			System.out.println(currentLine);
			System.out.println(currentLine.length());
			userPass = currentLine.split(":");

			if(userPass[0].equals(username)){
				if(pe.checkPassword(password, userPass[1])){
					return true;
				}
			}
		}
		
		
		
		return false;
	}
	//check admin is in adminlist.txt on dropbox
	public boolean adminValidate(){
		SecureDownload(userPath+"adminlist.txt", "/adminlist.txt", true);
		List<String> users = null;
		try{
			users = Files.readAllLines(Paths.get(userPath+"adminlist.txt"));
		}catch(IOException e){System.out.println("readException");}
		
		String currentLine;
		
		for(int i = 0; i < users.size(); i++){
			currentLine = users.get(i);
			System.out.println(currentLine);
			System.out.println(currentLine.length());

			if(currentLine.equals(username)){
				
					return true;
				
			}
		}
		
		
		
		return false;
	}
	//add admin to adminlist.txt
	public void addAdmin(String username){
		//download and convert to List<String>
		SecureDownload(userPath+"adminlist.txt", "/adminlist.txt", true);
		List<String> users = null;
		try{
			users = Files.readAllLines(Paths.get(userPath+"adminlist.txt"));
		}catch(IOException e){System.out.println("readException");}
		//check if admin is already in list
		boolean alreadyThere = false;
		for(int i = 0; i<users.size(); i++){
			if(users.get(i).equals(username))alreadyThere = true;
		}
		//if not, add to list, write back to file and reupload
		if(!alreadyThere){
			users.add(username);
	
			try{
				FileWriter fw = new FileWriter(userPath+"adminlist.txt");
				for(String str : users){
					fw.write(str+"\n");
				}
				fw.close();
			}catch(IOException e){System.out.println("filewriterException");}
			
			SecureUpload(userPath+"adminlist.txt", "/adminlist.txt", true);
		}
	}
	//remove admin from adminlist.txt
	public void removeAdmin(String username){
		SecureDownload(userPath+"adminlist.txt", "/adminlist.txt", true);
		List<String> users = null;
		try{
			users = Files.readAllLines(Paths.get(userPath+"adminlist.txt"));
		}catch(IOException e){System.out.println("readException");}
		
		for(int i = 0; i<users.size(); i++){
			if(users.get(i).equals(username))users.remove(i);
		}

		try{
			FileWriter fw = new FileWriter(userPath+"adminlist.txt");
			for(String str : users){
				fw.write(str+"\n");
			}
			fw.close();
		}catch(IOException e){System.out.println("filewriterException");}
		
		SecureUpload(userPath+"adminlist.txt", "/adminlist.txt", true);
	}
	
	//add user to userlist.txt
	public void addUser(String username, String password){
		//download userlist and convert to List<String>
		SecureDownload(userPath+"userlist.txt", "/userlist.txt", true);
		List<String> users = null;
		try{
			users = Files.readAllLines(Paths.get(userPath+"userlist.txt"));
		}catch(IOException e){System.out.println("readException");}
		//check if user is already in list
		boolean alreadyThere = false;
		for(int i = 0; i<users.size(); i++){
			if(users.get(i).split(":")[0].equals(username))alreadyThere = true;
		}
		//if not add username:encrypted password to list
		if(!alreadyThere){
			System.out.println("adding user");
			String ePassword = pe.encryptPassword(password);
			System.out.println(ePassword);
			String newLine = username+":"+ePassword;
			users.add(newLine);
			//write back to file
			try{
				FileWriter fw = new FileWriter(userPath+"userlist.txt");
				for(String str : users){
					fw.write(str+"\n");
				}
				fw.close();
			}catch(IOException e){System.out.println("filewriterException");}
			//reupload
			SecureUpload(userPath+"userlist.txt", "/userlist.txt", true);
		}
	}
	
	//remove user from userlist.txt
	public void removeUser(String username){
		//download userlist and convert to List<String>
		SecureDownload(userPath+"userlist.txt", "/userlist.txt", true);
		List<String> users = null;
		try{
			users = Files.readAllLines(Paths.get(userPath+"userlist.txt"));
		}catch(IOException e){System.out.println("readException");}
		
		//remove user if exists
		String[] userPass = new String[2];
		for(int i = 0; i<users.size(); i++){
			userPass = users.get(i).split(":");
			if(userPass[0].equals(username))users.remove(i);
		}
		//write string array to file and reupload
		try{
			FileWriter fw = new FileWriter(userPath+"userlist.txt");
			for(String str : users){
				fw.write(str+"\n");
			}
			fw.close();
		}catch(IOException e){System.out.println("filewriterException");}
		
		SecureUpload(userPath+"userlist.txt", "/userlist.txt", true);
	}
	
	
	
	
	
	//download and decrypt file from server, cannot access userlist.txt or adminlist.txt if admin is false
	public void SecureDownload(String userPath, String serverPath, boolean admin){
    	
		String tempServerPath = null;
    	//int index = serverPath.lastIndexOf(".");
		if((serverPath.contains("adminlist.txt") || serverPath.contains("userlist.txt")) && !admin){
			System.out.println("Cannot access, not an admin");
			System.exit(0);
		}
    	if(serverPath.contains(".encrypted"))tempServerPath = serverPath;
    	else tempServerPath = serverPath +".encrypted";
    	
    	//System.out.println(tempServerPath);
    	
    	//download encryped file
    	try(FileOutputStream out = new FileOutputStream(userPath+".encrypted")){//todo
        	client.files().download(tempServerPath).download(out);
        	
        } catch (DownloadErrorException e) {
        	System.out.println("DownloadErrorException");
		} catch (DbxException e) {
			System.out.println("DbxException");
		} catch (IOException e) {
			System.out.println("IOException");
		}
    	
    	//read file to bytearray
    	byte[] temp = null;
    	try{
    		temp = Files.readAllBytes(Paths.get(userPath+".encrypted"));
    		
    	} catch(IOException e){
    		System.out.println("toByteError");
    	}
    	
    	byte[] decryptedTemp = be.decrypt(temp);
    	
    	//write decrypted bytearray to file
    	try{
    		FileUtils.writeByteArrayToFile(new File(userPath), decryptedTemp);
    	}catch(IOException e){
    		System.out.println("writeError");
    	}
    	try{
    		Files.delete(Paths.get(userPath+".encrypted"));
    		
    	} catch(IOException e){
    		System.out.println("toByteError");
    	}
    	
    	
    }
    
    
    
    //encrypt and upload file to server, cannot access userlist.txt or adminlist.txt if admin is false
    public void SecureUpload(String userPath, String serverPath, boolean admin){
    	if((serverPath.contains("adminlist.txt") || serverPath.contains("userlist.txt")) && !admin){
			System.out.println("Cannot access, not an admin");
			System.exit(0);
		}
    	//read file to bytearray and encrypt
    	byte[] temp = null;
    	try{
    		temp = Files.readAllBytes(Paths.get(userPath));
    		
    	} catch(IOException e){
    		System.out.println("tempError");
    	}
    	
    	byte[] encryptedTemp = be.encrypt(temp);
    	String path = userPath+".encrypted";
    	System.out.println(path);
    	try{
    		FileUtils.writeByteArrayToFile(new File(path), encryptedTemp);
    	}catch(IOException e){
    		System.out.println("writeError");
    	}
    	
    	
    	//upload file to dropbox
    	try (InputStream in = new FileInputStream(path)) {
            FileMetadata metadata = client.files().uploadBuilder(serverPath+".encrypted").withMode(WriteMode.OVERWRITE).uploadAndFinish(in);
        } catch (UploadErrorException e) {
        	System.out.println("UploadErrorException");
		} catch (DbxException e) {
			System.out.println("DbxException");
			
		} catch (IOException e) {
			System.out.println("IOException");
		}
    	try{
    		Files.delete(Paths.get(userPath+".encrypted"));
    		
    	} catch(IOException e){
    		System.out.println("deleteError");
    	}
    }
	
}
