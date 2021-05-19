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

$vidplayer = '<div style="padding-top:50px;padding-left:20px">

			   <video style="width=80%;height:448" controls>
				<source src="'.$path.'" type="video/mp4">
				the browser does not support the video tag
			  </video>
			  
			  </div>';

echo $vidplayer
?>

</body>
</html>