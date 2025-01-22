package ru.yandex.practicum.filmorate.validation;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import ru.yandex.practicum.filmorate.model.user.User;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserValidationTest {
    final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    final Validator validator = factory.getValidator();
    User user;

    @BeforeEach
    void beforeEach() {
        user = new User();
        user.setId(1L);
        user.setEmail("userTest@yandex.ru");
        user.setLogin("prikol353");
        user.setName("Leonid");
        user.setBirthday(LocalDate.of(1997, 12, 31));
    }

    @DisplayName("Валидация поля email")
    @ParameterizedTest
    @CsvSource({
            "'', Email не может быть пустым",
            "null, Email не может быть пустым",
            "email, Неверный формат email",
            "@email, Неверный формат email",
            "email@, Неверный формат email",
            "email@@dd, Неверный формат email",
            "ema il@dd, Неверный формат email",
            "prikol353@yandex.ru, OK"
    })
    void emailValidation(String testEmail, String expectedErrorMessage) {
        if ("null".equals(testEmail)) {
            testEmail = null;
        }
        user.setEmail(testEmail);
        List<String> violations = validator.validate(user).stream()
                .map(ConstraintViolation::getMessage)
                .filter(expectedErrorMessage::equals)
                .toList();

        if (!violations.isEmpty()) {
            Assertions.assertEquals(expectedErrorMessage, violations.getFirst());
        }
    }

    @DisplayName("Валидация поля login")
    @ParameterizedTest
    @CsvSource({
            "'', Логин не может быть пустым",
            "null, Логин не может быть пустым",
            "' prikol353', Логин не может содержать пробелы",
            "'prikol353 ', Логин не может содержать пробелы",
            "'pri kol353', Логин не может содержать пробелы",
            "'prikol\t353', Логин не может содержать пробелы",
            "'prikol353', OK",
    })
    void loginValidation(String testLogin, String expectedErrorMessage) {
        user.setLogin(testLogin);
        List<String> violations = validator.validate(user).stream()
                .map(ConstraintViolation::getMessage)
                .filter(expectedErrorMessage::equals)
                .toList();

        if (!violations.isEmpty()) {
            Assertions.assertEquals(expectedErrorMessage, violations.getFirst());
        }
    }

    @DisplayName("Валидация поля name")
    @ParameterizedTest
    @CsvSource({
            "'', prikol353",
            "null, prikol353",
            "Viktoria, Viktoria"
    })
    void nameValidation(String testName, String expectedName) {
        if ("null".equals(testName)) {
            testName = null;
        }
        user.setName(testName);
        Assertions.assertEquals(expectedName, user.getName());
    }


    @DisplayName("Валидация поля birthday")
    @Test
    void birthdayValidation() {
        LocalDate nowDate = LocalDate.now().plusDays(1);
        user.setBirthday(nowDate);
        Set<ConstraintViolation<User>> violations = validator.validate(user);

        Assertions.assertEquals("Дата рождения не может быть в будущем",
                violations.iterator().next().getMessage());
    }
}
