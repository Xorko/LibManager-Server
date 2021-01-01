package org.libmanager.server.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.libmanager.server.response.Response;
import org.libmanager.server.service.ItemService;
import org.libmanager.server.util.TokenUtil;
import org.mockito.MockedStatic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemController.class)
public class ItemControllerTest {

    @MockBean
    private ItemService itemService;

    @Autowired
    private MockMvc mockMvc;

    @Nested
    class DeleteItem {

        private final String uri = "/item/delete/{id}";

        @Test
        @DisplayName("Delete should return OK if params are correct and token is valid and is admin")
        public void deleteItem_shouldReturnOk_whenParamsAreCorrectAndTokenIsValidAndIsAdmin() throws Exception {
            try (MockedStatic<TokenUtil> mockedTokenUtil = mockStatic(TokenUtil.class)) {
                mockedTokenUtil.when(() -> TokenUtil.isValid("Foo"))
                               .thenReturn(true);
                mockedTokenUtil.when(() -> TokenUtil.isAdmin("Foo"))
                               .thenReturn(true);

                when(itemService.delete(1)).thenReturn(true);

                mockMvc.perform(post(uri, 1)
                        .param("token", "Foo"))
                       .andExpect(status().isOk())
                       .andExpect(jsonPath("$.code").value(Response.Code.OK.toString()));
            }
        }

        @Test
        @DisplayName("Delete should return NOT_FOUND if item is not found")
        public void deleteItems_shouldReturnNotFound_whenCorrectParamsAndNotFound() throws Exception {
            try (MockedStatic<TokenUtil> mockedTokenUtil = mockStatic(TokenUtil.class)) {
                mockedTokenUtil.when(() -> TokenUtil.isValid("Foo"))
                               .thenReturn(true);
                mockedTokenUtil.when(() -> TokenUtil.isAdmin("Foo"))
                               .thenReturn(true);

                when(itemService.delete(1)).thenReturn(false);

                mockMvc.perform(post(uri, 1)
                        .param("token", "Foo"))
                       .andExpect(status().isOk())
                       .andExpect(jsonPath("$.code").value(Response.Code.NOT_FOUND.toString()));
            }
        }

        @Test
        @DisplayName("Delete should return INVALID_TOKEN if the token is invalid")
        public void deleteItems_shouldReturnInvalidToken_whenInvalidToken() throws Exception {
            try (MockedStatic<TokenUtil> mockedTokenUtil = mockStatic(TokenUtil.class)) {
                mockedTokenUtil.when(() -> TokenUtil.isValid("Foo"))
                               .thenReturn(false);

                mockMvc.perform(post(uri, 1)
                        .param("token", "Foo"))
                       .andExpect(status().isOk())
                       .andExpect(jsonPath("$.code").value(Response.Code.INVALID_TOKEN.toString()));
            }
        }

        @Test
        @DisplayName("Delete should return INSUFFICIENT_PERMISSIONS if the token is not an admin token")
        public void deleteItems_shouldReturnInsufficientPermissions_whenTokenIsNotAnAdminToken() throws Exception {
            try (MockedStatic<TokenUtil> mockedTokenUtil = mockStatic(TokenUtil.class)) {
                mockedTokenUtil.when(() -> TokenUtil.isValid("Foo"))
                               .thenReturn(true);
                mockedTokenUtil.when(() -> TokenUtil.isAdmin("Foo"))
                               .thenReturn(false);

                mockMvc.perform(post(uri, 1)
                        .param("token", "Foo"))
                       .andExpect(status().isOk())
                       .andExpect(jsonPath("$.code").value(Response.Code.INSUFFICIENT_PERMISSIONS.toString()));
            }
        }
    }

}
