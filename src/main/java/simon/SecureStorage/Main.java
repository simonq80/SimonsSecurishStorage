


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

    
    
    public static void main(String args[]) throws DbxException, IOException {
    	
    	String userPath = "/home/simon/dropbox/";
    	String key = "qwerty";
        // Create Dropbox client
        DbxRequestConfig config = new DbxRequestConfig("dropbox/java-tutorial", "en_US");
        DbxClientV2 client = new DbxClientV2(config, ACCESS_TOKEN);
        
        Account userA = new Account("simonq", "qwerty", key, client, userPath);
        if(!userA.validate()){
        	System.out.println("username/password/key incorrect");
        	System.exit(0);
        }
        // Get current account info
        FullAccount account = userA.client.users().getCurrentAccount();
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
        //userA.SecureUpload("/home/simon/dropbox/userlist.txt", "/userList.txt");
        userA.addUser("simonq", "qwerty");
        //userA.SecureDownload(userPath+"maven.zip", "/maven.txt");
        
    }
}
