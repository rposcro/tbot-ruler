window.saySomething = function() {
    console.log("saying something");
}

window.jsonEditorInit = function(divId) {
    const container = document.getElementById(divId);
    const options = {
        "modes": [ "tree", "code", "form", "text", "view" ],
        "mode": "code"
    };
    window._jsonEditor = new JSONEditor(container, options);
}

window.jsonEditorSet = function(payload) {
    window._jsonEditor.set(payload);
}

window.jsonEditorGet = function() {
    return window._jsonEditor.get();
}