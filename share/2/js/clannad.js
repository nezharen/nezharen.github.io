var stageCount = 0, frameCount = 0, stageSum = 0, frameSum = 0;
var wrongSum = 0;
var rightId = -1;
var num = 0;

function analyzeSentence(data)
{
	function getData(ord, num)
	{
		switch (num)
		{
		case 0:
			return data.contents[ord].hira;
		case 1:
			return data.contents[ord].kata;
		case 2:
			return data.contents[ord].roman;
		}
	}

	function getRandomOrd()
	{
		var ans;

		while (true)
		{
			ans = parseInt(Math.random() * 50);
			if ((ans != 36) && (ans != 38) && (ans < 46))
				return ans;
		}
	}

	function checkAnswer()
	{
		if ($(this).attr("id") != (rightId + ""))
			wrongSum++;
		gotoNextFrame();
	}

	function checkChoice()
	{
		gotoFrame(stageCount + parseInt($(this).attr("id")) + 1, 0);
		$("div#character").click(gotoNextFrame);
		$("div#BackGround").click(gotoNextFrame);		
	}

	var s = "";

	if ((num >=0) && (num <= 9))
	{
		var start = num * 5;
		s += "第" + (num + 1) + "行假名为" + data.contents[start].hira + "行假名，分别为<br/>平假名：";
		for (var i = 0; i < 5; i++)
		{
			s += data.contents[start + i].hira;
			s += "(";
			s += data.contents[start + i].roman;
			s += ")　";
		}
		s += "<br/>片假名：";
		for (var i = 0; i < 5; i++)
		{
			s += data.contents[start + i].kata;
			s += "(";
			s += data.contents[start + i].roman;
			s += ")　";
		}
		$("div#conversation").html(s);
		return;
	}

	if (num == 100)
	{
		$("div#BackGround").unbind("click");
		$("div#character").unbind("click");
		var rightOrd = getRandomOrd();
		var question = parseInt(Math.random() * 3);
		var answer, t;
		while (true)
		{
			answer = parseInt(Math.random() * 3);
			if (answer != question)
				break;
		}
		rightId = parseInt(Math.random() * 3);
		s += ("与" + getData(rightOrd, question) + "对应的假名或罗马音为：<ul id=listItem>");
		for (var i = 0; i < 3; i++)
		{
			s += ("<li id=" + i + ">");
			if (rightId == i)
				s += getData(rightOrd, answer);
			else
			{
				while (true)
				{
					t = getRandomOrd();
					if (t != rightOrd)
						break;
				}
				s += getData(t, parseInt(Math.random() * 3));
			}
			s += "</li>";
		}
		s += "</ul>";
		$("div#conversation").html(s);
		$("li").click(checkAnswer);
		return;
	}

	if (num == 101)
	{
		if (wrongSum > 5)
			gotoFrame(stageCount + 1, 0);
		else
			gotoFrame(stageCount + 2, 0);
		$("div#character").click(gotoNextFrame);
		$("div#BackGround").click(gotoNextFrame);
		return;
	}

	if (num == 200)
	{
		$("div#BackGround").unbind("click");
		$("div#character").unbind("click");
		s = "<ul id=listItem><li id=0>可以亲你一下吗？</li><li id=1>可以亲你姐姐一下吗？</li><li id=2>可以帮我占卜吗？</li></ul>"
		$("div#conversation").html(s);
		$("li").click(checkChoice);
		return;
	}

	if (num == 300)
	{
		gotoFrame(stageSum - 2, 0);
		return;
	}

	if (num == 301)
	{
		gotoFrame(stageSum - 4, 0);
		return;
	}

	if (num == 400)
	{
		wrongSum = 0;
		gotoFrame(0, 0);
		$("button").addClass("enabled");
		$("button").removeAttr("disabled");
		return;
	}
}

function processData(data)
{
	stageSum = data.stages.length;
	frameSum = data.stages[stageCount].frames.length;
	if (frameCount == 0)
	{
		$("div#BackGround").css("background-image", "url(" + data.stages[stageCount].backgroundImage + ")");
		if ($("audio#music").attr("src") != data.stages[stageCount].backgroundMusic)
			$("audio#music").attr("src", data.stages[stageCount].backgroundMusic);
	}
	$("div#character").css("transition", "");
	if (data.stages[stageCount].frames[frameCount].roleImage == "")
	{
		$("div#character").fadeOut("1000");
		$("div#character").css("background-image", "none");
	}
	else
		if ($("div#character").css("background-image") == "none")
		{
			$("div#character").css("background-image", "url(" + data.stages[stageCount].frames[frameCount].roleImage + ")");
			$("div#character").fadeIn("1000");
		}
		else
		{
			$("div#character").css("transition", "background-image 1s");
			$("div#character").css("background-image", "url(" + data.stages[stageCount].frames[frameCount].roleImage + ")");
		}
	if (data.stages[stageCount].frames[frameCount].sentence == "")
		$("div#conversationBody").hide();
	else
		if ($("div#conversation").html() == "")
			$("div#conversationBody").show();
	$("div#characterName").text(data.stages[stageCount].frames[frameCount].name);
	if ((data.stages[stageCount].frames[frameCount].sentence != "") && (data.stages[stageCount].frames[frameCount].sentence.lastIndexOf("#") == (data.stages[stageCount].frames[frameCount].sentence.length - 1)))
	{
		num = parseInt(data.stages[stageCount].frames[frameCount].sentence);
		$.ajax({
			url: "json/japanese.json",
			success: analyzeSentence,
			dataType: "json"
		});
	}
	else
		$("div#conversation").html(data.stages[stageCount].frames[frameCount].sentence);
}

function gotoFrame(stage, frame)
{
	if ((stage == 5) && (frame == 0))
	{
		localStorage.pass = "true";
		$("button").removeClass("enabled");
		$("button").attr("disabled", "disabled");
	}

	stageCount = stage;
	frameCount = frame;
	$.ajax({
		url: "json/clannad.json",
		success: processData,
		dataType: "json"
	});
}

function gotoNextFrame()
{
	if ((frameCount + 1) < frameSum)
		gotoFrame(stageCount, frameCount + 1);
	else
		if ((stageCount + 1) < stageSum)
			gotoFrame(stageCount + 1, 0);
}

function preloadImage(data)
{
	for (var i = 0; i < data.stages.length; i++)
	{
		$("<img />").attr("src", data.stages[i].backgroundImage);
		for (var j = 0; j < data.stages[i].frames.length; j++)
			$("<img />").attr("src", data.stages[i].frames[j].roleImage);
	}
}

function getReady()
{
	gotoFrame(0, 0);
	$("div#character").click(gotoNextFrame);
	$("div#BackGround").click(gotoNextFrame);
	$("button").click(function (){gotoFrame(5, 0);});
	if (localStorage.pass == "true")
		$("button").addClass("enabled");
	else
		$("button").attr("disabled", "disabled");
	$.ajax({
		url: "json/clannad.json",
		success: preloadImage,
		dataType: "json"
	});
}

