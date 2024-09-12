package test;

import dto.UserResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static test.BaseTest.*;

public class DeleteUserTest {
    //sterge user

    @Test
    @org.testng.annotations.Test
    public void deleteUserTest() {
        // Înregistrează un nou utilizator și obține token-ul de acces
        Response registerUser = registerUser(201);
        String accessToken = registerUser.jsonPath().getString("accessToken");

        // Obține informațiile utilizatorului curent
        Response userInfo = getRequestWithAccessToken(ME_PATH, 200, accessToken);
        UserResponse userResponse = userInfo.as(UserResponse.class);
        String id = userResponse.getId();

        // Obține token-ul de acces al administratorului
        String accessAdminToken = getAdminAccessToken();

        // Șterge utilizatorul cu token-ul de acces al administratorului
        Response deleteUserResponse = deleteRequest(DELETE_USER_PATH + id, 204, accessAdminToken);
        assertEquals(204, deleteUserResponse.getStatusCode(), "User deletion failed");

        // Încearcă să obții informațiile utilizatorului după ștergere
        Response getUserInfoAfterDeletion = getRequestWithAccessToken(ME_PATH, 404, accessToken);
        String errorText = getUserInfoAfterDeletion.getBody().jsonPath().getString("message");
        assertEquals("User not found!", errorText);
    }

//testul se asigură că sistemul nu permite ștergerea unui utilizator
//fără un token de acces valid și că mesajul de eroare returnat este corect
    @Test
    @org.testng.annotations.Test
    public void unauthorizedDeleteUserTest() {
        // Înregistrează un utilizator și obține tokenul de acces
        Response registerUser = registerUser(201);
        String accessToken = registerUser.jsonPath().getString("accessToken");

        // Obține informațiile utilizatorului înregistrat folosind tokenul de acces
        Response userInfo = getRequestWithAccessToken(ME_PATH, 200, accessToken);
        UserResponse userResponse = userInfo.as(UserResponse.class);
        String id = userResponse.getId(); // Extrage ID-ul utilizatorului

        // Încearcă să ștergi utilizatorul fără a include un token de acces (401 Unauthorized)
        Response deleteUserResponse = deleteRequest(DELETE_USER_PATH + id, 401, null);
        String errorText = deleteUserResponse.getBody().jsonPath().getString("message");

        // Verifică dacă mesajul de eroare din răspuns este "Unauthorized"
        assertEquals("Unauthorized", errorText);
    }

}

