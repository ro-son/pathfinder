function MapWrapper() {
    const [map, setMap] = React.useState();
    const mapRef = React.useRef();
    mapRef.current = map;

    const [select, setSelect] = React.useState("first");
    const selectRef = React.useRef();
    selectRef.current = select;

    const [firstCoord, setFirstCoord] = React.useState();
    const firstCoordRef = React.useRef();
    firstCoordRef.current = firstCoord;

    const [secondCoord, setSecondCoord] = React.useState();
    const secondCoordRef = React.useRef();
    secondCoordRef.current = secondCoord;

    React.useEffect( () => {
        const initialMap = new ol.Map({
            target: 'map',
            layers: [
                new ol.layer.Tile({
                    source: new ol.source.OSM()
                })
            ],
            view: new ol.View({
                center: ol.proj.fromLonLat([-79.38084448091215, 43.64798583561901]),
                zoom: 16
            })
        });

        initialMap.on('click', handleMapClick);
        setMap(initialMap);

        $.ajax({
            type: "POST",
            contentType: "application/json",
            url: "/loadmap",
            success: function(data) {
                var features = [];

                for (var i = 0; i < data.length; i++) {
                    var marker = new ol.Feature({
                        geometry: new ol.geom.Point(ol.proj.fromLonLat([data[i][1], data[i][0]]))
                    });
                    features.push(marker);
                }

                var layer = new ol.layer.Vector({
                    source: new ol.source.Vector({
                        features: features
                    }),
                    style: new ol.style.Style({
                        image: new ol.style.Icon({
                            anchor: [0.005, 5],
                            anchorXUnits: 'fraction',
                            anchorYUnits: 'pixels',
                            scale: 0.05,
                            src: 'https://openlayers.org/en/latest/examples/data/icon.png'
                        })
                    })
                });

                initialMap.addLayer(layer);
            }
        });
    }, [])

    const handleMapClick = (event) => {
        const clickedCoord = mapRef.current.getCoordinateFromPixel(event.pixel);
        const transformedCoord = ol.proj.transform(clickedCoord, 'EPSG:3857', 'EPSG:4326');

        if (selectRef.current == "first") {
            setFirstCoord(transformedCoord);
            setSelect(select => { return "second"; });
        } else if (selectRef.current == "second") {
            setSecondCoord(transformedCoord);
            setSelect(select => { return "first"; });

            $.ajax({
                type: "GET",
                contentType: "application/json",
                url: "/findpath",
                data: {
                    "firstlat": firstCoordRef.current[1],
                    "firstlon": firstCoordRef.current[0],
                    "secondlat": secondCoordRef.current[1],
                    "secondlon": secondCoordRef.current[0]
                },
                success: function(data) {
                    var path = [];
                    for (var i = 0; i < data.length; i++) {
                        path.push([data[i][1], data[i][0]]);
                    }

                    const lineString = new ol.geom.LineString(path);
                    lineString.transform('EPSG:4326', 'EPSG:3857');

                    const featureLayer = new ol.Feature({
                        geometry: lineString
                    });
                    const source = new ol.source.Vector({
                        features: [featureLayer]
                    });
                    var vector = new ol.layer.Vector({
                        source: source,
                        style: new ol.style.Style({
                            stroke: new ol.style.Stroke({
                                color: "red",
                                width: 3
                            })
                        })
                    });

                    var layerToRemove;
                    mapRef.current.getLayers().forEach(function(layer) {
                        if (layer.get('name') != undefined && layer.get('name') === 'currentPath') {
                            layerToRemove = layer;
                        }
                    });
                    mapRef.current.removeLayer(layerToRemove);

                    vector.set('name', 'currentPath');
                    mapRef.current.addLayer(vector);
                }
            });
        }
    }

    return (
        <div>
            { select == "first" ? "SELECT FIRST POINT" : "SELECT SECOND POINT" }
        </div>
    )
}

ReactDOM.render(
    <MapWrapper />,
    document.getElementById('message')
)