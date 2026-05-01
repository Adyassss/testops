package api.requests.steps;

import api.generators.RandomModelGenerator;
import api.models.AdminCanCreateUserRequest;
import api.requests.skelethon.Endpoint;
import api.requests.skelethon.requests.CrudRequesters;
import api.specs.RequestSpec;
import api.specs.ResponseSpec;
import common.storage.SessionStorage;

public class AdminSteps extends BaseSteps {
    public AdminSteps(String username, String password) {
        super(username, password);
    }

    public static String createToken() {
        AdminCanCreateUserRequest randomUser = RandomModelGenerator.generate(AdminCanCreateUserRequest.class);
        return new CrudRequesters(RequestSpec.adminRequest(),
                Endpoint.ADMIN_USER,
                ResponseSpec.created())
                .post(randomUser)
                .extract()
                .header("Authorization");
    }

    public static String createTokenUnAuth() {
        AdminCanCreateUserRequest randomUser = RandomModelGenerator.generate(AdminCanCreateUserRequest.class);
        return new CrudRequesters(RequestSpec.unauthSpec(),
                Endpoint.ADMIN_USER,
                ResponseSpec.unauthorized())
                .post(randomUser)
                .extract()
                .header("Authorization");
    }
}
