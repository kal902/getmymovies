<!DOCTYPE html>
<html>
<title>GETMYMOVIES</title>
<meta name="viewport">
<link rel="stylesheet" href="lib/w3.css">
<body>

<?php
$username = "root";
$passwd = "";
$servername = "localhost";

//create connection
$conn = mysql_connect($servername,$username,$passwd);

//check connection
if(!$conn){
	die("connection failed: ".mysql_error());
}
//select db
mysql_select_db("getmymovie");
$name = $_GET["name"];
$mainname = $_GET["mainname"];
$id = $_GET["id"];
$tablename = $mainname.$name;
$sql = 'SELECT * FROM '.$tablename;
$datalist =mysql_query($sql,$conn);

$header = '<header class="w3-container w3-teal" style="width:100%">
				<div class="w3-container w3-center">
					<h1>GetMyMovies</h1>
				</div>
			</header>';
$header2 = '<header class="w3-container w3-sand" style="width:100%">
				<div class="w3-container w3-center">
					<h1 class="w3-wide">'.$name.'</h1>
				</div>
			</header>';

echo $header;
echo $header2;

while($row = mysql_fetch_array($datalist)){
	$movname = $row["moviename"];
	$movpath = $row["path"];
	$link = '<form action ="download.php" style="padding-left:20px:padding-right:5px;padding-top:5px;padding-bottom:5px;">
				<input type=hidden name="path" value="'.$movpath.'"/>
				<input type=hidden name="id" value="'.$id.'"/>
				<input type=hidden name="name" value="'.$movname.'"/>
				<input class="w3-btn-block w3-white w3-border w3-border-green w3-round w3-wide" type="submit" value="'.$movname.'"/>
			</form>';
	echo $link;
}
mysql_close($conn);
?>


</body>
</html>