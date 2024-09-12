package test;

import com.github.javafaker.Faker;
import dto.UserResponse;
import dto.UserUpdateRequest;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static test.BaseTest.*;

public class UpdateUsersTest {

    //schimba datele
    @Test
    @org.testng.annotations.Test //arata ca e test unit
    public void successUpdateUserInfoTest() {
        // Creează un obiect Faker pentru a genera date aleatorii
        Faker faker = new Faker();

        // Înregistrează un utilizator și obține tokenul de acces
        Response registerUser = registerUser(201);
        String accessToken = registerUser.jsonPath().getString("accessToken");

        // Generează date noi pentru utilizator
        String name = faker.name().firstName();
        String surname = faker.name().lastName();
        String phone = "+13453454545";

        // Obține informațiile curente ale utilizatorului folosind tokenul de acces
        Response userInfo = getRequestWithAccessToken(ME_PATH, 200, accessToken);
        UserResponse userResponse = userInfo.as(UserResponse.class);
        String id = userResponse.getId(); // Extrage ID-ul utilizatorului

        // Creează un obiect UserUpdateRequest cu noile date ale utilizatorului
        UserUpdateRequest updateRequest = UserUpdateRequest.builder()
                .name(name)
                .surname(surname)
                .phone(phone)
                .build();

        // Trimite cererea de actualizare a informațiilor utilizatorului
        putRequest(UPDATE_USER_PATH + id, 200, updateRequest, accessToken);

        // Obține informațiile actualizate ale utilizatorului și le compară cu datele trimise
        Response updatedUserInfo = getRequestWithAccessToken(ME_PATH, 200, accessToken);
        UserResponse updatedUserResponse = updatedUserInfo.as(UserResponse.class);

        // Verifică dacă informațiile actualizate sunt cele așteptate
        assertEquals(name, updatedUserResponse.getName());
        assertEquals(surname, updatedUserResponse.getSurname());
        assertEquals(phone, updatedUserResponse.getPhone());
    }

    // verifică dacă actualizarea informațiilor unui utilizator cu date
// goale nu modifică datele curente ale utilizatorului
    @Test
    @org.testng.annotations.Test //arata ca e test unit
    public void updateUserInfoWithEmptyDataTest() {
        // Înregistrează un utilizator și obține tokenul de acces
        Response registerUser = registerUser(201);
        String accessToken = registerUser.jsonPath().getString("accessToken");

        // Definește datele goale pentru actualizare
        String name = "";
        String surname = "";
        String phone = "";

        // Obține informațiile curente ale utilizatorului folosind tokenul de acces
        Response userInfo = getRequestWithAccessToken(ME_PATH, 200, accessToken);
        UserResponse userResponse = userInfo.as(UserResponse.class);
        String id = userResponse.getId(); // Extrage ID-ul utilizatorului

        // Creează o cerere de actualizare cu date goale
        UserUpdateRequest updateRequest = UserUpdateRequest.builder()
                .name(name)
                .surname(surname)
                .phone(phone)
                .build();

        // Trimite cererea de actualizare și așteaptă un răspuns cu cod 400 (Bad Request)
        putRequest(UPDATE_USER_PATH + id, 400, updateRequest, accessToken);

        // Obține informațiile actualizate ale utilizatorului și verifică dacă acestea nu s-au schimbat
        Response updatedUserInfo = getRequestWithAccessToken(ME_PATH, 200, accessToken);
        UserResponse updatedUserResponse = updatedUserInfo.as(UserResponse.class);

        // Verifică dacă datele nu s-au actualizat (deoarece am trimis date goale)
        assertNotEquals(name, updatedUserResponse.getName());
        assertNotEquals(surname, updatedUserResponse.getSurname());
        assertNotEquals(phone, updatedUserResponse.getPhone());
    }

    //fără un token de acces valid
    @Test
    @org.testng.annotations.Test //arata ca e test unit
    public void unauthorizedUpdateUserInfoTest() {
        // Creează un obiect Faker pentru generarea de date false
        Faker faker = new Faker();

        // Înregistrează un utilizator și obține tokenul de acces
        Response registerUser = registerUser(201);
        String accessToken = registerUser.jsonPath().getString("accessToken");

        // Generați date false pentru actualizare
        String name = faker.name().firstName();
        String surname = faker.name().lastName();
        String phone = "+13453454545";

        // Obține informațiile curente ale utilizatorului folosind tokenul de acces
        Response userInfo = getRequestWithAccessToken(ME_PATH, 200, accessToken);
        UserResponse userResponse = userInfo.as(UserResponse.class);
        String id = userResponse.getId(); // Extrage ID-ul utilizatorului

        // Creează o cerere de actualizare cu datele generate
        UserUpdateRequest updateRequest = UserUpdateRequest.builder()
                .name(name)
                .surname(surname)
                .phone(phone)
                .build();

        // Trimite cererea de actualizare fără token de acces și așteaptă un răspuns cu cod 401 (Unauthorized)
        putRequest(UPDATE_USER_PATH + id, 401, updateRequest, null);

        // Verifică dacă informațiile utilizatorului nu s-au schimbat (deoarece cererea a fost neautorizată)
        Response updatedUserInfo = getRequestWithAccessToken(ME_PATH, 200, accessToken);
        UserResponse updatedUserResponse = updatedUserInfo.as(UserResponse.class);

        // Verifică că datele utilizatorului nu

    }
}
