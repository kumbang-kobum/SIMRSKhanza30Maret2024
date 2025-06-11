<?php
    if(strpos($_SERVER['REQUEST_URI'],"pages")){
        exit(header("Location:../index.php"));
    }
    
    $namars        = getOne("select setting.nama_instansi from setting");
    $nopernyataan  = "";
    $norawat       = "";
    $dokterdpjpranap = getOne("SELECT dokter.nm_dokter FROM kamar_inap JOIN dpjp_ranap ON kamar_inap.no_rawat = dpjp_ranap.no_rawat JOIN dokter ON dpjp_ranap.kd_dokter = dokter.kd_dokter");
    
    $_sql          = "select * from antripemilihandpjp" ;  
    $hasil         = bukaquery2($_sql);
    while ($data = mysqli_fetch_array ($hasil)){
        $nopernyataan = $data['no_surat'];
        $norawat      = $data['no_rawat'];
    }
    
    $no_rkm_medis = "";
    $nm_pasien    = "";
    $jk           = "";
    $umur         = "";
    $tgl_lahir    = "";
    $alamat       = "";
    $no_tlp       = "";
    
    $_sql2  = "select reg_periksa.no_rawat,pasien.no_rkm_medis,pasien.nm_pasien,if(pasien.jk='L','LAKI-LAKI','PEREMPUAN') as jk,
               pasien.umur,DATE_FORMAT(pasien.tgl_lahir,'%d-%m-%Y') as tgl_lahir,concat(pasien.alamat,', ',kelurahan.nm_kel,', ',kecamatan.nm_kec,', ',kabupaten.nm_kab) as alamat, 
               pasien.no_tlp from reg_periksa inner join pasien on reg_periksa.no_rkm_medis=pasien.no_rkm_medis 
               inner join kelurahan on pasien.kd_kel=kelurahan.kd_kel
               inner join kecamatan on pasien.kd_kec=kecamatan.kd_kec 
               inner join kabupaten on pasien.kd_kab=kabupaten.kd_kab
               where reg_periksa.no_rawat='".$norawat."'" ;  
    $hasil2 = bukaquery2($_sql2);
    while ($data2  = mysqli_fetch_array ($hasil2)){
        $no_rkm_medis = $data2['no_rkm_medis'];
        $nm_pasien    = $data2['nm_pasien'];
        $jk           = $data2['jk'];
        $umur         = $data2['umur'];
        $tgl_lahir    = $data2['tgl_lahir'];
        $alamat       = $data2['alamat'];
        $no_tlp       = $data2['no_tlp'];
    }
    
    $tanggal                        = "";
    $nama_pj                        = "";
    $umur_pj                        = "";
    $no_ktppj                       = "";
    $alamatpj                       = "";
    $no_telppj                      = "";
    $hubungan                       = "";
    $_sql2  = "select DATE_FORMAT(persetujuan_pernyataan_pemilihan_dpjp.tanggal,'%d-%m-%Y') as tanggal,persetujuan_pernyataan_pemilihan_dpjp.nama_pj,
               persetujuan_pernyataan_pemilihan_dpjp.umur_pj,persetujuan_pernyataan_pemilihan_dpjp.no_ktppj,persetujuan_pernyataan_pemilihan_dpjp.alamatpj,
               persetujuan_pernyataan_pemilihan_dpjp.no_telppj,persetujuan_pernyataan_pemilihan_dpjp.hubungan
               from persetujuan_pernyataan_pemilihan_dpjp where persetujuan_pernyataan_pemilihan_dpjp.no_surat='$nopernyataan'" ;  
    $hasil2 = bukaquery2($_sql2);
    while ($data2  = mysqli_fetch_array ($hasil2)){
        $tanggal                        = $data2['tanggal'];
        $nama_pj                        = $data2['nama_pj'];
        $umur_pj                        = $data2['umur_pj'];
        $no_ktppj                       = $data2['no_ktppj'];
        $alamatpj                       = $data2['alamatpj'];
        $no_telppj                      = $data2['no_telppj'];
        $hubungan                       = $data2['hubungan'];
    }
?>

<!DOCTYPE html>
<html>
<head>
    <title>SIMKES Khanza</title>
    <script src="js/jquery.min.js"></script>
    <script src="js/webcam.min.js"></script>
    <link rel="stylesheet" href="css/bootstrap.min.css" />
    <style type="text/css">
        #results { padding: 0px; background:#EEFFEE; width: 490; height: 390 }
    </style>
</head>
<body>
    <div class="container">
        <h5 class="text-dark"><center><button class="btn btn-secondary" onclick="window.location.reload();">Refresh</button><br/><br/>PERNYATAAN PEMILIHAN DPJP NO. <?=$nopernyataan;?></center></h5>
        <h7 class="text-dark"><center>Tanggal <?=$tanggal;?></center></h7><br/>
        <form method="POST" action="pages/storeImage.php" onsubmit="return validasiIsi();" enctype=multipart/form-data>
            <input type="hidden" name="nopernyataan" value="<?=$nopernyataan;?>">
            <h7 class="text-dark">
                Saya yang membuat pernyataan di bawah ini yang menerima informasi (Pasien Sendiri / Keluarga Pasien / Penanggung Jawab Pasien) :
            </h7>
            <table class="default" width="98%" border="0" align="center" cellpadding="3px" cellspacing="0px">
                <tr class="text-dark">
                    <td width="25%">Nama</td>
                    <td width="70%">: <?=$nama_pj;?></td>
                </tr>
                <tr class="text-dark">
                    <td width="25%">Umur</td>
                    <td width="75%">: <?=$umur_pj;?></td>
                </tr>
                <tr class="text-dark">
                    <td width="25%">Nomor KTP</td>
                    <td width="75%">: <?=$no_ktppj;?></td>
                </tr>
                <tr class="text-dark">
                    <td width="25%">Alamat Tinggal</td>
                    <td width="75%">: <?=$alamatpj;?></td>
                </tr>
                <tr class="text-dark">
                    <td width="25%">Nomor Telp/HP</td>
                    <td width="75%">: <?=$no_telppj;?></td>
                </tr>
                <tr class="text-dark">
                    <td width="25%">Hubungan Dengan Pasien</td>
                    <td width="75%">: <?=$hubungan;?></td>
                </tr>
                
            </table>
            <br/>
            <h7 class="text-dark">
                Terhadap Pasien : 
            </h7>
            <table class="default" width="98%" border="0" align="center" cellpadding="3px" cellspacing="0px">
                <tr class="text-dark">
                    <td width="25%">Nama Pasien</td>
                    <td width="70%">: <?=$nm_pasien;?></td>
                </tr>
                <tr class="text-dark">
                    <td width="25%">Nomor Rekam Medis</td>
                    <td width="75%">: <?=$no_rkm_medis;?></td>
                </tr>
                <tr class="text-dark">
                    <td width="25%">Jenis Kelamin</td>
                    <td width="75%">: <?=$jk;?></td>
                </tr>
                <tr class="text-dark">
                    <td width="25%">Tanggal Lahir</td>
                    <td width="75%">: <?=$tgl_lahir;?></td>
                </tr>
            </table>
            <br/>
            <h7 class="text-dark">
                Dengan ini menyatakan dengan sadar dan sesungguhnya bahwa :
            </h7>
            <br/>
            <h7 class="text-dark"><li>
                telah menerima dan memahami informasi mengenai dokter penanggung jawab pasien selama di rumah sakit
            </h7>
            <h7 class="text-dark"><li>
                Berdasarkan hal tersebut diatas saya memilih dokter : <b><?=$dokterdpjpranap;?> </b>sebagai dokter penangung jawab
            </h7>
            <br/>
            <h7 class="text-dark">
                Demikian penyataan ini saya buat dengan sebenar-benarnya.
            </h7>
            <br/>
            <br/>
            <h7 class="text-dark"><center>Yang Membuat Pernyataan</center></h7>
            <div class="row">
                <div class="col-md-6">
                    <div id="my_camera"></div>
                    <input type="hidden" name="image" class="image-tag" onkeydown="setDefault(this, document.getElementById('MsgIsi1'));" id="TxtIsi1">
                </div>
                <div class="col-md-6">
                    <div id="results"><h7 class="text-success"><center>Gambar akan diambil jika anda sudah mengeklik ya</center></h7></div>
                    <span id="MsgIsi1" style="color:#CC0000; font-size:10px;"></span>
                </div>
                <div class="col-md-12 text-center">
                    <br>
                    <input type="button" class="btn btn-warning" value="Ya, Saya sebagai pembuat persetujuan" onClick="take_snapshot()">
                    <button class="btn btn-danger">Simpan</button>
                </div>
            </div>
        </form>
    </div>
    
    <script language="JavaScript">
        Webcam.set({
            width: 490,
            height: 390,
            image_format: 'jpeg',
            jpeg_quality: 90
        });

        Webcam.attach( '#my_camera' );

        function take_snapshot() {
            Webcam.snap( function(data_uri) {
                $(".image-tag").val(data_uri);
                document.getElementById('results').innerHTML = '<img src="'+data_uri+'"/>';
            } );
        }
    </script>
</body>
</html>

