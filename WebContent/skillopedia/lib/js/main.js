function open_dropdown() {
	$(".dropdown").siblings(".dropdown-cover").show();
	$(".dropdown").addClass("animated");
}

function close_dropdown() {
	$(".dropdown").removeClass("animated");
	$(".dropdown").siblings(".dropdown-cover").hide();
}


function open_left_feature(e) {
	var target = e.currentTarget;
	$(target).addClass("active");
	$(target).parents(".feature-section").addClass("active-left")
}

function close_left(e) {
	e.preventDefault();
	e.stopPropagation();
	var target = e.currentTarget;
	$(target).parents(".feature-left").removeClass("active");
	$(target).parents(".feature-section").removeClass("active-left")
}


function open_right_feature(e) {
	var target = e.currentTarget;
	$(target).addClass("active");
	$(target).parents(".feature-section").addClass("active-right")
}

function close_right(e) {
	e.preventDefault();
	e.stopPropagation();
	var target = e.currentTarget;
	$(target).parents(".feature-right").removeClass("active");
	$(target).parents(".feature-section").removeClass("active-right")
}


$(document).ready(function() {
	$(".nav-btn").live("click", open_dropdown);
	$(".dropdown-cover").live("click", close_dropdown);
	$(".feature-left").hover(function() {
		$(this).parent().addClass("hover-left")
	}, function() {
		$(this).parent().removeClass("hover-left");
	});
	// $(".feature-left").bind("click", open_left_feature);
	// $(".feature-right").bind("click", open_right_feature);
	// $(".close_left").bind("click", close_left);
	// $(".close_right").bind("click", close_right);
	$(".feature-right").hover(function() {
		$(this).parent().addClass("hover-right")
	}, function() {
		$(this).parent().removeClass("hover-right")
	});
	$(document).magnificPopup({
		delegate: '.image-gallaxy a', // the selector for gallery item
		type: 'image',
		gallery: {
			enabled: true
		}
	});
})