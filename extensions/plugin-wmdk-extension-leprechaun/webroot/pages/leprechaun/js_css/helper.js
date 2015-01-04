function hCreateTestPage() {
    with (parent) {
        hNewPage(function () {
            uProspective('resource-explorer', false);
            leprechaunSpotlight('fileExplorer');
            uAlert(leprechaun.i18n.PagePostCreate, {fn: leprechaunSpotlightoff, modal: false, minWidth: 300});
        });
        leprechaunTooltip('newPageFileName', leprechaun.i18n.NewPageFileName, 1000, 5000, 'bl');
        leprechaunTooltip('newPageFileType', leprechaun.i18n.NewPageFileType, 3000);
        leprechaunTooltip('btn-ok', leprechaun.i18n.NewPageBtnOk, 5000);
    }
}
function hImportTestDomain() {
    with (parent) {
        uProspective('datasource-explorer', false);
        loadTip(32, function () {
            hImportDomain(hDomainPostGenerate);
            leprechaunTooltip('importDomainSchema', leprechaun.i18n.ImportDomainSchema, 1000, 5000, 'bl');
            leprechaunTooltip('importDomainTable', leprechaun.i18n.ImportDomainTable, 3000);
            leprechaunTooltip('importDomainEntityManager', leprechaun.i18n.ImportDomainEngine, 5000);
            leprechaunTooltip('btn-ok', leprechaun.i18n.DomainBtnOk, 7000);
        }, function () {
            leprechaunSpotlight('tbImportDomain');
        });
    }
}
function hDomainPostGenerate() {
    with (parent) {
        uProspective('domain-manager', false);
        leprechaunSpotlight('entityManager');
        uAlert(leprechaun.i18n.DomainPostGenerate, {fn: leprechaunSpotlightoff, modal: false, minWidth: 300});
    }
}
function hCreateTestDomain() {
    with (parent) {
        var inst = hDefineDomain(true, hDomainPostGenerate);
        inst.formPanel.getForm().setValues({'table': 't_test_table', 'domain-name': 'tTestTable'});
        leprechaunTooltip('btn-ok', leprechaun.i18n.DomainBtnOk, 2000, 10000);
        var store = inst.fieldsPanel.getStore();
        store.loadData([
            ['id', 'id', 'integer', 'Identity'],
            ['firstName', 'first_name', 'string', 'First Name'],
            ['lastName', 'last_name', 'string', 'Last Name'],
            ['zip', 'zip', 'integer', 'ZIP Code']
        ]);
    }
}
function hConfigureDatasources() {
    with (parent) {
        loadTip(9, function () {
            hDatasourceConfigure(function () {
                uProspective('datasource-explorer', false);
                leprechaunSpotlight('datasourceExplorer');
                uAlert(leprechaun.i18n.DatasourcePostConfig, {fn: leprechaunSpotlightoff, modal: false, minWidth: 300});
            });
        });
    }
}
function hWelcomeConfigurePlugins() {
    with (parent) {
        loadTip(28, hPlugins);
    }
}
function hWelcomeExportApplication() {
    with (parent) {
        loadTip(27, hExport);
    }
}