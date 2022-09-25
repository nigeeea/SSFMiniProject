package MiniProject.SSF.controllers;

import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import MiniProject.SSF.models.Food;
import MiniProject.SSF.repositories.FoodRepository;
import MiniProject.SSF.services.FoodFinderService;
import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObject;
import jakarta.json.JsonObjectBuilder;
import jakarta.json.JsonReader;


@Controller

@RequestMapping(path = "/")
public class FoodFinderController {

    @Autowired
    private FoodFinderService ffSvc;

    @Autowired FoodRepository foodRepo;


    //might want to edit this part to ensure the path name
    @GetMapping(path = "/loginpage")
    public String displayLoginPage()
    {
        return "loginpage";
    }

    @PostMapping(path = "/logintest")
    public String displayLoginTest(
        @RequestParam(name = "username") String username,
        @RequestParam(name = "password") String password,
        Model model)
    {   
        
        if(ffSvc.retrieveValue(username+"acct").equals(password)){
            
            model.addAttribute("username", username);

            return "searchrecipe";
        }
        else
        
        return "loginfail";
    }

    @GetMapping(path = "/displayrecipe")
    public String displayRandomRecipe(
        @RequestParam(name = "userInput", defaultValue = "chinese") String input,
        @RequestParam(name = "maxCalorieInput", defaultValue = "800") String maxCalInput,
        @RequestParam(name = "username") String username,
        Model model
    )
    {

        model.addAttribute("username", username);
        model.addAttribute("inputz", input);
        model.addAttribute("maxCalzInput", maxCalInput);
        
        Food myFood = ffSvc.getFood(input, maxCalInput);
        model.addAttribute("thefood", myFood);

        //hidden form fields
        model.addAttribute("hiddenid",myFood.getId());
        model.addAttribute("hiddenrecipename",myFood.getRecipeName());
        model.addAttribute("hiddenimage",myFood.getImage());
        model.addAttribute("hiddencalories",myFood.getCalories());
        model.addAttribute("hiddenurl",myFood.getUrl());
        
        return "displayrecipe";

    }

    @PostMapping(path = "/savedpage")
    public String displaySavedPage(
        @RequestParam(name = "username") String username,
        @RequestParam(name = "id") Integer id,
        @RequestParam(name = "recipename") String recipename,
        @RequestParam(name = "image") String image,
        @RequestParam(name = "url") String url,
        @RequestParam(name = "calories") Integer calories,
        // @RequestParam(name = "foodListz") List<Food> foodlist,
        Model model
    )
    {   

    //if jsonarray for saved recipes has not been created and is null
        if(foodRepo.retrieveIt(username).equals("nothing")){

            //create a food jsonarray and store it in redis
        JsonArrayBuilder myJsonArrayBuilder = Json.createArrayBuilder();
        JsonObjectBuilder jsonOB1 = Json.createObjectBuilder()
        .add("recipename", recipename)
        .add("id", id)
        .add("image", image)
        .add("calories", calories)
        .add("url", url);
        JsonObject jsonObject1 = jsonOB1.build();
        JsonArray finalJsonArray = myJsonArrayBuilder.add(jsonObject1).build();
        ffSvc.savingIt(username, finalJsonArray.toString());
        }

    //else if created already, add on to the array
        else{

            //retrieve the already saved recipes
            String prevSavedRecipes = foodRepo.retrieveIt(username);

            //create the recipe to be saved in json object format and save to string
            JsonObjectBuilder jsonOB1 = Json.createObjectBuilder()
            .add("recipename", recipename)
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

        model.addAttribute("id",id);
        model.addAttribute("recipename",recipename);
        model.addAttribute("image",image);
        model.addAttribute("username", username);

        return "savedpage";
    }

    @GetMapping(path = "searchrecipe")
    public String displaySearchRecipe(
        @RequestParam(name = "username") String username,
        Model model
    )
    {
        model.addAttribute("username", username);
        return "searchrecipe";
    }


    @GetMapping(path = "/signuppage")
    public String displaySignUpPage(){
        return "signuppage";
    }

    @PostMapping(path = "/signupsuccesspage")
    public String displaySignUpSuccessPage(
        @RequestParam(name = "username") String usernameInput,
        @RequestParam(name = "password") String passwordInput,
        Model model
    ){
        ffSvc.savingUsers(usernameInput, passwordInput);
        model.addAttribute("username", usernameInput);

        return "signupsuccesspage";
    }

    @GetMapping(path = "/savedrecipes")
    public String savedrecipes(
        @RequestParam(name = "username") String username,
        Model model)
        {
            model.addAttribute("username", username);

            if(foodRepo.retrieveIt(username).equals("nothing")){
                return "emptysavedrecipes";
            }

            else {

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
            List<Food> foodlist = new ArrayList<>();
            
            for(int i=0; i<savedRecipesArray.size(); i++){
                JsonObject recipeObject = savedRecipesArray.getJsonObject(i);
                foodlist.add(ffSvc.JsontoFoodObject(recipeObject));
                
            }

            model.addAttribute("foodlist", foodlist);

            //display all saved objects in html through thymeleaf iteration

            return "savedrecipes";
        }
    }

}
