function _browser() {
	return {
		// Opera 8.0+
		isOpera: (!!window.opr && !!opr.addons) || !!window.opera || navigator.userAgent.indexOf(' OPR/') >= 0,
		// Firefox 1.0+
		isFirefox: typeof InstallTrigger !== 'undefined',
		// At least Safari 3+: "[object HTMLElementConstructor]"
		isSafari: Object.prototype.toString.call(window.HTMLElement).indexOf('Constructor') > 0,
		// Internet Explorer 6-11
		isIE: /*@cc_on!@*/ false || !!document.documentMode,
		// Edge 20+
		isEdge: !isIE && !!window.StyleMedia,
		// Chrome 1+
		isChrome: !!window.chrome && !!window.chrome.webstore,
		// Blink engine detection
		isBlink: (isChrome || isOpera) && !!window.CSS,
	}
}