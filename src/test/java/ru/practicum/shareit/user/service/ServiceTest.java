package ru.practicum.shareit.user.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import org.junit.jupiter.api.BeforeEach;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.equalTo;

@Transactional
@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ServiceTest {

    private final EntityManager entityManager;
    private final UserService userService;
    private UserDto userDto;

    @BeforeEach
    void initialize() {
        userDto = new UserDto(null,"Vas", "vas@mail.com");
    }

    private void addUsers() {

        userService.addUser(new UserDto(null,"Ivan", "ivan@mail.com"));

        userService.addUser(new UserDto(null,"Denis", "denis@mail.com"));

        userService.addUser(new UserDto(null,"Stas", "stas@mail.com"));
    }

    @Test
    void saveTest() {

        userService.addUser(userDto);

        User user = entityManager.createQuery(
                        "SELECT user FROM User user " +
                                "WHERE user.email = :email",
                        User.class)
                .setParameter("email", userDto.getEmail())
                .getSingleResult();

        assertThat(user.getId(), notNullValue());

        assertThat(user.getName(), equalTo(userDto.getName()));

        assertThat(user.getEmail(), equalTo(userDto.getEmail()));
    }

    @Test
    void getTest() {

        userService.addUser(userDto);

        var user = entityManager.createQuery(
                        "SELECT user FROM User user " +
                                "WHERE user.email = :email",
                        User.class)
                .setParameter("email", userDto.getEmail())
                .getSingleResult();

        var userDtoFrom = userService.getUser(user.getId());

        assertThat(userDtoFrom.getId(), equalTo(user.getId()));

        assertThat(userDtoFrom.getName(), equalTo(user.getName()));

        assertThat(userDtoFrom.getEmail(), equalTo(user.getEmail()));
    }

    @Test
    void updateTest() {

        userService.addUser(userDto);

        var user = entityManager.createQuery(
                        "SELECT user FROM User user " +
                                "WHERE user.email = :email",
                        User.class)
                .setParameter("email", userDto.getEmail())
                .getSingleResult();

        UserDto dto = new UserDto(null,"Kirill", "kirill@mail.com");

        userService.updateUser(dto, user.getId());

        var updatedUser = entityManager.createQuery(
                        "SELECT user FROM User user " +
                                "WHERE user.id = :id",
                        User.class)
                .setParameter("id", user.getId())
                .getSingleResult();

        assertThat(updatedUser.getId(), notNullValue());

        assertThat(updatedUser.getName(), equalTo(dto.getName()));

        assertThat(updatedUser.getEmail(), equalTo(dto.getEmail()));
    }

    @Test
    void deleteTest() {

        addUsers();

        List<User> usersBefore = entityManager
                .createQuery("SELECT user FROM User user", User.class)
                .getResultList();

        assertThat(usersBefore.size(), equalTo(3));

        userService.deleteUser(usersBefore.get(0).getId());

        List<User> usersAfter = entityManager
                .createQuery("SELECT user FROM User user", User.class)
                .getResultList();

        assertThat(usersAfter.size(), equalTo(2));
    }

    @Test
    void getAllTest() {

        addUsers();

        List<User> users = entityManager
                .createQuery("SELECT user FROM User user", User.class)
                .getResultList();

        assertThat(users.size(), equalTo(3));
    }
}
