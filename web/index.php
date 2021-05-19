<!DOCTYPE html>
<html>
<title>GETMYMOVIES</title>
<meta name="viewport">
<link rel="stylesheet" href="lib/w3.css">
<body>

<?php
//get id from java server
$port = 56321;
$ip = "localhost";
$sock = fsockopen($ip,$port)
	or die("error! could not create socket\n");
$id = "getmymovie".PHP_EOL;
fwrite($sock, $id)
	or die("failed to write to the socket\n");
$newid = fread($sock,1024)
	or die("failed to read from the socket\n");
$username = "root";
$passwd = "";
$servername = "localhost";
fclose($sock);
//create connection
$conn = mysql_connect($servername,$username,$passwd);

//check connection
if(!$conn){
	die("connection failed: ".mysql_error());
}
//select db
mysql_select_db("getmymovie");
$sql = "SELECT * FROM category";
$retval=mysql_query($sql,$conn);
if(!$retval){
	die("could not get data: ".mysql_error());
}
$a = '<nav class="w3-sidenav w3-white w3-card-12 w3-animate-left" style="display:none;width:35%">

		<a href="javascript:void(0)" onclick="w3_close()" class="w3-closenav w3-xxlarge w3-green">Close &times;</a>

		<div class="w3-container w3-light-green">
			<h2 style="padding-left:11.5%">Main Categories</h2>
		</div>

		<br>
		<br>';
echo $a;
while($row=mysql_fetch_array($retval)){
	$name=$row["name"];
	$viewtype=$row["viewtype"];
	$link = "";
	if($viewtype=="Gridview"){
		$link = '<form action ="subcatsgridview.php" style="padding-left:5px:padding-right:5px;padding-top:5px;padding-bottom:5px;">
					<input type=hidden name="name" value="'.$name.'"/>
					<input type=hidden name="id" value="'.$newid.'"/>
					<input class="w3-btn-block w3-white w3-border w3-border-green w3-round w3-wide" type="submit" value="'.$name.'"/>
				 </form>';
	}
	else{
		$link = '<form action ="subcatslistview.php" style="padding-left:5px:padding-right:5px;padding-top:5px;padding-bottom:5px;">
					<input type=hidden name="name" value="'.$name.'"/>
					<input type=hidden name="id" value="'.$newid.'"/>
					<input class="w3-btn-block w3-white w3-border w3-border-green w3-round w3-wide" type="submit" value="'.$name.'"/>
				</form>';

	}
	
	echo $link;
}
$b = '</nav>';
echo $b;

$style = '<style>
table, th, td {
    padding: 10px;
}
table {
    border-spacing: 5px;
}
</style>';

$header = '<header class="w3-container w3-green" style="width:100%">

			<span class="w3-opennav w3-xxxlarge" onclick="w3_open()">&#9776;</span>
			<div class="w3-container w3-center">
				<h1>GetMyMovies</h1>
				<h4>'.$newid.'</h4>
			</div>

		   </header>';
echo $style;
echo $header;

$homepagedataquery ="SELECT * FROM homepage";
$data=mysql_query($homepagedataquery,$conn);
if(!$retval){
	die("could not get data: ".mysql_error());
}
$startrow = '<tr>';
$endrow = '</tr>';
$tablestart = '<table>';
$tableend = '</table>';
$columncount = 0;
echo $tablestart;
echo $startrow;
while($row = mysql_fetch_array($data)){
	$imgpath = $row['image'];
	$movpath = $row['moviepath'];
	$movname = $row['movname'];
	$displayimage = '<td>
			<div class="w3-card-4">
				<img src="data:image/jpeg;base64,'.base64_encode($imgpath).'" alt="deadsound" style="width:100%;height:350px;">

				<div class="w3-container">

				<div style="float:left">
					<p>'.$movname.'</p>
				</div>

				<div style="float:right;padding-left:10px">
					<form action ="stream.php" style="padding-left:5px:padding-right:5px;padding-top:5px;padding-bottom:5px;">
						<input type=hidden name="path" value="'.$movpath.'"/>
						<input type=hidden name="id" value="'.$newid.'"/>
						<input type=hidden name="name" value="'.$movname.'"/>
						<input style="width:40px;height:40px" type="image" src="lib\res\play.png"/>
					</form>
				</div>

				<div style="width:40px;float:right;padding-top:5px">
				<form action ="download.php" style="padding-left:5px:padding-right:5px;padding-top:5px;padding-bottom:5px;">
					<input type=hidden name="path" value="'.$movpath.'"/>
					<input type=hidden name="id" value="'.$newid.'"/>
					<input type=hidden name="name" value="'.$movname.'"/>
					<input style="width:40px;height:40px" type="image" src="lib\res\down.png"/>
				</form>
				</div>

				</div>

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



<script>
function w3_open() {
    document.getElementsByClassName("w3-sidenav")[0].style.display = "block";
}
function w3_close() {
    document.getElementsByClassName("w3-sidenav")[0].style.display = "none";
}
</script>


</body>
</html>