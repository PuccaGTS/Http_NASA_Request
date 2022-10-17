import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.FileOutputStream;
import java.io.IOException;

public class Main {
    public static final String keyNasa = "8rw2gkzoYl48dscP2dLSbac98YyhWdamW0Fy5MT5";
    public static final String REMOTE_SERVICE_URL = "https://api.nasa.gov/planetary/apod?api_key=" + keyNasa;
    public static ObjectMapper mapper = new ObjectMapper();

    public static void main(String[] args) {
        HttpGet request = new HttpGet(REMOTE_SERVICE_URL);

        try (CloseableHttpClient httpClient = HttpClientBuilder.create()
                .setDefaultRequestConfig(RequestConfig.custom()
                        .setConnectTimeout(5000)
                        .setSocketTimeout(30000)
                        .setRedirectsEnabled(false)
                        .build())
                .build();
             CloseableHttpResponse response = httpClient.execute(request)) {

            SpaceInfo spaceInfo = mapper.readValue(
                    response.getEntity().getContent(),
                    new TypeReference<>() {
                    }
            );

            String mediaURL = spaceInfo.getUrl();
            String nameMedia = mediaURL.substring((mediaURL.lastIndexOf("/") + 1));
            HttpGet requestPhoto = new HttpGet(mediaURL);

            CloseableHttpResponse responsePhoto = httpClient.execute(requestPhoto);
            FileOutputStream fos = new FileOutputStream("PhotoNASA/" + nameMedia);
            byte[] buffer = responsePhoto.getEntity().getContent().readAllBytes();
            fos.write(buffer);
            fos.flush();

            fos.close();
            responsePhoto.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
