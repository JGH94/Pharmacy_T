package com.jucha.pharmacy.encryption

import java.io.*
import java.security.Key
import javax.crypto.Cipher
import kotlin.jvm.Throws


class FileCoder (val key: Key, val transformation: String){
    @Throws(Exception::class)
    fun encrypt(source: File, dest: File) {
        crypt(Cipher.ENCRYPT_MODE, source, dest)
    }

    @Throws(Exception::class)
    private fun crypt(mode: Int, source: File, dest: File) {
        val cipher: Cipher = Cipher.getInstance(transformation)
        cipher.init(mode, key)
        var input: InputStream? = null
        var output: OutputStream? = null
        try {
            input = BufferedInputStream(FileInputStream(source))
            output = BufferedOutputStream(FileOutputStream(dest))
            val buffer = ByteArray(1024)
            var read = -1
            while (input.read(buffer).also({ read = it }) != -1) {
                output.write(cipher.update(buffer, 0, read))
            }
            output.write(cipher.doFinal())
        } finally {
            if (output != null) {
                try {
                    output.close()
                } catch (ie: IOException) {
                }
            }
            if (input != null) {
                try {
                    input.close()
                } catch (ie: IOException) {
                }
            }
        }
    }

}
