package com.example.test_api.services;

import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Service;

@Service
public class SigningService {

    KeyPair keyPair1;

    KeyPair keyPair2;

    @PostConstruct
    public void init() throws NoSuchAlgorithmException {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(2048); // Specify the desired key size
        keyPair1 = keyPairGenerator.generateKeyPair();
        keyPair2 = keyPairGenerator.generateKeyPair();
    }

    public KeyPair getKeyPair1() {
        return keyPair1;
    }

    public KeyPair getKeyPair2() {
        return keyPair2;
    }

    public byte[] hashData(String data) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hashedData = digest.digest(data.getBytes(StandardCharsets.UTF_8));
        return hashedData;
    }

    public byte[] signHash(byte[] hashedData, PrivateKey pk)
            throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        Signature signature = Signature.getInstance("SHA256withRSA");

        signature.initSign(pk);
        signature.update(hashedData);
        byte[] signedHash = signature.sign();

        return signedHash;
    }

    public boolean verify(byte[] hashedData, byte[] signedHash, PublicKey publicKey)
            throws SignatureException, InvalidKeyException, NoSuchAlgorithmException {
        Signature signature = Signature.getInstance("SHA256withRSA");

        signature.initVerify(publicKey);
        signature.update(hashedData);

        return signature.verify(signedHash);
    }

    public void printKeys() {
        // Convert PublicKey to byte[]
        byte[] publicKeyBytes = keyPair1.getPublic().getEncoded();
        System.out.println(publicKeyBytes);
        System.out.println("Public Key: " + bytesToHex(publicKeyBytes));
        printByteArrayCode(publicKeyBytes);

        // Convert PrivateKey to byte[]
        byte[] privateKeyBytes = keyPair1.getPrivate().getEncoded();
        System.out.println("Private Key: " + bytesToHex(privateKeyBytes));
        printByteArrayCode(privateKeyBytes);
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02X", b));
        }
        return sb.toString();
    }

    private static void printByteArrayCode(byte[] byteArray) {
        StringBuilder sb = new StringBuilder();
        sb.append("new byte[] {");
        for (int i = 0; i < byteArray.length; i++) {
            sb.append(String.format("(byte)0x%02X", byteArray[i]));
            if (i < byteArray.length - 1) {
                sb.append(", ");
            }
            if ((i + 1) % 16 == 0) {
                sb.append("\n");
            }
        }
        sb.append("};");
        System.out.println(sb.toString());
    }
}
