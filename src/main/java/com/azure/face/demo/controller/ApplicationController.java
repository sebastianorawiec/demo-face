package com.azure.face.demo.controller;

import com.azure.face.demo.model.MatchedPerson;
import com.azure.face.demo.model.Persons;
import com.azure.face.demo.model.repository.MatchesRepository;
import com.azure.face.demo.service.AzureService;
import com.azure.face.demo.service.DetectionService;
import com.azure.face.demo.service.PersonsService;
import com.azure.face.demo.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;

@Controller
public class ApplicationController {


   @Autowired
   PersonsService personsService;
   @Autowired
   AzureService azureService;
   @Autowired
   DetectionService detectionService;
   @Autowired
   MatchesRepository matchesRepository;


   @GetMapping("/")
   public String index(Persons person, HttpServletRequest request) {

       return "index";
   }


   @GetMapping("/adm")
   public String showAdminPage(Model model, HttpServletRequest request) {


        model.addAttribute("persons",personsService.getAll(getPagination(request)));
        return "adm";
   }


    @GetMapping("/adm/log")
    public String log(Model model, HttpServletRequest request) {

        model.addAttribute("logs",matchesRepository.findAll(getPagination(request)));
        return "logs";
    }

    @GetMapping("/adm/add")
    public String showAddForm(Persons persons,Model model) {

       return "add";
    }

    @GetMapping("/adm/delete/{id}")
    public String deletePerson(@PathVariable("id") long id) {
        personsService.removePerson(id);
        return "redirect:/adm";
    }


    @PostMapping("/adm/add")
    public String addPerson(@ModelAttribute @Validated Persons person, BindingResult result) {

       if (result.hasErrors()) {
           return "add";
        }

       personsService.addPerson(person);
       return "redirect:/adm";
    }

    @PostMapping("/")
    public String findPerson(@RequestParam MultipartFile photo,Model model, HttpServletRequest request) {

        MatchedPerson person = detectionService.findPerson(photo, Utils.getClientIp(request));
        model.addAttribute("person",person);
        return "index";
    }


    private PageRequest getPagination(HttpServletRequest request){

        int page = 0;
        int size = 6; //default page size

        if (request.getParameter("page") != null && !request.getParameter("page").isEmpty()) {
            page = Integer.parseInt(request.getParameter("page")) - 1;
        }

        if (request.getParameter("size") != null && !request.getParameter("size").isEmpty()) {
            size = Integer.parseInt(request.getParameter("size"));
        }

        return PageRequest.of(page, size);

    }

}


