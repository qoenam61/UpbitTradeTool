package com.example.upbitautotrade.api;

import android.util.Log;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ChanceRetrofit extends DefaultRetrofit {
    private String TAG = "ChanceRetrofit";

    protected ArrayList<String> mQueryElements;

    public ChanceRetrofit(String accessKey, String secretKey) {
        super(accessKey, secretKey);
    }

    @Override
    protected String getAuthToken() {
        if (mAccessKey == null || mSecretKey == null) {
            return null;
        }
        String queryString = String.join("&", mQueryElements.toArray(new String[0]));
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("SHA-512");
            md.update(queryString.getBytes("UTF-8"));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        String queryHash = String.format("%0128x", new BigInteger(1, md.digest()));

        Algorithm algorithm = Algorithm.HMAC256(mSecretKey);
        String jwtToken = JWT.create()
                .withClaim("access_key", mAccessKey)
                .withClaim("nonce", UUID.randomUUID().toString())
                .withClaim("query_hash", queryHash)
                .withClaim("query_hash_alg", "SHA512")
                .sign(algorithm);

        String authenticationToken = "Bearer " + jwtToken;
        return authenticationToken;
    }

    @Override
    public void setParam(String param1, String param2, String param3) {
        HashMap<String, String> params = new HashMap<>();
        params.put("market", param1);

        mQueryElements = new ArrayList<>();
        for(Map.Entry<String, String> entity : params.entrySet()) {
            mQueryElements.add(entity.getKey() + "=" + entity.getValue());
        }
    }

    @Override
    public void setParam(String param1, String param2, String param3, String param4, String param5, String param6) {

    }

    @Override
    public void setParam(Map<String, String> map) {

    }
}
