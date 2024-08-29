window.saySomething = function() {
    console.log("jsonEditor actions loaded");
}

window.jsonEditorInit = function(divId) {
    console.log("jsonEditor init @" + divId);
    const container = document.getElementById(divId);
    const options = {
        "modes": [ "tree", "code", "form", "text", "view" ],
        "mode": "code"
    };
    window._jsonEditor = new JSONEditor(container, options);
}

window.jsonEditorSet = function(payload) {
    console.log("jsonEditor init set payload");
    window._jsonEditor.set(payload);
}

window.jsonEditorGet = function() {
    console.log("jsonEditor init get payload");
    return window._jsonEditor.get();
}