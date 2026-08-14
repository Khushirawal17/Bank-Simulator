package com.example.bank.simulator1.security;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class EncryptionService {

    private static final String ALGORITHM = "AES/GCM/NoPadding";
    private static final int GCM_TAG_LENGTH = 128;
    private static final int IV_LENGTH = 12;

    private final SecretKeySpec secretKey;
    private final SecureRandom secureRandom = new SecureRandom();

    public EncryptionService(
            @Value("${bank.encryption.secret}") String secret) {

        this.secretKey = new SecretKeySpec(
                secret.getBytes(StandardCharsets.UTF_8),
                "AES"
        );
    }

    // keep your encrypt() method exactly as it is

    

    public String encrypt(String plainText) {

        try {

            // Generate a new IV for every encryption
            byte[] iv =
                    new byte[IV_LENGTH];

            secureRandom.nextBytes(iv);

            GCMParameterSpec parameterSpec =
                    new GCMParameterSpec(
                            GCM_TAG_LENGTH,
                            iv
                    );

            Cipher cipher =
                    Cipher.getInstance(ALGORITHM);

            cipher.init(
                    Cipher.ENCRYPT_MODE,
                    secretKey,
                    parameterSpec
            );

            byte[] encrypted =
                    cipher.doFinal(
                            plainText.getBytes(
                                    StandardCharsets.UTF_8
                            )
                    );

            /*
             * Store IV + encrypted data together.
             *
             * IV is not secret.
             */
            ByteBuffer buffer =
                    ByteBuffer.allocate(
                            iv.length + encrypted.length
                    );

            buffer.put(iv);
            buffer.put(encrypted);

            return Base64.getEncoder()
                    .encodeToString(
                            buffer.array()
                    );

        } catch (Exception exception) {

            throw new IllegalStateException(
                    "Unable to encrypt payload",
                    exception
            );
        }
    }
}