package org.libmanager.server.service;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.libmanager.server.entity.Book;
import org.libmanager.server.repository.ItemRepository;
import org.libmanager.server.service.impl.ItemServiceImpl;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ItemServiceTest {

    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    private final ItemService itemService = new ItemServiceImpl();

    @Nested
    class delete {

        @Test
        @DisplayName("Returns true if the item is found and deleted")
        public void delete_shouldReturnTrue_whenItemIsFoundAndDeleted() {
            when(itemRepository.findById(1L)).thenReturn(Optional.of(new Book()));

            boolean result = itemService.delete(1);

            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("Returns false if the item is not found")
        public void delete_shouldReturnFalse_whenItemIsNotFound() {
            when(itemRepository.findById(1L)).thenReturn(Optional.empty());

            boolean result = itemService.delete(1);

            assertThat(result).isFalse();
        }

    }

}
