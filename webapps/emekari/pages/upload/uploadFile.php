<?php
// Pastikan permintaan datang melalui POST dan berisi file
if ($_SERVER['REQUEST_METHOD'] == 'POST' && isset($_FILES['file'])) {
    // Tentukan direktori tempat file akan disimpan, pastikan direktori ini memiliki izin tulis (chmod 777 jika di Linux/MacOS)
    $uploadDirectory = __DIR__ . '/uploads/';
    
    // Buat direktori jika belum ada
    if (!is_dir($uploadDirectory)) {
        mkdir($uploadDirectory, 0777, true);
    }

    // Tentukan jalur lengkap file
    $filePath = $uploadDirectory . basename($_FILES['file']['name']);
    
    // Pindahkan file yang diunggah ke direktori tujuan
    if (move_uploaded_file($_FILES['file']['tmp_name'], $filePath)) {
        echo json_encode([
            "status" => "success",
            "message" => "File berhasil diunggah",
            "filePath" => $filePath
        ]);
    } else {
        http_response_code(500);
        echo json_encode([
            "status" => "error",
            "message" => "Gagal mengunggah file"
        ]);
    }
} else {
    // Jika permintaan tidak valid, kirim respons 400
    http_response_code(400);
    echo json_encode([
        "status" => "error",
        "message" => "Permintaan tidak valid"
    ]);
}
?>
