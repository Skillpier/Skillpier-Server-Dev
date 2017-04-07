//	resize example boxes
$(function() {
	var $b = $('body'),
		$w = $('#wrapper'),
		$d = $('.divider');
	
	var leftPost = false,
		width = false;
	
	$(window).on(
		'resize',
		function()
		{
			leftPos = $w.offset().left;
			width = $w.width();

			$d.trigger( 'mousemove', [ true ] )
		}
	).trigger( 'resize' );

	$d.each(
		function()
		{
			var $d = $(this),
				$e = $d.closest( '.example' ),
				$w = $e.find( '.w' ),
				$o = $e.find( '.wo' );

			var $emw = $w.find( 'em' ),
				$emo = $o.find( 'em' );

			var dragging = false,
				mousePos = false;

			$d.on(
				'mousedown',
				function( e )
				{
					dragging = true;
				}
			);
			$b.on(
				'mousemove',
				function( e, force )
				{
					if ( dragging || force )
					{
						e.preventDefault();
						if ( e.pageX )
						{
							mousePos = e.pageX;
						}
						if ( mousePos && leftPos && width )
						{
							var perc = Math.round( Math.max( 20, Math.min( 80, ( mousePos - leftPos ) * 100 / width ) ) );
							$w.css( 'width', perc + '%' );
							$o.css( 'width', ( 100 - perc ) + '%' );
							$d.css( 'left', perc + '%' );
							
							$emw.css( 'right', ( 100 - perc ) + '%' );
							$emo.css( 'left', perc + '%' );
						}
					}
				}
			);
			$b.on(
				'mouseup mouseleave',
				function( e )
				{
					dragging = false;
				}
			);
		}
	);
});

//	scroll through page
$(function() {
	var $nav = $('nav'),
		$subs = $($('section').get().reverse());

	var $window = $(window),
		$html = $( 'html, body' );

	$nav.find( 'a' ).on(
		'click',
		function( e )
		{
			e.preventDefault();
			$html.animate({
				scrollTop: $($(this).attr( 'href' )).offset().top
			}, 800);
		}
	);

	$window.bind(
		'scroll',
		function( e )
		{
			var st = $(window).scrollTop();
			for ( var a = 0; a < $subs.length; a++ )
			{
				if ( st > $subs.eq( a ).offset().top - 75)
				{
					$nav.find( 'li' ).removeClass( 'selected' );
					$nav.find( 'a[href="#' + $subs.eq( a ).attr( 'id' ) + '"]' ).parent().addClass( 'selected' )
					break;
				}
			}
		}
	);
});

//	email link
$(function() {
	var i = 'info',
		f = 'frebsite',
		n = 'nl';

	$('#email').attr( 'href', 'mailto:' + i + '@' + f + '.' + n );
});