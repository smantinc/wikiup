function hImportDomain(callback)
{
	var tableStore;
	var table, schema, manager;
	function getPreviewURI()
	{
		var l = (schema || wmdk.schema), t = (table || wmdk.table), m = (manager || wmdk.entityManager);
		return l && t ? 'template/scaffold/preview.html?l=' + l + '&table=' + t + '&manager=' + m + '&ds=' + wmdk.datasourceManager : 'about:blank';
	}
	function updatePreview()
	{
		$('#preview-browser').attr('src', getPreviewURI());
	}
	var inst = uFormWindow({
	},[
			uSchemaComboBox({
				select: function(cmp) {
					schema = cmp.getValue();
					tableStore.setBaseParam('l', schema);
					tableStore.reload();
				}
			}, {
				load: function(store) {
					if(!inst.formPanel.get(0).getValue())
						inst.formPanel.get(0).setValue(wmdk.schema);
				}
			}, {
				id: 'importDomainSchema'
			}), {
			xtype: 'combo',
			id: 'importDomainTable',
			fieldLabel: i18n.ui.label.DBTable,
			name: 'table',
			value: wmdk.table,
			displayField: 'text',
			valueField: 'text',
			triggerAction: 'all',
			typeAhead: true,
			listeners: {
				select: function(cmp) {
					table = cmp.getValue();
					updatePreview();
				}
			},
			store: tableStore = new Ext.data.JsonStore({
				autoDestory: true,
				autoLoad: false,
				url: rURI('ds!tableList.json'),
				baseParams: {
					l: wmdk.schema
				},
				fields: ['leaf','text']
			})
		},{
			fieldLabel: i18n.ui.label.DomainName,
			name: 'domain-name'
		},uEntityManagerComboBox({
				select: function(cmp) {
					manager = cmp.getValue();
					updatePreview();
				}
			},
			{},
			{id: 'importDomainEntityManager'}
		), uTemplateGroupbox(), {
			xtype: 'panel',
			title: i18n.ui.title.OperationPreview,
			html: '<iframe id="preview-browser" src="' + getPreviewURI() + '" frameborder="0" height="100%" width="100%" transparent="true"></iframe>',
			anchor: '100% -128'
		}], {
			title: i18n.ui.title.ImportDomain,
			width: 550,
			height: 700
		}, function(win, fp) {
			_doScaffoldGeneration(fp.getForm(), win, callback);
		}
	);

	return false;
}

function hDelete()
{
	var node = wmdk.fileExplorerNode;
	if(node)
		uConfirm(rFormat(i18n.ui.prompt.DeleteConfirm, [wmdk.fileExplorerNode.text]), function() {
			Ext.Ajax.request({
				url: 'ws!delete.json',
				params: {uri: node.id},
				success: function(resp) {
					node.remove();
				}
			});
		});
}

function hNewPage(callback)
{
	var eNode = wmdk.fileExplorerNode;
	var path = eNode && eNode.parentNode ? (eNode.isLeaf() ? eNode.parentNode.id : eNode.id) : null;
	if(path)
		path = path.charAt(path.length - 1) == '/' ? path : path + '/';
	uFormWindow({
	},[{
            fieldLabel: i18n.ui.label.Name,
            name: 'name',
			allowBlank: false,
			id: 'newPageFileName',
			value: path ? path : undefined
        }, uComboBox({
				fieldLabel: i18n.ui.label.Type,
				id: 'newPageFileType',
				displayField: 'name',
				valueField: 'name',
				hiddenName: 'type',
				hiddenValue: 'html',
				listeners: {
					select: function(cmp, record) {
						$('#preview-browser').attr('src', record.data.uri);
					}
				}
			},{
				url: 'ws!types.json',
				fields: ['name', 'uri']
			}
		),{
            xtype: 'panel',
			title: 'Page Preview',
			html: '<iframe id="preview-browser" src="template/new-page/html.html" frameborder="0" height="100%" width="100%" transparent="true"></iframe>',
			anchor: '100% -52'
        }], {
			title: i18n.ui.title.NewPage,
			width: 760,
			height: 550
		}, function(win, fp) {
			fp.getForm().submit({
				url:'ws!create.json',
				waitMsg: i18n.ui.prompt.GeneratingPage,
				success: function(form, action) {
						win.close();
						uReloadFileExplorer(eNode ? (eNode.isLeaf() ? eNode.parentNode : eNode) : getFileExplorer().root);
						rCallback(callback);
					}
				});
		}
	);
}
function hEditPage(uri, title, editing, tbars, conf)
{
	uri = uri || wmdk.uri;
	if(uri)
	if(wmdk.explorers[uri])
		uGetClientTabPanel().activate(wmdk.explorers[uri]);
	else
	{
		var el = document.getElementById('wmdk-explorer-panel').cloneNode(true);
		var iframe = $(el).attr('id', null).find('iframe').attr('src', uri);
		var textarea = $('textarea', el);
		var tbar = ['->'];
		if(tbars)
			tbar = tbar.concat(tbars);
		tbar = tbar.concat([{text: i18n.ui.text.OpenInNewBrowser, iconCls: 'wmdk-tb-open-in-new',
						handler: function() {
							window.open(iframe.contents().get(0).location.href);
						}},
					'-']);
		function rFrameSrc() {
			return iframe.contents().get(0).location.pathname;
		}
		function hSave(callback) {
			var box = uWait(i18n.ui.prompt.SavingPage, i18n.ui.title.StandBy);
			hSavePage(rFrameSrc(), textarea.val(), function() {
				box.hide();
				rCallback(callback);
			});
		}
		function hTextareaKey(evt) {
			var key = evt.which;
			if(evt.ctrlKey)
			 	switch(key)
				{
				case 83:
				case 115:
					if(evt.preventDefault)
						evt.preventDefault();
					hSave();
					break;
				case 192:
					hToggled(true);
					break;
				}
		}
		var loaded = false;
		var editButton;
		function hToggled(manual) {
			$(el).toggleClass('wmdk-browsing').toggleClass('wmdk-editing');
			if(manual == true)
				editButton.toggle();
			if($(el).hasClass('wmdk-browsing'))
				uReload($('iframe', el).get(0));
			else
				rLoadContent();
		}
		textarea.keydown(hTextareaKey);

		function rLoadContent() {
			if(!loaded)
				$.post(rURI('ws!get.do'), {uri: rFrameSrc()}, function(resp) {
					textarea.val(resp);
					textarea.focus();
				});
			loaded = true;
		}
		
		tbar.push({text: i18n.ui.text.SaveAndClose, iconCls: 'wmdk-tb-save', handler: function() {uCloseTab(uGetActiveTab());}},
				{text: i18n.ui.text.Save, iconCls: 'wmdk-tb-save', handler: hSave, tooltip: {text: 'Ctrl + S'}},
				'-',
				editButton = new Ext.Toolbar.Button({text: i18n.ui.text.Edit, iconCls: 'wmdk-tb-page-edit', enableToggle: true, pressed: editing != false, handler: hToggled, tooltip: {text: 'Ctrl + ~'}}));
		wmdk.explorers[uri] = uAddTab(jQuery.extend({
			title: title || rGetFilename(uri),
			contentEl: el,
			border: false,
			listeners : {
				close: function() {
					delete wmdk.explorers[uri];
				}
			},
			tbar: tbar
		}, conf));

		if(editing != false)
			iframe.load(function() {
				iframe.unbind('load');
				hToggled();
			});
		textarea.height(textarea.parent().height());
		return el;
	}
}
function hDomainPage(name, manager)
{
	var el;
	var combo = uComboBox({
			displayField: 'name',
			valueField: 'index',
			emptyText: 'Current Views Available',
			listeners: {
				select: function(cmp, record) {
					$('iframe', el).attr('src', record.data.index);
				}
			}
		}, {
			url: rURI('scaffold!templates.json?all=false&domain=' + name),
			fields: ['name', 'index']
		});
	var tbar = [combo, '-'];
	var uri = rURI('ent!show.x?name=' + name + '&manager=' + manager);
	if(wmdk.explorers[uri])
	{
		uGetClientTabPanel().remove(wmdk.explorers[uri]);
		delete wmdk.explorers[uri];
	}
	el = hEditPage(uri, name, false, tbar);
}
function hSavePage(uri, content, callback)
{
	Ext.Ajax.request({
		url: 'ws!update.json',
		params: {
			uri: uri,
			content: content
		},
		success: function() {
			rCallback(callback);
		}
	});
}
function hExport()
{
	var name, org, rev = 'SNAPSHOT', description;
	function getPreviewURI()
	{
		return 'template/export/preview.html?name=' + (name ? name : '') + '&org=' + (org ? org : '') + '&rev=' + (rev ? rev : '') + '&description=' + (description ? description : '');
	}
	function updatePreview()
	{
		$('#preview-browser').attr('src', getPreviewURI());
	}
	uFormWindow({
		method: 'POST',
		standardSubmit: true
	}, [{
			fieldLabel: i18n.ui.label.ModuleName,
			name: 'name',
			listeners: {
				change: function(cmp) {
					name = cmp.getValue();
					updatePreview();
				}
			}
		},{
			fieldLabel: i18n.ui.label.OrganisationName,
			name: 'org',
			listeners: {
				change: function(cmp) {
					org = cmp.getValue();
					updatePreview();
				}
			}
		},{
			fieldLabel: i18n.ui.label.Revision,
			name: 'rev',
			value: rev,
			listeners: {
				change: function(cmp) {
					rev = cmp.getValue();
					updatePreview();
				}
			}
		},{
			fieldLabel: i18n.ui.label.Description,
			name: 'description',
			listeners: {
				change: function(cmp) {
					description = cmp.getValue();
					updatePreview();
				}
			}
		},{
			xtype: 'panel',
			title: i18n.ui.title.OperationPreview,
			html: '<iframe id="preview-browser" src="template/export/preview.html" frameborder="0" height="100%" width="100%" transparent="true"></iframe>',
			anchor: '100% -103'
        }], {
        title: i18n.ui.title.ApplicationExport,
        width: 500,
        height: 650
	}, function(win, formPanel) {
		formPanel.getForm().getEl().dom.action = 'ws!export.do';
		formPanel.getForm().submit();
	});
}
function hPlugins()
{
	var installed = [];
	function isInstalled(moduleName) {
		var i;
		for(i = 0;i < installed.length; i ++)
			if(installed[i].name == moduleName)
				return true;
		return false;
	}
	function moduleDisplay(json, contentEl, li, mapping) {
		function getInfo(info, name)
		{
			var pName = mapping[name] ? mapping[name] : name;
			return info[pName];
		}
		var i, ps = i18n.ui.tooltip.PluginStatus;
		for(i = 0; i < json.length; i ++)
			if(json[i])
			{
				var node = li.clone().attr('id', null).removeClass('x-hide-display');
				var info = json[i];
				var title = getInfo(info, 'title');
				var name = getInfo(info, 'name');
				$('.wmdk-title', node).text(title || name || 'Unknown Module');
				$('.wmdk-brief', node).text(getInfo(info, 'brief'));
				$('.wmdk-org', node).text(getInfo(info, 'org'));
				$('.wmdk-rev', node).text(getInfo(info, 'rev'));
				$('.wmdk-name', node).text(name);
				$.each(['hidden', 'required', 'removed'], function(k, v) {
					if(info[v])
						node.addClass('u-' + v);
				});
				node.addClass('u-' + info.status);
				contentEl.append(node);
				var tip = ps[getInfo(info, 'status')];
				if(tip)
					new Ext.ToolTip({
						target: node.get(0),
						html: tip.Content,
						title: tip.Title,
						autoHide: true,
						closable: false
					});
			}
	}
	$.post('plg!list.json', {},
		function(resp) {
			installed = rToJson(resp);
			var contentEl = $('#wmdk-module-installed').clone().attr('id', null);
			var availableContentEl = $('#wmdk-module-available').clone().attr('id', null);
			var li = $('#wmdk-installed-item', contentEl);
			var offset = 0, noMorePlugins = false;
			function availablePluginDisplay(delta) {
				var o = offset + delta;
				if(o >= 0 && !(delta > 0 && noMorePlugins))
				{
					var ul = $('.wmdk-module-manager', availableContentEl).html('');
					var wait = uWait(i18n.ui.title.StandBy);
					availableContentEl.find('input').focus();
					offset = o;
					$.getJSON('http://wikiup.org/repo!explore.json?callback=?&o=' + o, function(json) {
						var li = $('#wmdk-available-item', availableContentEl);
						var i;
						wait.hide();
						noMorePlugins = !json.relative;
						for(i = 0; i < json.relative.length; i ++)
							if(isInstalled(json.relative[i]['artifact.name']))
								json.relative[i] = null;
						moduleDisplay(json.relative, ul, li, {name: 'artifact.name', org: 'artifact.org', title: 'meta.title', brief: 'meta.brief', description: 'meta.description'});
					});
				}
			}
			function hPluginUpdate() {
				var wait = uWait(i18n.ui.title.StandBy);
				$.post('op!update.x', {}, function() {
					wait.hide();
					uAlert(i18n.ui.prompt.PluginInstalled, {fn: function() {window.location.reload()}});
				});
			}
			moduleDisplay(installed, contentEl, li, {});
			var win = new Ext.Window({
					width: 450,
					height: 550,
					layout: 'fit',
					plain: true,
					title: i18n.ui.title.AvailablePlugins,
					bodyStyle: 'padding:5px;',
					items: {
						xtype: 'tabpanel',
						frame: false,
						activeTab: 0,
						items: [{
								title: i18n.ui.text.Installed,
								tbar: ['->', {text: 'Update', width: 80, disabled: !psUpdater, handler: hPluginUpdate, iconCls: 'wmdk-tb-update'}],
								contentEl: contentEl.get(0),
								autoScroll: true
							},{
								title: i18n.ui.text.Available,
								tbar: [{xtype: 'textfield', style: {margin: '1px 0 0 5px'}}, '->', {text: 'Previous', width: 60, iconCls: 'wmdk-tb-previous', handler: function() {
									availablePluginDisplay(-15);
								}}, {text: 'Next', width: 80, iconCls: 'wmdk-tb-next', handler: function() {
									availablePluginDisplay(15);
								}}],
								contentEl: availableContentEl.get(0),
								autoScroll: true,
								listeners: {
									show: function() {
										rDefer(function() {
											availablePluginDisplay(0);
										});
									}
								}
							}
						]
					},
					buttons: [{
						text: i18n.ui.text.OK, handler: function() {
							var params = {};
							var pluginInstalled = false;
							var wait = uWait(i18n.ui.title.StandBy);
							var toInstall = [];
							$('.u-mounting', availableContentEl).each(function() {
								var tag = $(this);
								var data = {};
								$.each(['org', 'rev', 'name'], function(k, v) {
									data[v] = $('.wmdk-' + v, tag).text();
								});
								toInstall.push(data);
								pluginInstalled = true;
							});
							function activate() {
								$('.wmdk-item', contentEl).each(function() {
									var node = $(this);
									var name = node.find('.wmdk-name').text();
									if(node.hasClass('u-disabled'))
										params[name] = 'off';
									if(node.hasClass('u-removed'))
										params[name] = 'removed';
								});
								$.post('plg!activate.x', params, function() {
									win.close();
									wait.hide();
									if(pluginInstalled)
										uAlert(i18n.ui.prompt.PluginInstalled, {fn: function() {window.location.reload()}});
									else
										window.location.reload();
								});
							}
							hMount(toInstall, activate);
						}
					},{
						text: i18n.ui.text.Cancel, handler: function() {win.close()}
					}]
				}
			);
			win.show();
		}
	);
}
function hPluginsItemClicked(tag)
{
	var container = $(tag).parents('.wmdk-module-manager');
	var node = $(tag);
	container.find('.u-selected').removeClass('u-selected');
	node.addClass('u-selected');
}
function hPluginsItemStatusToggle(tag, clazz)
{
	var node = $(tag).parents('.wmdk-item');
	window.setTimeout(function() {node.toggleClass(clazz).removeClass('u-selected');}, 0);
}
function hRenamePage()
{
	if(wmdk.fileExplorerNode)
		Ext.MessageBox.prompt('Rename', 'Please enter new file name:', function(btn, text) {
			if(btn == 'ok')
			{
				var node = wmdk.fileExplorerNode;
				Ext.Ajax.request({
					url: 'ws!rename.json',
					params: {
						uri: node.id,
						name: text
						},
					success: function(resp) {
						node.setText(text);
						node.id = node.parentNode.id + '/' + text;
					}
				});
			}
		});
}
function hAbout()
{
	$.get('plg!revision.json', {module: 'wikiup-plugin-wmdk'}, function(resp) {
		var json = rToJson(resp);
		$('#wmdk-revision').text(json[0].rev);
		win = new Ext.Window({
			title: 'About WMDK',
			layout: 'fit',
			width: 400,
			height: 300,
			plain: true,
			border: false,
			items: {
				xtype: 'panel',
				frame: true,
				contentEl: $('#wmdk-about-panel').clone().get(0)
			},
			buttons: [{
				text: i18n.ui.text.Close,
				handler: function(){
					win.close();
				}
			}]
		});
		win.show();
	});
}
function hChangeDatasource(name, callback)
{
	Ext.Ajax.request({
		url: rURI('ds!default.json'),
		params: {
			ds: name
		},
		success: callback
	});
}
function hChangeEntityManager(name, callback)
{
	Ext.Ajax.request({
		url: rURI('ent!default.json'),
		params: {
			manager: name
		},
		success: callback
	});
}
function hDefineDomain(doScaffold, callback)
{
	var fieldIndex = 0;
	var store;
	var fieldsPanel = new Ext.grid.EditorGridPanel({
			store: store = new Ext.data.ArrayStore({
			fields: ['name', 'fieldName', 'type', 'description'],
			idIndex: 0
		}),
		sm: new Ext.grid.RowSelectionModel({singleSelect:true}),
        cm: new Ext.grid.ColumnModel({
				defaults: {
					sortable: false,
					menuDisabled: true
				},
				columns: [{
					header: i18n.ui.label.Name,
					dataIndex: 'name',
					width: 100,
					editor: new Ext.form.TextField({
						allowBlank: false
					})
				}, {
					header: i18n.ui.label.FieldName,
					dataIndex: 'fieldName',
					width: 100,
					editor: new Ext.form.TextField({
						allowBlank: false
					})
				}, {
					header: i18n.ui.label.Type,
					dataIndex: 'type',
					width: 80,
					editor: uComboBox({
							displayField: 'name',
							valueField: 'name',
							forceSelection: true
						},{
							url: rURI('ent!fieldset.json'),
							fields: ['name']
						})
				}, {
					id: 'description',
					header: i18n.ui.label.Description,
					dataIndex: 'description',
					editor: new Ext.form.TextField({
					})
				}
			]
		}),
        autoExpandColumn: 'description',
        clicksToEdit: 1,
		layout: 'fit',
		tbar: [
				'<b>' + i18n.ui.title.DomainFields + '</b>',
				'->',
				{text: i18n.ui.text.Add, iconCls: 'wmdk-tb-field-add', handler: function() {
						var idx = (++fieldIndex);
						var p = new store.recordType({
							name: 'field' + idx,
							fieldName: 'field' + idx,
							type: 'string'
						});
						fieldsPanel.stopEditing();
						store.add(p);
						fieldsPanel.startEditing(idx, 0);
					}
				},
				{text: i18n.ui.text.Delete, iconCls: 'wmdk-tb-field-delete', handler: function() {
					var sm = fieldsPanel.getSelectionModel();
					if(sm.getCount() > 0)
						store.remove(sm.getSelected());
				}}
		],
		border: true,
		flex: 3
    });

	var inst = uFormWindow({
	}, [{
            fieldLabel: i18n.ui.label.DomainName,
            name: 'domain-name'
        },uSchemaComboBox(
			{}, {
				load: function(store) {
					inst.formPanel.getForm().setValues({
						l: wmdk.schema
					});
				}
			}),{
            fieldLabel: i18n.ui.label.DBTable,
            name: 'table'
        }, uEntityManagerComboBox(
			{},{}
		), uTemplateGroupbox()], {
			title: i18n.ui.title.CreateDomain,
			layout: 'vBox',
			layoutConfig: {
				align: 'stretch',
				pack: 'start'
			},
			items: fieldsPanel
		}, function(win, form) {
				var i;
				var desc = [];
				for(i = 0;i < store.getCount(); i ++)
				{
					var rec = store.getAt(i);
					desc.push(rec.get('fieldName') + ',' + rec.get('type'));
				}
				var vals = form.getForm().getValues();
				if((vals.table || vals['domain-name']) && vals.l)
					form.getForm().submit({
						url: rURI('ent!define.json'),
						params: {
							definition: desc.join('|')
						},
						waitMsg: i18n.ui.prompt.CreatingTable,
						success: function(form, action) {
							uReloadDatasourceExplorer(wmdk.datasourceExplorerNode);
							if(doScaffold)
								_doScaffoldGeneration(form, win, callback);
							else
							{
								win.close();
								rCallback(callback);
							}
						}
					});
		}, false);
	store.add(new store.recordType({
		name: 'id',
		fieldName: 'id',
		type: 'integer',
		description: 'Identity'
	}));
	inst.win.show();
	inst.fieldsPanel = fieldsPanel;
	return inst;
}
function hPageScaffold()
{
	var sm = getEntityManager().getSelectionModel();
	var rec = sm.getSelected();
	if(rec)
	{
		var idx = rec.id.indexOf(':');
		var catalog = rec.data.catalog;
		var schema = rec.data.schema;
		var table = rec.data.table;
		jQuery.post(rURI('scaffold!page.json'), {table: table, l: catalog + '@' + schema, 'domain-name': idx != -1 ? rec.id.substring(idx + 1) : rec.id, manager: idx != -1 ? rec.id.substring(0, idx) : wmdk.entityManager}, function(resp) {
			var json = rToJson(resp);
			hDomainPage(json.name, wmdk.entityManager);
		});
	}
}
function _doScaffoldGeneration(form, win, callback)
{
	form.submit({
		url: rURI('scaffold!gen.json'),
		waitMsg: i18n.ui.prompt.GeneratingDomain,
		success: function(form, action) {
				win.close();
				uHistory('wmdk-history-domain-define');
				wmdk.history.lastDomainURL = '/domain/' + action.result['domain-name'] + '/html/list.html';
				uReloadFileExplorer();
				uReloadEntityManager();
				rCallback(callback);
			}
		});
}
function hDropTable()
{
	var l = wmdk.schema, t = wmdk.table;
	if(l && t && wmdk.datasourceExplorerNode)
		Ext.MessageBox.prompt(i18n.ui.title.Confirm, i18n.ui.prompt.DropTableConfirm, function(btn, text) {
			if(btn == 'ok' && text.toLowerCase() == t.toLowerCase())
			{
				Ext.Ajax.request({
					url: rURI('ds!drop.json'),
					params: {
						l: l,
						table: t
					},
					success: function(resp) {
						wmdk.datasourceExplorerNode.remove();
					}
				});
			}
		});
}
function hReportBug()
{
	window.open('https://sourceforge.net/tracker/?func=add&group_id=241260&atid=1115395');
}
function hDatasourceConfigure(callback)
{
	Ext.Ajax.request({
		url: rURI('ds!properties.json'),
		success: function(resp) {
			var json = Ext.util.JSON.decode(resp.responseText);
			uFormWindow({
			}, [{
					fieldLabel: i18n.ui.label.ConnectionURL,
					name: 'connection-url',
					value: json['connection-url']
				},{
					fieldLabel: i18n.ui.label.User,
					name: 'user',
					value: json.user,
					anchor: '60%'
				},{
					fieldLabel: i18n.ui.label.Password,
					name: 'password',
					value: json.password,
					anchor: '60%'
				},{
					hidden: true,
					name: 'ds',
					value: wmdk.datasourceManager
				}], {
				title: i18n.ui.title.DatasourceConfigure,
				width: 650,
				height: 160
			}, function(win, formPanel) {
				formPanel.getForm().submit({
					url: rURI('ds!configure.json'),
					success: function()	{
						win.close();
						uReloadDatasourceExplorer();
						rCallback(callback);
					}
				});
			});
		}
	});
}
function hRecentDomain()
{
	return uExploreTo(wmdk.history.lastDomainURL, true);
}
function hMount(toInstall, callback)
{
	var progressBox;
	function progress(mid, fin) {
		$.post('plg!mping.json', {mid: mid}, function(resp) {
			var json = rToJson(resp);
			if(json.finished)
				rCallback(fin);
			else
			{
				if(json.contentLength)
				{
					var v = json.bytesReceived / json.contentLength;
					progrossBox.updateProgress(v, json.bytesReceived + ' / ' + json.contentLength, i18n.ui.text.Downloading + ' ' + json.artifactName);
				}
				window.setTimeout(function() {
					$.post('plg!mping.json', {mid: mid}, function() {
						progress(mid, fin);
					});
				}, 1000);
			}
		});
	}
	function mountOne() {
		if(toInstall.length > 0)
			$.post('plg!mount.json', toInstall.pop(), function(resp) {
				var json = rToJson(resp);
				progrossBox = uProgress(i18n.ui.title.StandBy, i18n.ui.text.Downloading);
				progress(json.mid, mountOne);
			});
		else
			rCallback(callback);
	}
	mountOne();
}