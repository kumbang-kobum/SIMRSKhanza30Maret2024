package bridging;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import fungsi.akses;
import fungsi.koneksiDB;
import fungsi.sekuel;
import java.awt.HeadlessException;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Properties;
import javax.net.ssl.SSLContext;
import javax.net.ssl.X509TrustManager;
import javax.swing.JOptionPane;
import java.util.HashMap;
import java.util.Map;
import java.io.BufferedReader;
import java.io.OutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

public class BridgingWA {
    private static final Properties prop = new Properties();
    private sekuel Sequel=new sekuel();
    private String Key,pass , url , token ,requestJson, urlApi = "" , sender = "" , number ="" , message = "" , reurn = "" , device_id = "" , group_pendaftaran = "" , group_keuangan= "", api_key="";
    private HttpHeaders headers ;
    private HttpEntity requestEntity;
    private ObjectMapper mapper = new ObjectMapper();
    private JsonNode root;
    private JsonNode nameNode;
    private JsonNode response;


    public String getHmac() {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] hashInBytes = md.digest(pass.getBytes(StandardCharsets.UTF_8));

            StringBuilder sb = new StringBuilder();
            for (byte b : hashInBytes) {
                sb.append(String.format("%02x", b));
            }
            Key=sb.toString();
        } catch (Exception ex) {
            System.out.println("Notifikasi : "+ex);
        }
	return Key;
    }


    public RestTemplate getRest() throws NoSuchAlgorithmException, KeyManagementException {
        SSLContext sslContext = SSLContext.getInstance("SSL");
        javax.net.ssl.TrustManager[] trustManagers= {
            new X509TrustManager() {
                public X509Certificate[] getAcceptedIssuers() {return null;}
                public void checkServerTrusted(X509Certificate[] arg0, String arg1)throws CertificateException {}
                public void checkClientTrusted(X509Certificate[] arg0, String arg1)throws CertificateException {}
            }
        };
        sslContext.init(null,trustManagers , new SecureRandom());
        SSLSocketFactory sslFactory=new SSLSocketFactory(sslContext,SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
        Scheme scheme=new Scheme("https",443,sslFactory);
        HttpComponentsClientHttpRequestFactory factory=new HttpComponentsClientHttpRequestFactory();
        factory.getHttpClient().getConnectionManager().getSchemeRegistry().register(scheme);
        return new RestTemplate(factory);
    }

//    public String sendWaBatal(String nm_dokter, String no_rkm_medis, String nm_pasien){
//        try {
//            message = "Telah dilakukan pembatalan berobat \na/n *_"+nm_pasien+"_* dengan No.RM "+no_rkm_medis+", "
//                    + "yang berobat ke "+nm_dokter+"  \n \n Petugas yang membatalkan: "+akses.getkode();
//            number = "62895355284030";
//            token = Sequel.cariIsi("SELECT value FROM vnsimple_wa WHERE module='wagateway' AND field = 'token'");
//            urlApi = Sequel.cariIsi("SELECT value FROM vnsimple_wa WHERE module='wagateway' AND field = 'server'");
//            sender = Sequel.cariIsi("SELECT value FROM vnsimple_wa WHERE module='wagateway' AND field = 'phonenumber'");
//            url = urlApi+"/send-message";
//            HttpHeaders headers = new HttpHeaders();
//            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
//
//            MultiValueMap<String, String> map= new LinkedMultiValueMap<String, String>();
//            map.add("type", "text");
//            map.add("sender", sender);
//            map.add("number", number);
//            map.add("api_key", token);
//            map.add("message", message);
//
//            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(map, headers);
//
//            ResponseEntity<String> response = new RestTemplate().postForEntity( url, request , String.class );
//            root = mapper.readTree(response.getBody());
//            System.out.println(root);
//            if(root.path("status").asText().equals("true")){
//                //JOptionPane.showMessageDialog(null,root.path("msg").asText());
//                reurn = root.path("msg").asText();
//            }else {
//                JOptionPane.showMessageDialog(null,root.path("msg").asText());
//            }
//        } catch (Exception ex) {
//            System.out.println("Notifikasi : "+ex);
//            System.out.println(url);
//            if(ex.toString().contains("UnknownHostException")){
//                JOptionPane.showMessageDialog(null,"Koneksi ke server watzap.vnsimple.com terputus...!");
//            }
//        }
//        return token;
//    }

    public String sendWaHapusPengeluaran(String tgl, String kategori, String petugas, String pengeluaran, String keterangan){
        try {
            message = "*WARNING #hapuspengeluaran*,\n\n*telah dilakukan penghapusan data yang di entry*\nTgl: *_"+tgl+"_*\nKategori: "+kategori+"\n"
                    + "Petugas entry: "+petugas+"\n"
                    + "Pemasukan: Rp "+pengeluaran+"\n"
                    + "Keterangan: "+keterangan+"\n\n"
                    + "Petugas yang menghapus: "+akses.getkode();
//            group = "Recepcionist part II";
            group_keuangan = Sequel.cariIsi("SELECT value FROM vnsimple_wa WHERE module='wagateway' AND field = 'group_keuangan'");
            device_id = Sequel.cariIsi("SELECT value FROM vnsimple_wa WHERE module='wagateway' AND field = 'device_id_keuangan'");
            urlApi = Sequel.cariIsi("SELECT value FROM vnsimple_wa WHERE module='wagateway' AND field = 'urlapi'");
            url = urlApi+"/api/sendGroup";
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            MultiValueMap<String, String> map= new LinkedMultiValueMap<String, String>();
            map.add("type", "text");
            map.add("group", group_keuangan);
            map.add("device_id", device_id);
            map.add("message", message);

            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(map, headers);

            ResponseEntity<String> response = new RestTemplate().postForEntity( url, request , String.class );
            root = mapper.readTree(response.getBody());
            System.out.println(root);
            if(root.path("status").asText().equals("true")){
                //JOptionPane.showMessageDialog(null,root.path("msg").asText());
                reurn = root.path("msg").asText();
            }else {
                JOptionPane.showMessageDialog(null,root.path("msg").asText());
            }
        } catch (Exception ex) {
            System.out.println("Notifikasi : "+ex);
            System.out.println(url);
            if(ex.toString().contains("UnknownHostException")){
                JOptionPane.showMessageDialog(null,"Koneksi ke server watzap.vnsimple.com terputus...!");
            }
        }
        return token;
    }

    public String sendWaHapusPemasukan(String tgl, String kategori, String petugas, String pemasukan, String dari, String keperluan){
        try {
            message = "*WARNING #hapuspemasukan*,\n\n*telah dilakukan penghapusan data yang di entry*\nTgl: *_"+tgl+"_*\nKategori: "+kategori+"\n"
                    + "Petugas entry: "+petugas+"\n"
                    + "Pemasukan: Rp "+pemasukan+"\n"
                    + "Terima dari: "+dari+"\n"
                    + "Keperluan: "+keperluan+"\n\n"
                    + "Petugas yang menghapus: "+akses.getkode();
//            group = "Recepcionist part II";
            group_keuangan = Sequel.cariIsi("SELECT value FROM vnsimple_wa WHERE module='wagateway' AND field = 'group_keuangan'");
            device_id = Sequel.cariIsi("SELECT value FROM vnsimple_wa WHERE module='wagateway' AND field = 'device_id_keuangan'");
            urlApi = Sequel.cariIsi("SELECT value FROM vnsimple_wa WHERE module='wagateway' AND field = 'urlapi'");
            url = urlApi+"/api/sendGroup";
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            MultiValueMap<String, String> map= new LinkedMultiValueMap<String, String>();
            map.add("type", "text");
            map.add("group", group_keuangan);
            map.add("device_id", device_id);
            map.add("message", message);

            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(map, headers);

            ResponseEntity<String> response = new RestTemplate().postForEntity( url, request , String.class );
            root = mapper.readTree(response.getBody());
            System.out.println(root);
            if(root.path("status").asText().equals("true")){
                //JOptionPane.showMessageDialog(null,root.path("msg").asText());
                reurn = root.path("msg").asText();
            }else {
                JOptionPane.showMessageDialog(null,root.path("msg").asText());
            }
        } catch (Exception ex) {
            System.out.println("Notifikasi : "+ex);
            System.out.println(url);
            if(ex.toString().contains("UnknownHostException")){
                JOptionPane.showMessageDialog(null,"Koneksi ke server watzap.vnsimple.com terputus...!");
            }
        }
        return token;
    }

    public String sendWaBatal(String nm_dokter, String no_rkm_medis, String nm_pasien, String poliklinik, String no_rawat, String tgl_berobat){
        try {
            message = "*_Gunakan dengan bijak menu pembatalan pasien,_*\n*_karena dapat menyebabkan biaya registrasi menjadi Rp.0_*\n \nTelah dilakukan pembatalan berobat \n"
                    + "no.Rawat: *_"+no_rawat+"_*\n"
                    + "a/n: *_"+nm_pasien+"_*\n"
                    + "No.RM: *_"+no_rkm_medis+"_*\n"
                    + "tgl. brobat: *_"+tgl_berobat+"_*\n"
                    + "Ke: *_"+poliklinik+"_*\n"
                    + "dokter: "+nm_dokter+"  \n \n Petugas yang membatalkan: "+akses.getkode();
//            group = "Recepcionist part II";
            group_pendaftaran = Sequel.cariIsi("SELECT value FROM vnsimple_wa WHERE module='wagateway' AND field = 'group'");
            device_id = Sequel.cariIsi("SELECT value FROM vnsimple_wa WHERE module='wagateway' AND field = 'device_id'");
            urlApi = Sequel.cariIsi("SELECT value FROM vnsimple_wa WHERE module='wagateway' AND field = 'urlapi'");
            url = urlApi+"/api/sendGroup";
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            MultiValueMap<String, String> map= new LinkedMultiValueMap<String, String>();
            map.add("type", "text");
            map.add("group", group_pendaftaran);
            map.add("device_id", device_id);
            map.add("message", message);

            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(map, headers);

            ResponseEntity<String> response = new RestTemplate().postForEntity( url, request , String.class );
            root = mapper.readTree(response.getBody());
            System.out.println(root);
            if(root.path("status").asText().equals("true")){
                //JOptionPane.showMessageDialog(null,root.path("msg").asText());
                reurn = root.path("msg").asText();
            }else {
                JOptionPane.showMessageDialog(null,root.path("msg").asText());
            }
        } catch (Exception ex) {
            System.out.println("Notifikasi : "+ex);
            System.out.println(url);
            if(ex.toString().contains("UnknownHostException")){
                JOptionPane.showMessageDialog(null,"Koneksi ke server watzap.vnsimple.com terputus...!");
            }
        }
        return token;
    }

//    public String sendWa(String no_rkm_medis, String nama, String tanggal, String poli, String tanggal_booking, String jam_booking, String no_reg){
//        try {
//            message = "Mengingatkan kembali, kepada saudara/i 📢\n👔 Nama: *_"+nama+"_*\n🗃️ No.RM: "+no_rkm_medis+"\n"
//                    + "📆 Tgl booking:"+tanggal_booking+" "+jam_booking+"\n🚪 poliklinik: *"+poli+"*\n📆 Tgl periksa: "+tanggal+"\n🎯 No antrian: *"+no_reg+"*.\n\n"
//                    + "Untuk konfirmasi kehadiran, cukup membalas pesan ini dengan iya/tidak.\n\n\nCustomer Service "+akses.getnamars();
//            number = Sequel.cariIsi("SELECT no_tlp FROM pasien WHERE no_rkm_medis = "+no_rkm_medis);
//            device_id = Sequel.cariIsi("SELECT value FROM vnsimple_wa WHERE module='wagateway' AND field = 'device_id'");
//            urlApi = Sequel.cariIsi("SELECT value FROM vnsimple_wa WHERE module='wagateway' AND field = 'urlapi'");
//            url = urlApi+"/api/send";
//            HttpHeaders headers = new HttpHeaders();
//            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
//
//            MultiValueMap<String, String> map= new LinkedMultiValueMap<String, String>();
//            map.add("type", "text");
//            map.add("number", number);
//            map.add("device_id", device_id);
//            map.add("message", message);
//
//            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(map, headers);
//
//            ResponseEntity<String> response = new RestTemplate().postForEntity( url, request , String.class );
//            root = mapper.readTree(response.getBody());
//            System.out.println(root);
//            if(root.path("status").asText().equals("true")){
//                //JOptionPane.showMessageDialog(null,root.path("msg").asText());
//                reurn = root.path("msg").asText();
//            }else {
//                JOptionPane.showMessageDialog(null,root.path("msg").asText());
//            }
//        } catch (Exception ex) {
//            System.out.println("Notifikasi : "+ex);
//            System.out.println(url);
//            if(ex.toString().contains("UnknownHostException")){
//                JOptionPane.showMessageDialog(null,"Koneksi ke server watzap.vnsimple.com terputus...!");
//            }
//        }
//        return token;
//    }
    
    public String sendWa(String no_rkm_medis, String nama, String tanggal, String poli, String tanggal_booking, String jam_booking, String no_reg){
        try {
            message = "Mengingatkan kembali, kepada saudara/i 📢\n👔 Nama: *_"+nama+"_*\n🗃️ No.RM: "+no_rkm_medis+"\n"
                    + "📆 Tgl booking:"+tanggal_booking+" "+jam_booking+"\n🚪 poliklinik: *"+poli+"*\n📆 Tgl periksa: "+tanggal+"\n🎯 No antrian: *"+no_reg+"*.\n\n"
                    + "Mohon konfirmasi kehadiran, dengan membalas pesan ini iya/tidak.\n"
                    + "Terimaka kasih.\n\n\n"
                    + "Customer Service "+akses.getnamars();
            number = Sequel.cariIsi("SELECT no_tlp FROM pasien WHERE no_rkm_medis = "+no_rkm_medis);
            sender = Sequel.cariIsi("SELECT value FROM vnsimple_wa WHERE module='watzap' AND field = 'sender'");
            api_key = Sequel.cariIsi("SELECT value FROM vnsimple_wa WHERE module='watzap' AND field = 'api_key'");
            urlApi = Sequel.cariIsi("SELECT value FROM vnsimple_wa WHERE module='watzap' AND field = 'urlapi'");
            url = urlApi+"/send-message";
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            MultiValueMap<String, String> map= new LinkedMultiValueMap<String, String>();
//            map.add("type", "text");
            map.add("api_key", api_key);
            map.add("sender", sender);
            map.add("number", number);
            map.add("message", message);

            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(map, headers);

            ResponseEntity<String> response = new RestTemplate().postForEntity( url, request , String.class );
            root = mapper.readTree(response.getBody());
            System.out.println(root);
            if(root.path("status").asText().equals("true")){
                //JOptionPane.showMessageDialog(null,root.path("msg").asText());
                reurn = root.path("msg").asText();
            }else {
                JOptionPane.showMessageDialog(null,root.path("msg").asText());
            }
        } catch (Exception ex) {
            System.out.println("Notifikasi : "+ex);
            System.out.println(url);
            if(ex.toString().contains("UnknownHostException")){
                JOptionPane.showMessageDialog(null,"Koneksi ke server watzap.vnsimple.com terputus...!");
            }
        }
        return token;
    }

    public String sendWaUTD(String no_rkm_medis, String nama, String tanggal){
        try {
            message = "Mengingatkan kepada saudara "+nama+", untuk jadwal donor darah pada tanggal "+tanggal+". Terima kasih. Customer Service "+akses.getnamars();
            number = Sequel.cariIsi("SELECT no_tlp FROM pasien WHERE no_rkm_medis = "+no_rkm_medis);
            token = "";
            urlApi = "http://192.168.0.2:10000";
            sender = "628115167345";
            url = urlApi+"/wagateway/kirimpesan";
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            MultiValueMap<String, String> map= new LinkedMultiValueMap<String, String>();
            map.add("type", "text");
            map.add("sender", sender);
            map.add("number", number);
            map.add("api_key", token);
            map.add("message", message);

            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(map, headers);

            ResponseEntity<String> response = new RestTemplate().postForEntity( url, request , String.class );
            root = mapper.readTree(response.getBody());
            System.out.println(root);
            if(root.path("status").asText().equals("true")){
                //JOptionPane.showMessageDialog(null,root.path("msg").asText());
                reurn = root.path("msg").asText();
            }else {
                JOptionPane.showMessageDialog(null,root.path("msg").asText());
            }
        } catch (Exception ex) {
            System.out.println("Notifikasi : "+ex);
            System.out.println(url);
            if(ex.toString().contains("UnknownHostException")){
                JOptionPane.showMessageDialog(null,"Koneksi ke server watzap.vnsimple.com terputus...!");
            }
        }
        return token;
    }
    
    //hasil baca Radiologi
    public String SendHasilBaca(String no_rawat) {
   
    try {
        String no_rkm_medis = Sequel.cariIsi("SELECT no_rkm_medis FROM reg_periksa WHERE no_rawat = '" + no_rawat + "'");
        String nama = Sequel.cariIsi("SELECT nm_pasien FROM pasien WHERE no_rkm_medis = '" + no_rkm_medis + "'");
        String hasilrad = Sequel.cariIsi("SELECT hasil FROM hasil_radiologi WHERE no_rawat = '" + no_rawat + "'");
        String mediarad = Sequel.cariIsi("SELECT lokasi_gambar FROM gambar_radiologi WHERE no_rawat = '" + no_rawat + "'");
        number = Sequel.cariIsi("SELECT no_tlp FROM pasien WHERE no_rkm_medis = '" + no_rkm_medis + "'");

        message = "kepada saudara/i 📢\n👔 "
                + "Nomor Rawat: *_" + no_rawat + "_*\n"
                + "🗃️ No.RM: " + no_rkm_medis + "\n "
                + "Nama: " + nama + "\n "
                + "Hasil Baca: \n"
                +  hasilrad + "\n\n"
                + "Informasi lebih lanjut Wa. 0811725127\n"
                + "Terimakasih, Salam sehat"
                + "Unit Radiologi Petugas : " +Sequel.cariIsi("select pegawai.nama from pegawai where pegawai.nik=?",akses.getkode())
                + "Unit Radiologi "+akses.getnamars();
        
        sender = Sequel.cariIsi("SELECT value FROM vnsimple_wa WHERE module='watzap' AND field = 'sender'");
         api_key = Sequel.cariIsi("SELECT value FROM vnsimple_wa WHERE module='watzap' AND field = 'api_key'");
         urlApi = Sequel.cariIsi("SELECT value FROM vnsimple_wa WHERE module='watzap' AND field = 'urlapi'");
         url = urlApi + "/send-message";
        
        HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            MultiValueMap<String, String> map= new LinkedMultiValueMap<String, String>();map.add("api_key", api_key);
            map.add("sender", sender);
            map.add("number", number);
            map.add("message", message);

            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(map, headers);

            ResponseEntity<String> response = new RestTemplate().postForEntity( url, request , String.class );
            root = mapper.readTree(response.getBody());
            System.out.println(root);
            if(root.path("status").asText().equals("true")){
            reurn = root.path("msg").asText();
            }else {
                JOptionPane.showMessageDialog(null,root.path("msg").asText());
            }
        } catch (Exception ex) {
            System.out.println("Notifikasi : "+ex);
            System.out.println(url);
            if(ex.toString().contains("UnknownHostException")){
                JOptionPane.showMessageDialog(null,"Koneksi ke server watzap.vnsimple.com terputus...!");
            }
        }
        return token;   
}
//hasil baca Radiologi kirim ke dokter perujuk
    public String SendHasilBacadr(String no_rawat) {
   
    try {
        String no_rkm_medis = Sequel.cariIsi("SELECT no_rkm_medis FROM reg_periksa WHERE no_rawat = '" + no_rawat + "'");
        String nama = Sequel.cariIsi("SELECT nm_pasien FROM pasien WHERE no_rkm_medis = '" + no_rkm_medis + "'");
        String hasilrad = Sequel.cariIsi("SELECT hasil FROM hasil_radiologi WHERE no_rawat = '" + no_rawat + "'");
        String mediarad = Sequel.cariIsi("SELECT lokasi_gambar FROM gambar_radiologi WHERE no_rawat = '" + no_rawat + "'");
        String querydrperujuk = "SELECT dokter.no_telp FROM dokter " +
               "INNER JOIN periksa_radiologi ON dokter.kd_dokter = periksa_radiologi.dokter_perujuk " +
               "INNER JOIN hasil_radiologi ON periksa_radiologi.no_rawat = hasil_radiologi.no_rawat " +
               "WHERE hasil_radiologi.no_rawat = '" + no_rawat + "'";
        number = Sequel.cariIsi(querydrperujuk);

        message = "Berikut dokter hasil radiologi : 📢\n👔 "
                + "Nomor Rawat: *_" + no_rawat + "_*\n🗃️ "
                + "No.RM: " + no_rkm_medis + "\n "
                + "Nama: " + nama + "\n "
                + "Hasil Baca Radiologi: \n"
                +  hasilrad +""
                + "\nTerimakasih telah berkunjung semoga selalu sehat."
                + "\n\n\nUnit Radiologi Petugas : " +Sequel.cariIsi("select pegawai.nama from pegawai where pegawai.nik=?",akses.getkode());
        
        sender = Sequel.cariIsi("SELECT value FROM vnsimple_wa WHERE module='watzap' AND field = 'sender'");
         api_key = Sequel.cariIsi("SELECT value FROM vnsimple_wa WHERE module='watzap' AND field = 'api_key'");
         urlApi = Sequel.cariIsi("SELECT value FROM vnsimple_wa WHERE module='watzap' AND field = 'urlapi'");
         url = urlApi + "/send-message";
        
        HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            MultiValueMap<String, String> map= new LinkedMultiValueMap<String, String>();map.add("api_key", api_key);
            map.add("sender", sender);
            map.add("number", number);
            map.add("message", message);

            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(map, headers);

            ResponseEntity<String> response = new RestTemplate().postForEntity( url, request , String.class );
            root = mapper.readTree(response.getBody());
            System.out.println(root);
            if(root.path("status").asText().equals("true")){
            reurn = root.path("msg").asText();
            }else {
                JOptionPane.showMessageDialog(null,root.path("msg").asText());
            }
        } catch (Exception ex) {
            System.out.println("Notifikasi : "+ex);
            System.out.println(url);
            if(ex.toString().contains("UnknownHostException")){
                JOptionPane.showMessageDialog(null,"Koneksi ke server watzap.vnsimple.com terputus...!");
            }
        }
        return token;   
}

//informasi dokter tidak buka poli
    public String sendWaPoliTutup(String no_rawat, String tanggal, String poli, String no_reg){
        try {
            String no_rkm_medis = Sequel.cariIsi("SELECT no_rkm_medis FROM reg_periksa WHERE no_rawat = '" + no_rawat + "'");
            String nama = Sequel.cariIsi("SELECT nm_pasien FROM pasien WHERE no_rkm_medis = '" + no_rkm_medis + "'");
            message = "kepada saudara/i 📢\n👔 Nama: *_"+nama+"_*\n"
                    + "🗃️ No.RM: "+no_rkm_medis+"\n"
                    + "📆 Tgl periksa:"+tanggal+"\n"
                    + "🚪 poliklinik: *"+poli+"*\n"
                    + "🎯 No antrian: *"+no_reg+"*.\n"
                    + "Maaf. poli *"+poli+"* tidak *buka* Mohon konfirmasi jadwal kembali ke no WA ."
                    + "0811725127\n "
                    + "Terimakasih "+akses.getnamars()
                    + "Terimakasih:  " +Sequel.cariIsi("select pegawai.nama from pegawai where pegawai.nik=?",akses.getkode())
                    + ""+akses.getnamars();
            number = Sequel.cariIsi("SELECT no_tlp FROM pasien WHERE no_rkm_medis = "+no_rkm_medis);
            sender = Sequel.cariIsi("SELECT value FROM vnsimple_wa WHERE module='watzap' AND field = 'sender'");
            api_key = Sequel.cariIsi("SELECT value FROM vnsimple_wa WHERE module='watzap' AND field = 'api_key'");
            urlApi = Sequel.cariIsi("SELECT value FROM vnsimple_wa WHERE module='watzap' AND field = 'urlapi'");
            url = urlApi+"/send-message";
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            MultiValueMap<String, String> map= new LinkedMultiValueMap<String, String>();
//            map.add("type", "text");
            map.add("api_key", api_key);
            map.add("sender", sender);
            map.add("number", number);
            map.add("message", message);

            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(map, headers);

            ResponseEntity<String> response = new RestTemplate().postForEntity( url, request , String.class );
            root = mapper.readTree(response.getBody());
            System.out.println(root);
            if(root.path("status").asText().equals("true")){
                JOptionPane.showMessageDialog(null,root.path("msg").asText());
                reurn = root.path("msg").asText();
            }else {
                JOptionPane.showMessageDialog(null,root.path("msg").asText());
            }
        } catch (Exception ex) {
            System.out.println("Notifikasi : "+ex);
            System.out.println(url);
            if(ex.toString().contains("UnknownHostException")){
                JOptionPane.showMessageDialog(null,"Koneksi ke server watzap.vnsimple.com terputus...!");
            }
        }
        return api_key; 
    }  
    
    //kirim hasil baca lab ke pasien
        public String SendHasilBacaLab(String no_rawat) {
   
    try {
        String no_rkm_medis = Sequel.cariIsi("SELECT no_rkm_medis FROM reg_periksa WHERE no_rawat = '" + no_rawat + "'");
        String nama = Sequel.cariIsi("SELECT nm_pasien FROM pasien WHERE no_rkm_medis = '" + no_rkm_medis + "'");
        String hasillab = Sequel.cariIsi("SELECT nilai FROM detail_periksa_lab WHERE no_rawat = '" + no_rawat + "'");
        String nilairujuk = Sequel.cariIsi("SELECT nilai_rujukan FROM detail_periksa_lab WHERE no_rawat = '" + no_rawat + "'");
        String pemeriksaan = Sequel.cariIsi("SELECT jns_perawatan_lab.nm_perawatan " +
                                    "FROM jns_perawatan_lab " +
                                    "INNER JOIN detail_periksa_lab " +
                                    "ON jns_perawatan_lab.kd_jenis_prw = detail_periksa_lab.kd_jenis_prw " +
                                    "WHERE detail_periksa_lab.no_rawat = '" + no_rawat + "'");
        number = Sequel.cariIsi("SELECT no_tlp FROM pasien WHERE no_rkm_medis = '" + no_rkm_medis + "'");

        message = "kepada saudara/i 📢\n👔 "
                + "Nomor Rawat: *_" + no_rawat + "_*\n"
                + "🗃️ No.RM: " + no_rkm_medis + "\n "
                + "Pemeriksaan :"+ pemeriksaan +"\n "
                + "Nama: " + nama + "\n "
                + "Hasil Lab: " + hasillab + "\n "
                + "Nilai Rujuk :" +nilairujuk+"\n"
                + "Terimakasih telah berkunjung semoga selalu sehat.\n\n\n"
                + "Unit Laboratorium Petugas : " +Sequel.cariIsi("select pegawai.nama from pegawai where pegawai.nik=?",akses.getkode());
        
        sender = Sequel.cariIsi("SELECT value FROM vnsimple_wa WHERE module='watzap' AND field = 'sender'");
         api_key = Sequel.cariIsi("SELECT value FROM vnsimple_wa WHERE module='watzap' AND field = 'api_key'");
         urlApi = Sequel.cariIsi("SELECT value FROM vnsimple_wa WHERE module='watzap' AND field = 'urlapi'");
         url = urlApi + "/send-message";
        
        HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            MultiValueMap<String, String> map= new LinkedMultiValueMap<String, String>();map.add("api_key", api_key);
            map.add("sender", sender);
            map.add("number", number);
            map.add("message", message);

            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(map, headers);

            ResponseEntity<String> response = new RestTemplate().postForEntity( url, request , String.class );
            root = mapper.readTree(response.getBody());
            System.out.println(root);
            if(root.path("status").asText().equals("true")){
            reurn = root.path("msg").asText();
            }else {
                JOptionPane.showMessageDialog(null,root.path("msg").asText());
            }
        } catch (Exception ex) {
            System.out.println("Notifikasi : "+ex);
            System.out.println(url);
            if(ex.toString().contains("UnknownHostException")){
                JOptionPane.showMessageDialog(null,"Koneksi ke server watzap.vnsimple.com terputus...!");
            }
        }
        return token;   
}
//hasil baca laboratorium kirim ke dokter perujuk
    public String SendHasilBacaLabdr(String no_rawat) {
   
    try {
        String no_rkm_medis = Sequel.cariIsi("SELECT no_rkm_medis FROM reg_periksa WHERE no_rawat = '" + no_rawat + "'");
        String nama = Sequel.cariIsi("SELECT nm_pasien FROM pasien WHERE no_rkm_medis = '" + no_rkm_medis + "'");
        String hasilrad = Sequel.cariIsi("SELECT nilai FROM detail_periksa_lab WHERE no_rawat = '" + no_rawat + "'");
        //String mediarad = Sequel.cariIsi("SELECT lokasi_gambar FROM gambar_radiologi WHERE no_rawat = '" + no_rawat + "'");
        String querydrperujuk = "SELECT dokter.no_telp FROM dokter " +
               "INNER JOIN periksa_lab ON dokter.kd_dokter = periksa_lab.dokter_perujuk " +
               "INNER JOIN detail_periksa_lab ON periksa_lab.no_rawat = periksa_lab.no_rawat " +
               "WHERE detail_periksa_lab.no_rawat = '" + no_rawat + "'";
        number = Sequel.cariIsi(querydrperujuk);

        message = "Berikut dokter hasil Laborat : 📢\n👔 "
                + "Nomor Rawat: *_" + no_rawat + "_*\n"
                + "🗃️ No.RM: " + no_rkm_medis + "\n "
                + "Nama: " + nama + "\n "
                + "Hasil Baca: " + hasilrad 
                +"\nTerimakasih telah berkunjung semoga selalu sehat."
                + "\n\n\nUnit Radiologi Unit Petugas : " +Sequel.cariIsi("select pegawai.nama from pegawai where pegawai.nik=?",akses.getkode());
        
        sender = Sequel.cariIsi("SELECT value FROM vnsimple_wa WHERE module='watzap' AND field = 'sender'");
         api_key = Sequel.cariIsi("SELECT value FROM vnsimple_wa WHERE module='watzap' AND field = 'api_key'");
         urlApi = Sequel.cariIsi("SELECT value FROM vnsimple_wa WHERE module='watzap' AND field = 'urlapi'");
         url = urlApi + "/send-message";
        
        HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            MultiValueMap<String, String> map= new LinkedMultiValueMap<String, String>();map.add("api_key", api_key);
            map.add("sender", sender);
            map.add("number", number);
            map.add("message", message);

            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(map, headers);

            ResponseEntity<String> response = new RestTemplate().postForEntity( url, request , String.class );
            root = mapper.readTree(response.getBody());
            System.out.println(root);
            if(root.path("status").asText().equals("true")){
            reurn = root.path("msg").asText();
            }else {
                JOptionPane.showMessageDialog(null,root.path("msg").asText());
            }
        } catch (Exception ex) {
            System.out.println("Notifikasi : "+ex);
            System.out.println(url);
            if(ex.toString().contains("UnknownHostException")){
                JOptionPane.showMessageDialog(null,"Koneksi ke server watzap.vnsimple.com terputus...!");
            }
        }
        return token;   
}
    //kirim pesan dinamis dlgre
    public String sendWaDlgReg(String no_rawat, String tanggal, String poli, String no_reg, String pesan_dinamis){
        try {
            String no_rkm_medis = Sequel.cariIsi("SELECT no_rkm_medis FROM reg_periksa WHERE no_rawat = '" + no_rawat + "'");
            String nama = Sequel.cariIsi("SELECT nm_pasien FROM pasien WHERE no_rkm_medis = '" + no_rkm_medis + "'");
            message = "kepada saudara/i 📢\n👔 Nama: *_"+nama+"_*\n"
                    + "🗃️ No.RM: "+no_rkm_medis+"\n"
                    + "📆 Tgl periksa:"+tanggal+"\n"
                    + "🚪 poliklinik: *"+poli+"*\n"
                    + "🎯 No antrian: *"+no_reg+"*.\n"
                    + "*"+pesan_dinamis+"*\n\n"
                    + "> Customer Service :  " +Sequel.cariIsi("select pegawai.nama from pegawai where pegawai.nik=?",akses.getkode())
                    + ""+akses.getnamars();
            number = Sequel.cariIsi("SELECT no_tlp FROM pasien WHERE no_rkm_medis = "+no_rkm_medis);
            sender = Sequel.cariIsi("SELECT value FROM vnsimple_wa WHERE module='watzap' AND field = 'sender'");
            api_key = Sequel.cariIsi("SELECT value FROM vnsimple_wa WHERE module='watzap' AND field = 'api_key'");
            urlApi = Sequel.cariIsi("SELECT value FROM vnsimple_wa WHERE module='watzap' AND field = 'urlapi'");
            url = urlApi+"/send-message";
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            MultiValueMap<String, String> map= new LinkedMultiValueMap<String, String>();
//            map.add("type", "text");
            map.add("api_key", api_key);
            map.add("sender", sender);
            map.add("number", number);
            map.add("message", message);

            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(map, headers);

            ResponseEntity<String> response = new RestTemplate().postForEntity( url, request , String.class );
            root = mapper.readTree(response.getBody());
            System.out.println(root);
            if(root.path("status").asText().equals("true")){
                JOptionPane.showMessageDialog(null,root.path("msg").asText());
                reurn = root.path("msg").asText();
            }else {
                JOptionPane.showMessageDialog(null,root.path("msg").asText());
            }
        } catch (Exception ex) {
            System.out.println("Notifikasi : "+ex);
            System.out.println(url);
            if(ex.toString().contains("UnknownHostException")){
                JOptionPane.showMessageDialog(null,"Koneksi ke server watzap.vnsimple.com terputus...!");
            }
        }
        return api_key;
    }
    public String sendObatWA(String no_rkm_medis, String nama, String tanggal, String poli){
    String reurn = "";
    try {
        String message = "Memberitahukan, kepada saudara/i 📢\n👔 Nama: *_"+nama+"_*\n🗃️ No.RM: "+no_rkm_medis+"\n"
                + "Pengobatan di poliklinik: *"+poli+"*\n📆 Tgl periksa: "+tanggal+"\n"
                + "Bahwa resep obat anda telah selesai dan bisa diambil di loket pengambilan resep.\n"
                + "Terimaka kasih.\n\n\n"
                + "Farmasi RS Handayani.\n "+akses.getkode();
        
        String number = Sequel.cariIsi("SELECT no_tlp FROM pasien WHERE no_rkm_medis = '"+no_rkm_medis+"'");
        String sender = Sequel.cariIsi("SELECT value FROM vnsimple_wa WHERE module='watzap' AND field = 'sender'");
        String api_key = Sequel.cariIsi("SELECT value FROM vnsimple_wa WHERE module='watzap' AND field = 'api_key'");
        String urlApi = Sequel.cariIsi("SELECT value FROM vnsimple_wa WHERE module='watzap' AND field = 'urlapi'");
        String url = urlApi+"/send-message";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> map= new LinkedMultiValueMap<String, String>();
        map.add("api_key", api_key);
        map.add("sender", sender);
        map.add("number", number);
        map.add("message", message);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(map, headers);
        ResponseEntity<String> response = new RestTemplate().postForEntity( url, request , String.class );
        
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(response.getBody());
        System.out.println(root);
        
        if(root.path("status").asText().equals("true")){
            reurn = root.path("msg").asText();
        } else {
            JOptionPane.showMessageDialog(null, root.path("msg").asText());
        }
    } catch (Exception ex) {
        System.out.println("Notifikasi : "+ex);
        System.out.println(url);
        if(ex.toString().contains("UnknownHostException")){
            JOptionPane.showMessageDialog(null,"Koneksi ke server watzap.vnsimple.com terputus...!");
        }
    }
    return reurn;
}
}
