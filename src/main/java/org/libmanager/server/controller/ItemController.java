package org.libmanager.server.controller;

import org.libmanager.server.model.Response;
import org.libmanager.server.service.ItemService;
import org.libmanager.server.util.TokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/item")
public class ItemController {

    @Autowired
    private ItemService itemService;

    /**
     * Delete the given book
     * @param token The token of the user (must be admin=
     * @param id    The id of the item to delete
     * @return      A JSON response with OK code and true if the item was successfully deleted, an error code and false
     *              otherwise
     */
    @PostMapping(path = "/delete/{id}")
    public @ResponseBody
    Response<Boolean> delete(
            @RequestParam String token,
            @PathVariable long id
    ) {
        if (TokenUtil.isValid(token)) {
            if (TokenUtil.isAdmin(token)) {
                boolean deleted = itemService.delete(id);
                if (deleted)
                    return new Response<>(Response.Code.OK, true);
                else
                    return new Response<>(Response.Code.NOT_FOUND, false);
            }
            return new Response<>(Response.Code.INSUFFICIENT_PERMISSIONS, false);
        }
        return new Response<>(Response.Code.INVALID_TOKEN, false);
    }

}
