package api;
import api.generators.RandomData;
import api.models.comparison.ModelAssertions;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import api.requests.steps.AdminSteps;
import api.requests.steps.UserDepositSteps;
import api.requests.steps.UserProfileSteps;

public class UserCanDepositTest extends BaseTest {

    @Test
    public void userCanDeposit() {
        float amount = RandomData.getAmount();
        String userToken = AdminSteps.createToken();

        int senderId = UserProfileSteps.createUserProfileId(userToken);

        float balanceBefore = UserProfileSteps.getUserProfileAccountBalance(userToken, senderId);

        UserDepositSteps.depositMoney(userToken, senderId, amount);

        float balanceAfter = UserProfileSteps.getUserProfileAccountBalance(userToken, senderId);

        ModelAssertions.assertThatModels(balanceBefore + amount, balanceAfter).match();
    }

    @MethodSource("api.generators.RandomData#NegativeAmount")
    @ParameterizedTest
    public void userCantDepositWithInvalidData(float amount) {

        String userToken = AdminSteps.createToken();

        int senderId = UserProfileSteps.createUserProfileId(userToken);

        float balanceBefore = UserProfileSteps.getUserProfileAccountBalance(userToken, senderId);

        UserDepositSteps.depositMoneyWithInvalidData(userToken, senderId, amount);

        float balanceAfter = UserProfileSteps.getUserProfileAccountBalance(userToken, senderId);

        ModelAssertions.assertThatModels(balanceBefore, balanceAfter).match();

    }

    @MethodSource("api.generators.RandomData#NegativeAmount")
    @ParameterizedTest
    public void userCantDepositWithUnAuth(float amount) {

        String userToken = AdminSteps.createToken();

        int senderId = UserProfileSteps.createUserProfileId(userToken);

        float balanceBefore = UserProfileSteps.getUserProfileAccountBalance(userToken, senderId);

        UserDepositSteps.depositMoneyWithUnAuth(senderId, amount);

        float balanceAfter = UserProfileSteps.getUserProfileAccountBalance(userToken, senderId);

        ModelAssertions.assertThatModels(balanceBefore, balanceAfter).match();

    }

    @MethodSource("api.generators.RandomData#NegativeAmount")
    @ParameterizedTest
    public void userCantDepositWithAccess(float amount) {

        String userToken = AdminSteps.createToken();

        int senderId = UserProfileSteps.createUserProfileId(userToken);

        int randomId = RandomData.randomInt();

        float balanceBefore = UserProfileSteps.getUserProfileAccountBalance(userToken, senderId);

        UserDepositSteps.depositMoneyWithAccess(userToken, randomId, amount);

        float balanceAfter = UserProfileSteps.getUserProfileAccountBalance(userToken, senderId);

        ModelAssertions.assertThatModels(balanceBefore, balanceAfter).match();

    }
}


