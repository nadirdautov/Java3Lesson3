package Server.Authorization;

public class UserData {
    private final String login;
    private final String password;
    private final String nickName;

    public UserData(String login, String password, String nickName) {
        this.login = login;
        this.password = password;
        this.nickName = nickName;
    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }

    public String getNickName() {
        return nickName;
    }

    public void info() {
        System.out.printf("Login: %s, Password: %s, Nickname: %s" , this.login, this.password, this.nickName);
        System.out.println();
    }
}
