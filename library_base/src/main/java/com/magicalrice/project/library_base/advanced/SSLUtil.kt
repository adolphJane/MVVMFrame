package com.magicalrice.project.library_base.advanced

import java.io.IOException
import java.io.InputStream
import java.security.*
import java.security.cert.CertificateException
import java.security.cert.CertificateFactory
import javax.net.ssl.*


/**
 * @package com.magicalrice.project.library_base.advanced
 * @author Adolph
 * @date 2019-04-22 Mon
 * @description Https 证书工具类
 */

object SSLUtil {
    //使用命令keytool -printcert -rfc -file srca.cer 导出证书为字符串，然后将字符串转换为输入流，如果使用的是okhttp可以直接使用new Buffer().writeUtf8(s).inputStream()

    /**
     * 返回SSLSocketFactory
     *
     * @param certificates 证书的输入流
     * @return SSLSocketFactory
     */
    fun getSSLSocketFactory(vararg certificates: InputStream): SSLSocketFactory? {
        return getSSLSocketFactory(null, *certificates)
    }

    /**
     * 双向认证
     * @param keyManagers KeyManager[]
     * @param certificates 证书的输入流
     * @return SSLSocketFactory
     */
    fun getSSLSocketFactory(
        keyManagers: Array<KeyManager>?,
        vararg certificates: InputStream
    ): SSLSocketFactory? {
        try {
            val certificateFactory = CertificateFactory.getInstance("X.509")
            val keyStore = KeyStore.getInstance(KeyStore.getDefaultType())
            keyStore.load(null)
            var index = 0
            for (certificate in certificates) {
                val certificateAlias = Integer.toString(index++)
                keyStore.setCertificateEntry(
                    certificateAlias,
                    certificateFactory.generateCertificate(certificate)
                )
                try {
                    certificate?.close()
                } catch (e: IOException) {
                }

            }
            val sslContext = SSLContext.getInstance("TLS")
            val trustManagerFactory =
                TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm())
            trustManagerFactory.init(keyStore)
            sslContext.init(keyManagers, trustManagerFactory.trustManagers, SecureRandom())
            return sslContext.socketFactory
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return null
    }

    /**
     * 获得双向认证所需的参数
     * @param bks bks证书的输入流
     * @param keystorePass 秘钥
     * @return KeyManager[]对象
     */
    fun getKeyManagers(bks: InputStream, keystorePass: String): Array<KeyManager>? {
        var clientKeyStore: KeyStore? = null
        try {
            clientKeyStore = KeyStore.getInstance("BKS")
            clientKeyStore!!.load(bks, keystorePass.toCharArray())
            val keyManagerFactory =
                KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm())
            keyManagerFactory.init(clientKeyStore, keystorePass.toCharArray())
            return keyManagerFactory.keyManagers
        } catch (e: KeyStoreException) {
            e.printStackTrace()
        } catch (e: UnrecoverableKeyException) {
            e.printStackTrace()
        } catch (e: CertificateException) {
            e.printStackTrace()
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return null
    }
}