package test;

import dto.UserResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class GetMyInfoTest extends BaseTest{
//permite unui utilizator autentificat (admin în acest caz) să obțină informațiile sale de profil

    @Test
    @org.testng.annotations.Test
    public void getUserInfoTest() {
        // Obține token-ul de acces pentru un utilizator cu rol de administrator
        String accessToken = getAdminAccessToken();

        // Trimite o cerere GET către ruta ME_PATH cu token-ul de acces și așteaptă un răspuns de succes (cod 200)
        Response userInfo = getRequestWithAccessToken(ME_PATH, 200, accessToken);

        // Deserializează răspunsul în obiectul UserResponse, care reprezintă informațiile utilizatorului
        UserResponse userResponse = userInfo.as(UserResponse.class);

        // Verifică dacă adresa de email a utilizatorului returnată este "qwerty@gm.com"
        assertEquals("qwerty@gm.com", userResponse.getEmail());
    }


//un utilizator încearcă să acceseze resurse fără a se autentifica
    @Test
    @org.testng.annotations.Test
    public void unauthorizedGetUserInfoTest() {
        // Trimite o cerere GET către ME_PATH fără token de acces (token-ul este un șir gol) și așteaptă un răspuns cu status 401
        Response userInfo = getRequestWithAccessToken(ME_PATH, 401, "");

        // Verifică dacă codul de status al răspunsului este 401 (Unauthorized)
        assertEquals(401, userInfo.getStatusCode());

        // Extrage mesajul din câmpul "httpStatus" din corpul răspunsului JSON
        String status = userInfo.getBody().jsonPath().getString("httpStatus");

        // Verifică dacă mesajul din câmpul "httpStatus" este "UNAUTHORIZED"
        assertEquals("UNAUTHORIZED", status);
    }

}
