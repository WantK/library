package com.example.library.Controller;

import com.example.library.Entity.Location;
import com.example.library.Repository.locationRepository;
import com.example.library.Util.CookieUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

@Controller
public class locationController {

    @Autowired
    locationRepository LocationRepository;

    @RequestMapping(value = "/addLocation", method = RequestMethod.POST)
    @ResponseBody
    public String addLocation(@Valid Location location){
        List<Location> locationList = LocationRepository.findByLocationFloor(location.getLocationFloor());
        for(int i = 0; i < locationList.size(); i++) {
            if (location.getLocationClass().equals(locationList.get(i).getLocationClass()) && location.getLocationShelf().equals(locationList.get(i).getLocationShelf()))
                return "The location has been in use, it can't be add again.";
        }
        LocationRepository.save(location);
        return "The location has been add successfully.";
    }

    @RequestMapping(value="/manageLocation", method = RequestMethod.GET)
    public String showLocation(HttpServletRequest request, Model model){
        List<Location> locationList = LocationRepository.findAll();
        model.addAttribute("locationList",locationList);
        model.addAttribute("librarianName", CookieUtils.getCookieValue(request,"librarianName"));
        return "librarian/manageLocation";
    }


    @RequestMapping(value="/editLocation", method = RequestMethod.PUT)
    @ResponseBody
    public String editLocation(@Valid Location location,@RequestParam("locationId")Integer locationId){
        location.setLocationId(locationId);

        List<Location> locationList = LocationRepository.findByLocationFloor(location.getLocationFloor());
        for(int i = 0; i < locationList.size(); i++) {
            if (location.getLocationClass().equals(locationList.get(i).getLocationClass()) && location.getLocationShelf().equals(locationList.get(i).getLocationShelf()))
                return "The location has been in use, it can't be add again.";
        }
        LocationRepository.save(location);
        return "The location has been add successfully.";
    }

    @RequestMapping(value="/deleteLocation", method = RequestMethod.DELETE)
    @ResponseBody
    public void deleteLocation(@RequestParam("locationId")Integer locationId){
        LocationRepository.deleteById(locationId);
    }
}
