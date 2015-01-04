function showExample(path) {
    var header = document.getElementById('leprechaun-sample-header').cloneNode(true);
    header.id = '';
    header.style.display = '';
    with (parent) {
        var idx = 0;
        jQuery.get('../leprechaun/examples!list.json', {path: path}, function (resp) {
            var panels = eval('(' + resp + ')');

            function getExampleSrc(panel) {
                return panel.sourceView ? 'ws!get.do?uri=' + panel.src : panel.src;
            }

            function loadExample(i) {
                var tabpanel = Ext.getCmp('tpExamples');
                var p = panels[i];
                tabpanel.removeAll();
                for (i = 0; i < p.length; i++) {
                    var n = p[i];
                    var buf = ['<iframe frameborder="0" width="100%" height="100%" src="',
                        'about:blank',
                        '"></iframe>'
                    ];
                    buf[1] = getExampleSrc(n);
                    var panel = $.extend({
                        layout: 'fit',
                        html: buf.join('')
                    }, n);
                    panel.title += (panel.sourceView ? ' (Source)' : '');
                    tabpanel.add(panel);
                }
                tabpanel.setActiveTab(0);
            }

            function next() {
                if (idx + 1 < panels.length)
                    loadExample(++idx);
            }

            function previous() {
                if (idx > 0)
                    loadExample(--idx);
            }

            var win = new Ext.Window({
                width: 860,
                height: 620,
                layout: {
                    type: 'vbox',
                    padding: '2',
                    align: 'stretch'
                },
                plain: true,
                title: 'Examples',
                items: [{
                    xtype: 'panel',
                    layout: {
                        type: 'hbox',
                        padding: '2',
                        align: 'stretch'
                    },
                    flex: 1,
                    margins: '0 0 5 0',
                    items: [
                        {xtype: 'button', text: 'Previous', flex: 1, handler: previous},
                        {xtype: 'panel', flex: 10, margins: '5', html: header.innerHTML, border: false},
                        {xtype: 'button', text: 'Next', flex: 1, handler: next}
                    ]
                },
                    {
                        xtype: 'tabpanel',
                        id: 'tpExamples',
                        frame: false,
                        flex: 7,
                        activeTab: 0,
                        items: []
                    }]
            });
            win.show();
            loadExample(0);
        });
    }
}

function pluginPromoteConfirm(pluginName) {
    with (parent) {
        uConfirm(rFormat(leprechaun.i18n.PluginConfirm, [pluginName]), function () {
            var url = 'http://wikiup.org/repo!rev.json?callback=?&name=' + pluginName;
            var wait = uWait(i18n.ui.title.StandBy);
            jQuery.getJSON(url, function (json) {
                hMount([{org: 'wikiup', rev: json.rev, name: pluginName}], function () {
                    $.post(rURI('plg!reload.x'), function () {
                        wait.hide();
                        alert(i18n.ui.prompt.PluginInstalled);
                        window.location.reload();
                    });
                });
            });
        });
    }
}