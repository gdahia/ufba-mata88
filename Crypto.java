import java.nio.file.*;
import java.io.*;
import java.security.spec.*;
import java.security.*;
import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

public class Crypto {
  public static String encrypt(String message, Key key, String algorithm)
      throws GeneralSecurityException {
    Cipher cipher = Cipher.getInstance(algorithm);
    cipher.init(Cipher.ENCRYPT_MODE, key);
    return Base64.getEncoder().encodeToString(cipher.doFinal(message.getBytes()));
  }

  public static String decrypt(String message, Key key, String algorithm)
      throws GeneralSecurityException {
    Cipher cipher = Cipher.getInstance(algorithm);
    cipher.init(Cipher.DECRYPT_MODE, key);
    return new String(cipher.doFinal(Base64.getDecoder().decode(message)));
  }

  public static void saveKeyPair(String path, KeyPair keys) throws Exception {
    System.out.println("Saving key pair to \"" + Paths.get(path) + "\"...");

    // save private key
    byte[] privKeyBytes = keys.getPrivate().getEncoded();
    FileOutputStream privKeyfos = new FileOutputStream(Paths.get(path) + "/priv.rsa");
    privKeyfos.write(privKeyBytes);
    privKeyfos.close();

    // save public key
    byte[] pubKeyBytes = keys.getPublic().getEncoded();
    FileOutputStream pubKeyfos = new FileOutputStream(Paths.get(path) + "/pub.rsa");
    pubKeyfos.write(pubKeyBytes);
    pubKeyfos.close();

    System.out.println("Done");
  }

  public static KeyPair getKeys(String path) throws GeneralSecurityException {
    try {
      // attempt to read keys from given folder
      // read pubKey
      byte[] keyBytes = Files.readAllBytes(Paths.get(path + "/pub.rsa"));
      X509EncodedKeySpec pubSpec = new X509EncodedKeySpec(keyBytes);
      KeyFactory kf = KeyFactory.getInstance("RSA");
      PublicKey pubKey = kf.generatePublic(pubSpec);

      // read privKey
      keyBytes = Files.readAllBytes(Paths.get(path + "/priv.rsa"));
      PKCS8EncodedKeySpec privSpec = new PKCS8EncodedKeySpec(keyBytes);
      kf = KeyFactory.getInstance("RSA");
      PrivateKey privKey = kf.generatePrivate(privSpec);

      return new KeyPair(pubKey, privKey);
    } catch (IOException e) {
      // generate new key pair
      System.out.println("Generating new key pair...");
      KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
      keyPairGenerator.initialize(4096);
      KeyPair keys = keyPairGenerator.genKeyPair();
      System.out.println("Done");

      // save key pair
      if (path != null)
        try {
          saveKeyPair(path, keys);
        } catch (Exception e2) {
          System.err.println("Unable to save key pair");
        }

      return keys;
    }
  }

  public static String secureRandomString(int length) {
    SecureRandom random = new SecureRandom();
    byte[] bytes = new byte[length];
    random.nextBytes(bytes);
    return Base64.getEncoder().encodeToString(bytes);
  }

  public static Key getSymmetricKey() throws GeneralSecurityException {
    String keyString = secureRandomString(16);
    byte[] keyBytes = Base64.getDecoder().decode(keyString);
    Key key = new SecretKeySpec(keyBytes, "AES");
    return key;
  }

  public static SealedObject sealObject(Serializable obj, Key key) throws Exception {
    Cipher cipher = Cipher.getInstance("RSA");
    cipher.init(Cipher.ENCRYPT_MODE, key);
    return new SealedObject(obj, cipher);
  }

  public static Object unsealObject(SealedObject obj, Key key) throws Exception {
    return obj.getObject(key);
  }
}
