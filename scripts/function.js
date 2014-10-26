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

function getReady()
{
	$(window).resize(checkSize);
	checkSize();
}

