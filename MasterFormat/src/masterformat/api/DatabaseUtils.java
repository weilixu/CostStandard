package masterformat.api;

import java.io.FileInputStream;
import java.util.Properties;

public final class DatabaseUtils {
    private static final String CONFIG_PROPERTIES = "database.properties";
    private static final String DATABASE_URL = "database.url";
    private static final String DATABASE_USER = "database.user";
    private static final String DATABASE_PASSWORD = "database.password";
    
    private static String url = null;
    private static String user = null;
    private static String password = null;
    
    private static Properties databaseProps;
    
    static{
	initProperties();
    }
    
    private static void initProperties(){
	String tempUrl = url;
	String tempUser = user;
	String tempPassword = password;
	
	try{
	    databaseProps = new Properties();
	    FileInputStream databaseIn = new FileInputStream(CONFIG_PROPERTIES);
	    
	    databaseProps.load(databaseIn);
	    databaseIn.close();
	    
	    url = databaseProps.getProperty(DATABASE_URL);
	    user = databaseProps.getProperty(DATABASE_USER);
	    password = databaseProps.getProperty(DATABASE_PASSWORD);
	}catch(Exception e){
	    url = tempUrl;
	    user = tempUser;
	    password = tempPassword;
	}
    }
    
    public static String getUrl(){
	return url;
    }
    
    public static String getUser(){
	return user;
    }
    
    public static String getPassword(){
	return password;
    }

}
