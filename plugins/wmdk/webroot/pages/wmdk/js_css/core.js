function rHover(tag, orgClass)
{
	if(orgClass)
	{
		$(tag).removeClass(orgClass);
		$(tag).hover(function() {$(this).removeClass(orgClass).addClass('wmdk-hover')}, function() {$(this).addClass(orgClass).removeClass('wmdk-hover')});
	}
	else
		$(tag).hover(function() {$(this).addClass('wmdk-hover')}, function() {$(this).removeClass('wmdk-hover')});
	$(tag).addClass('wmdk-hover');
}
function rExtend(target)
{
	var i;
	for(i = 1; i < arguments.length; i++)
		jQuery.extend(target, arguments[i]);
	return target;
}
function rURI(uri)
{
	if(uri)
	{
		if(uri.charAt(0) == '/')
			return contextPath + uri.substring(1);
		return contextPath + 'pages/wmdk/' + uri;
	}
	return uri; 
}
function rFlash(tag)
{
	with(top)
	{
		function highlight() {
			$(tag).addClass('wk-highlight');
		}
		function normal() {
			$(tag).removeClass('wk-highlight');
		}
		var i;
		for(i = 0; i < 4000; i += 1000) {
			window.setTimeout(highlight, i);
			window.setTimeout(normal, i + 500);
		}
	}
}
function rGetFilename(uri)
{
	var idx = uri.lastIndexOf('/');
	uri = idx != -1 ? uri.substring(idx + 1) : uri;
	idx = uri.indexOf('?');
	return idx != -1 ? uri.substring(0, idx) : uri;
}
function rFormat(str, args)
{
	return str.replace(/\{(\d+)\}/g, function(m, i){
		return args[i];
	});
}
function rToJson(resp)
{
	return eval('(' + resp + ')');
}
function rDefer(callback)
{
	window.setTimeout(callback, 0);
}
function rCallback(callback)
{
	if(callback)
		callback.call(this);
}
function _watch(obj, pattern)
{
  for(var key in obj)
  if(!pattern || key.indexOf(pattern) != -1)
  if(!confirm(key + ':' + obj[key]))
    break;
}