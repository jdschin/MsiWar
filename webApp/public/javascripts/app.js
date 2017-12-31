$(document).ready(function () {
    if ("WebSocket" in window) {
        console.log("WebSocket is supported by your Browser!");
    } else {
        console.log("WebSocket NOT supported by your Browser!");
        return;
    }
    var getScriptParamUrl = function () {
        var scripts = document.getElementsByTagName('script');
        var lastScript = scripts[scripts.length - 1];
        return lastScript.getAttribute('data-url');
    };

    var url = getScriptParamUrl();
    var connection = new WebSocket(url);

    connection.onmessage = function (event) {
        var openingImage = document.getElementById("openingImage");
        if(openingImage !== null) {
            openingImage.remove();
        }
        addTable(JSON.parse(event.data))
    }
});

function addTable(json) {
    var tableGrid = document.getElementById("grid");
    while (tableGrid.hasChildNodes()) {
        tableGrid.removeChild(tableGrid.firstChild);
    }

    var levelBackgroundImagePath = json["levelBackgroundImagePath"]
    var rows = json["rows"]

    for (var i = 0; i < rows.length; i++) {
        var obj = rows[i];
        var tr = document.createElement('TR');
        tableGrid.appendChild(tr);
        for (var j = 0; j < obj.length; j++) {
            var td = document.createElement('TD');
            td.height = '60'
            td.width = '60'
            var objJ = obj[j];
            if (objJ["path"] !== "undefined") {
                var image = document.createElement("IMG");
                image.src = objJ["path"]
                image.height = '50'
                image.width = 50
                td.appendChild(image);
            }

            tr.appendChild(td);

        }
    }
    tableGrid.setAttribute('background', levelBackgroundImagePath)
}