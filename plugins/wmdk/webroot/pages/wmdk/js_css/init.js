var wmdk = {
		schema: '',
		table: '',
		path: '',
		uri: '',
		entityManager: '',
		fileExplorerNode: null,
		datasourceExplorerNode: null,
		datasourceManager: null,
		selection: {
			emNode: null
		},
		history: {
			lastDomainURL: null
		},
		explorers: {
		}
	};

Ext.onReady(function(){
	Ext.QuickTips.init();
	initMenu();
	var viewport = new Ext.Viewport({
		layout: 'border',
		items: [
		new Ext.BoxComponent({
			region: 'north',
			contentEl: 'wmdk-header',
			height: 32
		}), {
			region: 'east',
			split: true,
			width: 225,
			minSize: 175,
			maxSize: 400,
			margins: '0 5 0 0',
			layout: 'vbox',
			border: false,
			items: [{
				xtype: 'panel',
				id: 'wmdk-dynamic-help-sign',
				title: i18n.ui.title.DynamicContext,
				contentEl: 'wmdk-dynamic-context-reference',
				flex: 2,
				margins: '0 0 5 0',
				width: '100%'
			},{
				xtype: 'treepanel',
				flex: 3,
				title: i18n.ui.title.ProcessorContext,
				id: 'wmdk-tp-processor-context',
				margins: '0 0 5 0',
				width: '100%',
				animate: false,
				border: true,
				rootVisible: false,
				useArrows: true,
				autoScroll: true,
				containerScroll: true,
				dataUrl: 'ctx!contexts.json',
				listeners: {
					click: function(node) {
						if(!node.isLeaf())
							node.expand();
					},
					dblclick: function(node) {
						var path = [];
						var n = node;
						do
						{
							path.splice(0, 0, n.text);
							n = n.parentNode;
						} while(n.parentNode != null);
						if(node.isLeaf())
							Ext.Ajax.request({
								url: 'ctx!get.json',
								params: {
									name: path.join('.')
								},
								success: function(resp) {
									var json = Ext.util.JSON.decode(resp.responseText);
									uAlert(json.value, {minWidth: 300});
								}
							});
					}
				},
				root: {
					nodeType: 'async',
					text: 'Ext JS',
					draggable: false,
					id: '/'
				}
			}, {
				xtype: 'grid',
				id: 'GSAs',
				flex: 3,
				title: i18n.ui.title.ServletAction,
				width: '100%',
				store: new Ext.data.GroupingStore({
					url: 'ctx!actions.x',
					autoLoad: false,
					reader: new Ext.data.XmlReader({record: 'action'}, [
						   {name: 'class'},
						   {name: 'scope'}
						]),
					groupField: 'scope'
				}),
				colModel: new Ext.grid.ColumnModel({
					columns: [
						{header: "Scope", width: 30, dataIndex: 'scope'},
						{header: "Class", width: 70, dataIndex: 'class'}
					],
					defaults: {
						sortable: false,
						menuDisabled: true
					}
				}),
				view: new Ext.grid.GroupingView({
					forceFit: true
				})
			}
			]
		}, 
		{
		region: 'west',
		id: 'west-container',
		layout: 'accordion',
		width: 200,
		minSize: 175,
		maxSize: 400,
		border: true,
		split: true,
		items:[{
			xtype: 'treepanel',
			title: i18n.ui.title.FileExplorer,
			id: 'fileExplorer',
			animate: false,
			border: false,
			layout: 'fit',
			rootVisible: false,
			useArrows: true,
			autoScroll: true,
			containerScroll: true,
			dataUrl: 'ws!list.json',
			tbar: ['->',
				   {text: i18n.ui.text.Delete, iconCls: 'wmdk-tb-delete', handler: hDelete},
				   {text: i18n.ui.text.Rename, iconCls: 'wmdk-tb-rename', handler: hRenamePage, hidden: true},
				   '-',
				   {text: i18n.ui.text.Create, iconCls: 'wmdk-tb-page-add', handler: hNewPage},
				   {text: i18n.ui.text.Edit, iconCls: 'wmdk-tb-page-edit', handler: function() {hEditPage();}}],
			listeners: {
				expand: function() {
					uSetProspective('resource-explorer');
				},
				click: function(node) {
					var changed = wmdk.uri != node.id;
					wmdk.fileExplorerNode = node;
					wmdk.uri = node.id;
					if(changed)
					if(node.isLeaf())
						uExploreTo(node.id, true);
					else
					{
						node.expand();
						wmdk.path = node.id;
					}
				},
				dblclick: function(node) {
					wmdk.fileExplorerNode = node;
					wmdk.uri = node.id;
					if(node.leaf)
						hEditPage(node.id);
				},
				contextmenu: function(node, e) {
					var menuC = new Ext.menu.Menu({minWidth: 200,
							items:[
							{text: i18n.ui.text.Create, handler: hNewPage},
							{text: i18n.ui.text.Edit, handler: function(){hEditPage();}},
							'-',
							{text: i18n.ui.text.Delete, handler: hDelete},
							{text: i18n.ui.text.Rename, handler: hRenamePage}]
						});
					menuC.showAt(e.xy);
				}
			},
			root: {
				nodeType: 'async',
				draggable: false,
				id: '/'
			}
		},{
			xtype: 'grid',
			id: 'entityManager',
			title: i18n.ui.title.EntityManager,
			width: '100%',
			tbar: ['->',
				{text: i18n.ui.text.NewDomain, iconCls: 'wmdk-tb-domain-add', handler: hDefineDomain},
				{text: i18n.ui.text.PageScaffold, iconCls: 'wmdk-tb-page-add', handler: hPageScaffold}],	
			listeners: {
				click: function() {
					var cmp = Ext.getCmp('entityManager');
					var sm = cmp.getSelectionModel();
					var sel = sm.getSelected();
					if(sel != wmdk.selection.emNode)
						hDomainPage(sel.data.name, sel.data.manager);
					wmdk.selection.emNode = sel;
				}
			},
			store: new Ext.data.Store({
				url: rURI('ent!entities.x'),
				autoDestroy: true,
				autoLoad: true,
				reader: new Ext.data.XmlReader({
						record: 'entity',
						id: 'id'
					},[
						{name: 'id'},
						{name: 'name'},
						{name: 'manager'},
						{name: 'table'},
						{name: 'schema'},
						{name: 'catalog'}
					]
				)
			}),
			colModel: new Ext.grid.ColumnModel({
					columns: [
						{header: i18n.ui.label.Name, width: 80, dataIndex: 'name'},
						{header: i18n.ui.label.DBTable, width: 100, dataIndex: 'table'}
					],
					defaults: {
						sortable: false,
						menuDisabled: true
					}
				})
		},
		{
			xtype: 'treepanel',
			title: i18n.ui.title.DatasourceExplorer,
			id: 'datasourceExplorer',
			animate: false,
			border: false,
			layout: 'fit',
			rootVisible: false,
			useArrows: true,
			autoScroll: true,
			containerScroll: true,
			dataUrl: rURI('ds!list.json'),
			tbar: ['->',
				   {text: i18n.ui.text.Delete, iconCls: 'wmdk-tb-delete', handler: hDropTable},
				   {text: i18n.ui.text.ImportDomain, iconCls: 'wmdk-tb-domain-edit', handler: hImportDomain, id: 'tbImportDomain'}],
			listeners: {
				expand: function() {
					uSetProspective('datasource-explorer');
				},
				click: function(node) {
					wmdk.schema = node.isLeaf() ? node.parentNode.id : node.text;
					wmdk.datasourceExplorerNode = node;
					if(node.isLeaf())
					{
						wmdk.table = node.text;
						uExploreTo(rURI('ent!describe.x?l=' + encodeURIComponent(wmdk.schema) + '&table=' + wmdk.table + '&manager=' + wmdk.entityManager + '&ds=' + wmdk.datasourceManager), true);
					}
					else
						node.expand();
				}
			},
			root: {
				nodeType: 'async',
				draggable: false,
				id: 'root'
			}
		}]
		}
		, new Ext.TabPanel({
			region: 'center',
			id: 'clientTabPanel',
			activeTab: 0,
			items: [{
				tbar: ['->',
						{text: i18n.ui.text.OpenInNewBrowser, iconCls: 'wmdk-tb-open-in-new',
							handler: function() {
								window.open($('#wmdk-explorer-browsing-frame').attr('src'));
							}},
						'-',
						{text: i18n.ui.text.Edit, iconCls: 'wmdk-tb-page-edit', handler: function() {
							var uri = uLocation($('#wmdk-explorer-browsing-frame').get(0)).pathname;
							if(uri != 'about:blank')
								hEditPage(uri);
					}}],
					contentEl: 'wmdk-explorer-browsing',
					id: 'wmdk-exploer-tab',
					title: i18n.ui.title.WebBrowser,
					layout: 'fit'
			}]
		})]
	});
	var m = 'sman' + 'tinc' + '@gm' + 'ail.' + 'com';
	$('#wmdk-mailbox').text(m).attr('href', 'mailto:' + m);
});

function initMenu()
{
	var entityManager, datasourceManager;
	var lc = 0;
	function onEntityManagerChecked(node, checked)
	{
		if(checked)
		{
			var name = node.text || node.name;
			hChangeEntityManager(name, function() {
				wmdk.entityManager = name;
				entityManager.setText(name);
			});
		}
	}
	function setDatasourceManager(node)
	{
		var name = node.text || node.name;
		wmdk.datasourceManager = name;
		datasourceManager.setText(name);
	}
	function onDatasourceManagerChecked(node, checked)
	{
		if(checked)
		{
			hChangeDatasource(node.text, function() {
				var cmp = getDatasourceExplorer();
				setDatasourceManager(node);
				uReloadDatasourceExplorer(cmp.root);
			});
		}
	}
	function renderToolbar()
	{
		if(lc > 1)
		{
			tb.render('wmdk-system-menu');
			tb.doLayout();
		}
	}

    var tb = new Ext.Toolbar({id:'wmdk-main-toolbar'});

	tb.add('->',
		   {
            text: i18n.ui.text.Export,
			id: 'wmdk-tb-export',
            iconCls: 'wmdk-tb-export',
            tooltip: {text: i18n.ui.tooltip.Export.Content, title: i18n.ui.tooltip.Export.Brief},
			handler: hExport
        },
		'-',{
            text: i18n.ui.text.Plugins,
			id: 'wmdk-tb-plugin',
            iconCls: 'wmdk-tb-plugin',
            tooltip: {text: i18n.ui.tooltip.Plugins.Content, title: i18n.ui.tooltip.Plugins.Brief},
			handler: hPlugins
        },
		'-',
		entityManager = new Ext.Toolbar.SplitButton({
            text: '',
            tooltip: {text: i18n.ui.tooltip.EntityManager.Content, title: i18n.ui.tooltip.EntityManager.Brief},
            id: 'wmdk-tb-entity-manager',
            iconCls: 'wmdk-tb-entity-manager',
			minWidth: 100,
            menu : {
				minWidth: 100,
                items: []
            }
        }),
		'-',
		datasourceManager = new Ext.Toolbar.SplitButton({
			id: 'wmdk-tb-datasource',
            text: '',
            tooltip: {text: i18n.ui.tooltip.DatasourceManager.Content,
					title: i18n.ui.tooltip.DatasourceManager.Brief
				},
            iconCls: 'wmdk-tb-datasource',
			minWidth: 100,
			handler: hDatasourceConfigure,
            menu : {
				minWidth: 100,
                items: []
            }
        }),
		'-',
		{
			id: 'wmdk-tb-bug-report',
			text: i18n.ui.text.BugReport,
			iconCls: 'wmdk-tb-bug',
			tooltip: {text: i18n.ui.tooltip.BugReport.Content, title: i18n.ui.tooltip.BugReport.Brief},
			handler: hReportBug},
		'-',
		{text: i18n.ui.text.AboutWMDK, iconCls: 'wmdk-tb-app', handler: hAbout});

	Ext.Ajax.request({
		url: rURI('ent!list.json'),
		success: function(resp)
		{
			var json =  Ext.util.JSON.decode(resp.responseText);
			var i;
			for(i = 0; i < json.length; i++)
			{
				if(json[i].active)
				{
					wmdk.entityManager = json[i].name;
					entityManager.setText(json[i].name);
				}
				entityManager.menu.add({
                    text: json[i].name, checkHandler: onEntityManagerChecked, group: 'entityManager', checked: json[i].active
                });
			}
			lc ++;
			renderToolbar();
		}
	});
	Ext.Ajax.request({
		url: rURI('ds!srcs.json'),
		success: function(resp)
		{
			var json =  Ext.util.JSON.decode(resp.responseText);
			var i;
			for(i = 0; i < json.length; i++)
			{
				if(json[i].active)
					setDatasourceManager(json[i]);
				datasourceManager.menu.add({
					text: json[i].name,
					checkHandler: onDatasourceManagerChecked,
					group: 'datasourceManager',
					checked: json[i].active
                });
			}
			lc ++;
			renderToolbar();
		}
	});
}

