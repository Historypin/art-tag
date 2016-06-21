package sk.eea.arttag.model.form;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;
import org.hibernate.validator.constraints.ScriptAssert;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@ScriptAssert(lang = "javascript", script = "_this.password === _this.repeatPassword")
public class RegisterForm {

    @Email
    @NotEmpty
    private String email;

    @NotNull
    @Size(min = 3)
    private String nickname;

    @NotNull
    @Size(min = 8)
    private String password;

    private String repeatPassword;

    @AssertTrue
    private boolean conditionsAccepted;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRepeatPassword() {
        return repeatPassword;
    }

    public void setRepeatPassword(String repeatPassword) {
        this.repeatPassword = repeatPassword;
    }

    public boolean isConditionsAccepted() {
        return conditionsAccepted;
    }

    public void setConditionsAccepted(boolean conditionsAccepted) {
        this.conditionsAccepted = conditionsAccepted;
    }

    @Override
    public String toString() {
        return "RegisterForm{" +
            "email='" + email + '\'' +
            ", nickname='" + nickname + '\'' +
            ", conditionsAccepted=" + conditionsAccepted +
            '}';
    }
}
