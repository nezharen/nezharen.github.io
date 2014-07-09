var newsCount = 1;

function processData(data) {
	$("#image-session").css("background-image", data.img);
}

function gotoNews(x) {
	$.ajax({
		url: "json/" + x + ".json",
		success: processData,
		dataType: "json"
	})
}

