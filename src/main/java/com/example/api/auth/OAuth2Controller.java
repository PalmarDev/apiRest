package com.example.api.auth;

import com.example.api.Jwt.JwtService;
import com.example.api.User.User;
import com.example.api.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/oauth2")
public class OAuth2Controller {
    @Autowired
    private OAuth2AuthorizedClientService auth2AuthorizedClientService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private JwtService jwtService;

    @GetMapping("/login")
    public String login(){
        return "redirect:/oauth2/authorize/google";
    }

    @GetMapping("/loginSuccess")
    public AuthResponse loginSuccess(@RegisteredOAuth2AuthorizedClient OAuth2AuthorizedClient authorizedClient){

        String username = authorizedClient.getPrincipalName();


        User user = userRepository.findByUsername(username).orElse(null);

        if (user == null) {
            user = new User();
            user.setUsername(username);
            userRepository.save(user);
        }

        UserDetails userDetails = User.builder()
                .name(user.getName())
                .lastname(user.getLastname())
                .username(user.getUsername())
                .email(user.getEmail())
                .password(user.getPassword())
                .build();

        String token = jwtService.getToken(userDetails);

        return AuthResponse.builder()
                .token(token)
                .build();
    }
}