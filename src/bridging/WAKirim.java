package bridging;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.util.Properties;

public class WAKirim {

    private static final String DEFAULT_UPLOAD_URL = "http://10.8.0.6/upload_lab.php";
    private static final String DEFAULT_WAHA_URL = "https://waha.rsuhandayani.co.id";
    private static final String DEFAULT_AUTH_TOKEN = "SayangAnisaNairaNafasya";

    private static final String AUTH_TOKEN = getEnv("UPLOAD_TOKEN", DEFAULT_AUTH_TOKEN);
    private static final String WAHA_URL = getEnv("WAHA_URL", DEFAULT_WAHA_URL);

    // ================== UPLOAD PDF ==================
    public static void uploadPDFToServer(File file) {
        try {
            String boundary = Long.toHexString(System.currentTimeMillis());
            String CRLF = "\r\n";

            URL url = new URL(getUploadURL());
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
            conn.setRequestProperty("Authorization", "Bearer " + AUTH_TOKEN);

            try (OutputStream output = conn.getOutputStream()) {
                PrintWriter writer = new PrintWriter(new OutputStreamWriter(output, "UTF-8"), true);
                writer.append("--").append(boundary).append(CRLF);
                writer.append("Content-Disposition: form-data; name=\"file\"; filename=\"")
                        .append(file.getName()).append("\"").append(CRLF);
                writer.append("Content-Type: application/pdf").append(CRLF).append(CRLF);
                writer.flush();

                Files.copy(file.toPath(), output);
                output.flush();

                writer.append(CRLF).flush();
                writer.append("--").append(boundary).append("--").append(CRLF).flush();
            }

            int responseCode = conn.getResponseCode();
            if (responseCode == 200) {
                System.out.println("✅ Upload berhasil ke: " + WAHA_URL + "/HasilLab/" + file.getName());
            } else {
                System.out.println("❌ Upload gagal. Kode respons: " + responseCode);
                try (InputStream errorStream = conn.getErrorStream()) {
                    if (errorStream != null) {
                        String response = new String(errorStream.readAllBytes());
                        System.out.println("Server response: " + response);
                    }
                }
            }

        } catch (Exception e) {
            System.out.println("❌ Gagal upload ke server: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // ================== KIRIM TEKS UNTUK PRO ==================
//    public static void kirimTeks(String chatId, String pesan) {
//        try {
//            URL url = new URL(WAHA_URL + "/api/sendText");
//            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//            conn.setRequestMethod("POST");
//            conn.setRequestProperty("Content-Type", "application/json");
//            conn.setDoOutput(true);
//
//            String payload = String.format(
//                    "{\"chatId\":\"%s\",\"text\":\"%s\"}",
//                    chatId, pesan.replace("\"", "\\\"")
//            );
//
//            try (OutputStream os = conn.getOutputStream()) {
//                os.write(payload.getBytes("utf-8"));
//            }
//
//            int status = conn.getResponseCode();
//            System.out.println("WA kirim teks, status: " + status);
//            conn.disconnect();
//
//        } catch (Exception e) {
//            System.err.println("Gagal kirim teks ke WA: " + e.getMessage());
//        }
//    }
    
    // ================== KIRIM TEKS UNTUK GRATIS DENGAN LINK ==================
    public static void kirimTeks(String chatId, String pesan) {
    try {
        URL url = new URL(WAHA_URL + "/api/sendText");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);

        String cleanText = escapeJson(pesan);

        String payload = String.format("""
                {
                    "chatId": "%s",
                    "text": "%s",
                    "session": "default"
                }
                """, chatId, cleanText);

        try (OutputStream os = conn.getOutputStream()) {
            os.write(payload.getBytes("utf-8"));
        }

        int status = conn.getResponseCode();
        System.out.println("WA kirim teks, status: " + status);

        if (status >= 400) {
            try (var err = conn.getErrorStream()) {
                if (err != null) {
                    System.out.println("❌ Error dari WAHA:");
                    err.transferTo(System.out);
                }
            }
        }

        conn.disconnect();

    } catch (Exception e) {
        System.err.println("Gagal kirim teks ke WA: " + e.getMessage());
    }
}

    // ================== KIRIM FILE ==================
    public static void kirimFile(String chatId, String fileUrl, String fileName, String caption) {
        try {
            URL url = new URL(WAHA_URL + "/api/sendFile");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");

            String json = String.format("""
            {
              "chatId": "%s",
              "fileName": "%s",
              "file": "%s",
              "caption": "%s",
              "session": "default"
            }
            """,
            chatId.replace("\"", "\\\""),
            fileName.replace("\"", "\\\""),
            fileUrl.replace("\"", "\\\""),
            caption.replace("\"", "\\\"")
            );

            try (OutputStream os = conn.getOutputStream()) {
                os.write(json.getBytes("utf-8"));
            }

            int responseCode = conn.getResponseCode();
            System.out.println("WA kirim file, status: " + responseCode);
        } catch (Exception e) {
            System.out.println("WA kirim gagal: " + e.getMessage());
        }
    }

    // ================== HELPER ==================
    public static String getUploadURL() {
        try {
            Properties prop = new Properties();
            prop.loadFromXML(new FileInputStream("database.xml"));
            return prop.getProperty("UPLOADLABURL", DEFAULT_UPLOAD_URL);
        } catch (Exception e) {
            return DEFAULT_UPLOAD_URL;
        }
    }

    private static String getEnv(String key, String defaultVal) {
        String val = System.getenv(key);
        return (val != null && !val.isEmpty()) ? val : defaultVal;
    }
    
    public static String escapeJson(String text) {
    return text.replace("\\", "\\\\")
               .replace("\"", "\\\"")
               .replace("\n", "\\n")
               .replace("\r", "\\r")
               .replace("\t", "\\t");
}
}