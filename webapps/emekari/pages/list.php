<?php 
if (strpos($_SERVER['REQUEST_URI'], "pages")) {
    if (!strpos($_SERVER['REQUEST_URI'], "/pages/upload/uploads")) {
        exit(header("Location:../index.php"));
    }
}
?>
<div id="post">        
    <form name="frm_aturadmin" onsubmit="return validasiIsi();" method="post" action="" enctype="multipart/form-data">
        <?php
            $action = isset($_GET['action']) ? $_GET['action'] : NULL;
            $keyword = validTeks(str_replace("_", " ", isset($_GET['keyword']) ? $_GET['keyword'] : NULL));
            $id = validTeks4(isset($_GET['id']) ? $_GET['id'] : NULL, 10);
            $no_rawat = "";
            $document_id = "";
            $upload_date = "";
            $filename = "";
            
            if ($action == "TAMBAH") {
                $max = getOne("SELECT IFNULL(MAX(CONVERT(RIGHT(id,7),signed)),0)+1 FROM uploaded_documents");
                $id = "D" . sprintf("%07s", $max);
            } else if ($action == "UBAH") {
                $sql = "SELECT * FROM uploaded_documents WHERE id='$id'";
                $result = bukaquery($sql);
                while ($row = mysqli_fetch_array($result)) {
                    $id = $row["id"];
                    $no_rawat = $row["no_rawat"];
                    $document_id = $row["document_id"];
                    $upload_date = $row["upload_date"];
                    $filename = $row["filename"];
                }
            }
            echo "<input type=hidden name=action value=$action><input type=hidden name=id value=$id>";
        ?>

        <table width="100%" align="center" border="0" cellpadding="0" cellspacing="0" class="tbl_form">
            <tr class="isi2">
                <td width="15%">ID</td>
                <td width="35%">
                    :&nbsp;<input name="id" type="text" class="text" id="TxtIsi1" value="<?php echo $id; ?>" size="20" maxlength="10" pattern="[a-zA-Z0-9-]{1,10}" title="a-z, A-Z, 0-9 (Max 10 chars)" required autofocus>
                    <span id="MsgIsi1" style="color:#CC0000; font-size:10px;"></span>
                </td>
                <td width="15%">No Rawat</td>
                <td width="35%">
                    :&nbsp;<input name="no_rawat" type="text" class="text" id="TxtIsi5" value="<?php echo $no_rawat; ?>" size="20" maxlength="10" required>
                    <span id="MsgIsi5" style="color:#CC0000; font-size:10px;"></span>
                </td>
            </tr>
            <tr class="isi2">
                <td width="15%">Document ID</td>
                <td width="35%">
                    :&nbsp;<input name="document_id" type="text" class="text" id="TxtIsi2" value="<?php echo $document_id; ?>" size="20" maxlength="20" required>
                    <span id="MsgIsi2" style="color:#CC0000; font-size:10px;"></span>
                </td>
                <td width="15%">Upload Date</td>
                <td width="35%">
                    :&nbsp;<input name="upload_date" type="date" class="text" id="TxtIsi6" value="<?php echo $upload_date; ?>" required>
                    <span id="MsgIsi6" style="color:#CC0000; font-size:10px;"></span>
                </td>
            </tr>
            <tr class="isi2">
                <td width="15%">File</td>
                <td width="35%">
                    :&nbsp;<input name="filename" type="file" class="text" id="TxtIsi9" accept=".pdf" required>
                    <span id="MsgIsi9" style="color:#CC0000; font-size:10px;"></span>
                </td>
            </tr> 
        </table>
        <div align="center">
            <input name="BtnSimpan" type="submit" class="button" value="&nbsp;&nbsp;Simpan&nbsp;&nbsp;">
            &nbsp;
            <input name="BtnKosong" type="reset" class="button" value="&nbsp;&nbsp;Kosong&nbsp;&nbsp;">
        </div>
        <?php
            if (isset($_POST['BtnSimpan'])) {
                $id = validTeks4(trim($_POST['id']), 10);
                $no_rawat = validTeks4(trim($_POST['no_rawat']), 10);
                $document_id = validTeks4(trim($_POST['document_id']), 20);
                $upload_date = validTeks4(trim($_POST['upload_date']), 10);
                $filename = "pages/upload/uploads/" . basename($_FILES['filename']['name']);
                
                if ((!empty($id)) && (!empty($no_rawat)) && (!empty($document_id)) && (!empty($upload_date)) && (!empty($filename))) {
                    if ($action == "TAMBAH") {
                        if (strtolower(pathinfo($filename, PATHINFO_EXTENSION)) == "pdf") {
                            if (move_uploaded_file($_FILES['filename']['tmp_name'], $filename)) {
                                Tambah("uploaded_documents", "'$id', '$no_rawat', '$document_id', '$upload_date', '$filename'", "Dokumen");
                                echo "<meta http-equiv='refresh' content='1;URL=?act=List&action=TAMBAH'>";
                            }
                        } else {
                            echo "File must be in PDF format.";
                        }
                    } else if ($action == "UBAH") {
                        if (strtolower(pathinfo($filename, PATHINFO_EXTENSION)) == "pdf") {
                            if (move_uploaded_file($_FILES['filename']['tmp_name'], $filename)) {
                                Ubah("uploaded_documents", "no_rawat='$no_rawat', document_id='$document_id', upload_date='$upload_date', filename='$filename' WHERE id='$id'", "Dokumen");
                                echo "<meta http-equiv='refresh' content='1;URL=?act=List&action=TAMBAH'>";
                            }
                        } else {
                            echo "File must be in PDF format.";
                        }
                    }
                } else {
                    echo "All fields are required.";
                }
            }
        ?>
    </form>
    <div style="width: 100%; height: 72%; overflow: auto;">
    <?php
        $sql = "SELECT * FROM uploaded_documents WHERE id LIKE '%$keyword%' OR no_rawat LIKE '%$keyword%' OR document_id LIKE '%$keyword%' ORDER BY id";
        $result = bukaquery($sql);
        
        if (mysqli_num_rows($result) != 0) {
            echo "<table width='100%' border='0' align='center' cellpadding='0' cellspacing='0' class='tbl_form'>
                    <tr class='head2'>
                        <td width='10%'>Process</td>
                        <td width='10%'>ID</td>
                        <td width='20%'>No Rawat</td>
                        <td width='20%'>Document ID</td>
                        <td width='20%'>Upload Date</td>
                        <td width='20%'>Filename</td>
                    </tr>";
            while ($row = mysqli_fetch_array($result)) {                        
                echo "<tr class='isi'>
                        <td align='center'>
                            <a href='?act=List&action=UBAH&id=".$row["id"]."'>[edit]</a>
                            <a href='?act=List&action=HAPUS&id=".$row["id"]."'>[delete]</a>
                            <a target='_blank' href='/pages/upload/uploads/".$row["filename"]."'>[File]</a>
                        </td>
                        <td>".$row["id"]."</td>
                        <td>".$row["no_rawat"]."</td>
                        <td>".$row["document_id"]."</td>
                        <td>".$row["upload_date"]."</td>
                        <td>".$row["filename"]."</td>
                    </tr>";
            }
            echo "</table>";
        } else {
            echo "<table width='100%' border='0' align='center' cellpadding='0' cellspacing='0' class='tbl_form'>
                    <tr class='head2'>
                        <td colspan='6' align='center'>No Records Found</td>
                    </tr>
                </table>";
        }
    ?>
    </div>
    <?php
        if ($action == "HAPUS") {
            $id = validTeks4($_GET['id'], 10);
            $file = getOne("SELECT filename FROM uploaded_documents WHERE id='$id'");
            if (unlink($file)) {
                Hapus("uploaded_documents", "id='$id'", "?act=List&action=TAMBAH");
            }
        }
    ?>
</div>
