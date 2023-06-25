package virusanalyzer;

import javax.xml.bind.DatatypeConverter;
import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

public class AnalyzingLogic {

    private static final int BUFFER_SIZE = 8192;

    public String md5Generator(String path) {
        String md5Checksum = null;

        try {
            MessageDigest md = MessageDigest.getInstance("MD5");

            FileInputStream fis = new FileInputStream(new File(path));
            FileChannel channel = fis.getChannel();
            ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);
            int bytesRead = channel.read(buffer);
            while (bytesRead != -1) {
                buffer.flip();
                md.update(buffer);
                buffer.clear();
                bytesRead = channel.read(buffer);
            }

            byte[] digest = md.digest();
            md5Checksum = DatatypeConverter.printHexBinary(digest).toLowerCase();
            fis.close();
        } catch (NoSuchAlgorithmException e) {
        } catch (Exception e) {
        }
        return md5Checksum;
    }

    public int analyze(String fileChecksum, ArrayList<String> virusDefinitions) {
        int index = -1;
        for (int i = 0; i < virusDefinitions.size(); i++) {
            if (fileChecksum.equals(virusDefinitions.get(i))) {
                index = i;
                return index;
            }
        }
        return index;
    }
}
