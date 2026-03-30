package com.example.giga_chat_pet.data.remote

import android.content.Context
import com.example.giga_chat_pet.R
import okhttp3.OkHttpClient
import java.security.KeyStore
import java.security.cert.CertificateFactory
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManagerFactory
import javax.net.ssl.X509TrustManager

fun buildSslOkHttpClient(context: Context): OkHttpClient.Builder {
    val cf = CertificateFactory.getInstance("X.509")

    val cert = context.resources.openRawResource(R.raw.russian_trusted_root_ca)
        .use { cf.generateCertificate(it) }

    val keyStore = KeyStore.getInstance(KeyStore.getDefaultType()).apply {
        load(null, null)
        setCertificateEntry("russian_trusted_root_ca", cert)
    }

    val tmf = TrustManagerFactory
        .getInstance(TrustManagerFactory.getDefaultAlgorithm())
        .apply { init(keyStore) }

    val trustManager = tmf.trustManagers[0] as X509TrustManager

    val sslContext = SSLContext.getInstance("TLS").apply {
        init(null, arrayOf(trustManager), null)
    }

    return OkHttpClient.Builder()
        .sslSocketFactory(sslContext.socketFactory, trustManager)
}
