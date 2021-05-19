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
$mainname = $_GET["name"];
$id = $_GET["id"];
$header = '<header class="w3-container w3-teal" style="width:100%">
				<div class="w3-container w3-center">
					<h1>GetMyMovies</h1>
				</div>
			</header>';
$header2 = '<header class="w3-container w3-sand" style="width:100%">
				<div class="w3-container w3-center">
					<h1 class="w3-wide">'.$mainname.'</h1>
				</div>
			</header>';
$style = '<style>
			 table, th, td {
   			 padding: 10px;
			 }
			 table {
    			border-spacing: 5px;
			 } 
		  </style>';
echo $header;
echo $header2;
echo $style;
//fetch the subcategories 

$tablename = 'subcats_'.$mainname;
$sql = 'SELECT * FROM '.$tablename;
$subcats = mysql_query($sql,$conn);

$startrow = '<tr>';
$endrow = '</tr>';
$tablestart = '<table>';
$tableend = '</table>';
$columncount = 0;
echo $tablestart;
echo $startrow;
while($row = mysql_fetch_array($subcats)){
	$image = $row['image'];
	$movname = $row['name'];
	$displayimage = '<td>
			<div class="w3-card-4">
				<img src="data:image/jpeg;base64,'.base64_encode($image).'" alt="deadsound" style="width:100%;height:300px;">

				<form action ="list.php" style="padding-left:5px:padding-right:5px;padding-top:5px;padding-bottom:5px;">
					<input type=hidden name="name" value="'.$movname.'"/>
					<input type=hidden name="mainname" value="'.$mainname.'"/>
					<input type=hidden name="id" value="'.$id.'"/>
					<input class="w3-btn-block w3-white w3-border w3-border-green w3-round w3-wide" type="submit" value="'.$movname.'"/>
				</form>
			</div>
		</td>';
	if($columncount!=3){
		
		echo $displayimage;
		$columncount = $columncount + 1;
	}
	else{
		$columncount = 0;
		echo $endrow;
		echo $startrow;
		echo $displayimage;
		$columncount = $columncount + 1;
	}
}
echo $endrow;
echo $tableend;

mysql_close($conn);
?>

</body>
</html>