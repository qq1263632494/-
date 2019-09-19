import java.net.HttpURLConnection;
import java.net.URL;

public class DownloadUtil {
    public static void download(String url, String target) throws Exception{
        URL url1 = new URL(url);
        HttpURLConnection connection = (HttpURLConnection)url1.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Charset", "UTF-8");
        connection.setRequestProperty("Connection", "Keep-Alive");
        int size = connection.getContentLength();
        System.out.println(size);
    }
}
