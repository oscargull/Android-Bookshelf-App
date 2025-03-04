package com.example.bookshelfapp.utils;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.SecureRandom;
import java.util.Base64;

public class Security {
    public String[] hashing(String passw){
        char[] password = passw.toCharArray();
        byte[] salt = new byte[16];
        SecureRandom random = new SecureRandom();
        random.nextBytes(salt);

        String[] hash = new String[2];
        hash[0] = Base64.getEncoder().encodeToString(salt);

        try {
            SecretKey key = pbkdf2(password, salt);
            hash[1] = Base64.getEncoder().encodeToString(key.getEncoded());
        }catch (Exception e){
            e.printStackTrace();
        }
        return hash;
    }

    public SecretKey pbkdf2(char[] password, byte[] salt) throws Exception {
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSha1");
        PBEKeySpec spec = new PBEKeySpec(password, salt, 4096, 256);
        return factory.generateSecret(spec);
    }
}
