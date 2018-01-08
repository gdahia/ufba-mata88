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

  public static String signAndEncrypt(String data, PrivateKey signatureKey, Key encryptionKey)
      throws GeneralSecurityException {
    // init signature resources
    Signature rsa = Signature.getInstance("SHA256withRSA");
    rsa.initSign(signatureKey);

    // feed data to be signed
    rsa.update(Base64.getDecoder().decode(data));

    // sign and convert signed data to string
    byte[] signed = rsa.sign();
    String signedData = Base64.getEncoder().encodeToString(signed);

    // split into halves for encryption and encrypt
    int halfLength = signedData.length() / 2;
    String encryptedSignedData1 = encrypt(signedData.substring(0, halfLength), encryptionKey);
    String encryptedSignedData2 = encrypt(signedData.substring(halfLength), encryptionKey);

    return encryptedSignedData1 + encryptedSignedData2;
  }

  public static boolean verifySignature(String signature, String data, PublicKey signatureKey)
      throws GeneralSecurityException {
    // init signature resources
    Signature rsa = Signature.getInstance("SHA256withRSA");
    rsa.initVerify(signatureKey);

    // feed data to be verified
    rsa.update(Base64.getDecoder().decode(data));

    return rsa.verify(Base64.getDecoder().decode(signature));
  }

  public static boolean verifyEncryptedSignature(String data, String encryptedSignedData,
      Key encryptionKey, PublicKey signatureKey) throws GeneralSecurityException {
    // decrypt halves separately and concatenate
    int halfLength = encryptedSignedData.length() / 2;
    String signedData1 = decrypt(encryptedSignedData.substring(0, halfLength), encryptionKey);
    String signedData2 = decrypt(encryptedSignedData.substring(halfLength), encryptionKey);

    return verifySignature(signedData1 + signedData2, data, signatureKey);
  }
}
