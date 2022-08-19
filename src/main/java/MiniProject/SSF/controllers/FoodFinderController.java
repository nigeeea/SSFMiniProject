package MiniProject.SSF.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import MiniProject.SSF.models.Food;
import MiniProject.SSF.services.FoodFinderService;

@Controller

@RequestMapping(path = "/")
public class FoodFinderController {

    @Autowired
    private FoodFinderService ffSvc;

    @GetMapping(path = "/displayrecipe")
    public String displayRandomRecipe(
        @RequestParam(name = "userInput", defaultValue = "chicken") String input,
        Model model
    )
    {
        model.addAttribute("inputz", input);
        
        List<Food> finalFoodList = ffSvc.getFood(input);
        model.addAttribute("foodListz", finalFoodList);
        
        return "displayrecipe";


    }
}
