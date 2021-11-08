package com.example.doancn.Utilities

import android.util.Base64
import android.util.Log
import io.jsonwebtoken.Jwts
import java.security.KeyFactory
import java.security.spec.X509EncodedKeySpec


object TokenManager {

    fun getRole(token: String): String {
        val s = token.substring(7)
        Log.d("TokenManager", "token: $s")
        val i: Int = s.lastIndexOf('.')
        val withoutSignature: String = s.substring(0, i + 1)
        val publickeyencoded =
            Jwts.parserBuilder().build()
                .parseClaimsJwt(withoutSignature.trim()).header.get("publicKey")
        val bytekey = Base64.decode(publickeyencoded.toString(), Base64.DEFAULT)
        val publicKeyX509 = X509EncodedKeySpec(bytekey)
        val kf: KeyFactory = KeyFactory.getInstance("RSA")
        val publicKey = kf.generatePublic(publicKeyX509)
        return Jwts.parserBuilder().setSigningKey(publicKey).build()
            .parseClaimsJws(s.trim()).body.get(
                "role"
            ).toString()

    }

}

