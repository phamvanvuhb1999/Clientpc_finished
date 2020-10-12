import java.io.Serializable;

public class User implements Serializable{
    private String userName;
    private String passWord;

    public User(){
        this.userName = " ";
        this.passWord = " ";
    }

    public User(String userName, String passWord){
        this.userName = userName;
        this.passWord = passWord;
    }

    public String getPassword(){
        return this.passWord;
    }

    public void setPassword(String password){
        this.passWord = password;
    }

    public String getUsername(){
        return this.userName;
    }

    public void setUsername(String username){
        this.userName = username;
    }
}
