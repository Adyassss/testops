package api;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import api.generators.RandomData;
import api.models.comparison.ModelAssertions;
import api.requests.steps.AdminSteps;
import api.requests.steps.ChangeNameSteps;
import api.requests.steps.UserProfileSteps;

import javax.validation.constraints.AssertTrue;


public class UserCanChangeUsernameTest extends BaseTest {

    @Test
    public void userCanChangeUsername() {
        String name = RandomData.getName();

        String userToken = AdminSteps.createToken();

        ChangeNameSteps.changeName(userToken, name);

        String nameAfter = UserProfileSteps.getUserProfileName(userToken);

        ModelAssertions.assertThatModels(name, nameAfter).match();

    }

    @ParameterizedTest
    @MethodSource("api.generators.RandomData#NegativeNames")
    public void userCantChangeUsernameWithInvalidData(String invalidName) {
        String userToken = AdminSteps.createToken();

        String nameBefore = UserProfileSteps.getUserProfileName(userToken);

        ChangeNameSteps.changeNameWithInvalidData(userToken, invalidName);

        String nameAfter = UserProfileSteps.getUserProfileName(userToken);

        ModelAssertions.assertThatModels(nameBefore, nameAfter).match();
    }

    @ParameterizedTest
    @MethodSource("api.generators.RandomData#NegativeNames")
    public void AdminCantChangeUsername(String invalidName) {
        ChangeNameSteps.AdminCantChangeName(invalidName);

    }

    @ParameterizedTest
    @MethodSource("api.generators.RandomData#NegativeNames")
    public void userCantChangeUsernameUnAuthAdmin(String invalidName) {
        AdminSteps.createTokenUnAuth();
    }

}
