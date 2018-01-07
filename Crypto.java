import java.nio.file.*;
import java.io.*;
import java.security.spec.*;
import java.security.*;
import javax.crypto.Cipher;
import java.util.Base64;

public class Crypto {
  public static String encrypt(String message, Key key) throws GeneralSecurityException {
    Cipher cipher = Cipher.getInstance("RSA");
    cipher.init(Cipher.ENCRYPT_MODE, key);
    return Base64.getEncoder().encodeToString(cipher.doFinal(message.getBytes()));
  }

  public static String decrypt(String message, Key key) throws GeneralSecurityException {
    Cipher cipher = Cipher.getInstance("RSA");
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
      keyPairGenerator.initialize(2048);
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

  public static String getEncryptedSignature(PrivateKey signatureKey, Key encryptionKey)
      throws GeneralSecurityException {
    // generate signature data randomly
    SecureRandom random = new SecureRandom();
    byte[] bytes = new byte[128];
    random.nextBytes(bytes);
    String data = Base64.getEncoder().encodeToString(bytes);

    // generate signature
    Signature rsa = Signature.getInstance("SHA256withRSA");
    rsa.initSign(signatureKey);
    rsa.update(bytes);
    byte[] signed = rsa.sign();
    String signature = Base64.getEncoder().encodeToString(signed);

    // encrypt halves and concatenate
    int halfLength = signature.length() / 2;
    String cryptSignature1 = encrypt(signature.substring(0, halfLength), encryptionKey);
    String cryptSignature2 = encrypt(signature.substring(halfLength), encryptionKey);
    String cryptSignature3 = encrypt(data, encryptionKey);

    return cryptSignature1 + cryptSignature2 + cryptSignature3;
  }

  public static boolean verifySignature(String signature, String data, PublicKey signatureKey)
      throws GeneralSecurityException {
    Signature rsa = Signature.getInstance("SHA256withRSA");
    rsa.initVerify(signatureKey);

    // data to be verified
    byte[] bytes = Base64.getDecoder().decode(data);
    rsa.update(bytes);

    return rsa.verify(Base64.getDecoder().decode(signature));
  }

  public static boolean verifyEncryptedSignature(String cryptSignature, Key encryptionKey,
      PublicKey signatureKey) throws GeneralSecurityException {
    int thirdLength = cryptSignature.length() / 3;
    String signature1 = decrypt(cryptSignature.substring(0, thirdLength), encryptionKey);
    String signature2 =
        decrypt(cryptSignature.substring(thirdLength, 2 * thirdLength), encryptionKey);
    String data = decrypt(cryptSignature.substring(2 * thirdLength), encryptionKey);
    return verifySignature(signature1 + signature2, data, signatureKey);
  }
}
