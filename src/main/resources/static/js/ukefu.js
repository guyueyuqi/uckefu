
var layer , iframe , layerwin , cursession ;
$(document).ready(function(){	
	layui.use(['layer'], function(){
		layer = layui.layer;	 	 
	});
	$(document).on('click','[data-toggle="ajax"]', function ( e ) {
		var url = $(this).attr("href");
		var title = $(this).attr("title") ? $(this).attr("title") : $(this).attr("data-title");
		var artwidth = $(this).attr("data-width") ? $(this).attr("data-width") : 800 ;
		var artheight = $(this).attr("data-height") ? $(this).attr("data-height") : 400 ;
		top.iframe = window.frameElement && window.frameElement.id || '';
		$.ajax({
			url:url,
			cache:false,
			success: function(data){
				top.layerwin = top.layer.open({title:title, type: 1, area:[artwidth+"px" , artheight+"px"] ,content: data});
			}
		});
		
		return false;
	});
	
	$(document).on('click','[data-toggle="load"]', function ( e ) {
		var url = $(this).attr("href");
		var target = $(this).data("target");
		$.ajax({
			url:url,
			cache:false,
			success: function(data){
				$(target).empty().html(data);
			}
		});
		
		return false;
	});
	
	$(document).on('click','[data-toggle="tip"]', function ( e ) {
		var title = $(this).attr("title") ? $(this).attr("title") : $(this).attr("data-title");
		var href = 	$(this).attr('href') ;
		top.layer.confirm(title, {icon: 3, title:'提示'}, function(index){
			top.layer.close(index);
			location.href = href ;
		});
		return false;
	});
	$(document).on('submit.form.data-api','form', function ( e ) {
		var formValue = $(e.target) ;
		if(iframe){
			$(e.target).attr('target' , iframe);
		}
		if(layerwin){
			layer.close(layerwin);
		}
	});
});

function loadURL(url , panel , callback  , append){
	loadURLWithTip(url  , panel , callback , append , false) ;
}

function loadURLWithTip(url , panel , callback , append  , tip){
	$.ajax({
		url:url,
		cache:false,
		success: function(data){
			if(panel){
				if(append){
					$(panel).append(data);
				}else{
					$(panel).empty().html(data);
				}
			}
			if(callback){
				callback(data);			
			}
		},
		error:  function(xhr, type, s){	
			if(xhr.getResponseHeader("emsg")){
				art.alert(xhr.getResponseHeader("emsg"));
			}
		}
	}).done(function(){
		
	});
}
