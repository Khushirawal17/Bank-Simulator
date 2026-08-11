package com.example.bank.simulator1.security;


import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class EncryptionService {

    private static final String ALGORITHM =
            "AES/GCM/NoPadding";

    private static final int GCM_TAG_LENGTH = 128;

    private final SecretKeySpec secretKey;

    private final byte[] initializationVector;

    public EncryptionService(
            String secret,
            String initializationVector) {

        this.secretKey =
                new SecretKeySpec(
                        secret.getBytes(
                                StandardCharsets.UTF_8
                        ),
                        "AES"
                );

        this.initializationVector =
                initializationVector.getBytes(
                        StandardCharsets.UTF_8
                );
    }

    public String encrypt(String plainText) {

        try {

            Cipher cipher =
                    Cipher.getInstance(ALGORITHM);

            GCMParameterSpec parameterSpec =
                    new GCMParameterSpec(
                            GCM_TAG_LENGTH,
                            initializationVector
                    );

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

            return Base64.getEncoder()
                    .encodeToString(encrypted);

        } catch (Exception exception) {

            throw new IllegalStateException(
                    "Unable to encrypt payload",
                    exception
            );
        }
    }
}