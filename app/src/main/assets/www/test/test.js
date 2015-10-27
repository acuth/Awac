
var pageNames = ['simple','refresh','dialogs','storage','actions','pages','sideways','colors','backtask','custom'];
var pageTitles = ['Adding Web Pages','Supporting Refresh','Dialogs and Alerts','App Storage','Adding Actions','Opening Pages',
'Replacing Pages','Setting Colors','Background Page','Do It Yourself'];

function openFooterBlurb() {
  $('#details-blurb').show(400);
  $('#details-more').hide();
  //setTimeout(function() { $('html, body').animate({scrollTop:$(document).height()}, 'slow'); },400);
}

function loadNavigation() {
  if (!_awac) return;
  var e = document.getElementById("navigation");
  if (!e) return;
  e = $(e);
  e.addClass('holder');
  var i = getPageIndex(_awac.getPageTag());
  if (i > 0)
    $(document.createElement('div')).addClass('left small btn lite').html('PREV').click(showPrevPage).appendTo(e);
  if (i < pageNames.length-1)  
    $(document.createElement('div')).addClass('right small btn lite').html('NEXT').click(showNextPage).appendTo(e);
  $(document.createElement('div')).css('clear','both').appendTo(e);
}

function loadFooter(logging) {
  var href = window.location.href;
  var i = href.indexOf('?rnd=');
  if (i != -1) href = href.substring(0,i);
  i = href.indexOf('&rnd=');
  if (i != -1) href = href.substring(0,i);
  var html = '<div class="link">Loaded from <a target="_blank" href="'+href+'">'+href+'</a></div>'+
            '<div class="more"><a id="details-more" href="javascript:openFooterBlurb();">more...</a></div>'+
            '<div style="clear:both;"></div>';
  document.getElementById('details-footer').innerHTML = html;
  
  loadNavigation();
  
  var index = _awac ? getPageIndex(_awac.getPageTag()) : -1;
  if (index != -1) _awac.setTitle(pageTitles[index]);
  
  if (logging) addPLogging();
}


function addPLogging() {
   var e = document.getElementById('log');
   if (e) return;
   var div = $(document.createElement('div')).addClass('holder logstyle').appendTo($('#content'));
   $(document.createElement('div')).attr('id','log').html('-log-goes-here-').appendTo(div);
}

function pLog(msg) {
   var e = $('#log');
   if (e.length > 0) e.html((new Date()).format('HH:MM:ss')+' '+ msg + '<br/>' + e.html());
}

function getPageIndex(name) {
  for (var i=0;i<pageNames.length;i++) {
    if (name == pageNames[i]) {
      return i;
    }
  }
  return -1;
}

var _verbose = false;
function verboseDebug() {
  _verbose = true;
  addPLogging();
}

function showPage(action) {
  if (action == 'verbose_debug') {
    verboseDebug();
    return;
  }
  if (action.indexOf('show_') == -1) return false;
  action = action.substring(5);
  var depth = _awac.getStackDepth();
  var pageIndex = getPageIndex(_awac.getPageTag());
  var actionIndex = getPageIndex(action);
  if (depth === 0)
    _awac.openPage(action,action+'.html');
  else if (actionIndex != pageIndex) 
    _awac.replacePage(action,action+'.html',null,actionIndex < pageIndex?false:true);
  return true;
}


function showNextPage() {
  var pageIndex = getPageIndex(_awac.getPageTag());
  if (pageIndex == pageNames.length-1) return;
  var name = pageNames[pageIndex+1];
  _awac.replacePage(name,name+'.html',null,true);
}

function showPrevPage() {
  var pageIndex = getPageIndex(_awac.getPageTag());
  if (pageIndex === 0) return;
  var name = pageNames[pageIndex-1];
  _awac.replacePage(name,name+'.html',null,false);
}


function setButton(btn,cb) {
	var span = btn.find('span').first();
	span.click(function(event) {
		var e = $(this);
		if(e.find(".circle").length === 0) {
			e.prepend("<span class='circle'></span>");
		}
		var circle = e.find(".circle");
		circle.removeClass("animate");
		if(!circle.height() && !circle.width()) {
			var d = Math.max(e.outerWidth(), e.outerHeight());
			circle.css({height: d, width: d});
		}
		var x = event.pageX - e.offset().left - circle.width()/2;
		var y = event.pageY - e.offset().top - circle.height()/2;
		circle.css({top: y+'px', left: x+'px'}).addClass("animate");
	});
	btn.click(function(event) { cb(event); });
}
