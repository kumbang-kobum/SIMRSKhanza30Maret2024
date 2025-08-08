package bridging;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;

public class WAKirim {

    private static final String WAHA_URL = "https://xxx.xxxx.co.id";
    private static final String UPLOAD_URL = WAHA_URL + "/HasilLab/upload_lab.php";
    private static final String AUTH_TOKEN = "SayangAnisaNairaNafasya"; // samakan dengan upload_lab.php

    /**
     * Upload file PDF hasil lab ke server WAHA
     * @param file PDF yang akan diupload
     * @return URL publik hasil upload, atau null jika gagal
     */
    public static String uploadPDFToServer(File file) {
        String uploadedUrl = null;
        try {
            String boundary = Long.toHexString(System.currentTimeMillis());
            String CRLF = "\r\n";

            URL url = new URL(UPLOAD_URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
            conn.setRequestProperty("Authorization", "Bearer " + AUTH_TOKEN);

            try (OutputStream output = conn.getOutputStream();
                 PrintWriter writer = new PrintWriter(new OutputStreamWriter(output, "UTF-8"), true)) {

                // Field file
                writer.append("--").append(boundary).append(CRLF);
                writer.append("Content-Disposition: form-data; name=\"file\"; filename=\"")
                        .append(file.getName()).append("\"").append(CRLF);
                writer.append("Content-Type: application/pdf").append(CRLF).append(CRLF);
                writer.flush();

                Files.copy(file.toPath(), output);
                output.flush();
                writer.append(CRLF).flush();

                // End boundary
                writer.append("--").append(boundary).append("--").append(CRLF).flush();
            }

            int responseCode = conn.getResponseCode();
            String response = new String(conn.getInputStream().readAllBytes());

            if (responseCode == 200) {
                // Ambil "url" dari JSON
                uploadedUrl = extractJsonValue(response, "url");
                if (uploadedUrl == null || uploadedUrl.isEmpty()) {
                    uploadedUrl = WAHA_URL + "/HasilLab/" + file.getName(); // fallback
                }
                System.out.println("✅ Upload berhasil ke: " + uploadedUrl);
            } else {
                System.out.println("❌ Upload gagal: " + responseCode + " - " + response);
            }

        } catch (Exception e) {
            System.out.println("❌ Gagal upload PDF: " + e.getMessage());
            e.printStackTrace();
        }
        return uploadedUrl;
    }

    /**
     * Kirim teks ke WAHA
     */
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

        } catch (Exception e) {
            System.err.println("Gagal kirim teks ke WA: " + e.getMessage());
        }
    }

    // Helper JSON parsing sederhana
    private static String extractJsonValue(String json, String key) {
        String pattern = "\"" + key + "\"";
        int idx = json.indexOf(pattern);
        if (idx == -1) return null;
        int start = json.indexOf('"', idx + pattern.length() + 1);
        int end = json.indexOf('"', start + 1);
        if (start == -1 || end == -1) return null;
        return json.substring(start + 1, end);
    }

    private static String escapeJson(String text) {
        return text.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }
}