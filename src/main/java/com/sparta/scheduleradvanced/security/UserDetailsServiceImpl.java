package com.sparta.scheduleradvanced.security;

import com.sparta.scheduleradvanced.entity.User;
import com.sparta.scheduleradvanced.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Not Found " + username));

        // DB에 평문으로 저장된 비밀번호 암호화
        User encodedUser = new User(user.getUsername(), passwordEncoder.encode(user.getPassword()), user.getNickname(), user.getRole());

        return new UserDetailsImpl(encodedUser);
    }
}