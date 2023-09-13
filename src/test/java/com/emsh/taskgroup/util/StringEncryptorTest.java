package com.emsh.taskgroup.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class StringEncryptorTest {

    private final StringEncryptor stringEncryptor;

    @Autowired
    public StringEncryptorTest(StringEncryptor stringEncryptor) {
        this.stringEncryptor = stringEncryptor;
    }

    @Test
    public void EncryptAndDecryptStringTest() throws Exception {
        var text = "1";
        var encryptedText = stringEncryptor.encrypt(text);
        Assertions.assertNotEquals(encryptedText, text);
        var decryptedText = stringEncryptor.decrypt(encryptedText);
        Assertions.assertEquals(decryptedText, text);
    }
}
