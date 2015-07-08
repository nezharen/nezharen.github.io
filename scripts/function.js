function checkSize()
{
	if ($(document).width() < 800)
	{
		$("div#aside").hide();
		$("div#body").css("margin-right", "0%");
	}
	else
	{
		$("div#aside").show();
		$("div#body").css("margin-right", "21%");
	}
}

function loadIndexData(data)
{
	var n = data.index.length;
	var s;

	$("#body").empty();
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
		$("#body").append(s);
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

	$("#body").empty();
	for (var i = 0; i < n; i++)
	{
		if (data.index[i].type == "note")
		{
			s = "<div class=\"session\"><a id=\"" + data.index[i].link + "\" class=\"note-link\" href=\"#\"><h2>";
			s += data.index[i].caption + "</h2></a><p class=\"date\">" + data.index[i].date + "</p><hr />" + data.index[i].intro + "</div>";
			$("#body").append(s);
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

	$("#body").empty();
	for (var i = 0; i < n; i++)
	{
		if (data.index[i].type == "share")
		{
			s = "<div class=\"session\"><a href=\"" + data.index[i].link + "\" target=\"_blank\"><h2>";
			s += data.index[i].caption + "</h2></a><p class=\"date\">" + data.index[i].date + "</p><hr />" + data.index[i].intro + "</div>";
			$("#body").append(s);
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
	$("#body").empty();
	$("#body").append("<div class=\"session\" style=\"height:500px;\"><h1>" + data.caption + "</h1><hr />" + data.content);
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
	$("#body").empty();
	$("#body").append("<div class=\"session\"><h1>" + data.caption + "</h1><p class=\"date\">" + data.date + "</p><hr />" + data.content + "<div class=\"session\"><div id=\"disqus_thread\"></div><script type=\"text/javascript\">var disqus_shortname = 'nezharen';(function() {var dsq = document.createElement('script'); dsq.type = 'text/javascript'; dsq.async = true; dsq.src = '//' + disqus_shortname + '.disqus.com/embed.js'; (document.getElementsByTagName('head')[0] || document.getElementsByTagName('body')[0]).appendChild(dsq); })();</script><noscript>Please enable JavaScript to view the <a href=\"http://disqus.com/?ref_noscript\">comments powered by Disqus.</a></noscript><a href=\"http://disqus.com\" class=\"dsq-brlink\">comments powered by <span class=\"logo-disqus\">Disqus</span></a></div>");
}

function loadNoteJson(event)
{
	console.log($(this).attr("id"));
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

