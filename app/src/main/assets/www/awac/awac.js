var _awac_;

function MockContainer(awac) {
  this.debug = true;
  this.values = {};
  this.varName = null;
  this.awac = awac;
}

MockContainer.prototype.setVarName = function(varName) {
  if (this.debug) console.log('var '+varName+' = new Awac()');
  this.varName = varName;
};

MockContainer.prototype.toString = function() {
  return "{MockContainer var:"+this.varName+"}";
};

MockContainer.prototype.setTitle = function(title) {
  if (this.debug) console.log(this.varName+'.setTitle('+title+')');
  if (title) document.title = title;
};

MockContainer.prototype.addNavDrawerItem = function(json) {
  if (this.debug) console.log(this.varName+'.addNavDrawerItem('+json+')');
};

MockContainer.prototype.addOptionsMenuItem = function(json) {
  if (this.debug) console.log(this.varName+'.addOptionsMenuItem('+json+')');
};

MockContainer.prototype.addActionBarItem = function(json) {
  if (this.debug) console.log(this.varName+'.addActionBarItem('+json+')');
};

MockContainer.prototype.setPageColors = function(json) {
  if (this.debug) console.log(this.varName+'.setPageColors('+json+')');
};

MockContainer.prototype.setAppColors = function(json) {
  if (this.debug) console.log(this.varName+'.setAppColors('+json+')');
};

MockContainer.prototype.setHomeItem = function(json) {
  if (this.debug) console.log(this.varName+'.setHomeItem('+json+')');
};

MockContainer.prototype.startPage = function() {
  if (this.debug) console.log(this.varName+'.startPage()');
};

MockContainer.prototype.endPage = function() {
  if (this.debug) console.log(this.varName+'.endPage()');
  window.close();
};

MockContainer.prototype.unlockNavDrawer = function() {
  if (this.debug) console.log(this.varName+'.unlockNavDrawer()');
};

MockContainer.prototype.set = function(name,value) {
  if (this.debug) console.log(this.varName+'.set('+name+','+value+')');
  this.values[name] = value;
};

MockContainer.prototype.get = function(name) {
  var value = this.values[name];
  if (this.debug) console.log(this.varName+'.get('+name+')='+value);
  return value;
};

MockContainer.prototype.getImplementation = function() {
  if (this.debug) console.log(this.varName+'.getImplementation()=mock');
  return 'mock';
};

MockContainer.prototype.store = function(name,value) {
  if (this.debug) console.log(this.varName+'.store('+name+','+value+')');
  localStorage.setItem(name,value);
};

MockContainer.prototype.load = function(name) {
  var value = localStorage.getItem(name);
  if (this.debug) console.log(this.varName+'.load('+name+')='+value);
  return value;
};

MockContainer.prototype.sendMessage = function(msgId,value) {
  if (this.debug) console.log(this.varName+'.sendMessage('+msgId+','+value+')');
};

MockContainer.prototype.openPage = function(tag,url,value) {
  if (this.debug) console.log(this.varName+'.openPage('+tag+','+url+','+value+')');
  window.open(url+'?initparam='+value,tag);
};

MockContainer.prototype.replacePage = function(tag,url,value,next) {
  if (this.debug) console.log(this.varName+'.replacePage('+tag+','+url+','+value+','+next+')');
  window.open(url+'?initparam='+value,tag);
};

MockContainer.prototype.newApp = function(url) {
  if (this.debug) console.log(this.varName+'.newApp('+url+')');
  window.location.href = url;
};

MockContainer.prototype.startPage = function() {
  if (this.debug) console.log(this.varName+'.startPage()');
};

MockContainer.prototype.endPage = function(value) {
  if (this.debug) console.log(this.varName+'.endPage('+value+')');
  //window.close();
};

MockContainer.prototype.showDialog = function(msg,ok,cancel) {
  if (this.debug) console.log(this.varName+'.showDialog('+msg+','+ok+','+cancel+')');
  var yes = confirm(msg);
  this.awac.fireDialogResult(yes);
};

MockContainer.prototype.alert = function(msg) {
  if (this.debug) console.log(this.varName+'.alert('+msg+')');
  alert(msg);
};

MockContainer.prototype.gotOnRefreshCB = function() {
  if (this.debug) console.log(this.varName+'.gotOnRefreshCB()');
};

MockContainer.prototype.gotOnBackPressedCB = function() {
  if (this.debug) console.log(this.varName+'.gotOnBackPressedCB()');
};

MockContainer.prototype.gotOnActionCB = function() {
  if (this.debug) console.log(this.varName+'.gotOnActionCB()');
};

MockContainer.prototype.gotOnMessageCB = function() {
  if (this.debug) console.log(this.varName+'.gotOnMessageCB()');
};

MockContainer.prototype.gotOnPageCloseCB = function() {
  if (this.debug) console.log(this.varName+'.gotOnPageCloseCB()');
};

MockContainer.prototype.startRefresh = function() {
  if (this.debug) console.log(this.varName+'.startRefresh()');
};

MockContainer.prototype.endRefresh = function() {
  if (this.debug) console.log(this.varName+'.endRefresh()');
};

MockContainer.prototype.startBackground = function(url) {
  if (this.debug) console.log(this.varName+'.startBackground('+url+')');
};

MockContainer.prototype.stopBackground = function() {
  if (this.debug) console.log(this.varName+'.stopBackground()');
};

MockContainer.prototype.makeBackgroundRequest = function(msgId,value) {
  if (this.debug) console.log(this.varName+'.makeBackgroundRequest('+msgId+','+value+')');
};

MockContainer.prototype.callBackground = function() {
  if (this.debug) console.log(this.varName+'.callBackground()');
};

MockContainer.prototype.getStackDepth = function() {
  if (this.debug) console.log(this.varName+'.getStackDepth()=0');
  return 0;
};

MockContainer.prototype.getDims = function() {
  if (this.debug) console.log(this.varName+'.getDims()=768x1024');
  return '{"width":768,"height":1024}';
};

MockContainer.prototype.getParam=function(name) {
  var href = window.location.href;
  var i = href.indexOf('?');
  if (i == -1) return null;
  href = '&'+href.substring(i+1);
  var p = '&'+name+'=';
  i = href.indexOf(p);
  if (i == -1) return null;
  href = href.substring(i+p.length);
  i = href.indexOf('&');
  if (i != -1) href = href.substring(0,i);
  return href;
};

MockContainer.prototype.getInitParam = function() {
  var value = this.getParam('initparam');
  if (this.debug) console.log(this.varName+'.getInitParam()='+value);
  return value;
};

MockContainer.prototype.getPageTag = function() {
  if (this.debug) console.log(this.varName+'.getPageTag()=default');
  return 'default';
};

function Awac(varName) {
  this.varName = varName ? varName : 'x';
  //console.log(this.varName+' = new Awac('+_awac_+')');
  this.container = _awac_ ? _awac_ : new MockContainer(this);
  this.container.setVarName(this.varName);
  //console.log(' - this.container='+this.container);
  this.ondialog = null;
  this.onrefresh = null;
  this.onresult = null;
  this.onmessage = null;
  this.onaction = null;
  this.onpageclose = null;
  this.started = false;
  this.gottitle = false;
  this.debug = true;
  this.nBackgroundRequest = 0;
  this.onbackgroundresp = [];
}

Awac.prototype.toString = function() {
  return "{Awac var:"+this.varName+" loaded:"+this.loaded+" onresult:"+this.onresult+"}";
};

Awac.prototype.stringify = function(x) {
  if (!x) return 'null';
  var t = typeof(x);
  if (t === 'undefined') return 'null';
  if (t == 'string') return 'string:'+encodeURIComponent(x);
  if (t == 'number') return 'number:'+x;
  if (t == 'object' || t == 'array') return 'json:'+encodeURIComponent(JSON.stringify(x));
  return 'null';
};

Awac.prototype.parse = function(s) {
  if (!s) return null;
  if (typeof(s) === 'undefined') return null;
  if (s == 'null') return null;
  var i = s.indexOf(':');
  var t = s.substring(0,i);
  s = s.substring(i+1);
  if (t == 'string') return decodeURIComponent(s);
  if (t == 'number') return parseFloat(s);
  if (t == 'json') return JSON.parse(decodeURIComponent(s));
  return null;
};

Awac.prototype.setTitle = function(title) {
  this.container.setTitle(title);
  this.gottitle = true;
};

Awac.prototype.addNavDrawerItem = function(obj) {
  this.container.addNavDrawerItem(JSON.stringify(obj));
};

Awac.prototype.addOptionsMenuItem = function(obj) {
  this.container.addOptionsMenuItem(JSON.stringify(obj));
};

Awac.prototype.addActionBarItem = function(obj) {
  this.container.addActionBarItem(JSON.stringify(obj));
};

Awac.prototype.setAppColors = function(obj) {
  this.container.setAppColors(JSON.stringify(obj));
};

Awac.prototype.setPageColors = function(obj) {
  this.container.setPageColors(JSON.stringify(obj));
};

Awac.prototype.setHomeItem = function(obj) {
  this.container.setHomeItem(JSON.stringify(obj));
};

Awac.prototype.getStackDepth = function() {
  return this.container.getStackDepth();
};

Awac.prototype.getDims = function() {
  var result = JSON.parse(this.container.getDims());
  var w = $(window).width();
  result.height = result.height*w/result.width;
  result.width = w;
  return result;
};

Awac.prototype.unlockNavDrawer = function() {
  this.container.unlockNavDrawer();
};

Awac.prototype.set = function(name,value) {
  var v = this.stringify(value);
  this.container.set(name,v);
};

Awac.prototype.get = function(name) {
  var s = this.container.get(name);
  return this.parse(s);
};

Awac.prototype.getImplementation = function() {
  return this.container.getImplementation();
};

Awac.prototype.store = function(name,value) {
  var v = this.stringify(value);
  this.container.store(name,v);
};

Awac.prototype.load = function(name) {
  var s = this.container.load(name);
  return this.parse(s);
};

Awac.prototype.replyMessage = function(msgId,value) {
  if (msgId != -1) {
    var v = this.stringify(value);
    this.container.replyMessage(msgId,v);
  }
};

Awac.prototype.getInitParam = function() {
  var s = this.container.getInitParam();
  return this.parse(s);
};

Awac.prototype.getPageTag = function() {
  return this.container.getPageTag();
};

/* Call this to make the web page visible */
Awac.prototype.startPage = function() {
  if (!this.started) {
    this.started = true;
    if (!this.gottitle) {
      var t = document.title;
      if (t) this.setTitle(t);
    }
    this.container.startPage();
  }
};

Awac.prototype.setMdlCss = function(containerId,mdlCssUrl) {
  console.log('Awac.setMdlCss()');
  var dims = this.getDims();
  console.log(' - dims='+dims.width+'x'+dims.height);
  if (dims.width === 0 || dims.height === 0) {
    console.log(' - dims not set - retry in 50ms');
    var awac = this;
    setTimeout(function() { awac.setMdlCss(containerId,mdlCssUrl); },50);
    return;
  }
  
  var e = document.getElementById(containerId);
  e.style.width = dims.width+'px';
  e.style.height = dims.height+'px';
 
  var urls = mdlCssUrl.split(',');
  for (var i=0;i<urls.length;i++) {
  var link = document.createElement( 'link' );
    link.href = urls[i];
    link.type = 'text/css';
    link.rel = 'stylesheet';
    document.getElementsByTagName( 'head' )[0].appendChild( link );
  }
  // tell MDL component handler to upgrade the page 
  componentHandler.upgradeDom();
};
    
/* Call this to make the web page visible which uses Material Design Lite */
Awac.prototype.startMdlPage = function(containerId,mdlCssUrl) {
  this.startPage();
  var awac = this;
  setTimeout(function() { awac.setMdlCss(containerId,mdlCssUrl); },50);
};      

/* End displaying the page and pop it off the stack */
Awac.prototype.endPage = function(value) {
  var v = this.stringify(value);
  this.container.endPage(v);
};

/* Pop a new page onto the stack */
Awac.prototype.openPage = function(tag,url,value) {
  var v = this.stringify(value);
  this.container.openPage(tag,url,v);
};

/* Replace page on top of the stack */
Awac.prototype.replacePage = function(tag,url,value,next) {
  var v = this.stringify(value);
  this.container.replacePage(tag,url,v,next);
};

Awac.prototype.newApp = function(url) {
  this.container.newApp(url);
};

Awac.prototype.alert = function(msg) {
  this.container.alert(msg);
};

Awac.prototype.dialog = function(msg,ok,cancel,cb) {
  this.ondialog = cb;
  this.container.showDialog(msg,ok,cancel);
};

// set callbacks

Awac.prototype.setOnRefresh = function(cb) {
  this.container.gotOnRefreshCB();
  this.onrefresh = cb;
};

Awac.prototype.setOnResult = function(cb) {
  this.container.gotOnResultCB();
  this.onresult = cb;
};

Awac.prototype.setOnBackPressed = function(cb) {
  this.container.gotOnBackPressedCB();
  this.onbackpressed = cb;
};

Awac.prototype.setOnAction = function(cb) {
  this.container.gotOnActionCB();
  this.onaction = cb;
};

Awac.prototype.setOnMessage = function(cb) {
  this.container.gotOnMessageCB();
  this.onmessage = cb;
};

Awac.prototype.setOnPageClose = function(cb) {
  this.container.gotOnPageCloseCB();
  this.onpageclose = cb;
};

// fire callbacks

Awac.prototype.fireDialogResult = function(ok) {
  if (this.debug) console.log('Awac.fireDialogResult('+ok+')');
  if (this.ondialog) this.ondialog(ok);
  this.ondialog = null;
};

Awac.prototype.fireBackPressed = function() {
  if (this.debug) console.log('Awac.fireBackPressed()');
  if (this.onbackpressed) this.onbackpressed();
};

Awac.prototype.fireRefresh = function() {
  if (this.debug) console.log('Awac.fireRefresh()');
  if (this.onrefresh) this.onrefresh();
};

Awac.prototype.fireAction = function(action) {
  if (this.debug) console.log('Awac.fireAction('+action+')');
  if (this.onaction) this.onaction(action);
};

Awac.prototype.fireMessage = function(msgId,value) {
  if (this.debug) console.log('Awac.fireMessage('+msgId+','+value+')');
  if (this.onmessage) this.onmessage(msgId,this.parse(value));
};

Awac.prototype.firePageClose = function(tag,ok,value) {
  if (this.debug) console.log('Awac.firePageClose('+tag+','+ok+','+value+')');
  if (this.onpageclose) this.onpageclose(tag,ok,this.parse(value));
};

Awac.prototype.fireBackgroundResponse = function(msgId,value) {
  if (this.debug) console.log('Awac.fireBackgroundResponse('+msgId+','+value+')');
  var cb = this.onbackgroundresp[msgId];
  this.onbackgroundresp[msgId] = null;
  cb(this.parse(value));
};

//

Awac.prototype.startRefresh = function() {
  this.container.startRefresh();
};

Awac.prototype.endRefresh = function() {
  this.container.endRefresh();
};

Awac.prototype.startBackground = function(url) {
  this.container.startBackground(url);
};

Awac.prototype.stopBackground = function() {
  this.container.stopBackground();
};

Awac.prototype.getBackgroundResponse = function(value,cb) {
  var v = this.stringify(value);
  var msgId = this.nBackgroundRequest++;
  this.onbackgroundresp[msgId] = cb;
  this.container.makeBackgroundRequest(msgId,v);
};

Awac.prototype.makeBackgroundRequest = function(value) {
  var v = this.stringify(value);
  this.container.makeBackgroundRequest(-1,v);
};

Awac.prototype.callBackground = function() {
  this.container.callBackground();
};

