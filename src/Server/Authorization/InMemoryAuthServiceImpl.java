package Server.Authorization;

import java.util.HashMap;
import java.util.Map;

import static Server.Authorization.JdbcConnection.logins;
import static Server.Authorization.JdbcConnection.main;

public class InMemoryAuthServiceImpl implements AuthService{

    public final Map<String, UserData>users;

    public InMemoryAuthServiceImpl(){
        users = new HashMap<>();
        main();
        for (int i = 0; i<logins.size(); i++){
            users.put("login"+(i+1), logins.get(i));
        }
    }


    @Override
    public void start() {
        System.out.println("Authentication service initialized");
    }

    @Override
    public synchronized String getNickNameByLoginAndPassword(String login, String password) {
        UserData user = users.get(login);
        //We are looking for a user by login and password, if found, we return the nickname
        if(user != null && user.getPassword().equals(password)){
            return user.getNickName();
        }
        return null;
    }

    @Override
    public void end() {
        System.out.println("Authentication service disabled");
    }
}
