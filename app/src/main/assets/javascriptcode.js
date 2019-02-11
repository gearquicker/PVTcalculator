function plot(xJsonSurf, yJsonSurf, zJsonSurf, xPoint, yPoint, zPoint) {
    var x = JSON.parse(xJsonSurf);
    var y = JSON.parse(yJsonSurf);
    var z = JSON.parse(zJsonSurf);
    var layout = {
        scene: {
            aspectmode:'cube',
            xaxis: {title: 'P, atm'},
            yaxis: {title: 'T, C'},
            zaxis: {title: ''}
        }
    };

    var data = [{
        z: z,
        x: x,
        y: y,
        type: 'surface'
    }, {
        z: [parseFloat(zPoint)],
        x: [parseFloat(xPoint)],
        y: [parseFloat(yPoint)],
        mode: 'markers',
        type: 'scatter3d',
        marker: {
            color: 'rgb(23, 190, 207)',
            size: 10
        }
    }];

    Plotly.newPlot('plotter', data, layout);
}