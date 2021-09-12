package com.example.ibuy.utils;

import android.content.Context;
import android.os.Build;
import android.security.KeyPairGeneratorSpec;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.text.format.DateFormat;
import android.util.Base64;
import android.util.Log;

import androidx.annotation.RequiresApi;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.GCMParameterSpec;
import javax.security.auth.x500.X500Principal;

/**
 * Represent the values that will be used throughout the lifecycle of the app
 * @author Thierry Marcelin & Loudwige Odice
 * @version 1.0
 * @since 2020-11-25
 * @credits encrypt-decrypt-android-demo
 */

public class Utils {

    public static final String BASE_URL = "http://140.113.167.23/se2020/product/";

    private static final String AndroidKeyStore = "AndroidKeyStore";
    private static final String AES_MODE = "AES/GCM/NoPadding";
    private static final String FIXED_IV = "randomizemsg"; // to randomize the encrypted data( give any values to randomize)
    private static final String KEY_ALIAS = "samplekeyalias"; //give any key alias based on your application. It is different from the key alias used to sign the app.
    private static final String RSA_MODE =  "RSA/ECB/PKCS1Padding"; // RSA algorithm which has to be used for OS version less than M
    private static KeyStore keyStore;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public static void generateKey(Context context) {

        try {
            keyStore = KeyStore.getInstance(AndroidKeyStore);
            keyStore.load(null);

            if (!keyStore.containsAlias(KEY_ALIAS)) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    KeyGenerator keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, AndroidKeyStore);
                    keyGenerator.init(
                            new KeyGenParameterSpec.Builder(KEY_ALIAS,
                                    KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                                    .setBlockModes(KeyProperties.BLOCK_MODE_GCM).setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                                    .setRandomizedEncryptionRequired(false)
                                    .build());
                    keyGenerator.generateKey();
                }else{
                    // Generate a key pair for encryption
                    Calendar start = Calendar.getInstance();
                    Calendar end = Calendar.getInstance();
                    end.add(Calendar.YEAR, 30);
                    KeyPairGeneratorSpec spec;
                    spec = new KeyPairGeneratorSpec.Builder(context)
                            .setAlias(KEY_ALIAS)
                            .setSubject(new X500Principal("CN=" + KEY_ALIAS))
                            .setSerialNumber(BigInteger.TEN)
                            .setStartDate(start.getTime())
                            .setEndDate(end.getTime())
                            .build();
                    KeyPairGenerator kpg = null;
                    kpg.initialize(spec);
                    kpg.generateKeyPair();
                }
            }else{
                Log.d("keyStore","key already available");
            }
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        }
    }

    private static java.security.Key getSecretKey() throws Exception {
        return keyStore.getKey(KEY_ALIAS, null);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private static String encryptM(String input){
        Cipher c;
        try {
            c = Cipher.getInstance(AES_MODE);
            c.init(Cipher.ENCRYPT_MODE, getSecretKey(), new GCMParameterSpec(128, FIXED_IV.getBytes()));
            byte[] encodedBytes = c.doFinal(input.getBytes());
            return Base64.encodeToString(encodedBytes, Base64.DEFAULT);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private static byte[] decryptM(String encrypted){
        Cipher c;
        try {
            c = Cipher.getInstance(AES_MODE);
            c.init(Cipher.DECRYPT_MODE, getSecretKey(), new GCMParameterSpec(128, FIXED_IV.getBytes()));
            byte [] barr = Base64.decode(encrypted,Base64.DEFAULT);
            return c.doFinal(barr);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static String rsaEncrypt(byte[] secret) throws Exception{
        KeyStore.PrivateKeyEntry privateKeyEntry = (KeyStore.PrivateKeyEntry) keyStore.getEntry(KEY_ALIAS, null);
        // Encrypt the text
        Cipher inputCipher = Cipher.getInstance(RSA_MODE, "AndroidOpenSSL");
        inputCipher.init(Cipher.ENCRYPT_MODE, privateKeyEntry.getCertificate().getPublicKey());

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        CipherOutputStream cipherOutputStream = new CipherOutputStream(outputStream, inputCipher);
        cipherOutputStream.write(secret);
        cipherOutputStream.close();

        byte[] vals = outputStream.toByteArray();
        return Base64.encodeToString(vals, Base64.DEFAULT);
    }

    private static byte[] rsaDecrypt(String encrypted) throws Exception {
        KeyStore.PrivateKeyEntry privateKeyEntry = (KeyStore.PrivateKeyEntry)keyStore.getEntry(KEY_ALIAS, null);
        Cipher output = Cipher.getInstance(RSA_MODE, "AndroidOpenSSL");
        output.init(Cipher.DECRYPT_MODE, privateKeyEntry.getPrivateKey());
        byte [] barr = Base64.decode(encrypted,Base64.DEFAULT);
        CipherInputStream cipherInputStream = new CipherInputStream(
                new ByteArrayInputStream(barr), output);
        ArrayList<Byte> values = new ArrayList<>();
        int nextByte;
        while ((nextByte = cipherInputStream.read()) != -1) {
            values.add((byte)nextByte);
        }

        byte[] bytes = new byte[values.size()];
        for(int i = 0; i < bytes.length; i++) {
            bytes[i] = values.get(i);
        }
        return bytes;
    }

   public static String decrypt(String text) {
        try {
            byte[] d = new byte[0];
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                d = Utils.decryptM(text);
            } else {
                try {
                    d = Utils.rsaDecrypt(text);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            //assert d != null;
            return new String(d, "UTF-8");
        }catch (Exception e){
            e.printStackTrace();
        }
        return "";
    }

    public static String encrypt(String text) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                text = Utils.encryptM(text);
            } else {
                text = Utils.rsaEncrypt(text.getBytes());
            }
            assert text != null;
            return text;
        }catch (Exception e){
            e.printStackTrace();
        }
        return "";
    }

    public static String getDate(long time) {
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(time);
        return DateFormat.format("MMM dd, hh:mm a", cal).toString();
    }



}
