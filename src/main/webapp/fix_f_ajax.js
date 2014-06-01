/*
 * http://stackoverflow.com/questions/11408130/jsf-commandbutton-works-on-second-click
 * 
 * This is for a bug fix in Mojarra 2.0-2.2. When a component is rendered by AJAX,
 * it will not be included in the View tree of the page the first time it was loaded, 
 * and if that component contains new AJAX components especially <f:ajax> then the
 * listeners in the new component would not work the first time this component is
 * loaded. Subsequently when refreshed, the entire page is submitted back to the 
 * server where the new component is restored as part of the Restore View phase 
 * and its listeners are wired up. Subsequently, the new listeners would work.
 * 
 * This Javascript code is to solve this issue.
 */

jsf.ajax.addOnEvent(function(data) {
    if (data.status == "success") {
        fixViewState(data.responseXML);
    }
});

function fixViewState(responseXML) {
    var viewState = getViewState(responseXML);

    if (viewState) {
        for (var i = 0; i < document.forms.length; i++) {
            var form = document.forms[i];

            if (form.method == "post") {
                if (!hasViewState(form)) {
                    createViewState(form, viewState);
                }
            }
            else { // PrimeFaces also adds them to GET forms!
                removeViewState(form);
            }
        }
    }
}

function getViewState(responseXML) {
    var updates = responseXML.getElementsByTagName("update");

    for (var i = 0; i < updates.length; i++) {
        var update = updates[i];

        if (update.getAttribute("id").match(/^([\w]+:)?javax\.faces\.ViewState(:[0-9]+)?$/)) {
            return update.firstChild.nodeValue;
        }
    }

    return null;
}

function hasViewState(form) {
    for (var i = 0; i < form.elements.length; i++) {
        if (form.elements[i].name == "javax.faces.ViewState") {
            return true;
        }
    }

    return false;
}

function createViewState(form, viewState) {
    var hidden;

    try {
        hidden = document.createElement("<input name='javax.faces.ViewState'>"); // IE6-8.
    } catch(e) {
        hidden = document.createElement("input");
        hidden.setAttribute("name", "javax.faces.ViewState");
    }

    hidden.setAttribute("type", "hidden");
    hidden.setAttribute("value", viewState);
    hidden.setAttribute("autocomplete", "off");
    form.appendChild(hidden);
}

function removeViewState(form) {
    for (var i = 0; i < form.elements.length; i++) {
        var element = form.elements[i];
        if (element.name == "javax.faces.ViewState") {
            element.parentNode.removeChild(element);
        }
    }
}