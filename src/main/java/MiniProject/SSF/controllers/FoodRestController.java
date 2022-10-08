package MiniProject.SSF.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import MiniProject.SSF.repositories.FoodRepository;
import jakarta.json.Json;
import jakarta.json.JsonObject;

@RestController
@RequestMapping(path = "/user", produces = MediaType.APPLICATION_JSON_VALUE)
public class FoodRestController {
    
    @Autowired
    private FoodRepository foodRepo;

    @GetMapping(value = "{username}")
    public ResponseEntity<String> getUserRecipes(@PathVariable String username) {

        if(foodRepo.retrieveIt(username).equals("nothing")){
            JsonObject errorMessage = Json.createObjectBuilder()
                                        .add("error", "No recipes for user %s".formatted(username))
                                        .build();
                                        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                        .body(errorMessage.toString());

        }
        else {
            String personalFoodList = foodRepo.retrieveIt(username);
            
            return ResponseEntity.ok(personalFoodList);

        }
    }
}
