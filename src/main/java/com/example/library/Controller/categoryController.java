package com.example.library.Controller;

import com.example.library.Entity.Category;
import com.example.library.Repository.categoryRepository;
import com.example.library.Util.CookieUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
public class categoryController {

    @Autowired
    private categoryRepository CategoryRepository;

    @GetMapping(value = "/manageCategory")
    public String manageCategory(HttpServletRequest request,
                                 Model model){
        List<Category> categoryList = CategoryRepository.findAll();
        model.addAttribute("categoryList",categoryList);
        model.addAttribute("librarianName",CookieUtils.getCookieValue(request,"librarianName"));
        return "librarian/manageCategory";
    }

    @PostMapping(value = "/addCategory")
    @ResponseBody
    public String addCategory(@RequestParam(value = "category") String category){
        List<Category> categoryList = CategoryRepository.findAll();
        if(categoryList.size() != 0){
            for(int i = 0;i < categoryList.size();i++){
                if(categoryList.get(i).getCategory().equals(category)){
                    return "Already exist this category";
                }
            }
        }
        Category category1 = new Category();
        category1.setCategory(category);
        CategoryRepository.save(category1);
        return "Add successfully";
    }


    @PostMapping(value = "/deleteCategory")
    @ResponseBody
    public String deleteCategory(@RequestParam(value = "id") Integer id){
        CategoryRepository.deleteById(id);

        return "Delete successfully";
    }


    @PostMapping(value = "/updateCategory")
    @ResponseBody
    public String updateCategory(@RequestParam(value = "id") Integer id,
                                 @RequestParam(value = "category") String category){
        Category categorytemp = CategoryRepository.findByCategory(category);
        if(categorytemp!=null)
            return "Already exist this category";

        Category category1 = CategoryRepository.findById(id).orElse(null);
        category1.setCategory(category);
        CategoryRepository.save(category1);

        return "Update successfully";
    }
}
