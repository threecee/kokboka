/*jQuery methods for user-interaction and eventhandler*/
$(document).ready(function() {

	var contentType = getUrlVars()["service"];
	if(contentType != null){
		if(contentType.substr(0,11) == "oppskrifter")
		{
			//stick the footer at the bottom of the page if we're on an iPad due to viewport/page bugs in mobile webkit
			if(navigator.platform == 'iPad')
			{
				$("#footer").css("position", "absolute");
				$("#footer").css("top", "1100px");

			};
		}
	}

	/* Autocomplete av checkboxer må settes til OFF  */
	$(":checkbox").attr("autocomplete", "off");

	

	//navigation
	$('#section1').click(function() {
			$('#campaignSections ul li').removeClass('active');
			$('#settingsSection ul li').removeClass('active');
			$(this).parent('li').addClass('active');
			$('.section').hide();
	        $("#section1content").fadeIn("slow");
		});
		$('#section2').click(function() {
			$('#campaignSections ul li').removeClass('active');
			$('#settingsSection ul li').removeClass('active');
			$(this).parent('li').addClass('active');
			$('.section').hide();
	        $("#section2content").fadeIn("slow");
		});
		$('#section3').click(function() {
			$('#campaignSections ul li').removeClass('active');
			$('#settingsSection ul li').removeClass('active');
			$(this).parent('li').addClass('active');
			$('.section').hide();
	        $("#section3content").fadeIn("slow");
		});

	
		/* 
   			 Ved klikk på "legg i favoritt" -knapp
   		     Gjøres et ajax-call til '/under100/setFavorite' som lagrer oppskriften som favoritt på server. 
   		
   		*/
   		
		$("#recipeToolbar a.favBtn").click(function(){
			
			var favoriserButton = $(this);

		  if ($(this).attr('class') == 'favBtn favoriserBtn')
				  {
				      $.ajax({
			              type: "GET",
			              url: "/under100/setFavorite",
			              data: "recipeId=" + $(favoriserButton).attr('id'),
			              dataType: 'xml',
			              success: function(xml) {      				
			                 if($(xml).find("status").text() == 'ok')
			                 {
			              		$(favoriserButton).removeClass('favBtn favoriserBtn').addClass('favBtn favoriserBtn_favorisert');
			                 	changeHoverText(favoriserButton, 'added')
			                 }	 
			                
			                 else if($(xml).find("status").text() == 'ikke pålogget')
			                 	alert("Du må logge inn for å legge en oppskrift i favoritter");
	
			              }
			            });
				  }
				  else
				  {
				  	$.ajax({
			              type: "GET",
			              url: "/under100/unFavorite",
			               data: "recipeId=" + $(favoriserButton).attr('id'),
			              dataType: 'xml',
			              success: function(xml) {      				
			                 if($(xml).find("status").text() == 'ok')
			                 {
			                	$(favoriserButton).removeClass('favBtn favoriserBtn_favorisert').addClass('favBtn favoriserBtn'); 
			                 	changeHoverText(favoriserButton, 'removed');
			                 }
			                 else if($(xml).find("status").text() == 'ikke pålogget')
			                 	alert("Du må logge inn for å fjerne en oppskrift fra favoritter");
			              }
			            });	
	
				  }
			  
		});
		
		/*
		* Hover på favoriserknapper
		*
		*/
		
		$('#recipeToolbar a.favBtn').hover(function(){
			if($(this).next().hasClass('inRecipe')){ //knappen vi hovrer over ligger i oppskrift.jsp artikkel
				$(this).next().css('left', '170px');
				$(this).next().css('margin-top', '35px');
			}					
			else
				$(this).next().css('left', '32px');
		});
		
		$('#recipeToolbar a.print_button').hover(function(){
			if($(this).next().hasClass('printHover')){
				$(this).next().css('left', '170px');
				$(this).next().css('margin-top', '35px');
			}
		});
		
		$('#recipeToolbar a.favBtn').mouseout(function(){
			$(this).next().css('left', '-9999px');
	
		});
	
		$('#recipeToolbar a.print_button').mouseout(function(){
			$(this).next().css('left', '-9999px');
	
		});
		
		function changeHoverText(element, status){
			if(status == 'added')
				$(element).next().text('Fjern fra favoritter');
			else
				$(element).next().text('Legg til i favoritter');	
  		}
  		
  		//Showing error message from SMS sending
  		if($('.errorMessage').length != 0)
  		{
  			var message = $('.errorMessage').text();
  			alert(message);
  		}

		//show ok message from sms sending
		if($('.okMessage').length != 0)
		{
			$("#cboxOverlay").css({opacity: 0.5}).show();
			alert("Handlelappen er sendt");
			$("#cboxOverlay").hide();
		}
	
	
	$.ajaxSetup({
		   cache: false
		 });
		 
	$("a.personvernLink").fancybox({
		'hideOnContentClick': true
	});
	
	
	if($.browser.mozilla)
	{	
		window.addEventListener("load", function(){}, false);
		window.addEventListener("unload", function(){}, false);
	}
	

		
		setInterval(function() {
			
			$.ajax({
				   type: "GET",
				   url: "/under100/checkstatus",
				   data: "profileLastmodified=" + jQuery.parseJSON(JSON.stringify($.JSONCookie("familyprofile"))).lastModified,
				   dataType: "xml",
				   success: function(xml){   
		
					   var status = $(xml).find("status").text();
					   var dbMaxLastModified = $(xml).find("dbMaxLastModified").text();
					   
					   if(status == 'doSynch')
						   {
						  
						   	$.get("/under100/dinnerplan", function(data) {
						   		$('#middagsplanlegger').fadeTo(300, 0.2,"swing", function(){ 
						   		
						   			$('#middagsplanlegger').empty().append(data, loadContent());
						   			
						   		});
						   	});
						   	
						   	
							   $.cookie('familyprofile', null, {path:'/'});
							   var cookieStr;
							   eval('cookieStr = ' + '{"lastModified":"' + $(xml).find('familyprofile-lastmodified').text() + '","adults":"' + $(xml).find('familyprofile-adults').text() + '","children":"' + $(xml).find('familyprofile-children').text() + '"}', window);
							   $.JSONCookie('familyprofile', cookieStr, {expires: 10000, path: '/'});
					
						  }

					  
					   if(dbMaxLastModified != '')
					   {

		     		        $.cookie("dinnerPlanMaxLastModified", dbMaxLastModified, { path: '/' });
					   }
					   
				   }
			    });
		}, 30000);


	function loadContent()
	{
		$.getScript('/under100/js/jcarousellite_1.0.1.min.js' , function(){
			
			$.getScript('/under100/js/middagsplanlegger.js');
			$('#middagsplanlegger').fadeTo(1200, 1.0, "swing");
		});
	}
	
});

function getUrlVars() {
	var vars = {};
	var parts = window.location.href.replace(/[?&]+([^=&]+)=([^&]*)/gi, function(m,key,value) {
		vars[key] = value;
	});
	return vars;
}

function getWeekStartStop(startdate){
	var startDay = 1 //0=sunday, 1=monday etc.
	var d = startdate.getDay(); //get the day
	var weekStart = new Date(startdate.valueOf() - (d<=0 ? 7-startDay:d-startDay)*86400000); //rewind to start day
	var weekEnd = new Date(weekStart.valueOf() + 6*86400000); //add 6 days to get last day
	document.write(weekStart.format("d.m.yyyy") + " - " + weekEnd.format("d.m.yyyy"));
}



function weekOrganizer(){
	var plannedWeeks=new Array();
	
	$('#rightColumn .weekAndDate').each(function(index) {
    	
    	var week = $(this).attr('id').substring(4);
    	
    	if(plannedWeeks[week] > 0){
       		$(this).remove();	
    	}
    	else
    	{
    		plannedWeeks[week] = 1;
    		
    	}
    		
    		
	});
}




function validateEmail(email) {
	var re = /^(([^<>()[\]\\.,;:\s@\"]+(\.[^<>()[\]\\.,;:\s@\"]+)*)|(\".+\"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/
	return re.test(email);
 } 
 
 
 function toggleCampaignArchive(id){
	var e = document.getElementById(id);
	
	if ($(e).is(":hidden")) {
		
	/*@cc_on
   		@if (@_jscript_version == 5.7)
      		$(e).show();
      @else @*/
      	$(e).slideDown("slow");
   	/*@end
	@*/		
	
	} else {
		$(e).slideUp();
	}
 	
 }

 function getURLParam(srcUrl)
	{
	    var vars = [], hash;
	    var hashes = srcUrl.slice(srcUrl.indexOf('?') + 1).split('&');
	    for(var i = 0; i < hashes.length; i++)
	    {
	        hash = hashes[i].split('=');
	        vars.push(hash[0]);
	        vars[hash[0]] = hash[1];
	    }
	    
	    return vars;
	} 
 
 



