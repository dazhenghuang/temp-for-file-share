package config;

import java.io.InputStream;
import java.net.URL;
import java.util.Properties;
import java.util.ResourceBundle;


public class Config
{    
    private static ResourceBundle props        = null;
    public static String bulksPath = "";
    public static String piecesPath = "";
    public static String tempPath = "";
    public static String IVsPath = "";
    public static String indexPath = "";
    public static String outputDir = "";
    public static String cleanBatPath = "";
    public static String defaultOpen = "";
    public static String masterIndexPath = "";

    public Config()
    {
        try
        {
        	Properties props = new Properties();
            URL url = ClassLoader.getSystemResource("cryptoSystem.properties");
            props.load(url.openStream());
            System.out.println("Configuration Properties: " + props);

            bulksPath = props.getProperty("BulksPath");
            piecesPath = props.getProperty("PiecesPath");
            tempPath = props.getProperty("TempPath");
            IVsPath = props.getProperty("IVsPath");
            indexPath = props.getProperty("IndexPath");
            outputDir = props.getProperty("OutputDir");
            cleanBatPath = props.getProperty("CleanBatchPath");
            defaultOpen = props.getProperty("DefaultEncryptFolder");
            masterIndexPath = props.getProperty("MasterIndexPath");
            System.out.println(bulksPath);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
