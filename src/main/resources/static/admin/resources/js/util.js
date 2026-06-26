var server = "http://localhost:8081";	
//var server = "http://makeurpicks.com/ws/rest";
var server = ""
$.fn.serializeObject = function() {

	// convert the form to an array
	var a = this.serializeArray();

	var arr = {};
	// loop through all the form elements
	$.each(a, function() {
		// check to see if there is a . in the name
		if (this.name.indexOf(".") != -1) {
			// there is a doc, so we need to create some objects

			// convert the name into an array
			var b = this.name.split('.');

			// pull off the version item in the array
			var name = b.shift();

			// recurisvly call the method and repeat
			arr[name] = parseName({}, b, this.value);
		} else {
			// set the object value
			arr[this.name] = this.value;
		}

	});

	arr = JSON.stringify(arr);
	return arr;
};

function parseName(r, name, value) {

	if (name.length == 1) {
		// no ., so set the name to the value
		var key = name.shift();

		// create a new object and set the value
		var arr = {};
		arr[key] = value;

		// return the newly created object
		return arr;
	} else {
		var key = name.shift();
		var v = parseName(r, name, value);
		r[key] = v;

		return r;
	}
}

function getURLParameter(name) {
	return decodeURI(
	    (RegExp(name + '=' + '(.+?)(&|$)').exec(location.search)||[,null])[1]
	    );
	}

function getUrl(context)
{
	var url="";
	if (localStorage['tgt'])
	{
		tgt = localStorage['tgt'];
		if (tgt == null)
		{
			window.location.replace('login.html');
		}
		url = server + context +'?tgt='+tgt;
	}
	else
	{
		url = server + context;
	}
	return url;
}



