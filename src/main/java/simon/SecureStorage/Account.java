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
	private BasicPasswordEncryptor pe = new BasicPasswordEncryptor();
	private BasicBinaryEncryptor be = new BasicBinaryEncryptor();
	private String userPath;
	public Account(String username, String password, String key, DbxClientV2 client, String userPath){
		this.username = username;
		this.password = password;
		be.setPassword(key);
		this.client = client;
		this.userPath = userPath;
	}
	public boolean validate(){
		SecureDownload(userPath+"userlist.txt", "/userlist.txt");
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
	
	public void addUser(String username, String password){
		SecureDownload(userPath+"userlist.txt", "/userlist.txt");
		List<String> users = null;
		try{
			users = Files.readAllLines(Paths.get(userPath+"userlist.txt"));
		}catch(IOException e){System.out.println("readException");}
		
		String ePassword = pe.encryptPassword(password);
		System.out.println(ePassword);
		String newLine = username+":"+ePassword;
		users.add(newLine);

		try{
			FileWriter fw = new FileWriter(userPath+"userlist.txt");
			for(String str : users){
				fw.write(str+"\n");
			}
			fw.close();
		}catch(IOException e){System.out.println("filewriterException");}
		
		SecureUpload(userPath+"userlist.txt", "/userlist.txt");
	}
	
	
	
	
	
	
	public void SecureDownload(String userPath, String serverPath){
    	
		String tempServerPath = null;
    	//int index = serverPath.lastIndexOf(".");
    	if(serverPath.contains(".encrypted"))tempServerPath = serverPath;
    	else tempServerPath = serverPath +".encrypted";
    	
    	System.out.println(tempServerPath);
    	try(FileOutputStream out = new FileOutputStream(userPath+".encrypted")){//todo
        	client.files().download(tempServerPath).download(out);
        	
        } catch (DownloadErrorException e) {
        	System.out.println("DownloadErrorException");
		} catch (DbxException e) {
			System.out.println("DbxException");
		} catch (IOException e) {
			System.out.println("IOException");
		}
    	byte[] temp = null;
    	try{
    		temp = Files.readAllBytes(Paths.get(userPath+".encrypted"));
    		
    	} catch(IOException e){
    		System.out.println("toByteError");
    	}
    	byte[] decryptedTemp = be.decrypt(temp);
    	
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
    
    
    
    
    public void SecureUpload(String userPath, String serverPath){
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
