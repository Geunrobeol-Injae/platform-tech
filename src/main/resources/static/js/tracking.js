var updateInterval = 500;
var maxPathData = 8;
var pathData = [];
var currentPosition = { x: 0, y: 0 };

// WebSocket에서 좌표를 받아서 현재 위치를 업데이트하는 함수
function updateCurrentPosition(newX, newY) {
    currentPosition.x = newX;
    currentPosition.y = newY;
}

function drawPath() {
    var canvas = document.getElementById('canvas');

    while (canvas.firstChild) {
        canvas.removeChild(canvas.firstChild);
    }

    for (var i = 0; i < pathData.length; i++) {
        var dot = document.createElement('div');
        dot.className = i === pathData.length - 1 ? 'latest-dot' : 'dot';
        dot.style.left = pathData[i].x + 'px';
        dot.style.top = pathData[i].y + 'px';
        canvas.appendChild(dot);

        if (i < pathData.length - 1) {
            var line = document.createElement('div');
            line.className = 'line';
            var length = Math.sqrt(Math.pow(pathData[i + 1].y - pathData[i].y, 2) + Math.pow(pathData[i + 1].x - pathData[i].x, 2));
            line.style.width = length + 'px';
            line.style.height = '2px';
            var angle = Math.atan2(pathData[i + 1].y - pathData[i].y, pathData[i + 1].x - pathData[i].x);
            line.style.transform = 'rotate(' + angle + 'rad)';
            line.style.left = pathData[i].x + 'px';
            line.style.top = pathData[i].y + 'px';
            canvas.appendChild(line);
        }
    }
}

function generateNewPosition() {
    pathData.push({ x: currentPosition.x, y: currentPosition.y });

    if (pathData.length > maxPathData) {
        pathData.shift();
    }
}

setInterval(function() {
    generateNewPosition();
    drawPath();
}, updateInterval);

drawPath();


