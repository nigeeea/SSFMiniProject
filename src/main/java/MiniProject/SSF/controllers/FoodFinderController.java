package MiniProject.SSF.controllers;

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
import MiniProject.SSF.services.FoodFinderService;
import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObject;
import jakarta.json.JsonObjectBuilder;


@Controller

@RequestMapping(path = "/")
public class FoodFinderController {

    @Autowired
    private FoodFinderService ffSvc;


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
        
        return "displayrecipe";

    }

    @PostMapping(path = "/savedpage")
    public String displaySavedPage(
        @RequestParam(name = "username") String username,
        @RequestParam(name = "id") Integer id,
        @RequestParam(name = "recipename") String recipename,
        @RequestParam(name = "image") String image,
        // @RequestParam(name = "foodListz") List<Food> foodlist,
        Model model
    )
    {   
        //create a food jsonarray and store it in redis
        JsonArrayBuilder myJsonArrayBuilder = Json.createArrayBuilder();
        JsonObjectBuilder jsonOB1 = Json.createObjectBuilder().add("recipename", recipename);
        JsonObjectBuilder jsonOB2 = Json.createObjectBuilder().add("id", id);
        JsonObjectBuilder jsonOB3 = Json.createObjectBuilder().add("image", image);
        JsonObject jsonObject1 = jsonOB1.build();
        JsonObject jsonObject2 = jsonOB2.build();
        JsonObject jsonObject3 = jsonOB3.build();
        JsonArray finalJsonArray = myJsonArrayBuilder.add(jsonObject1).add(jsonObject2).add(jsonObject3).build();

        ffSvc.savingIt(username, finalJsonArray.toString());




        model.addAttribute("id",id);
        model.addAttribute("recipename",recipename);
        model.addAttribute("image",image);
        // for(Food f: foodlist){

        //         JsonObjectBuilder myJsonObjectBuilder1 = Json.createObjectBuilder();
        //         JsonObject myJsonObject1 = myJsonObjectBuilder1.add("id", f.getId()).build();
        //         myArrayBuilder.add(myJsonObject1);

        //         JsonObjectBuilder myJsonObjectBuilder2 = Json.createObjectBuilder();
        //         JsonObject myJsonObject2 = myJsonObjectBuilder2.add("id", f.getId()).build();
        //         myArrayBuilder.add(myJsonObject2);

        //         JsonObjectBuilder myJsonObjectBuilder3 = Json.createObjectBuilder();
        //         JsonObject myJsonObject3 = myJsonObjectBuilder3.add("id", f.getId()).build();
        //         myArrayBuilder.add(myJsonObject3);

        //         JsonObjectBuilder myJsonObjectBuilder4 = Json.createObjectBuilder();
        //         JsonObject myJsonObject4 = myJsonObjectBuilder4.add("id", f.getId()).build();
        //         myArrayBuilder.add(myJsonObject4);
        // }
        // JsonArray myJsonArray = myArrayBuilder.build();

        // ffSvc.savingIt(username, myJsonArray.toString());


        model.addAttribute("username", username);
        //request the object as a parameter
        //method to save the recipe from foodservice class
        return "savedpage";
    }

    @GetMapping(path = "searchrecipe")
    public String displaySearchRecipe(
        @RequestParam(name = "username") String username,
        Model model
    )
    {

        //add a method here to check if the account exists in the redis database in case people try to cheat by editing the
        // the html page or the query parameter
        //if (!ffSvc.retrieveIt(username+"acct").equals(password)){return create an account view html page}

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
            return "savedrecipes";
        }

}
