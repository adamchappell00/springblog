package com.codeup.springblog;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Objects;

@Controller
public class DiceController {


    @GetMapping("/roll-dice")
    public String rollDiceForm() {
        return "roll-dice";
    }

    @GetMapping("/roll-dice/{number}")
    public String showDice(@PathVariable int number, Model model){
        double RandNum = (Math.random() * 6) + 1;
        int dice = (int) RandNum;
        Boolean guessed = Objects.equals(number, dice);
        model.addAttribute("dice", dice);
        model.addAttribute("number", number);
        model.addAttribute("correct", guessed);
        return "roll-result";
    }
}
