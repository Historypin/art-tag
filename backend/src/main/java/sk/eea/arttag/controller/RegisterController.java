package sk.eea.arttag.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import sk.eea.arttag.game.service.GameService;
import sk.eea.arttag.model.User;
import sk.eea.arttag.model.form.RegisterForm;
import sk.eea.arttag.repository.UserRepository;

import javax.validation.Valid;

@Controller
public class RegisterController {

    private static final Logger LOG = LoggerFactory.getLogger(GameService.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;


    @RequestMapping(value = "/register", method = RequestMethod.GET)
    public String register(@ModelAttribute RegisterForm registerForm) {
        return "register";
    }

    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public String registerSubmit(@ModelAttribute @Valid RegisterForm registerForm, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "register";
        }

        LOG.debug("Received registration data: {}", registerForm);

        // validate existing account
        User user = userRepository.findOne(registerForm.getEmail());
        if(user != null) {
            bindingResult.rejectValue("email", "AlreadyUsed.registerForm.email");
            return "register";
        }

        // everything OK, create the user
        user = new User();
        user.setLogin(registerForm.getEmail());
        user.setNickName(registerForm.getNickname());
        user.setPassword(passwordEncoder.encode(registerForm.getPassword()));
        userRepository.save(user);

        return "login";
    }
}
