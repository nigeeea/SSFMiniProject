package MiniProject.SSF.services;

import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import MiniProject.SSF.models.Food;
import MiniProject.SSF.repositories.FoodRepository;
import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import jakarta.json.JsonObjectBuilder;
import jakarta.json.JsonReader;

@Service
public class FoodFinderService {
    
    //Change from random recipe API to search recipe API and create drop-down list and parameters cause the random recipe one is too random
    //Instance Variable: URL and apiKey

    private static final String URL = "https://api.spoonacular.com/recipes/complexSearch";

    @Value("${API_KEY}")
    private String key;

    @Autowired
    private FoodRepository foodRepo;

    //Method 1: return a list of object(food) - takes in a String "tag" e.g. chicken, fish, chocolate, vegetarian

    public List<Food> getFood(String input, String maxCalInput){

        String url = UriComponentsBuilder.fromUriString(URL)
        .queryParam("cuisine", input)
        .queryParam("number", "10") //hardset to 10 due to API call limitss...
        .queryParam("intolerances", "Shellfish")
        .queryParam("maxCalories", maxCalInput)
        .queryParam("apiKey", key)
        .toUriString();

        //create the GET Request
        RequestEntity<Void> req = RequestEntity.get(url).build();

        //Make the call to the API
        RestTemplate template = new RestTemplate();
        ResponseEntity<String> resp = template.exchange(req, String.class);


        //Check the response
        System.out.println(resp.getStatusCodeValue());
        String payload = resp.getBody();
        System.out.println(payload);

        //convert the payload into a JSONObject
        Reader myStrReader = new StringReader(payload);
        JsonReader myJsonReader = Json.createReader(myStrReader);
        JsonObject initialJsonObject = myJsonReader.readObject();
        JsonArray initialJsonArray = initialJsonObject.getJsonArray("results");

        
        //this method is to get a random recipe out of the results returned (can get from the size of the JSON Array)
        int randomNumber = (int)(Math.random() * initialJsonArray.size() + 1);

        //create a new JsonObject to contain the information that i want
        JsonObjectBuilder myJsonObjectBuilder = Json.createObjectBuilder();
        myJsonObjectBuilder
        .add("id", initialJsonArray.getJsonObject(randomNumber).getInt("id"))
        .add("image", initialJsonArray.getJsonObject(randomNumber).getString("image"))
        .add("recipeName", initialJsonArray.getJsonObject(randomNumber).getString("title"));
        JsonObject myJsonObject = myJsonObjectBuilder.build();

        //take values from myJsonObject and add them to a Food object then add the Food object to a list
        Food myFood = new Food();
        myFood = myFood.fromJSONToFood(myJsonObject);
        //myFood.setRecipeName(myJsonObject.getString("recipeName"));
        List<Food> myFoodList = new ArrayList<>();
        myFoodList.add(myFood);

        System.out.println(">>>>>>>>>>>>>>"+myJsonObject.getString("recipeName")+">>>>>>>>>>>>>>");

        return myFoodList;
    }

    public String savingIt(String cuisine, String recipeNamez){
        String something = foodRepo.saveIt(cuisine, recipeNamez);

        return something;
        
    }

}
