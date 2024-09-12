package test;

import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UploadImageTest extends BaseTest{
    //adauga poza

    @Test
    @org.testng.annotations.Test
    public void successUploadImageTest() {
        // Obține token-ul de acces al adminului
        String accessAdminToken = getAdminAccessToken();

        // Definirea fișierului de imagine pentru upload
        File imageFile = new File("C:\\Users\\Tel-ran.de\\Downloads\\cat.jpg");

        // Trimiterea request-ului pentru upload-ul imaginii
        Response uploadImageResponse = postRequestWithImage(POST_IMAGE_PATH, 201, imageFile, accessAdminToken);

        // Verificarea că răspunsul conține codul de stare 201
        assertEquals(uploadImageResponse.getStatusCode(), 201, "Upload image request should return status code 201");

        // Verificarea că răspunsul conține URL-ul imaginii returnate
        String imageUrl = uploadImageResponse.jsonPath().getString("imageUrl");
        System.out.println("Image uploaded successfully. Image URL: " + imageUrl);

        // (Opțional) Poți adăuga validări suplimentare pentru a verifica formatul URL-ului
        assertEquals(imageUrl.startsWith("https://chatty-images-s3.s3.eu-central-1.amazonaws.com/"), true, "Image URL should start with expected base URL");
    }
}
