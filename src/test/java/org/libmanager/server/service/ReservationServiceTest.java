package org.libmanager.server.service;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.libmanager.server.entity.Book;
import org.libmanager.server.entity.DVD;
import org.libmanager.server.entity.Item;
import org.libmanager.server.entity.Reservation;
import org.libmanager.server.entity.User;
import org.libmanager.server.repository.ItemRepository;
import org.libmanager.server.repository.ReservationRepository;
import org.libmanager.server.repository.UserRepository;
import org.libmanager.server.response.Response;
import org.libmanager.server.service.impl.ReservationServiceImpl;
import org.libmanager.server.specification.ReservationSpecification;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ReservationServiceTest {

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    private final ReservationServiceImpl reservationService = new ReservationServiceImpl();

    private static Reservation reservation;
    private static List<Reservation> reservationList;
    private static Book book;
    private static User user;

    @BeforeAll
    public static void setUp() {
        book = new Book();
        book.setTitle("Foo");
        book.setAuthor("Foo");
        book.setPublisher("Foo");
        book.setGenre("Foo");
        book.setIsbn("Foo");
        book.setReleaseDate(LocalDate.EPOCH);
        book.setTotalCopies(1);
        book.setAvailableCopies(1);
        book.setItemType();

        user = new User();
        user.setUsername("Foo");
        user.setFirstName("Foo");
        user.setLastName("Foo");
        user.setEmail("Foo");
        user.setPassword("Foo");
        user.setAddress("Foo");
        user.setBirthday(LocalDate.EPOCH);
        user.setAdmin(false);

        reservation = new Reservation();
        reservation.setId(1);
        reservation.setUser(user);
        reservation.setItem(book);

        reservationList = Arrays.asList(reservation, reservation, reservation);
    }

    @Nested
    class Add {

        private ReservationServiceImpl reservationServiceSpy;
        private Item itemSpy;

        @BeforeEach
        public void setUp() {
             reservationServiceSpy = spy(reservationService);
             itemSpy = spy(book);
        }

        @Test
        @DisplayName("Returns OK on success")
        public void add_returnsOK_whenReservationIsSuccessfullyCreated() {
            when(itemRepository.findById(1L)).thenReturn(Optional.of(itemSpy));
            when(userRepository.findById("Foo")).thenReturn(Optional.of(user));
            doReturn(1).when(itemSpy).getAvailableCopies();
            doReturn(true).when(reservationServiceSpy).checkReservationLimits(user, itemSpy);

            Response<Boolean> result = reservationServiceSpy.add("Foo", 1L);

            assertThat(result.getCode()).isEqualTo(Response.Code.OK);
        }

        @Test
        @DisplayName("Returns OK on success")
        public void add_decrementAvailableCopies_whenSuccessfullyCreated() {
            when(itemRepository.findById(1L)).thenReturn(Optional.of(itemSpy));
            when(userRepository.findById("Foo")).thenReturn(Optional.of(user));
            doReturn(true).when(reservationServiceSpy).checkReservationLimits(user, itemSpy);
            int oldAvailable = itemSpy.getAvailableCopies();

            reservationServiceSpy.add("Foo", 1L);

            assertThat(itemSpy.getAvailableCopies()).isEqualTo(oldAvailable - 1);
        }

        @Test
        @DisplayName("Returns MAX_RESERVATIONS_REACHED if reservation limit is reached")
        public void add_returnsMaxReservationsReached_whenReservationLimitIsReached() {
            when(itemRepository.findById(1L)).thenReturn(Optional.of(itemSpy));
            when(userRepository.findById("Foo")).thenReturn(Optional.of(user));
            doReturn(false).when(reservationServiceSpy).checkReservationLimits(user, itemSpy);

            Response<Boolean> result = reservationServiceSpy.add("Foo", 1L);

            assertThat(result.getCode()).isEqualTo(Response.Code.MAX_RESERVATIONS_REACHED);
        }

        @Test
        @DisplayName("Returns NOT_AVAILABLE if item has no available copy")
        public void add_returnsNotAvailable_whenNoAvailableCopy() {
            when(itemRepository.findById(1L)).thenReturn(Optional.of(itemSpy));
            when(userRepository.findById("Foo")).thenReturn(Optional.of(user));
            doReturn(0).when(itemSpy).getAvailableCopies();
            doReturn(true).when(reservationServiceSpy).checkReservationLimits(user, itemSpy);

            Response<Boolean> result = reservationServiceSpy.add("Foo", 1L);

            assertThat(result.getCode()).isEqualTo(Response.Code.NOT_AVAILABLE);
        }

        @Test
        @DisplayName("Returns NOT_FOUND if item is not found")
        public void add_returnsNotFound_whenItemIsNotFound() {
            when(itemRepository.findById(1L)).thenReturn(Optional.empty());
            when(userRepository.findById("Foo")).thenReturn(Optional.of(user));

            Response<Boolean> result = reservationService.add("Foo", 1L);

            assertThat(result.getCode()).isEqualTo(Response.Code.NOT_FOUND);
        }

        @Test
        @DisplayName("Returns NOT_FOUND if user is not found")
        public void add_returnsNotFound_whenUserIsNotFound() {
            when(itemRepository.findById(1L)).thenReturn(Optional.of(book));
            when(userRepository.findById("Foo")).thenReturn(Optional.empty());

            Response<Boolean> result = reservationService.add("Foo", 1L);

            assertThat(result.getCode()).isEqualTo(Response.Code.NOT_FOUND);
        }

    }

    @Nested
    class Delete {

        @Test
        @DisplayName("Returns OK when successfully deleted")
        public void delete_shouldReturnOK_whenSuccessfullyDeleted() {
            when(reservationRepository.findById(1L)).thenReturn(Optional.of(reservation));

            Response<Boolean> result = reservationService.delete(1L);

            assertThat(result.getCode()).isEqualTo(Response.Code.OK);
        }

        @Test
        @DisplayName("Returns OK when successfully deleted")
        public void delete_shouldIncrementAvailableCopies_whenSuccessfullyDeleted() {
            when(reservationRepository.findById(1L)).thenReturn(Optional.of(reservation));
            book.setAvailableCopies(0);
            int oldAvailable = book.getAvailableCopies();

            reservationService.delete(1L);

            assertThat(book.getAvailableCopies()).isEqualTo(oldAvailable + 1);
        }

        @Test
        @DisplayName("Returns NOT_FOUND when reservation is not found")
        public void delete_shouldReturnNotFound_whenReservationIsNotFound() {
            when(reservationRepository.findById(1L)).thenReturn(Optional.empty());

            Response<Boolean> result = reservationService.delete(1L);

            assertThat(result.getCode()).isEqualTo(Response.Code.NOT_FOUND);
        }

    }

    @Nested
    class Getters {

        @Nested
        class getAll {

            @Test
            @DisplayName("Returns reservation list")
            public void getAll_shouldReturnReservationList() {
                when(reservationRepository.findAll()).thenReturn(reservationList);

                Iterable<Reservation> result = reservationService.getAll();

                assertThat(result).isEqualTo(reservationList);
            }

        }

        @Nested
        class Get {

            @Test
            @DisplayName("Returns the reservation if found")
            public void get_shouldReturnReservation_whenFound() {
                when(reservationRepository.findById(1L)).thenReturn(Optional.of(reservation));

                Reservation result = reservationService.get(1L);

                assertThat(result).isNotNull();
                assertThat(result).isEqualTo(reservation);
            }

            @Test
            @DisplayName("Returns null if not found")
            public void get_shouldReturnNull_whenNotFound() {
                when(reservationRepository.findById(1L)).thenReturn(Optional.empty());

                Reservation result = reservationService.get(1L);

                assertThat(result).isNull();
            }

        }

        @Nested
        class GetByUser {

            @Test
            @DisplayName("Returns OK if user is found")
            public void getByUser_shouldReturnOK_whenUserIsFound() {
                when(userRepository.findById(user.getUsername())).thenReturn(Optional.of(user));
                when(reservationRepository.findReservationsByUser(user)).thenReturn(reservationList);

                Response<Iterable<Reservation>> result = reservationService.getByUser(user.getUsername());

                assertThat(result.getCode()).isEqualTo(Response.Code.OK);
            }

            @Test
            @DisplayName("Returns user's reservation list if user is found")
            public void getByUser_shouldReturnReservationListOfTheUser_whenUserIsFound() {
                when(userRepository.findById(user.getUsername())).thenReturn(Optional.of(user));
                when(reservationRepository.findReservationsByUser(user)).thenReturn(reservationList);

                Response<Iterable<Reservation>> result = reservationService.getByUser(user.getUsername());

                assertThat(result.getContent()).isEqualTo(reservationList);
            }

            @Test
            @DisplayName("Returns NOT_FOUND if user is not found")
            public void getByUser_shouldReturnNotFound_whenUserIsNotFound() {
                when(userRepository.findById(user.getUsername())).thenReturn(Optional.empty());

                Response<Iterable<Reservation>> result = reservationService.getByUser(user.getUsername());

                assertThat(result.getCode()).isEqualTo(Response.Code.NOT_FOUND);
            }

            @Test
            @DisplayName("Returns null if user is not found")
            public void getByUser_shouldReturnNull_whenUserIsNotFound() {
                when(userRepository.findById(user.getUsername())).thenReturn(Optional.empty());

                Response<Iterable<Reservation>> result = reservationService.getByUser(user.getUsername());

                assertThat(result.getContent()).isNull();
            }

        }

    }

    @Nested
    class Search {

        @Test
        @DisplayName("Returns matching reservations")
        public void search_shouldReturnMatchingReservations() {
            when(reservationRepository.findAll(any(ReservationSpecification.class))).thenReturn(reservationList);

            Iterable<Reservation> result = reservationService.search(
                    reservation.getId(),
                    reservation.getUser().getUsername(),
                    reservation.getItem().getTitle(),
                    reservation.getItemType()
            );

            assertThat(result).isEqualTo(reservationList);
        }

    }

    @Nested
    class Checks {

        @Test
        public void checkReservationLimits() {
            boolean result;

            DVD dvd = new DVD();
            dvd.setItemType();


            // If this returns false, then the user is a child
            user.setRegistrationDate(LocalDate.now());
            user.setBirthday(LocalDate.now());
            result = reservationService.checkReservationLimits(user, dvd);
            assertThat(result).isFalse();

            // If this returns false, then the user is an adult
            user.setRegistrationDate(LocalDate.now());
            user.setBirthday(LocalDate.now().minusYears(12));
            result = reservationService.checkReservationLimits(user, book);
            assertThat(result).isTrue();


            // Admin can borrow as many as he wants
            user.setAdmin(true);
            result = reservationService.checkReservationLimits(user, book);
            assertThat(result).isTrue();
        }

        @Test
        public void checkBookReservationLimits() {
            boolean result;

            // Not adult and less than 5 books borrowed
            result = reservationService.checkBookReservationLimits(0, false, 4);
            assertThat(result).isTrue();

            // Not adult and 5 books borrowed
            result = reservationService.checkBookReservationLimits(0, false, 5);
            assertThat(result).isFalse();

            // Adult, member since less than a year and less than 4 books borrowed
            result = reservationService.checkBookReservationLimits(0, true, 3);
            assertThat(result).isTrue();

            // Adult, member since less than a year and 4 books borrowed
            result = reservationService.checkBookReservationLimits(0, true, 4);
            assertThat(result).isFalse();

            // Adult, member since less than two years and less than 5 books borrowed
            result = reservationService.checkBookReservationLimits(1, true, 4);
            assertThat(result).isTrue();

            // Adult, member since less than two years and 5 books borrowed
            result = reservationService.checkBookReservationLimits(1, true, 5);
            assertThat(result).isFalse();

            // Adult, member since more than two years and less than 7 books borrowed
            result = reservationService.checkBookReservationLimits(2, true, 6);
            assertThat(result).isTrue();

            // Adult, member since more than two years and 7 books borrowed
            result = reservationService.checkBookReservationLimits(2, true, 7);
            assertThat(result).isFalse();

        }

        @Test
        public void checkDVDReservationLimits() {
            boolean result;

            // Not adult
            result = reservationService.checkDVDReservationLimits(0, false, 0);
            assertThat(result).isFalse();

            // Adult, member since less than a year and less than 2 DVDs borrowed
            result = reservationService.checkDVDReservationLimits(0, true, 1);
            assertThat(result).isTrue();

            // Adult, member since less than a year and 2 DVDs borrowed
            result = reservationService.checkDVDReservationLimits(0, true, 2);
            assertThat(result).isFalse();

            // Adult, member since less than two years and less than 3 DVDs borrowed
            result = reservationService.checkDVDReservationLimits(1, true, 2);
            assertThat(result).isTrue();

            // Adult, member since less than two years and 3 DVDs borrowed
            result = reservationService.checkDVDReservationLimits(1, true, 3);
            assertThat(result).isFalse();

            // Adult, member since more than two years and less than 5 DVDs borrowed
            result = reservationService.checkDVDReservationLimits(2, true, 4);
            assertThat(result).isTrue();

            // Adult, member since more than two years and 5 DVDs borrowed
            result = reservationService.checkDVDReservationLimits(2, true, 5);
            assertThat(result).isFalse();

        }

    }

}
