var newsNum, newsCount, remarkPageNum, remarkPageCount, timer;

function loadNewsData(data) {
	$("img#image").attr("src", data.news[newsCount].img);
	$("img#image").fadeIn("slow");
	$("div#image-caption").text(data.news[newsCount].caption);
	$("a.image-link").attr("href", data.news[newsCount].href);
}

function processNewsData(data) {
	newsNum = data.news.length;
	$("img#image").fadeOut("slow", (function (data) {return function() {loadNewsData(data);}})(data));
}

function gotoNews(x) {
	newsCount = x;
	$.ajax({
		url: "json/news.json",
		success: processNewsData,
		dataType: "json"
	});
	clearTimeout(timer);
	timer = setTimeout("gotoNextNews()", 5000);
}

function gotoNextNews() {
	if (newsCount < (newsNum - 1))
		gotoNews(newsCount + 1);
	else
		gotoNews(0);
}

function gotoPreviousNews() {
	if (newsCount > 0)
		gotoNews(newsCount - 1);
	else
		gotoNews(newsNum - 1);
}

function processRemarkPageData(data) {
	remarkPageNum = data.remarkPages.length;
	$("#remark-links p").text("第" + (remarkPageCount + 1) + "页，共" + remarkPageNum + "页");
	$("#remark-session").empty();
	for (var i = 0; i < data.remarkPages[remarkPageCount].remarks.length; i++)
	{
		if (i > 0)
			$("#remark-session").append($("<hr />"));
		var remarkDiv = $("<div class='remark-div' />");
		remarkDiv.append($("<img class='photo' />").attr("src", data.remarkPages[remarkPageCount].remarks[i].photo));
		remarkDiv.append($("<p class='author' />").text(data.remarkPages[remarkPageCount].remarks[i].author));
		remarkDiv.append($("<p class='date' />").text(data.remarkPages[remarkPageCount].remarks[i].date));
		remarkDiv.append($("<p class='remark' />").text(data.remarkPages[remarkPageCount].remarks[i].remark));
		$("#remark-session").append(remarkDiv);
	}
}

function gotoRemarkPage(x) {
	remarkPageCount = x;
	$.ajax({
		url: "json/remark.json",
		success: processRemarkPageData,
		dataType: "json"
	});
}

function gotoNextRemarkPage() {
	if (remarkPageCount < (remarkPageNum - 1))
		gotoRemarkPage(remarkPageCount + 1);
	else
		alert("已是最后一页。");
}

function gotoPreviousRemarkPage() {
	if (remarkPageCount > 0)
		gotoRemarkPage(remarkPageCount - 1);
	else
		alert("已是第一页。");
}

function gotoLastRemarkPage() {
	if (remarkPageCount < (remarkPageNum - 1))
		gotoRemarkPage(remarkPageNum - 1);
	else
		alert("已是最后一页。");
}

function gotoFirstRemarkPage() {
	if (remarkPageCount > 0)
		gotoRemarkPage(0);
	else
		alert("已是第一页。");
}

function getReady() {
	gotoNews(0);
	gotoRemarkPage(0);
	$("a#gotoPreviousNews").click(gotoPreviousNews);
	$("a#gotoNextNews").click(gotoNextNews);
	$("a#gotoFirstRemarkPage").click(gotoFirstRemarkPage);
	$("a#gotoPreviousRemarkPage").click(gotoPreviousRemarkPage);
	$("a#gotoNextRemarkPage").click(gotoNextRemarkPage);
	$("a#gotoLastRemarkPage").click(gotoLastRemarkPage);
}

