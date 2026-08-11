package org.example.app.tool;
import java.io.IOException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.Security;
import java.security.cert.CertificateException;
import java.security.spec.AlgorithmParameterSpec;
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
    public KeyStore Ed25519KeyStoreGenerator() throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException{
        String keystorePath = "ed25519-keystore.p12";
        String alias = "my-ed25519-key";
        char[] password = "ninia@0210".toCharArray();
        // AlgorithmParameterSpec
        // KeyPairGenerator keyGen = KeyPairGenerator.getInstance("Ed25519");
        // KeyPair keyPair = keyGen.generateKeyPair();
        
        KeyStore keyStore = KeyStore.getInstance("PKCS12");
        keyStore.load(null,password);
        return keyStore;
    }
    public static void main(){
        String txt = Security.getProviders().toString();
        System.out.println(txt);
    }
}
