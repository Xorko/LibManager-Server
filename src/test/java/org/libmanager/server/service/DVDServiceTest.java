package org.libmanager.server.service;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.libmanager.server.entity.DVD;
import org.libmanager.server.repository.DVDRepository;
import org.libmanager.server.repository.ItemRepository;
import org.libmanager.server.response.Response;
import org.libmanager.server.service.impl.DVDServiceImpl;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class DVDServiceTest {

    @Mock
    private DVDRepository dvdRepository;

    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    private final DVDService dvdService = new DVDServiceImpl();

    private static DVD dvd;
    private static List<DVD> dvdList;

    @BeforeAll
    public static void setUp() {
        dvd = new DVD();
        dvd.setTitle("Foo");
        dvd.setAuthor("Foo");
        dvd.setDuration("Foo");
        dvd.setGenre("Foo");
        dvd.setReleaseDate(LocalDate.EPOCH);
        dvd.setTotalCopies(1);
        dvd.setAvailableCopies(1);

        dvdList = Arrays.asList(dvd, dvd, dvd);
    }

    @Nested
    class Add {

        @Test
        @DisplayName("Returns Response with OK code if added successfully")
        public void add_shouldReturnResponseWithOKCode_whenAddedSuccessfully() {
            when(itemRepository.sumTotalCopies()).thenReturn(99_999L);

            Response<Boolean> result = dvdService.add(
                    dvd.getTitle(),
                    dvd.getAuthor(),
                    dvd.getDuration(),
                    dvd.getGenre(),
                    dvd.getReleaseDate().toString(),
                    dvd.getTotalCopies()
            );

            assertThat(result.getCode()).isEqualTo(Response.Code.OK);
        }

        @Test
        @DisplayName("Returns Response with OK code if item table is empty and dvd added successfully")
        public void add_shouldReturnResponseWithOKCode_whenItemTableIsEmptyAndDVDAddedSuccessfully() {
            when(itemRepository.sumTotalCopies()).thenReturn(null);

            Response<Boolean> result = dvdService.add(
                    dvd.getTitle(),
                    dvd.getAuthor(),
                    dvd.getDuration(),
                    dvd.getGenre(),
                    dvd.getReleaseDate().toString(),
                    dvd.getTotalCopies()
            );

            assertThat(result.getCode()).isEqualTo(Response.Code.OK);
        }

        @Test
        @DisplayName("Returns Response with MAX_ITEMS_REACHED code if item limit is reached")
        public void add_shouldReturnResponseWithMaxItemsReached_whenLimitIsReached() {
            when(itemRepository.sumTotalCopies()).thenReturn(100_000L);

            Response<Boolean> result = dvdService.add(
                    dvd.getTitle(),
                    dvd.getAuthor(),
                    dvd.getDuration(),
                    dvd.getGenre(),
                    dvd.getReleaseDate().toString(),
                    dvd.getTotalCopies()
            );

            assertThat(result.getCode()).isEqualTo(Response.Code.MAX_ITEMS_REACHED);
        }

    }

    @Nested
    class Edit {

        @Test
        @DisplayName("Returns Response with OK code if edited successfully")
        public void edit_shouldReturnResponseWithOK_whenEditedSuccessfully() {
            when(dvdRepository.findById(1L)).thenReturn(Optional.of(dvd));
            when(itemRepository.sumTotalCopies()).thenReturn(100_000L);

            Response<Boolean> result = dvdService.edit(
                    1L,
                    dvd.getTitle(),
                    dvd.getAuthor(),
                    dvd.getDuration(),
                    dvd.getGenre(),
                    dvd.getReleaseDate().toString(),
                    dvd.getTotalCopies()
            );

            assertThat(result.getCode()).isEqualTo(Response.Code.OK);
        }

        @Test
        @DisplayName("Returns Response with MAX_ITEMS_REACHED code if the new totalCopies value make sumTotalCopies more than 100,000")
        public void edit_shouldReturnResponseWithMaxItemsReached_whenLimitIsReached() {
            when(dvdRepository.findById(1L)).thenReturn(Optional.of(dvd));
            when(itemRepository.sumTotalCopies()).thenReturn(100_000L);

            Response<Boolean> result = dvdService.edit(
                    1L,
                    dvd.getTitle(),
                    dvd.getAuthor(),
                    dvd.getDuration(),
                    dvd.getGenre(),
                    dvd.getReleaseDate().toString(),
                    dvd.getTotalCopies() + 1
            );

            assertThat(result.getCode()).isEqualTo(Response.Code.MAX_ITEMS_REACHED);
        }

        @Test
        @DisplayName("Returns INVALID_TOTAL_COPIES if the new totalCopies value is lower than the availableCopies value")
        public void edit_shouldReturnInvalidTotalCopies_whenTotalCopiesIsLowerThanAvailableCopies() {
            DVD dvd = new DVD();
            dvd.setTitle("Foo");
            dvd.setAuthor("Foo");
            dvd.setDuration("Foo");
            dvd.setGenre("Foo");
            dvd.setReleaseDate(LocalDate.EPOCH);
            dvd.setAvailableCopies(2);
            dvd.setTotalCopies(8);

            when(dvdRepository.findById(1L)).thenReturn(Optional.of(dvd));
            when(itemRepository.sumTotalCopies()).thenReturn(100L);

            Response<Boolean> result = dvdService.edit(
                    1L,
                    dvd.getTitle(),
                    dvd.getAuthor(),
                    dvd.getDuration(),
                    dvd.getGenre(),
                    dvd.getReleaseDate().toString(),
                    1
            );

            assertThat(result.getCode()).isEqualTo(Response.Code.INVALID_TOTAL_COPIES);
        }

        @Test
        @DisplayName("Returns NOT_FOUND if dvd is not found")
        public void edit_shouldReturnNotFound_whenDVDIsNotFound() {
            when(dvdRepository.findById(1L)).thenReturn(Optional.empty());

            Response<Boolean> result = dvdService.edit(
                    1L,
                    dvd.getTitle(),
                    dvd.getAuthor(),
                    dvd.getDuration(),
                    dvd.getGenre(),
                    dvd.getReleaseDate().toString(),
                    1
            );

            assertThat(result.getCode()).isEqualTo(Response.Code.NOT_FOUND);
        }

    }

    @Nested
    class Getters {

        @Nested
        class GetAll {

            @Test
            @DisplayName("Returns the dvd list")
            public void getAll_shouldReturnDVDList() {
                when(dvdRepository.findAll()).thenReturn(dvdList);

                Iterable<DVD> result = dvdService.getAll();

                assertThat(result).isEqualTo(dvdList);
            }

        }

        @Nested
        class Get {

            @Test
            @DisplayName("Returns the dvd if found")
            public void get_shouldReturnDVD_whenFound() {
                when(dvdRepository.findById(1L)).thenReturn(Optional.of(dvd));

                DVD result = dvdService.get(1L);

                assertThat(result).isEqualTo(dvd);
            }

            @Test
            @DisplayName("Returns null if not found")
            public void get_shouldReturnNull_whenNotFound() {
                when(dvdRepository.findById(1L)).thenReturn(Optional.empty());

                DVD result = dvdService.get(1L);

                assertThat(result).isNull();
            }

        }

    }

    @Nested
    class Search {

        @Test
        @DisplayName("Returns matching dvds")
        public void search_shouldReturnMatchingDVDs() {
            when(dvdRepository.findAll(ArgumentMatchers.<Specification<DVD>>any())).thenReturn(dvdList);

            Iterable<DVD> result = dvdService.search(
                    dvd.getTitle(),
                    dvd.getAuthor(),
                    dvd.getGenre(),
                    dvd.getReleaseDate().toString(),
                    Boolean.toString(dvd.getStatus())
            );

            assertThat(result).isEqualTo(dvdList);
        }

    }

}