angular.module("Skillopedia").constant("config", {
	url: "http://47.88.79.54",
	imageUrl: "http://47.88.79.54/files/image?name=",
	request: {
		"SUCCESS": "200",
		"TOKEN_INVALID": "405"
	},
	response: {
		"SUCCESS": 1
	},
	common_params: {
		invoke: "h5",
		app_sign: "123456"
	},
	interceptor: [
		"about",
		"account",
		"authenication",
		"coach",
		"comments",
		"contact",
		"coupons_expired",
		"coupons",
		"courses",
		"create_course",
		"create_step",
		"detail",
		"favourite",
		"fillinorder",
		"index",
		"list",
		"messages",
		"order",
		"order_booking",
		"order_cancel",
		"order_comment",
		"order_confirm",
		"order_finish",
		"order_management",
		"order_refund",
		"orders",
		"orders_management",
		"privacy",
		"schedule",
		"services",
		"shoppingcart",
		"steps",
		"steps_draft",
		"steps_publish",
		"support",
		"search"
	]
});