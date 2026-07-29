package org.example.app.tool;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class LdTool {
    private final PasswordEncoder encoder;
    public LdTool(PasswordEncoder encoder){
        this.encoder = encoder;
    };
    public static String generateSecurityKey() throws NoSuchAlgorithmException{
        KeyGenerator keyGen = KeyGenerator.getInstance("HmacSHA256");
        keyGen.init(256);
        SecretKey secretKey = KeyGenerator.getInstance("HmacSHA256").generateKey();
        return Base64.getEncoder().encodeToString(secretKey.getEncoded());
    }
    public String encodePassword(String password){
       return encoder.encode(password); 
    }
}
