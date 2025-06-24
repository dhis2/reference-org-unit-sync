import org.apache.camel.BindToRegistry;
import org.apache.camel.PropertyInject;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.security.Key;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.security.Security;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

/**
 * Checks if the key store exists, and if not, create one while also generating an AES key to be used for
 * encrypting/decrypting the approval link query params.
 */
public class KeyStoreGenerator {
    private static final Logger LOGGER = LoggerFactory.getLogger(KeyStoreGenerator.class);

    @PropertyInject("keyStore.password")
    private String keyStorePassword;

    @PropertyInject("keyStore.path")
    private String keyStorePath;

    @BindToRegistry("secretKey")
    public Key secretKey() throws KeyStoreException, NoSuchProviderException, IOException, CertificateException, NoSuchAlgorithmException, UnrecoverableKeyException {
        if (Security.getProvider(BouncyCastleProvider.PROVIDER_NAME) == null) {
            Security.insertProviderAt(new BouncyCastleProvider(), 2);
        }

        File keyStoreFile = new File(keyStorePath);
        if (!keyStoreFile.exists()) {
            LOGGER.info("Generating key store...");

            try {
                KeyGenerator keyGenerator = KeyGenerator.getInstance("AES", "BC");
                keyGenerator.init(256, new SecureRandom());
                SecretKey secretKey = keyGenerator.generateKey();

                KeyStore keyStore = KeyStore.getInstance("BCFKS", "BC");
                keyStore.load(null, null);
                keyStore.setKeyEntry("orgUnitSync", secretKey, null, null);
                keyStore.store(new FileOutputStream(keyStoreFile), keyStorePassword.toCharArray());

                LOGGER.info("Key store generated at {}", keyStoreFile.toURI().toURL().toExternalForm());
            } catch (CertificateException | KeyStoreException | IOException
                     | NoSuchAlgorithmException | NoSuchProviderException e) {
                LOGGER.error(e.getMessage(), e);
            }
        } else {
            try {
                LOGGER.info("Re-using existing key store at {}", keyStoreFile.toURI().toURL().toExternalForm());
            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            }
        }

        KeyStore keyStore = KeyStore.getInstance("BCFKS", "BC");
        try (FileInputStream fileInputStream = new FileInputStream(keyStoreFile)) {
            keyStore.load(fileInputStream, keyStorePassword.toCharArray());
        }

        return keyStore.getKey("orgUnitSync", null);
    }
}
