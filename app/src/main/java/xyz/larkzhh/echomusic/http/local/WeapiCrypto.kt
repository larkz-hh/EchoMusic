package xyz.larkzhh.echomusic.http.local

import android.util.Base64
import java.security.KeyFactory
import java.security.spec.X509EncodedKeySpec
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

/** weapi 参数加密，算法与 NeteaseCloudMusicApi 一致 */
object WeapiCrypto {

    private const val PRESET_KEY = "0CoJUm6Qyw8W8jud"
    private const val IV = "0102030405060708"
    private const val BASE62 = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"

    private const val PUBLIC_KEY =
        "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDgtQn2JZ34ZC28NWYpAUd98iZ37BUrX/aKzmFbt7clFSs6sXq" +
            "HauqKWqdtLkF2KexO40H1YTX8z2lSgBBOAxLsvaklV8k4cBFK9snQXE9/DDaFt6Rr7iVZMldczhC0JNgTz+SHXT6" +
            "CBHuX3e9SdB1Ua44oncaTWz7OBGLbCiK45wIDAQAB"

    fun encrypt(text: String): Pair<String, String> {
        val secretKey = buildString {
            repeat(16) { append(BASE62.random()) }
        }
        val params = aes(aes(text, PRESET_KEY), secretKey)
        val encSecKey = rsa(secretKey.reversed())
        return params to encSecKey
    }

    private fun aes(text: String, key: String): String {
        val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
        cipher.init(
            Cipher.ENCRYPT_MODE,
            SecretKeySpec(key.toByteArray(), "AES"),
            IvParameterSpec(IV.toByteArray())
        )
        return Base64.encodeToString(cipher.doFinal(text.toByteArray()), Base64.NO_WRAP)
    }

    private fun rsa(text: String): String {
        val keySpec = X509EncodedKeySpec(Base64.decode(PUBLIC_KEY, Base64.DEFAULT))
        val publicKey = KeyFactory.getInstance("RSA").generatePublic(keySpec)
        val cipher = Cipher.getInstance("RSA/ECB/NoPadding")
        cipher.init(Cipher.ENCRYPT_MODE, publicKey)
        val raw = text.toByteArray()
        val block = ByteArray(128)
        System.arraycopy(raw, 0, block, 128 - raw.size, raw.size)
        return cipher.doFinal(block).joinToString("") { "%02x".format(it) }
    }
}
