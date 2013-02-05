/*
 * middagsplanlegger.js
 * 
 * Implementasjoner:
 * 
 * Jquery UI-funksjoner
 *  - Draggable
 *  - Droppable
 * 
 * Ajax (lagring p� server) 
 * 
 * Cookies (lagring lokalt )
 * 
 *  
 * */


/*jquery event and functions (on document.ready)*/

$(function() { 

	showHistorySplash();
	
	var startIndex = getStartIndex();
	
	/* jquery bind events*/
/*
	$(".weekplanner_days").jCarouselLite({
		btnNext : ".next_planner",
		btnPrev : ".prev_planner",
		circular : false,
		start: startIndex,
		afterEnd: weekplanner_afterEndCallback
	});			
*/




	$(".cimg").click(function() {
		if($(this).children("img").attr('class') != 'dropBilde')
		{   
			$(".recipe_details_box").not($(this).siblings(".recipe_details_box")).slideUp("slow");
			$(this).siblings(".recipe_details_box").slideToggle("slow");
		}
	});
	
	$('div.delBtn').bind('click', deleteRecipe);

	/*end bind events */
	
	/* Draggable events (lar brukeren dra i en oppskrift vha mouse-events)
	 * 
	 * funksjonen under kalles n�r oppskriften velges ved � dra i :
	 * - thumbnail p� artikkeliste (selector: #recipeList .recipeItem .recipeImage) 
	 * - thumbnail p� artikkelvisning (selector: div.dtp_bilde)   
	 * */
	
	$(".recipeItem .recipeImage, div.dtp_bilde ").draggable({
		revert : 'invalid',
		opacity : 1,
		cursor : 'hand',
		cursorAt: { cursor: "move", top: -10, left: 100 },
		helper : 'clone',
		appendTo : 'div#footer',
		zIndex : 2000,
		start: fadeItOut,
		stop: fadeItIn
	}); 
	
	/* funksjonen under kalles n�r oppskriften velges ved � dra i :
	 * hovedbilde p� artikkeliste (selector: .recipeImageDraggable)  
	 * */
	
	$(".recipeImageDraggable").draggable({
		revert : 'invalid',
		opacity : 1,
		cursor : 'hand',
		cursorAt: { cursor: "move", top: 0, left: 100 },
		helper : function() { return $('div.recipeItem').html();},
		appendTo : 'div#footer',
		zIndex : 2000,
		start: fadeItOut,
		stop: fadeItIn
	});
	
	/* funksjonen under kalles n�r oppskriften velges ved � dra i :
	 * thumbnail p� historikksiden (selector: .recipeItem .thumb .cimg)  
	 * */	
	
	
	$(".recipeItem .thumb .cimg").draggable({
		revert : 'invalid',
		opacity : 1,
		cursor : 'hand',
		cursorAt: { cursor: "move", top: 0, left: 100 },
		helper : function() { return $(this).find("div").html();},
		appendTo : 'div#footer',
		zIndex : 2000,
		start: fadeItOut,
		stop: fadeItIn
	}); 
	
	/* end Draggable events*/
	
	
	/* Droppable event (lar brukeren slippe en oppskrift i spesifiserte omr�der p� web siden.)
	 * 
	 * 
	 * N�r en oppskrift er valgt, skjer f�lgende:
	 * 
	 * - Oppskriften lagres p� server og/eller lokalt, avhengig av om brukeren er logget inn eller ikke.
	 * - Det legges til verkt�knapper p� oppskriften i middagsplanleggren, med knapper for 'Se' og 'Slett'.
	 * - Div animering 
	 * 
	 * funksjonen under kalles n�r en oppskrift slippes i middagsplanlegger (selector: ul#mycarousel_days div.carouselitem_planner div.cimg)
	 * */
	
	$("ul#mycarousel_days div.carouselitem_planner div.cimg").droppable({ 
		hoverClass : 'ui-state-hover',
		drop : function(event, ui) {
			
			/* Kaller funksjon for � vise splash for historikk */
			droppableShowHistorySplash();
			
			$(this).fadeTo(300, 0.2,"swing", addRecipe);
			$('div.delBtn').bind('click', deleteRecipe);

			$(this).children(".recipe_details_btn").bind('click', function() {
				$(this).siblings(".recipe_details_box").slideToggle("slow");	
			});
			
		
			var draggedItem = $(ui.draggable);
			
			var adults = "";
			var children = "";
			
			var day=$(this).closest("li").attr('id');
			var recipeId="";
			
			
			/*Uthenting av recipeId og evt familieinstillinger 
			*
			*  - Verdier for antall voksne og antall barn settes: 
			*  	> leses fra DOM hvis det ble dratt i sliderne før oppskriften ble valgt.
			*  	> hentes fra familieinstillinger - cookie
			*  - recipeId leses fra DOM
			*  
			*  */
			
			// Thumbnail
			if ($(draggedItem).hasClass('dtp_bilde'))
				{
					if($(draggedItem).children("a").attr('adults') && $(draggedItem).children("a").attr('children'))
					{   
						adults = $(draggedItem).children("a").attr('adults');
						children = $(draggedItem).children("a").attr('children');
					    
					}
					recipeId = 	$(draggedItem).attr('id');
				}

			//Hovedbilde
			else if($(draggedItem).hasClass('recipeImageDraggable'))
			{
				if($(ui.helper).children("a").attr('adults') && $(ui.helper).children("a").attr('children'))
				{   
					adults = $(ui.helper).children("a").attr('adults');
					children = $(ui.helper).children("a").attr('children');
				    
				}
				recipeId = 	$(ui.helper).attr('id');
			}
			

			/* N�r oppskriftren velges fra historikksiden:
			 * -  Antall porsjoner leses fra DOM  
			 *  - recideId leses fra DOM
			 *  */
			
			else if($(draggedItem).hasClass('cimg'))
				   recipeId = $(ui.helper).attr('id');
			
			
			/* N�r oppskriftren velges fra artikkelliste: 
			 *  - recideId leses fra DOM
			 *  */
			
			else if($(draggedItem).hasClass('recipeImage'))
				recipeId = 	$(draggedItem).parent().attr('id');

			
			
			/* Standard familieinstillinger leses fra familiyprofile-cookie */
			
			if(adults.length == 0 && children.length == 0) {
		
				var profileCookie = jQuery.parseJSON(JSON.stringify($.JSONCookie("familyprofile")));
				
				if(profileCookie.adults && profileCookie.children) {
					adults = profileCookie.adults;
					children = profileCookie.children;
				}
				
				else {
					adults = 2;
					children = 2;
				}				
			}


			/* Lagrer valgt oppskrift p� server / lokalt*/
			$.ajax({
				
				   type: "POST",
				   url: "/menus/planrecipe",
				   data: "recipeId=" + recipeId + "&day="+day,
				   dataType: "xml",
				   success: function(xml){   
				   	  
					   var lastModified = $(xml).find("lastModified").text();
					   if(lastModified == 'na')
					   	{      
						   		lastModified = $(xml).find("timestamp").text();
						}
					   
					   $.cookie("day-" + day, null, {path:'/'});
					   var cookieStr;
					   eval('cookieStr = ' + '{"lastModified":"' + lastModified + '","articleID":"' + recipeId +  '","adults":"' + adults +  '","children":"' + children + '"}', window);
					   $.JSONCookie('day-' + day, cookieStr, {expires: 14, path: '/'});
			
				   	   if($(xml).find("lastModified").text() != 'na')
				   	   {      
					     $.cookie("dinnerPlanMaxLastModified", lastModified, { path: '/' });
				   	   }
				   }
					
			    });		
		
			
			/* addRecipe ()
			 * 
			 * funksjonen bytter ut visningen i middagsplanleggeren for en dag, ved drag&drop av oppskrift.
			 * - legger til verktøyknapper med "Se" og "Slett" knapper.
			 * 
			 * */
			function addRecipe()
			{
				var url="";
				var element="";
				$(this).children().remove();
				$(this).siblings(".recipe_details_box").remove();
				
				
				// N�r oppskirften velges fra artikkelvisningssiden
				if($(ui.draggable).hasClass('dtp_bilde') || $(ui.draggable).hasClass('recipeImageDraggable'))
				{
					element = $(ui.helper).children("a").children("img");
					url = $(ui.helper).children('a').attr('href');
				}
				
				// N�r oppskirften velges fra artikkellistesiden
				else if($(draggedItem).hasClass('recipeImage'))
				{   
					element = $(ui.helper).find(".cimg").children("a").children("img");
					url = $(ui.helper).find('.cimg').children("a").attr('href');
				}

				
				// N�r oppskirften velges fra historikksiden
				else if($(draggedItem).hasClass('cimg'))
				{   
					
					element = $(ui.helper).children("img");
					url = $(draggedItem).children("a").attr('href');
				}
                
				var newDiv = '<div style="position: absolute; display: none;" class="recipe_details_box">' + 
                			 '<div style="position:absolute;" class="viewBtn"><a href="' + url + '?day=' + $(this).closest('li').attr('id') + '">' + 
                			 '<img alt="View Recipe" style="width:50px; height:35px; margin-left:25px;" src="/public/images/view_btn.png"></a></div>' +
                			 '<div style="cursor: pointer;" class="delBtn"><img alt="View Recipe" style="width:49px; height:33x; margin-left:26px; margin-top:35px" src="/public/images/del_btn.png"></div>' +
                			 '</div>';

			
				$(this).append(element).parent().children(".cheader_day").after(newDiv);
				$(this).fadeTo(600, 1.0, "swing");	
				$('div.delBtn').bind('click', deleteRecipe);
			}/* end addRecipe()*/
		
		}
	
	}); /*end droppable-event*/
	 
    
}); /* end - jquery event and functions (on document.ready)*/

			
			
			/* droppableShowHistorySplash ()
			 *
			 * funksjonen sjekker om splashen har blitt vist før. Hvis ikke skal den vises n�.
			 * */
			function droppableShowHistorySplash()
			{
				if($.cookie("historySplash") == null)
				{
					$('.historikkSplashContainer').fadeIn('slow');
					$.cookie('historySplash', 'show',{expires: 1, path: '/'});					
				}
			}

			/* ShowHistorySplash ()
			 *
			 * funksjonen sjekker om splashen har blitt vist før. Hvis ikke skal den vises n�.
			 *
			 *
			 * */


			 function showHistorySplash()
			 {
			 	if($.cookie("historySplash") == 'show')
			 	{
			 		$('.historikkSplashContainer').fadeIn('slow');		
			 		$.cookie('historySplash', 'show',{expires: 1, path: '/'});	
			 	}
			 		
			 }

			/* click-event for lukkeknappen p� historikksplash
			 * 
			 *	N�r knappen trykkes vil man kalle p� hideHistorySplash for � skjule splashbaren og lage en cookie
			 **/

			 $('.historikkSplashContainer .lukk').click(function(){
			 	hideHistorySplash();	
			 });

			/* hideHistorySplash ()
			 *
			 * funksjonen lager en cookie som sier at splashen er lukket av bruker. Dermed blir den borte i 5 dager
			 *
			 *
			 * */
			function hideHistorySplash()
			{
				$.cookie('historySplash', 'shown', {expires: 1, path: '/'});
				$('.historikkSplashContainer').fadeOut('slow');	
			}
			


/* deleteRecipe()
 * 
 * Funksjonen kalles n�r en oppskrift fjernes fra middagsplanleggeren. Følgende gj�res da:
 * 
 * - Diverse animering for � bytte ut oppskriftsbilde med "Dra ned oppskrift" - bildet
 * - Brukerens middagsplan oppdateres p� server og/eller lokalt.
 * 
 * */

function deleteRecipe() {

	$(this).closest('.carouselitem_planner').find('.cimg').empty();
	$(this).closest('.carouselitem_planner').find('.cimg').append('<img src="/public/images/drophere.png" class="dropBilde" width="100" height="100" />');
	$(this).closest(".recipe_details_box").slideUp("slow");
	
	var day=$(this).closest("li").attr('id'); 
	
	$.ajax({
			   type: "POST",
			   url: "/menus/unplanrecipe",
			   data: "day="+day,
			   dataType: "xml",
			   success: function(xml){
	
				   var lastModified = $(xml).find("lastModified").text();
				   if(lastModified == 'na')
				   	{      
					   		lastModified = $(xml).find("timestamp").text();
					}
				
				   $.cookie("day-" + day, null, {path:'/'});
				   var cookieStr;
				   
				   eval('cookieStr = ' + '{"lastModified":"' + lastModified + '","articleID":"' + -1 +  '","day":"' + day + '"}', window);
				   $.JSONCookie('day-' + day, cookieStr, {expires: 14, path: '/'});

			   	   if($(xml).find("lastModified").text() != 'na')
			   	   {      
				     $.cookie("dinnerPlanMaxLastModified", lastModified, { path: '/' });
			   	   }
			   
			}
	    });
	
}/*end deleteRecipe ()*/


/* Funksjoner for nedtoning p� siden n�r en oppskrift dras. Nedtoningen tilbakestilles n�r oppskriften slippes. */

function fadeItOut() {
	$("#recipeList, .leftColumn").fadeTo("fast", 0.3);
} /* end fadeItOut*/

function fadeItIn() {
	$("#recipeList, .leftColumn").fadeTo("fast", 1);
} /*fadeItIn*/


function weekplanner_afterEndCallback(items) {
	var index = $(items[0]).index();
	$.cookie('carouselCookie', index);
}

function getStartIndex() {
	if ($.cookie('carouselCookie') == null) {
		return null;
	} else {
		return parseInt($.cookie('carouselCookie'), 10);
	}
}