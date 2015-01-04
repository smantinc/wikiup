var autoCompleteChoices = [];

function autoComplete(tag) {
    var value = tag.value;
    var parsed = parseCommand(tag.value);
    if (parsed.arg)
        for (var key in autoCompleteChoices)
            if (autoCompleteChoices[key].indexOf(parsed.arg) == 0) {
                tag.value = parsed.cmd + ' ' + autoCompleteChoices[key];
                return;
            }
}

function onConsoleKeyPress(evt, tag) {
    var e = evt || window.event;
    switch (e.keyCode) {
        case 9:
            autoComplete(tag);
            return false;
    }
    return true;
}

function parseCommand(line) {
    var idx = line.indexOf(' ');
    var cmd = idx != -1 ? line.substring(0, idx) : line;
    var arg = idx != -1 ? line.substring(idx + 1) : '';
    return {cmd: cmd, arg: arg};
}

function onConsoleKeyUp(evt, tag) {
    var e = evt || window.event;
    $('#font-gauge').text(tag.value);
    if (e.keyCode == 13) {
        $('<div class="console-cmd">' + tag.value + '</div>').insertBefore('#stdout');
        var parsed = parseCommand(tag.value);
        var cmd = parsed.cmd;
        var arg = parsed.arg;
        $.post('console!' + cmd + '.json', {arg: arg}, function (resp) {
            var json = eval('(' + resp + ')');
            var tpl = $('#resp-tpl .' + cmd);
            if (cmd == 'cd')
                autoCompleteChoices = [];
            if (json instanceof Array) {
                for (var key in json)
                    _putLine(cmd, json[key], tpl);
            }
            else
                _putLine(cmd, json, tpl);
            $(window).scrollTop(Math.max(0, $('.console-screen').height() - $(window).height()));
        });
        tag.value = '';
    }
}

var decorations = {
    ls: function (json, tag) {
        $('.name', tag).addClass(json.folder ? 'folder' : 'file');
        if (!json.name)
            $(tag).remove();
    }
}

function _putLine(cmd, json, tpl) {
    var line = tpl.clone().attr('id', null);
    for (var k in json) {
        var v = json[k];
        if (typeof v == 'string')
            autoCompleteChoices.push(v);
        $(line).find('.' + k).text(json[k]);
    }
    $(line).insertBefore('#stdout');
    var p = $(line).parent().get(0);
    if (p)
        while (p.childNodes.length > 200)
            $(p.childNodes[0]).remove();
    if (decorations[cmd])
        decorations[cmd].call(this, json, line);
}
function _consoleFocus() {
    $('#console-line').focus();
}
$(window).load(_consoleFocus).click(_consoleFocus);
