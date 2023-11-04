<?php 
/* getId
** Usage  : Generate random unique ID
** Author : Pak Bambang
*/
function getId() {
    return str_replace('.', '', uniqid(rand(100,999),true));
}

# database
define("dsn","mysql:host=localhost;dbname=android");
define("user","root");
define("passwd","root");

$pdo    = new PDO(dsn, user, passwd);

if ($_SERVER['REQUEST_METHOD'] == 'POST') {
	$name = isset($_POST['name']) ? $_POST['name'] : '';
	$address = isset($_POST['address']) ? $_POST['address'] : '';
	$image1 = isset($_POST['image1']) ? $_POST['image1'] : '';
	$image2 = isset($_POST['image2']) ? $_POST['image2'] : '';
	$image3 = isset($_POST['image3']) ? $_POST['image3'] : '';
	$file1 = 'img/'.getId().'.jpg'; // img menandakan gambar akan disimpan dalam folder img
	$file2 = 'img/'.getId().'.jpg';
	$file3 = 'img/'.getId().'.jpg';

	// cek jika gambar tidak kosong, maka simpan gambar
	if($image1!=''){
		file_put_contents($file1, base64_decode($image1));
	}
	if($image2!=''){
  		file_put_contents($file2, base64_decode($image2));
  	}
  	if($image3!=''){
	  	file_put_contents($file3, base64_decode($image3));
	  }

	// simpan path image ke tabel user bersama kolom name dan address
	$query = "INSERT INTO user(name,address,image1,image2,image3) VALUES(?,?,?,?,?)";
	$s1    = $pdo->prepare($query);
  	$s1->execute([$name,$address,$file1,$file2,$file3]);
}
?>