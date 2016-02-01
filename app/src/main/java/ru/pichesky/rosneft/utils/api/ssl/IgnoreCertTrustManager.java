package ru.pichesky.rosneft.utils.api.ssl;


import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.X509TrustManager;

/**
 * Created by Alexander Smirnov on 11.12.2014.
 * Ignore all certificates
 */
public class IgnoreCertTrustManager implements X509TrustManager {


    @Override
    public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
    }

    @Override
    public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
    }

    @Override
    public X509Certificate[] getAcceptedIssuers() {
        return null;
    }
}