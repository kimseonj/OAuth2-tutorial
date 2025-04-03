package com.spring.oauth2tutorial.service;

import com.spring.oauth2tutorial.dto.*;
import com.spring.oauth2tutorial.entity.UserEntity;
import com.spring.oauth2tutorial.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Objects;

@RequiredArgsConstructor
@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        OAuth2User oAuth2User = super.loadUser(userRequest);
        System.out.println(oAuth2User);

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        System.out.println(registrationId);

        // OAuth2Response 인터페이스로 모든 OAuth2 로그인을 관리
        OAuth2Response oAuth2Response = null;
        if (registrationId.equals("naver")) {
            oAuth2Response = new NaverResponse(oAuth2User.getAttributes());
        } else if (registrationId.equals("google")) {
            oAuth2Response = new GoogleResponse(oAuth2User.getAttributes());
        } else {
            return null;
        }


        // 리소스 서버에서 발급받은 정보로 사용자를 특정할 ID값을 만듦
        String username = oAuth2Response.getName() + " " + oAuth2Response.getProviderId();

        UserEntity existData = userRepository.findByUsername(username);

        if (Objects.isNull(existData)) {

            UserEntity userEntity = new UserEntity();
            userEntity.setUsername(username);
            userEntity.setEmail(oAuth2Response.getEmail());
            userEntity.setName(oAuth2Response.getName());
            userEntity.setRole("ROLE_USER");

            userRepository.save(userEntity);

            UserDto userDto = new UserDto();
            userDto.setUsername(username);
            userDto.setName(oAuth2Response.getName());
            userDto.setRole("ROLE_USER");

            return new CustomOAuth2User(userDto);
        } else {
            existData.setEmail(oAuth2Response.getEmail());
            existData.setName(oAuth2Response.getName());

            userRepository.save(existData);

            UserDto userDto = new UserDto();
            userDto.setUsername(existData.getUsername());
            userDto.setName(oAuth2Response.getName());
            userDto.setRole(existData.getRole());

            return new CustomOAuth2User(userDto);

        }
    }

}
