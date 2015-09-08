package masterformat.api;

import java.io.FileInputStream;
import java.util.Properties;

public final class DatabaseUtils {
    private static final String CONFIG_PROPERTIES = "database.properties";
    private static final String DATABASE_URL = "database.url";
    private static final String DATABASE_USER = "database.user";
    private static final String DATABASE_PASSWORD = "database.password";
    private static final String EPLUS_DIR = "database.directory";
    private static final String EPLUS_WEA = "database.weather";
    
    private static String url = null;
    private static String user = null;
    private static String password = null;
    private static String directory = null;
    private static String weather = null;
    
    private static Properties databaseProps;
    
    static{
	initProperties();
    }
    
    private static void initProperties(){
	String tempUrl = url;
	String tempUser = user;
	String tempPassword = password;
	String tempDir = directory;
	String tempWea = weather;
	
	try{
	    databaseProps = new Properties();
	    FileInputStream databaseIn = new FileInputStream(CONFIG_PROPERTIES);
	    
	    databaseProps.load(databaseIn);
	    databaseIn.close();
	    
	    url = databaseProps.getProperty(DATABASE_URL);
	    user = databaseProps.getProperty(DATABASE_USER);
	    password = databaseProps.getProperty(DATABASE_PASSWORD);
	    directory = databaseProps.getProperty(EPLUS_DIR);
	    weather = databaseProps.getProperty(EPLUS_WEA);
	}catch(Exception e){
	    url = tempUrl;
	    user = tempUser;
	    password = tempPassword;
	    directory = tempDir;
	    weather = tempWea;
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
    
    public static String[] getEplusConfig(){
	String[] temp = {directory, weather};
	return temp;
    }

}
