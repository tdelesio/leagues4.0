//var server = "http://localhost:8080/ws/rest/admin";
var league = new String();
var week = new Number();
var playerID = new Number();
var weekmap = {};
// var template = '';
// var seasonjson = '';
function getPage() {

	// CLEAR AND LOAD
	// document.getElementById("loader-page").style.visibility = "visible";

//	var url = getUrl('/admin/valid');
//	$.ajax({
//		type : "GET",
//		url : url,
//		contentType : "application/json",
//		dataType : "json",
//		success : function(res) {
//			$("#result").text("");
//			$("#view").text("");
//			$("#subnav").text("");
//
//			var template = _.template($('#container').html());
//
//			// load the subnav
//			url = getUrl('/admin/leagues');
//			$.getJSON(url, function(json) {
//				$("#subnav").append(template({
//					data : json
//				}));
//
//				changePage();
//			});
//		},
//		error : function(res) {
//			window.location.replace('/ws/index.html');
//			
//			
//		}
//	});
	
	
	$("#result").text("");
	$("#view").text("");
	$("#subnav").text("");

	var template = _.template($('#container').html());

	// load the subnav
	//url = getUrl('/leagues');
	url = 'http://localhost:8082/leagues/'
	$.getJSON(url, function(json) {
		$("#subnav").append(template({
			data : json,
			
		}));

		changePage();
	});

}



function changePage() {
	var page = $("#page").val();

	$("#view").text("");
	switch (page) {

	// this is the make pics page
	case 'setupweek':

		$("#view").load('setupWeek.html', function() {
			
			

			
			
			var selectedSeason = $("#season").val();
			$("#seasonid").val(selectedSeason);

//			url = server + '/weeks/seasonid/' + selectedSeason;
			url = getUrl('/weeks/seasonid/' + selectedSeason);

			var weekHTML = "";
			$.getJSON(url,function(json){
				weekHTML += '<div id="week-container"><select name="Week" id="week" onchange="changeWeek();">';
				
				//loop over the weeks
				$.each(json, function(i,data){
					weekHTML += '<option value="';
					weekHTML += data.id;
					weekHTML += '"';
					if (i == 0)	
					{
						weekHTML += 'selected="selected"';
					}
					weekHTML += '>Week ';
					weekHTML += data.weekNumber;
					weekHTML += '</option>';
									
				});
				
				weekHTML += '</select></div>';
				
				//add the week select to the sub nav
				$("#weekNav").append(weekHTML);
				
				changeWeek();
			});
			
			

		});
		break;

	case 'createweek':

		$("#view").load('createWeek.html', function() {
			var selectedSeason = $("#season").val();
			$("#seasonid").val(selectedSeason);

		});
		// alert($("#view").find("#season").val());

		break;
	case 'clearcache':
		url = getUrl('/admin/cache');
		$.getJSON(url,function(json){
			
		});
		break;
	case 'creategame':

		// CRETE NEW SUBNAV

		break;
	}
}


function changeWeek() {
	
	$("#weeks").text("");
	
	//set the week variable based on the selection
	week = $("#weekNav").find("#week").val();
	
	//setup the game entry page
	var addgametemplate = _.template($('#addgame').html());
	
	// load the subnav
//	url = server + '/teams';
	url = getUrl('/teams/pickem');
	$.getJSON(url, function(json) {
		$("#weeks").append(addgametemplate({
			teams : json
		}));

		
		var today = new Date(),
        dow = today.getDay(),
        toAdd = dow === 0 ? 0 : 7 - dow,
        thisSunday = new Date(),
        dateFormat = 'mm/dd/yy';
		
		thisSunday.setDate(thisSunday.getDate()+toAdd);
		
		$( "#gsdate" )
        .val($.datepicker.formatDate(dateFormat,thisSunday))
        .datepicker({
            dateFormat: dateFormat
        });
		
		//set the hidden variable
		$("#weekid").val(week);
	});
	

	//get all the games
	var gamestemplate = _.template($('#games').html());
//	var gameUrl = server + '/games/weekid/'+week;
	var gameUrl = getUrl('/games/weekid/'+week);
	$.getJSON(gameUrl,function(json){
		
		$("#weeks").append(gamestemplate({
			games : json
		}));
		
	});
	
}

function createWeek() {
//	var url = server + "/week";
//	var token = $("meta[name='_csrf']").attr("content");
//    var header = $("meta[name='_csrf_header']").attr("content");
	var token = $("meta[name='_csrf']").attr("content");
	  if (!token) token = "";
	  var header = $("meta[name='_csrf_header']").attr("content");
	  if (!header) header = "";
	  
    alert(header);
    alert(token);
	var url = getUrl('/weeks/');
	$.ajax({
		type : "POST",
		beforeSend: function (request) {
	        request.setRequestHeader(header, token);
	     },
		url : url,
		contentType : "application/json",
		dataType : "json",
		data : $('form').serializeObject(),
		success : function(res) {
			$('#result').text(JSON.stringify(res));
		},
		error : function(res) {
			$('#result').text(JSON.stringify(res));
		}
	});
}

function addNewGame() {
	
	
	if( $("#favhome").is(":checked") == true ) {
		$("#favhome").attr('value','true');
        } else {
        	$("#favhome").prop('checked',true);
            //DONT' ITS JUST CHECK THE CHECKBOX TO SUBMIT FORM DATA    
        	$("#favhome").attr('value','false');
        }
	
//	var url = server + "/game";
	var url = getUrl('/games/');
	$.ajax({
		type : "POST",
		url : url,
		contentType : "application/json",
		dataType : "json",
		data : $('form').serializeObject(),
		success : function(res) {
			changeWeek();
//			$('#result').text(JSON.stringify(res));
		},
		error : function(res) {
			$('#result').text(JSON.stringify(res));
		}
	});
}

function updateScore(form) {
	
	var url = getUrl('/games/');
	
	$.ajax({
		type : "PUT",
		url : url,
		contentType : "application/json",
		dataType : "json",
		data : $(form).serializeObject(),
		success : function(res) {
			changeWeek();
			$('#result').text(JSON.stringify(res));
		},
		error : function(res) {
			$('#result').text(JSON.stringify(res));
		}
	});
}


function showSpan(ID) {

	// TOGGLE NAV
	for (g = 0; g < 3; g++) {
		$("#button_" + g).removeClass("selected");
		$(".sg" + g).addClass("hide");
	}

	$("#button_" + ID).addClass("selected");
	$(".sg" + ID).removeClass("hide");

}


// START THE APP

$(document).ready(function() {

	getPage();

});
