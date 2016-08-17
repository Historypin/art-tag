package sk.eea.arttag.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import sk.eea.arttag.game.service.GameService;
import sk.eea.arttag.model.IdentityProviderType;
import sk.eea.arttag.model.User;
import sk.eea.arttag.model.UserRole;
import sk.eea.arttag.model.form.RegisterForm;
import sk.eea.arttag.repository.UserRepository;

import javax.validation.Valid;

@Controller
public class IndexController {

    private static final Logger LOG = LoggerFactory.getLogger(GameService.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @RequestMapping("/")
    public String getIndex(@ModelAttribute RegisterForm registerForm) {
        return "index";
    }

    @RequestMapping(value = "/", method = RequestMethod.POST)
    public String registerSubmit(@ModelAttribute @Valid RegisterForm registerForm, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("registerError", true);
            return "index";
        }

        LOG.debug("Received registration data: {}", registerForm);

        // validate existing account
        User user = userRepository.findByEmail(registerForm.getEmail());
        if(user != null) {
            model.addAttribute("registerError", true);
            bindingResult.rejectValue("email", "AlreadyUsed.registerForm.email");
            return "index";
        }

        // everything OK, create the user
        user = new User();
        user.setIdentityProviderType(IdentityProviderType.LOCAL);
        user.setEmail(registerForm.getEmail());
        user.setNickName(registerForm.getNickname());
        user.setPassword(passwordEncoder.encode(registerForm.getPassword()));
        user.setEnabled(true);
        user.getUserRole().add(new UserRole(user, "ROLE_USER"));
        userRepository.save(user);

        return "redirect:/#signinmodal";
    }
}
