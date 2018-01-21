$(document).ready(function () {
    var gameStartedEvent = "GameStarted";
    var cellChangedEvent = "CellChanged";
    var attackActionResultEvent = "AttackActionResult";
    var playerWonEvent = "PlayerWon";

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

        var jsonData = JSON.parse(event.data);
        switch (jsonData.event) {
            case gameStartedEvent:
                var openingImage = document.getElementById("openingImage");
                if (openingImage !== null) {
                    openingImage.remove();
                }
                var wonImage = document.getElementById("wonImage");
                if (wonImage !== null) {
                    wonImage.remove();
                }

                createNewTable(jsonData);
                break;
            case cellChangedEvent:
                updateTable(jsonData);
                break;
            case attackActionResultEvent:
                showActionImage(jsonData);
                break;
            case playerWonEvent:
                showPlayerWonImage(jsonData);
                break;
            default:
                break;
        }
    }
});

function showPlayerWonImage(json) {
    var tableGrid = document.getElementById("grid");
    tableGrid.remove();

    var content = document.getElementById("content");
    var src = json["wonImagePath"];
    var image = createImageSizeUndefined(src);
    image.id = "wonImage";
    content.appendChild(image);
}

function showActionImage(json) {
    var rowIndex = json["rowIndex"];
    var columnIndex = json["columnIndex"];
    var attackSrc = json["attackImagePath"];
    var cellContentImageSrc = json["cellContentImagePath"];
    var cellContent = "";
    if (cellContentImageSrc !== "undefined") {
        cellContent = createImage(cellContentImageSrc)
    }
    var attackCellContent = createImage(attackSrc);

    updateCell(rowIndex, columnIndex, attackCellContent);
    setTimeout(function () {
        updateCell(rowIndex, columnIndex, cellContent)
    }, 1000)
}

function updateTable(json) {
    var cells = json["cells"];

    for (var i = 0; i < cells.length; i++) {
        var cell = cells[i];

        var rowIndex = cell["rowIndex"];
        var columnIndex = cell["columnIndex"];

        var src = cell["cellContentImagePath"];
        var cellContent = "";
        if (src !== "undefined") {
            cellContent = createImage(src)
        }
        updateCell(rowIndex, columnIndex, cellContent)
    }
}

function updateCell(rowIndex, columnIndex, cellContent) {
    var tableGrid = document.getElementById("grid");
    if (tableGrid) {
        if (tableGrid.hasChildNodes()) {
            var cell = tableGrid.rows[rowIndex].cells[columnIndex];
            while (cell.hasChildNodes()) {
                cell.removeChild(cell.firstChild);
            }
            if (cellContent !== "") {
                tableGrid.rows[rowIndex].cells[columnIndex].appendChild(cellContent);
            }
        }
    }
}

function createImage(src) {
    var image = document.createElement("IMG");
    image.src = src;
    image.height = 50;
    image.width = 50;
    return image;
}

function createImageSizeUndefined(src) {
    var image = document.createElement("IMG");
    image.src = src;
    return image;
}

function createNewTable(json) {

    var grid = document.getElementById("grid")
    if (grid != null) {
        grid.remove();
    }
    var content = document.getElementById("content");
    var tableGrid = document.createElement("table");
    tableGrid.id = "grid";
    content.appendChild(tableGrid);

    var levelBackgroundImagePath = json["levelBackgroundImagePath"]
    var rows = json["rows"];

    for (var i = 0; i < rows.length; i++) {
        var obj = rows[i];
        var tr = document.createElement('TR');
        tableGrid.appendChild(tr);
        for (var j = 0; j < obj.length; j++) {
            var td = document.createElement('TD');
            td.height = '60';
            td.width = '60';
            var objJ = obj[j];
            if (objJ["cellContentImagePath"] !== "undefined") {
                var image = createImage(objJ["cellContentImagePath"]);
                td.appendChild(image);
            }

            tr.appendChild(td);

        }
    }
    tableGrid.setAttribute('background', levelBackgroundImagePath);
}