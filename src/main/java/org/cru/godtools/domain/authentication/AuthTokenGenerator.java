package org.cru.godtools.domain.authentication;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.UUID;

/**
 * Created by matthewfrederick on 7/15/14.
 *
 * This class is modeled after AuthCodeGenerator found in the cru eventhub api.
 */
public class AuthTokenGenerator
{
    private static SecureRandom secureRandom;

    static
    {
        try
        {
            secureRandom = SecureRandom.getInstance("SHA1PRNG");
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public static String generate()
    {
        // Generate a random number
        String randomNum = new Integer(secureRandom.nextInt()).toString();

        // get its digest
        MessageDigest sha = null;

        try
        {
            sha = MessageDigest.getInstance("SHA-1");
        } catch (NoSuchAlgorithmException e)
        {
            e.printStackTrace();
            return UUID.randomUUID().toString();
        }
        byte[] result = sha.digest(randomNum.getBytes());

        return hexEncode(result);
    }

    static private String hexEncode(byte[] aInput)
    {
        StringBuilder result = new StringBuilder();
        char[] digits = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
        for (int idx = 0; idx < aInput.length; ++idx)
        {
            byte b = aInput[idx];
            result.append(digits[(b & 0xf0) >> 4]);
            result.append(digits[b & 0x0f]);
        }

        return result.toString();
    }
}
