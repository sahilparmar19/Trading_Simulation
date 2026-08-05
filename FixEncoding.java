import java.io.IOException;
import java.nio.file.*;
import java.nio.charset.StandardCharsets;

public class FixEncoding {
    public static void main(String[] args) throws IOException {
        Files.walk(Paths.get("src")).filter(Files::isRegularFile).forEach(path -> {
            if (path.toString().endsWith(".java")) {
                try {
                    byte[] bytes = Files.readAllBytes(path);
                    String content;
                    
                    // Check for UTF-8 BOM
                    if (bytes.length >= 3 && (bytes[0] & 0xFF) == 0xEF && (bytes[1] & 0xFF) == 0xBB && (bytes[2] & 0xFF) == 0xBF) {
                        content = new String(bytes, 3, bytes.length - 3, StandardCharsets.UTF_8);
                    } 
                    // Check for UTF-16 LE BOM
                    else if (bytes.length >= 2 && (bytes[0] & 0xFF) == 0xFF && (bytes[1] & 0xFF) == 0xFE) {
                        content = new String(bytes, 2, bytes.length - 2, StandardCharsets.UTF_16LE);
                    }
                    // Check for UTF-16 BE BOM
                    else if (bytes.length >= 2 && (bytes[0] & 0xFF) == 0xFE && (bytes[1] & 0xFF) == 0xFF) {
                        content = new String(bytes, 2, bytes.length - 2, StandardCharsets.UTF_16BE);
                    }
                    else {
                        // try UTF-8 directly
                        content = new String(bytes, StandardCharsets.UTF_8);
                    }
                    
                    // Some PS replacements might inject ZERO WIDTH NO-BREAK SPACE (\uFEFF)
                    content = content.replace("\uFEFF", "");

                    Files.write(path, content.getBytes(StandardCharsets.UTF_8));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
