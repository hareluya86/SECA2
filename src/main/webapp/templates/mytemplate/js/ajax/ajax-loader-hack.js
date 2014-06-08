
function bindajax(id) {
    var cl = new CanvasLoader(id);
    cl.show(); // Hidden by default
}
$(document).ready(function() {
    //$('.ui-shadow').removeClass('ui-shadow');//removing the loginbox shadow borders by PF
    bindajax('canvasloader-container');
});