var updateInterval = 500;
var maxPathData = 8;
var pathsData = {};

function updateCurrentPosition(name, newX, newY) {
    if (!pathsData[name]) {
        pathsData[name] = [];
    }
    pathsData[name].push({ x: newX, y: newY });

    if (pathsData[name].length > maxPathData) {
        pathsData[name].shift();
    }
}

function drawPath() {
    var canvas = document.getElementById('canvas');

    // Clear existing dots and paths
    while (canvas.firstChild) {
        canvas.removeChild(canvas.firstChild);
    }

    Object.keys(pathsData).forEach(name => {
        var path = pathsData[name];
        for (var i = 0; i < path.length; i++) {
            var dot = document.createElement('div');
            dot.className = i === path.length - 1 ? 'latest-dot' : 'dot';
            dot.style.left = path[i].x + 'px';
            dot.style.top = path[i].y + 'px';
            canvas.appendChild(dot);

            if (i < path.length - 1) {
                var line = document.createElement('div');
                line.className = 'line';
                var length = Math.sqrt(Math.pow(path[i + 1].y - path[i].y, 2) + Math.pow(path[i + 1].x - path[i].x, 2));
                line.style.width = length + 'px';
                line.style.height = '2px';
                var angle = Math.atan2(path[i + 1].y - path[i].y, path[i + 1].x - path[i].x);
                line.style.transform = 'rotate(' + angle + 'rad)';
                line.style.left = path[i].x + 'px';
                line.style.top = path[i].y + 'px';
                canvas.appendChild(line);
            }
        }
    });
}

setInterval(function() {
    var positionTable = document.getElementById("position-table");
    var rows = positionTable.querySelectorAll("tbody tr");

    rows.forEach(row => {
        var name = row.cells[0].textContent;
        var x = parseFloat(row.cells[1].textContent);
        var y = parseFloat(row.cells[2].textContent);
        updateCurrentPosition(name, x, y);
    });

    drawPath();
}, updateInterval);

drawPath();
