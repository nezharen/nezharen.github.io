var newsNum = 0, newsCount = 0, remarkPageNum = 0, remarkPageCount = 0, timer;

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
	remarkPageNum = Math.ceil(data.remarks.length / 10);
	$("#remark-links p").text("第" + (remarkPageCount + 1) + "页，共" + remarkPageNum + "页");
	$("#remark-session").empty();
	for (var i = remarkPageCount * 10; (i < (remarkPageCount + 1) * 10) && (i < data.remarks.length); i++) {
		if (i > remarkPageCount * 10)
			$("#remark-session").append($("<hr />"));
		var remarkDiv = $("<div class='remark-div' />");
		remarkDiv.append($("<img class='photo' />").attr("src", data.remarks[i].photo));
		remarkDiv.append($("<p class='author' />").text(data.remarks[i].author));
		remarkDiv.append($("<p class='date' />").text(data.remarks[i].date));
		remarkDiv.append($("<p class='remark' />").text(data.remarks[i].remark));
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

function getCookie() {
	var c_start = document.cookie.indexOf("lastViewLink=");
	if (c_start != -1) {
		c_start = c_start + 13;
		var c_end = document.cookie.indexOf(";", c_start);
		if (c_end == -1)
			c_end = document.cookie.length;
		return unescape(document.cookie.substring(c_start, c_end));
	}
	return "";
}

function setCookie() {
	var exdate = new Date();
	exdate.setDate(exdate.getDate() + 365);
	document.cookie = "lastViewLink=" + escape($("a.image-link").attr("href")) + ";expires=" + exdate.toGMTString()
}

function checkCookie() {
	var s = getCookie();
	if ((s != null) && (s != ""))
		console.log("lastViewLink=" + s);
}

function getReady() {
	$("a#gotoPreviousNews").click(gotoPreviousNews);
	$("a#gotoNextNews").click(gotoNextNews);
	$("a#gotoFirstRemarkPage").click(gotoFirstRemarkPage);
	$("a#gotoPreviousRemarkPage").click(gotoPreviousRemarkPage);
	$("a#gotoNextRemarkPage").click(gotoNextRemarkPage);
	$("a#gotoLastRemarkPage").click(gotoLastRemarkPage);
	$("a.image-link").click(setCookie);
	gotoNews(0);
	gotoRemarkPage(0);
	checkCookie();
}

