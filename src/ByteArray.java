import java.util.Arrays;
import java.sql.Blob;
import java.sql.SQLException;
import javax.sql.rowset.serial.SerialBlob;
public class ByteArray {
    byte[] data;
    public ByteArray(byte[] data) { this.data = data; }
    public int hashCode() { return Arrays.hashCode(data); }
    public boolean equals(Object obj) {
        return (obj instanceof ByteArray && Arrays.equals(data, ((ByteArray) obj).data));
    }
    public Blob getBlob() throws SQLException { return new SerialBlob(data); }
}