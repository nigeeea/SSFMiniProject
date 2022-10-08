package MiniProject.SSF.services;

import java.io.Reader;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.google.common.hash.Hashing;

import MiniProject.SSF.models.Food;
import MiniProject.SSF.repositories.FoodRepository;
import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObject;
import jakarta.json.JsonObjectBuilder;
import jakarta.json.JsonReader;

@Service
public class FoodFinderService {

    private static final String URL = "https://api.spoonacular.com/recipes/complexSearch";

    @Value("${API_KEY}")
    private String key;

    @Autowired
    private FoodRepository foodRepo;

    //Method 1: return a list of object(food) - takes in a String "tag" e.g. chicken, fish, chocolate, vegetarian
    public Food getFood(String input, String maxCalInput){

        String url = UriComponentsBuilder.fromUriString(URL)
        .queryParam("cuisine", input)
        .queryParam("number", "10") //hardset to 10 due to API call limitss...
        .queryParam("maxCalories", maxCalInput)
        .queryParam("apiKey", key)
        .queryParam("instructionsRequired", "true")
        .queryParam("addRecipeInformation", "true")
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

        //get the recipe calories
        JsonObject initialJsonCaloriesObject = initialJsonArray.getJsonObject(randomNumber).getJsonObject("nutrition");
        Integer recipeCalories = initialJsonCaloriesObject.getJsonArray("nutrients").getJsonObject(0).getInt("amount");

        //create a new JsonObject to contain the information that i want
        JsonObjectBuilder myJsonObjectBuilder = Json.createObjectBuilder();
        myJsonObjectBuilder
        .add("id", initialJsonArray.getJsonObject(randomNumber).getInt("id"))
        .add("image", initialJsonArray.getJsonObject(randomNumber).getString("image"))
        .add("recipeName", initialJsonArray.getJsonObject(randomNumber).getString("title"))
        .add("url", initialJsonArray.getJsonObject(randomNumber).getString("spoonacularSourceUrl"))
        .add("calories", recipeCalories);
        
        JsonObject myJsonObject = myJsonObjectBuilder.build();

        //take values from myJsonObject and add them to a Food object then add the Food object to a list
        Food myFood = new Food();
        myFood = myFood.fromJSONToFood(myJsonObject);
        
        List<Food> myFoodList = new ArrayList<>();
        myFoodList.add(myFood);

        System.out.println(">>>>>>>>>>>>>>"+myJsonObject.getString("recipeName")+">>>>>>>>>>>>>>");

        return myFood;
    }

    //method 2: this method saves AND retrieves data to and from Redis and returns a String - see foodrepo
    public String savingIt(String cuisine, String recipeNamez){
        String something = foodRepo.saveIt(cuisine, recipeNamez);

        return something;
    }

    //method 3: this method saves users during sign up and encrypts passwords with a sha256 function 
    public String savingUsers(String username, String password){
        username = username + "acct";
        password = Hashing.sha256().hashString(password, StandardCharsets.UTF_8).toString();
        String something = foodRepo.saveIt(username, password);

        return something;    
    }

    //method 4: this method to retrieve from Redis using key - see foodrepo
    public String retrieveValue(String key){
        String value = foodRepo.retrieveIt(key);
        return value;
    }


    //method 5: method to convert from Json to food
    public Food JsontoFoodObject(JsonObject recipe){
        Food newFood = new Food();
        newFood.setCalories(recipe.getInt("calories"));
        newFood.setId(recipe.getInt("id"));
        newFood.setImage(recipe.getString("image"));
        newFood.setRecipeName(recipe.getString("recipename"));
        newFood.setUrl(recipe.getString("url"));

        return newFood;
    }

    //method 6: method to save recipes
    public void saveRecipe(String username, Integer id, String recipeName, String image, String url, Integer calories){

        //if jsonarray for saved recipes has not been created and is null
        if(foodRepo.retrieveIt(username).equals("nothing")){

        //create a food jsonarray and store it in redis
        JsonArrayBuilder myJsonArrayBuilder = Json.createArrayBuilder();
        JsonObjectBuilder jsonOB1 = Json.createObjectBuilder()
        .add("recipename", recipeName)
        .add("id", id)
        .add("image", image)
        .add("calories", calories)
        .add("url", url);
        JsonObject jsonObject1 = jsonOB1.build();
        JsonArray finalJsonArray = myJsonArrayBuilder.add(jsonObject1).build();
        savingIt(username, finalJsonArray.toString());
        }

        //else if created already, add on to the array
        else{

            //retrieve the already saved recipes
            String prevSavedRecipes = foodRepo.retrieveIt(username);

            //create the recipe to be saved in json object format and save to string
            JsonObjectBuilder jsonOB1 = Json.createObjectBuilder()
            .add("recipename", recipeName)
            .add("id", id)
            .add("image", image)
            .add("calories", calories)
            .add("url", url);

            JsonObject jsonObject1 = jsonOB1.build();

            //Turn the saved json array into an array and add the new json object into the array and save it
            Reader myStringReader = new StringReader(prevSavedRecipes);
            JsonReader myJsonReader = Json.createReader(myStringReader);
            JsonArray prevSavedJsonArray = myJsonReader.readArray();

            JsonArrayBuilder finalJsonArrayBuilder = Json.createArrayBuilder();

            for(int i=0; i<prevSavedJsonArray.size(); i++){
                finalJsonArrayBuilder.add(prevSavedJsonArray.get(i));
            }

            

            String finalJsonArrayToSave = finalJsonArrayBuilder.add(jsonObject1).build().toString();  
            //String finalJsonToSave = prevSavedRecipes + "," + jsonObject1.toString();

            foodRepo.justSavingIt(username, finalJsonArrayToSave);
        }

    }

    //method 7: method to get saved recipes
    public ArrayList<Food> getSavedRecipes(String username){

        String savedRecipes = foodRepo.retrieveIt(username);

        //Turn the saved json array into an array and add the new json object into the array and save it
        Reader myStringReader = new StringReader(savedRecipes);
        JsonReader myJsonReader = Json.createReader(myStringReader);
        JsonArray prevSavedJsonArray = myJsonReader.readArray();

        JsonArrayBuilder finalJsonArrayBuilder = Json.createArrayBuilder();

        for(int i=0; i<prevSavedJsonArray.size(); i++){
            finalJsonArrayBuilder.add(prevSavedJsonArray.get(i));
        }

        JsonArray savedRecipesArray = finalJsonArrayBuilder.build();

        //convert each object int the array into a food object and add it to a "Food" list
        ArrayList<Food> foodlist = new ArrayList<>();
        
        for(int i=0; i<savedRecipesArray.size(); i++){
            JsonObject recipeObject = savedRecipesArray.getJsonObject(i);
            foodlist.add(JsontoFoodObject(recipeObject));
            
            
        }

        return foodlist;
    }
}
