package net.noliaware.yumi_contributor.commun.util

import android.content.Context
import android.util.Base64
import org.json.JSONObject
import java.io.BufferedReader
import java.io.FileInputStream
import java.io.InputStreamReader
import java.nio.charset.StandardCharsets
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec

class FileIOUtils {

    companion object {

        private const val AES_MODE = "AES/CBC/PKCS7Padding"
        private const val SALT = "salt"
        private const val IV = "iv"
        private const val ENCRYPTED = "encrypted"

        private fun generateSecretKeySpec(password: String, salt: ByteArray?): SecretKeySpec {
            val pbKeySpec = PBEKeySpec(password.toCharArray(), salt, 1324, 256)
            val secretKeyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1")
            val keyBytes = secretKeyFactory.generateSecret(pbKeySpec).encoded
            return SecretKeySpec(keyBytes, "AES")
        }

        private fun encryptText(clearText: String, password: String): Map<String, String> {

            val random = SecureRandom()
            val salt = ByteArray(256)
            random.nextBytes(salt)

            val keySpec = generateSecretKeySpec(password, salt)

            val ivRandom = SecureRandom()
            val iv = ByteArray(16)
            ivRandom.nextBytes(iv)
            val ivSpec = IvParameterSpec(iv)

            val cipher = Cipher.getInstance(AES_MODE)
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec)
            val encrypted = cipher.doFinal(clearText.toByteArray(StandardCharsets.UTF_8))

            return hashMapOf<String, String>().also {
                it[SALT] = Base64.encodeToString(salt, Base64.NO_WRAP)
                it[IV] = Base64.encodeToString(iv, Base64.NO_WRAP)
                it[ENCRYPTED] = Base64.encodeToString(encrypted, Base64.NO_WRAP)
            }
        }

        private fun saveFileToInternalStorage(
            context: Context,
            fileName: String,
            dataToSave: Map<String, String>
        ) = context.openFileOutput(fileName, Context.MODE_PRIVATE).use {
            it.write(JSONObject(dataToSave).toString().toByteArray())
            it.close()
        }

        /*fun encryptDataAndSaveInFile(
            context: Context,
            fileName: String,
            dataToSave: String
        ) = CoroutineScope(Dispatchers.IO).launch {
            saveFileToInternalStorage(
                context,
                fileName,
                encryptText(dataToSave, DataManager.get().encryptionVector)
            )
        }

         */

        private fun decryptText(data: Map<String, String>, password: String): String {

            try {

                val salt = Base64.decode(data[SALT], Base64.NO_WRAP)
                val iv = Base64.decode(data[IV], Base64.NO_WRAP)
                val encrypted = Base64.decode(data[ENCRYPTED], Base64.NO_WRAP)

                val keySpec = generateSecretKeySpec(password, salt)

                val cipher = Cipher.getInstance(AES_MODE)
                val ivSpec = IvParameterSpec(iv)
                cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec)

                return String(cipher.doFinal(encrypted), Charsets.UTF_8)

            } catch (e: Exception) {
                e.printStackTrace()
            }

            return ""
        }

        private fun readFileFromInternalStorage(context: Context, fileName: String): String {

            val fis: FileInputStream
            try {
                fis = context.openFileInput(fileName)
            } catch (ex: Exception) {
                return ""
            }

            val bufferedReader = BufferedReader(InputStreamReader(fis))
            val stringBuilder = StringBuilder()
            var text: String? = null

            while (run {
                    text = bufferedReader.readLine()
                    text
                } != null) {
                stringBuilder.append(text)
            }
            return stringBuilder.toString()
        }

        /*suspend fun readEncryptedDataFromFile(context: Context, fileName: String): String =
            withContext(Dispatchers.IO) {

                val dataString = readFileFromInternalStorage(context, fileName)

                if (dataString.isBlank())
                    return@withContext ""

                val dataJson = JSONObject(dataString)

                val outputMap = hashMapOf<String, String>().also {
                    it[SALT] = dataJson.optString(SALT)
                    it[IV] = dataJson.optString(IV)
                    it[ENCRYPTED] = dataJson.optString(ENCRYPTED)
                }

                return@withContext decryptText(outputMap, DataManager.get().encryptionVector)
            }

         */
    }
}