package org.libmanager.server.controller;

import org.libmanager.server.entity.DVD;
import org.libmanager.server.response.Response;
import org.libmanager.server.service.DVDService;
import org.libmanager.server.util.TokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(path = "/item/dvd")
public class DVDController {

    @Autowired
    private DVDService dvdService;

    /**
     * Add a DVD
     * @param token         The token of the user (must be admin)
     * @param title         The title of the DVD
     * @param director      The director of the DVD
     * @param duration      The duration of the DVD
     * @param genre         The genre of the DVD
     * @param releaseDate   The release date of the DVD
     * @param totalCopies   The number of total copies of the DVD
     * @return              A response with OK code and true if the add was successful, the error and false otherwise
     */
    @PostMapping(path = "/add")
    public @ResponseBody
    Response<Boolean> add(
            @RequestParam String token,
            @RequestParam String title,
            @RequestParam String director,
            @RequestParam String duration,
            @RequestParam String genre,
            @RequestParam String releaseDate,
            @RequestParam int totalCopies
    ) {
        if (TokenUtil.isValid(token)) {
            if (TokenUtil.isAdmin(token))
                return dvdService.add(title, director, duration, genre, releaseDate, totalCopies);
            return new Response<>(Response.Code.INSUFFICIENT_PERMISSIONS, false);
        }
        return new Response<>(Response.Code.INVALID_TOKEN, false);
    }

    /**
     * Edit the given DVD
     * @param token         The token of the user (must be admin)
     * @param id            The id of the DVD to edit
     * @param title         The new title of the DVD
     * @param director      The new director of the DVD
     * @param duration      The new duration of the DVD
     * @param genre         The new genre of the DVD
     * @param releaseDate   The new release date of the DVD
     * @param totalCopies   The new total number of total copies of the DVD
     * @return              A JSON response with OK code and true if the edit was successful, the error and false otherwise
     */
    @PostMapping(path = "/edit/{id}")
    public @ResponseBody
    Response<Boolean> edit(
            @RequestParam String token,
            @PathVariable long id,
            @RequestParam String title,
            @RequestParam String director,
            @RequestParam String duration,
            @RequestParam String genre,
            @RequestParam String releaseDate,
            @RequestParam int totalCopies
    ) {
        if (TokenUtil.isValid(token)) {
            if (TokenUtil.isAdmin(token))
                return dvdService.edit(id, title, director, duration, genre, releaseDate, totalCopies);
            return new Response<>(Response.Code.INSUFFICIENT_PERMISSIONS, false);
        }
        return new Response<>(Response.Code.INVALID_TOKEN, false);
    }

    /**
     * Get all DVDs
     * @return A JSON response with OK code and an array with all DVDs
     */
    @GetMapping(path = "/all")
    public @ResponseBody
    Response<Iterable<DVD>> getAll() {
        return new Response<>(Response.Code.OK, dvdService.getAll());
    }

    /**
     * Get a DVD
     * @param id    The id of the DVD to get
     * @return      A JSON response with OK code and the DVD if found, NOT_FOUND code and null otherwise
     */
    @GetMapping(path = "/get/{id}")
    public @ResponseBody
    Response<DVD> get(@PathVariable long id) {
        DVD dvd = dvdService.get(id);
        if (dvd != null)
            return new Response<>(Response.Code.OK, dvd);
        return new Response<>(Response.Code.NOT_FOUND, null);
    }

    /**
     * Search DVDs
     * @param title         The title to find
     * @param author        The author to find
     * @param genre         The genre to find
     * @param releaseDate   The release date to find
     * @param status        The status to find
     * @return              A JSON response with OK code and an array with all DVDs found
     */
    @GetMapping(path = "/search")
    public @ResponseBody
    Response<Iterable<DVD>> search(
            @RequestParam(defaultValue = "null") String title,
            @RequestParam(defaultValue = "null") String author,
            @RequestParam(defaultValue = "null") String genre,
            @RequestParam(defaultValue = "null") String releaseDate,
            @RequestParam(defaultValue = "null") String status
    ) {
        Iterable<DVD> dvdIterable = dvdService.search(title, author, genre, releaseDate, status);
        return new Response<>(Response.Code.OK, dvdIterable);
    }

}
