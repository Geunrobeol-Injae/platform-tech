var updateInterval = 500;
var maxPathData = 8;

var maxStride = 5;
var maxError = 4;
var maxErrorAngle = 360 - (maxError * 1);

var moveIndex = 0;
var pathData = [];

// TODO for simulation
var movePoints = [
    { x: 300, y: 189 },
    { x: 201, y: 201 },
    { x: 140, y: 200 },
    { x: 142, y: 201 }
];

pathData[0] = { x: 184, y: 82 };

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

function calculateDistance(dot, fixedDot) {
    return Math.sqrt(Math.pow(dot.y - parseInt(fixedDot.top), 2) + Math.pow(dot.x - parseInt(fixedDot.left), 2)).toFixed(2);
}

function toRSSI(distance) {
    // Assuming a simple linear mapping for demonstration
    // This is just an example and not accurate for all scenarios
    const slope = (20 - 150) / (350 - 0);
    const intercept = 20;

    // Calculate RSSI using the linear equation: RSSI = slope * distance + intercept
    let rssi = slope * distance + intercept;

    // Ensure the result is within the desired range
    rssi = Math.min(-20, Math.max(-150, rssi));

    // Round to the nearest integer
    return Math.round(rssi);
}

function generateNewPosition() {
    var lastPosition = pathData.length > 0 ? pathData[pathData.length - 1] : { x: 0, y: 0 };
    var targetPosition = movePoints[moveIndex];

    var deltaX = Math.min(maxStride * 2, Math.abs(targetPosition.x - lastPosition.x)) * Math.sign(targetPosition.x - lastPosition.x);
    var deltaY = Math.min(maxStride * 2, Math.abs(targetPosition.y - lastPosition.y)) * Math.sign(targetPosition.y - lastPosition.y);

    var newX = lastPosition.x + deltaX;
    var newY = lastPosition.y + deltaY;

    if (Math.abs(newX - targetPosition.x) <= maxError-1 && Math.abs(newY - targetPosition.y) <= maxError-1) {
        // If the target is reached, move to the next checkpoint
        moveIndex = (moveIndex + 1) % movePoints.length;
    } else {
        // Calculate the same direction as transformation in deltaX and deltaY
        var directionX = Math.sign(deltaX);
        var directionY = Math.sign(deltaY);

        // Add additional random error within maxErrorAngle degrees after calculating the new position
        var errorAngle = (Math.random() * maxErrorAngle - maxErrorAngle / 2) * (Math.PI / 180); // Convert degrees to radians
        var errorMagnitude = Math.random() * maxError * Math.sqrt(maxStride);

        var errorX = Math.cos(errorAngle) * errorMagnitude;
        var errorY = Math.sin(errorAngle) * errorMagnitude;

        newX += errorX * directionX;
        newY += errorY * directionY;
    }

    pathData.push({ x: newX, y: newY });

    if (pathData.length > maxPathData) {
        pathData.shift();
    }
}

setInterval(function() {
    generateNewPosition();
    drawPath();
}, updateInterval);

drawPath();