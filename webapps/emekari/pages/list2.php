<?php
if (strpos($_SERVER['REQUEST_URI'], "pages")) {
    exit(header("Location:../index.php"));
}
?>
<div id="post">        
    <?php
        $action = isset($_GET['action']) ? $_GET['action'] : NULL;
        $keyword = validTeks(isset($_GET['keyword']) ? str_replace("_", " ", $_GET['keyword']) : NULL);
    ?>
    <div style="width: 100%; height: 96%; overflow: auto;">
    <?php
        $_sql = "SELECT id, no_rawat, document_id, upload_date, filename FROM uploaded_documents WHERE 
                id LIKE '%$keyword%' OR no_rawat LIKE '%$keyword%' OR document_id LIKE '%$keyword%' 
                OR upload_date LIKE '%$keyword%' OR filename LIKE '%$keyword%' ORDER BY id";
        
        $hasil = bukaquery($_sql);
        $jumlah = mysqli_num_rows($hasil);

        if ($jumlah != 0) {
            echo "<table width='100%' border='0' align='center' cellpadding='0' cellspacing='0' class='tbl_form'>
                    <tr class='head2'>
                        <td width='5%'><div align='center'>ID</div></td>
                        <td width='15%'><div align='center'>No Rawat</div></td>
                        <td width='20%'><div align='center'>Document ID</div></td>
                        <td width='15%'><div align='center'>Upload Date</div></td>
                        <td width='35%'><div align='center'>File Name</div></td>
                        <td width='10%'><div align='center'>Action</div></td>
                    </tr>";
            while ($baris = mysqli_fetch_array($hasil)) {                        
                echo "<tr class='isi'>
                        <td align='center'>".$baris["id"]."</td>
                        <td align='left'>".$baris["no_rawat"]."</td>
                        <td align='left'>".$baris["document_id"]."</td>
                        <td align='center'>".$baris["upload_date"]."</td>
                        <td align='left'>".$baris["filename"]."</td>
                        <td align='center'>
                            <a target='_blank' href='/pages/upload/uploads/".$baris["filename"]."'>[Download File]</a>
                        </td>
                    </tr>";
            }
            echo "</table>";
        } else {
            echo "<table width='100%' border='0' align='center' cellpadding='0' cellspacing='0' class='tbl_form'>
                    <tr class='head2'>
                        <td colspan='6' align='center'>No records found for the keyword '$keyword'.</td>
                    </tr>
                 </table>";
        }
    ?>
    </div>
    <?php
        echo("<table width='100%' border='0' align='center' cellpadding='0' cellspacing='0' class='tbl_form'>
                <tr class='head'>
                    <td><div align='left'>Total Data: $jumlah</div></td>                        
                </tr>     
             </table>");
    ?>
</div>
