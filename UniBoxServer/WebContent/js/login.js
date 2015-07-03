$(document).ready(function() {
	var handle = params("handle");
	if (handle != "") {
		switch (handle) {
		case "invalid_credentials":
			swal("Ups..", "Wrong username and password!", "error");
			break;
		case "logout":
			swal("Good job!", "You are logged out!", "success");
			break;
		}
	}
	$("#loginForm").submit(function() {
		$("#password").val(Base64.encode($("#passwordHolder").val()));
		return true;
	});
});

function params(val) {
	var result = "Not found", tmp = [];
	var items = location.search.substr(1).split("&");
	for (var index = 0; index < items.length; index++) {
		tmp = items[index].split("=");
		if (tmp[0] === val)
			result = decodeURIComponent(tmp[1]);
	}
	return result;
};
