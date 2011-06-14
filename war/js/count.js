var officeNum;
var shopNum;

var lastOfficeSound;
var lastShopSound;

function positionInit() {
	var scoresW = $('.flipboards').width() + 20;
	$('#scores').css({
		'right' : scoresW
	})
}

function eventListenners() {
	$('body').keypress(function(e) {
		var charCode = e.which;
		var charStr = String.fromCharCode(charCode);
		if (charStr == 'o') {
			officeNum++;
			var officeNumStr = officeNum + '';
			printScores("#officeNum", officeNum);
			officePlaySound();
			postScores(officeNum,shopNum);
		}

		if (charStr == 's') {
			shopNum++;
			var shopNumStr = shopNum + '';
			printScores("#shopNum", shopNum);
			shopPlaySound();
			postScores(officeNum,shopNum);
		}

		if (charStr == 'r') {
			counterReset();
		}

		if (charStr == '+' || charStr == '=') {
			var top = parseInt($('#scores').css('top'));
			top++;
			$('#scores').css({
				'top' : top
			})
		}
		if (charStr == '-' || charStr == '_') {
			top = parseInt($('#scores').css('top'));
			top--;
			$('#scores').css({
				'top' : top
			})
		}
		if (charStr == ']' || charStr == '}') {
			var right = parseInt($('#scores').css('right'));
			right--;
			$('#scores').css({
				'right' : right
			})
		}
		if (charStr == '[' || charStr == '{') {
			right = parseInt($('#scores').css('right'));
			right++;
			$('#scores').css({
				'right' : right
			})
		}

		if (charStr == "'" || charStr == '"') {
			var margin = parseInt($('#shop').css('top'));
			margin++;
			$('#shop').css({
				'top' : margin
			})
		}
		if (charStr == ';' || charStr == ':') {
			margin = parseInt($('#shop').css('top'));
			margin--;
			$('#shop').css({
				'top' : margin
			})
		}

	});
}

function printScores(el, score) {
	if (score < 100) {
		if (score < 10) {
			$(el).html('<p>' + '00' + score + '</p>');
		} else {
			$(el).html('<p>' + '0' + score + '</p>');
		}
	} else {
		$(el).html('<p>' + score + '</p>');
	}
}


function postScores(score1, score2) {
	$.post("/scores", { office: score1, shop: score2 }, function(data) {
//		console.log(data);
	});
}

function getScores() {
	$.get("/scores", function(data) {
		console.log(data);
		officeNum= data[0].office;
		shopNum= data[0].shop;
		printScores("#officeNum", officeNum);
		printScores("#shopNum", shopNum);
	});
}

function counterReset() {
	officeNum = 0;
	shopNum = 0;
	printScores("#officeNum", officeNum);
	printScores("#shopNum", shopNum);
}

function inputRefresh() {
	$("#textinput").val("");
}


function EvalSound(soundobj) {
	var thissound = document.getElementById(soundobj);
	thissound.play();
}

function EvalSoundStop(soundobj) {
	var thissound = document.getElementById(soundobj);
	thissound.pause();
	document.getElementById(soundobj).currentTime = 0;
}

function shopPlaySound() {
	if (lastOfficeSound != undefined)
		EvalSoundStop(lastOfficeSound);
	if (lastShopSound != undefined)
		EvalSoundStop(lastShopSound);

	var randomnumber = (Math.floor(Math.random() * 9)) + 1;
	var shopSound2play = 'shopAudio' + randomnumber;

	lastShopSound = shopSound2play;
	EvalSound(shopSound2play);
}

function officePlaySound() {
	if (lastShopSound != undefined)
		EvalSoundStop(lastShopSound);
	if (lastOfficeSound != undefined)
		EvalSoundStop(lastOfficeSound);

	var randomnumber = (Math.floor(Math.random() * 9)) + 1;
	var officeSound2play = 'officeAudio' + randomnumber;

	lastOfficeSound = officeSound2play;
	EvalSound(officeSound2play);
}

$(function() {
	counterReset();
	getScores();
	setInterval("getScores()", 5000);
	positionInit();
	eventListenners();
	$("#textinput").focus().click();
})
