package test;

import dto.RefreshTokenRequest;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class RefreshTokenTest extends BaseTest{

    //Testul asigură că cererea de refresh a token-ului funcționează corect
    @Test
    @org.testng.annotations.Test//unit test
    public void successRefreshTokenTest() {

        // Înregistrează un utilizator și obține răspunsul
        Response authResponse = registerUser(201);
        // Extrage refreshToken-ul din răspunsul de înregistrare
        String refreshToken = authResponse.jsonPath().getString("refreshToken");

        // Creează o cerere de refresh a token-ului folosind refreshToken-ul obținut
        RefreshTokenRequest refreshTokenRequest = RefreshTokenRequest.builder()
                .refreshToken(refreshToken)
                .build();

        // Trimite cererea de refresh și obține răspunsul
        Response refreshResponse = postRequest(REFRESH_PATH, 201, refreshTokenRequest);

        // Verifică dacă răspunsul are codul de status 201 (Created), indicând succesul cererii de refresh
        assertEquals(201, refreshResponse.getStatusCode(), "Refresh token request should return status code 201");

        // Extrage noul accessToken din răspunsul de refresh
        String newAccessToken = refreshResponse.jsonPath().getString("accessToken");
        // Verifică dacă noul accessToken nu este null sau gol
        assertTrue(newAccessToken != null && !newAccessToken.isEmpty(), "New access token should not be null or empty");
    }

// cererile de refresh cu token-uri invalide
    @Test
    @org.testng.annotations.Test//test unit
    public void refreshTokenUnauthorizedTest() {
        // Creează o cerere de refresh a token-ului cu un refreshToken invalid
        RefreshTokenRequest refreshTokenRequest = RefreshTokenRequest.builder()
                .refreshToken("invalid_refresh_token") // Refresh token invalid
                .build();

        // Trimite cererea de refresh și așteaptă un răspuns cu cod 401 (Unauthorized)
        Response refreshResponse = postRequest(REFRESH_PATH, 401, refreshTokenRequest);

        // Verifică dacă codul de status al răspunsului este 401 (Unauthorized)
        assertEquals(401, refreshResponse.getStatusCode(), "Refresh token request with invalid token should return status code 401");

        // Verifică dacă mesajul de eroare conține textul specific "Unauthorized"
        String errorText = refreshResponse.getBody().jsonPath().getString("message");
        assertEquals("Unauthorized", errorText);
    }

}
