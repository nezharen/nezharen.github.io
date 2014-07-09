var newsNum, newsCount, remarkPageNum, remarkPageCount, timer;

function processNewsData(data) {
	newsNum = data.news.length;
	$("#image").attr("src", data.news[newsCount].img);
}

function gotoNews(x) {
	newsCount = x;
	$.ajax({
		url: "json/news.json",
		success: processNewsData,
		dataType: "json"
	});
	clearTimeout(timer);
	timer = setTimeout("gotoNextNews()", 3000);
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
		gotoRemarkPage(0);
}

function gotoPreviousRemarkPage() {
	if (remarkPageCount > 0)
		gotoNews(remarkPageCount - 1);
	else
		gotoNews(remarkPageNum - 1);
}

function getReady() {
	gotoNews(0);
	gotoRemarkPage(0);
}

