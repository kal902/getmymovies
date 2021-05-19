<!DOCTYPE html>
<html>
<title>GETMYMOVIES</title>
<meta name="viewport">
<link rel="stylesheet" href="lib/w3.css">
<body>

<header class="w3-container w3-teal" style="width:100%">
				<div class="w3-container w3-center">
					<h1>GetMyMovies</h1>
				</div>
</header>
<?php

$path = $_GET["path"];
$id = $_GET["id"];
$movname = $_GET["name"];

$id = $id;
$port = 56321;
$ip = "localhost";

$sock = fsockopen($ip,$port)
	or die("error! could not create socket\n");

$downloadedmov = "$ ".$movname." > ".$id.PHP_EOL;
fwrite($sock, $downloadedmov)
	or die("error! could not write to the socket");
fflush($sock);

fclose($sock);
$notice ='<p>
			notification have been sent to the admin.
		  </p>';
$notice2 = '<p> if the download doesnt start click thSe link below</p>';
$download = '<a href = "'.$path.'" download>download</a>';
echo '<br>';
echo '<br>';
echo $notice;
echo $notice2;
echo $download;
?>


</body>
</html>