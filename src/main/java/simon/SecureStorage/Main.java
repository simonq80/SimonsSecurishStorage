


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

import java.util.Date;
import java.util.List;

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
	
	
	
    private static final String ACCESS_TOKEN = "YVMLBbT9LfAAAAAAAAAACO8ieMv5s9sBa27LQ9CoA3NMoUF_s97z3DIyY_qWs-zu";

    
    
    
    private static void SecureDownload(DbxClientV2 client, String userPath, String serverPath){
    	
    	try(FileOutputStream out = new FileOutputStream(userPath)){
        	client.files().download(serverPath).download(out);
        	
        } catch (DownloadErrorException e) {
        	System.out.println("DownloadErrorException");
		} catch (DbxException e) {
			System.out.println("DbxException");
		} catch (IOException e) {
			System.out.println("IOException");
		}
    }
    
    
    
    
    private static void SecureUpload(DbxClientV2 client, BinaryEncryptor be, String userPath, String serverPath){
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
            FileMetadata metadata = client.files().uploadBuilder(serverPath+".encrypted").uploadAndFinish(in);
        } catch (UploadErrorException e) {
        	System.out.println("UploadErrorException");
		} catch (DbxException e) {
			System.out.println("DbxException");
			
		} catch (IOException e) {
			System.out.println("IOException");
		}
    }
    
    
    public static void main(String args[]) throws DbxException, IOException {
    	
    	String userPath = "/home/simon/dropbox/";
    	BasicBinaryEncryptor be = new BasicBinaryEncryptor();
    	String key = "qwerty";
    	be.setPassword(key);
        // Create Dropbox client
        DbxRequestConfig config = new DbxRequestConfig("dropbox/java-tutorial", "en_US");
        DbxClientV2 client = new DbxClientV2(config, ACCESS_TOKEN);
        // Get current account info
        FullAccount account = client.users().getCurrentAccount();
        System.out.println(account.getName().getDisplayName());

        // Get files and folder metadata from Dropbox root directory
        ListFolderResult result = client.files().listFolder("");
        
        
        

        while (true) {
            for (Metadata metadata : result.getEntries()) {
                System.out.println(metadata.getPathLower());
            }
            

            if (!result.getHasMore()) {
                break;
            }

            result = client.files().listFolderContinue(result.getCursor());
        }

        // Upload "test.txt" to Dropbox
        SecureUpload(client, be, "/home/simon/apache-maven-3.3.9-bin.zip", "/maven.txt");
        
        SecureDownload(client, userPath+"test.txt", "/test.txt");
        
    }
}
