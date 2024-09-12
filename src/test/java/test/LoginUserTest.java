package test;

import dto.LoginRequest;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class LoginUserTest extends BaseTest{
    //logare corecta

    @Test
    @org.testng.annotations.Test
    public void successAuthenticationTest() {
        // Creează o cerere de autentificare cu email și parolă valide
        LoginRequest loginRequest = LoginRequest.builder()
                .email("qwerty@gm.com") // Email valid
                .password("Sh123456") // Parolă validă
                .build();

        // Trimite cererea de autentificare și obține răspunsul
        Response authResponse = postRequest(AUTH_PATH, 200, loginRequest);

        // Verifică dacă răspunsul conține "accessToken", confirmând că autentificarea a avut succes
        assertTrue(authResponse.getBody().asString().contains("accessToken"));

        // Verifică dacă răspunsul conține "refreshToken", confirmând că autentificarea a avut succes
        assertTrue(authResponse.getBody().asString().contains("refreshToken"));
    }

//parola incorecta
    @Test
    @org.testng.annotations.Test//test unit
    public void invalidPasswordAuthTest() {
        // Creează o cerere de autentificare cu o parolă incorectă
        LoginRequest loginRequest = LoginRequest.builder()
                .email("qwerty@gm.com") // Email valid
                .password("User12345") // Parolă incorectă
                .build();

        // Trimite cererea de autentificare și așteaptă un răspuns cu cod 401 (Unauthorized)
        Response authWithWrongPasswordResponse = postRequest(AUTH_PATH, 401, loginRequest);

        // Verifică dacă codul de status al răspunsului este 401 (Unauthorized)
        assertEquals(401, authWithWrongPasswordResponse.getStatusCode());

        // Verifică dacă mesajul de eroare conține textul specific care indică faptul că parola nu se potrivește
        assertTrue(authWithWrongPasswordResponse.getBody().asString().contains("The password does not match"));
    }

//fara parola
@Test
@org.testng.annotations.Test//test unit
public void loginWithoutPassword() {
    // Creează o cerere de autentificare fără parolă
    LoginRequest loginRequest = LoginRequest.builder()
            .email("qwerty@gm.com") // Email valid
            .password("") // Parola este goală
            .build();

    // Trimite cererea de autentificare și așteaptă un răspuns cu cod 400 (Bad Request)
    Response authResponse = postRequest(AUTH_PATH, 400, loginRequest);

    // Verifică dacă codul de status al răspunsului este 400 (Bad Request)
    assertEquals(400, authResponse.getStatusCode());

    // Verifică dacă mesajul de eroare conține textul specific care indică faptul că parola nu poate fi goală
    assertTrue(authResponse.getBody().asString().contains("Password cannot be empty"));
}

}
