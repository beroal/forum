import javax.net.ssl.X509TrustManager;
import java.security.cert.X509Certificate;
import java.security.cert.CertificateException;
import java.util.Arrays;
class EqualServerTrustManager implements X509TrustManager {
    byte[] cert;
    public EqualServerTrustManager(byte[] cert) { this.cert = cert; }
    public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        throw new CertificateException();
    }
    public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        if (! Arrays.equals(cert, chain[0].getEncoded())) { throw new CertificateException(); }
    }
    public X509Certificate[] getAcceptedIssuers() { return new X509Certificate[0]; }
}
