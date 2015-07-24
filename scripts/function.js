function checkSize()
{
	if ($(document).width() < 800)
	{
		$("#aside").hide();
		$("#body").css("margin-right", "0%");
	}
	else
	{
		$("#aside").show();
		$("#body").css("margin-right", "21%");
	}
}

function loadIndexData(data)
{
	var n = data.index.length;
	var s;

	$("#body-content").empty();
	for (var i = 0; i < n; i++)
	{
		switch (data.index[i].type)
		{
		case "note":
			s = "<div class=\"session\"><a id=\"" + data.index[i].link + "\" class=\"note-link\" href=\"#\"><h2>";
			s += "【日志】";
			break;
		case "share":
			s = "<div class=\"session\"><a href=\"" + data.index[i].link + "\" target=\"_blank\"><h2>";
			s += "【分享】";
			break;
		case "about":
			s = "<div class=\"session\"><a class=\"about-link\" href=\"#\"><h2>";
			s += "【关于】";
			break;
		}
		s += data.index[i].caption + "</h2></a><p class=\"date\">" + data.index[i].date + "</p><hr />" + data.index[i].intro + "</div>";
		$("#body-content").append(s);
		if (data.index[i].type == "about")
			$(".about-link").click(loadAbout);
		else
			if (data.index[i].type == "note")
				$(".note-link").click(loadNoteJson);
	}
}

function loadIndex()
{
	$(".index").attr("id", "current-header-link");
	$(".note").removeAttr("id");
	$(".share").removeAttr("id");
	$(".about").removeAttr("id");
	$.ajax({
		url: "jsons/index.json",
		success: loadIndexData,
		dataType: "json"
	});
}

function loadNoteData(data)
{
	var n = data.index.length;
	var s;

	$("#body-content").empty();
	for (var i = 0; i < n; i++)
	{
		if (data.index[i].type == "note")
		{
			s = "<div class=\"session\"><a id=\"" + data.index[i].link + "\" class=\"note-link\" href=\"#\"><h2>";
			s += data.index[i].caption + "</h2></a><p class=\"date\">" + data.index[i].date + "</p><hr />" + data.index[i].intro + "</div>";
			$("#body-content").append(s);
			$(".note-link").click(loadNoteJson);
		}
	}
}

function loadNote()
{
	$(".note").attr("id", "current-header-link");
	$(".index").removeAttr("id");
	$(".share").removeAttr("id");
	$(".about").removeAttr("id");
	$.ajax({
		url: "jsons/index.json",
		success: loadNoteData,
		dataType: "json"
	});
}

function loadShareData(data)
{
	var n = data.index.length;
	var s;

	$("#body-content").empty();
	for (var i = 0; i < n; i++)
	{
		if (data.index[i].type == "share")
		{
			s = "<div class=\"session\"><a href=\"" + data.index[i].link + "\" target=\"_blank\"><h2>";
			s += data.index[i].caption + "</h2></a><p class=\"date\">" + data.index[i].date + "</p><hr />" + data.index[i].intro + "</div>";
			$("#body-content").append(s);
		}
	}
}

function loadShare()
{
	$(".share").attr("id", "current-header-link");
	$(".index").removeAttr("id");
	$(".note").removeAttr("id");
	$(".about").removeAttr("id");
	$.ajax({
		url: "jsons/index.json",
		success: loadShareData,
		dataType: "json"
	});
}

function loadAboutData(data)
{
	$("#body-content").empty();
	$("#body-content").append("<div class=\"session\" style=\"height:500px;\"><h1>" + data.caption + "</h1><hr />" + data.content);
}

function loadAbout()
{
	$(".about").attr("id", "current-header-link");
	$(".index").removeAttr("id");
	$(".note").removeAttr("id");
	$(".share").removeAttr("id");
	$.ajax({
		url: "jsons/about.json",
		success: loadAboutData,
		dataType: "json"
	});
}

function loadNoteJsonData(data)
{
	$("#body-content").empty();
	$("#body-content").append("<div class=\"session\"><h1>" + data.caption + "</h1><p class=\"date\">" + data.date + "</p><hr />" + data.content);
}

function loadNoteJson(event)
{
	$(".note").attr("id", "current-header-link");
	$(".index").removeAttr("id");
	$(".share").removeAttr("id");
	$(".about").removeAttr("id");
	$.ajax({
		url: $(this).attr("id"),
		success: loadNoteJsonData,
		dataType: "json"
	});
}

function getReady()
{
	$(window).resize(checkSize);
	checkSize();
	$(".index").click(loadIndex);
	$(".note").click(loadNote);
	$(".share").click(loadShare);
	$(".about").click(loadAbout);
	loadIndex();
}

