package com.example.doancn.Utilities

import android.util.Base64
import io.jsonwebtoken.Jwts
import java.security.KeyFactory
import java.security.PublicKey
import java.security.spec.X509EncodedKeySpec


object TokenManager {
    private lateinit var publicKey: PublicKey
    private lateinit var token: String
    lateinit var role: String
    lateinit var userToken : String

    fun getpublickey(token: String) {
        val i: Int = token.lastIndexOf('.')
        val withoutSignature: String = token.substring(0, i + 1)
        val publickeyencoded =
            Jwts.parserBuilder().build().parseClaimsJwt(withoutSignature).header.get("publicKey")
        val bytekey = Base64.decode(publickeyencoded.toString(), Base64.DEFAULT)
        val publicKeyX509 = X509EncodedKeySpec(bytekey)
        val kf: KeyFactory = KeyFactory.getInstance("RSA")
        publicKey = kf.generatePublic(publicKeyX509)
        this.token = token
    }

    fun readrolefromtokenJws() { // dam bao chu ky cho chac
        role = Jwts.parserBuilder().setSigningKey(publicKey).build().parseClaimsJws(token).body.get(
            "role"
        ).toString()
    }

}

