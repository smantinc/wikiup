function uHover(tag, orgClass)
{
  if(orgClass)
  {
    $(tag).removeClass(orgClass);
    $(tag).hover(function() {$(this).removeClass(orgClass).addClass('u-hover')}, function() {$(this).addClass(orgClass).removeClass('u-hover')});
  }
  else
    $(tag).hover(function() {$(this).addClass('u-hover')}, function() {$(this).removeClass('u-hover')});
  $(tag).addClass('u-hover');
}
function getEntityManager()
{
	return Ext.getCmp('entityManager');
}
function getFileExplorer()
{
	return Ext.getCmp('fileExplorer');
}
function getDatasourceExplorer()
{
	return Ext.getCmp('datasourceExplorer');
}
function uReloadFileExplorer(node)
{
	var n = _getReloadableNode(node || getFileExplorer().root);
	getFileExplorer().loader.load(n, function() {n.expand();});
}
function uReloadDatasourceExplorer(node)
{
	var n = _getReloadableNode(node || getDatasourceExplorer().root);
	getDatasourceExplorer().loader.load(n, function() {n.expand();});
}
function uReloadEntityManager()
{
	var cmp = Ext.getCmp('entityManager');
	cmp.store.reload();
}
function _getReloadableNode(node)
{
	return node.isLeaf() ? node.parentNode : node;
}
function uExploreTo(uri, activate)
{
	var pc = Ext.getCmp('wmdk-tp-processor-context');
	pc.setRootNode({
		nodeType: 'async',
		text: '',
		draggable: false,
		id: uri
	});
	var gc = Ext.getCmp('GSAs');
	gc.store.reload({params: {uri: uri}});
	$('#wmdk-explorer-browsing-frame').attr('src', uri);
	if(activate)
		uGetClientTabPanel().activate(Ext.getCmp('wmdk-exploer-tab'));
}
function uLocation(f)
{
	return (f.contentWindow || f.contentDocument).location;
}
function uReload(f)
{
	uLocation(f || window).reload(true);
}
function uAddTab(i)
{
	var client = uGetClientTabPanel();
	if(i.closable != false)
		i.closable = true;
	i.layout = 'fit';
	var tab = client.add(i);
	client.activate(tab);
	return tab;
}
function uGetClientTabPanel()
{
	return Ext.getCmp('clientTabPanel');
}
function uGetActiveTab()
{
	return uGetClientTabPanel().getActiveTab();
}
function uCloseTab(tab)
{
	var client = Ext.getCmp('clientTabPanel');
	client.remove(tab);
}
function uEntityManagerComboBox(comboListener, storeListener, combo)
{
	return uComboBox(jQuery.extend({
		fieldLabel: i18n.ui.label.EntityManager,
		hiddenName: 'manager',
		displayField: 'name',
		valueField: 'name',
		value: wmdk.entityManager,
		listeners: jQuery.extend({}, comboListener)
	}, combo), {
		url: rURI('ent!list.json'),
		fields: ['name','active'],
		listeners: jQuery.extend({}, storeListener)
	});
}
function uTemplateGroupbox()
{
	var templates = rToJson(jQuery.ajax({url: 'scaffold!templates.json', async: false}).responseText);
	var items = [];
	for(var i = 0; i < templates.length; i ++)
	{
		var v = templates[i];
		items.push(jQuery.extend(v, {boxLabel: v.name, name: 'wk-scaffold-' + v.name, checked: v.disabled != true}));
	}
	return {
		xtype: 'checkboxgroup',
		fieldLabel: i18n.ui.label.Templates,
		itemCls: 'x-check-group-alt',
		columns: 5,
		items: items
	};
}
function uSchemaComboBox(comboListener, storeListener, combo)
{
	return uComboBox(jQuery.extend({
		fieldLabel: i18n.ui.label.DBSchema,
		hiddenName: 'l',
		displayField: 'text',
		valueField: 'id',
		allowBlank: false,
		listeners: jQuery.extend({}, comboListener)
	}, combo), {
		url: rURI('ds!schemaList.json'),
		fields: ['id','text'],
		listeners: jQuery.extend({}, storeListener)
	});
}
function uAlert(text, config)
{
	var conf = rExtend({
		title: i18n.ui.title.Alert,
		msg: text,
		buttons: Ext.MessageBox.OK
	}, config);
	Ext.MessageBox.show(conf);
}
function uWait(title, msg, config)
{
	return Ext.MessageBox.wait(title, msg, config);
}
function uProgress(title, msg, progressText)
{
	return Ext.MessageBox.progress(title, msg, progressText);
}
function uConfirm(text, callback)
{
	Ext.MessageBox.confirm(i18n.ui.title.Confirm, text, function(btn) {
		if(btn == 'yes' && callback)
			callback.call(this);
	});
}
function uComboBox(combo, store)
{
	return jQuery.extend({
			xtype: 'combo',
			triggerAction: 'all',
			typeAhead: true,
			store: new Ext.data.JsonStore(jQuery.extend({
				autoDestory: true,
				autoLoad: true
			}, store))
	}, combo);
}
function uFormWindow(form, items, w, callback, autoShow)
{
	var instance = {};
	instance.formPanel = new Ext.form.FormPanel(
		rExtend({
				baseCls: 'x-plain',
				defaultType: 'textfield',
				labelWidth: 105,
				defaults: {
					anchor:'100%'
				}
			},
			{items: items},
			form));
	w.items = w.items ? [instance.formPanel].concat(w.items) : instance.formPanel;
	instance.win = new Ext.Window(
		rExtend({
			width: 500,
			height: 600,
			layout: 'fit',
			plain: true,
			bodyStyle: 'padding:5px;',
			buttons: [{
				id:'btn-ok', text: i18n.ui.text.OK, handler: function() {
					if(callback)
						callback.call(this, instance.win, instance.formPanel);
					else
						win.close();
				}
			},{
				id:'btn-cancel', text: i18n.ui.text.Cancel, handler: function() {instance.win.close()}
			}]
		}, w)
	);
	if(autoShow != false)
		instance.win.show();
	return instance;
}
function uProspective(id, ln)
{
	switch(id)
	{
		case 'domain-manager':
			Ext.getCmp('west-container').layout.setActiveItem(1);
			break;
		case 'datasource-explorer':
			Ext.getCmp('west-container').layout.setActiveItem(2);
			break;
		case 'resource-explorer':
			Ext.getCmp('west-container').layout.setActiveItem(0);
			break;
	}
	if(ln != false)
		uSetProspective(id);
	return false;
}
function uSetProspective(id)
{
	$('#wmdk-dynamic-context-reference ul').each(function() {
		var s = $(this);
		s.css('display', s.hasClass('wmdk-' + id + '-prospective') ? 'block' : 'none');
	});
	return false;
}
function uHistory(id)
{
	$('#wmdk-dynamic-context-reference').children().addClass(id);
	return false;
}