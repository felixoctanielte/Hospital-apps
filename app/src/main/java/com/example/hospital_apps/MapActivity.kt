package com.example.hospital_apps

import android.os.Bundle
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity

class MapActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

        val webView: WebView = findViewById(R.id.geoMapFull)
        val webSettings: WebSettings = webView.settings
        webSettings.javaScriptEnabled = true
        webSettings.domStorageEnabled = true
        webView.webViewClient = WebViewClient()

        // ==== Ganti API KEY kamu di sini ====
        val apiKey = "283f56a80bb04fd6a65cd9b98b46f18e"

        // Lokasi awal dan tujuan (contoh)
        val startLat = -6.2012
        val startLon = 106.8164
        val destLat = -6.2146
        val destLon = 106.8451

        val htmlContent = """
            <!DOCTYPE html>
            <html>
            <head>
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <link rel="stylesheet" href="https://unpkg.com/leaflet@1.7.1/dist/leaflet.css" />
                <script src="https://unpkg.com/leaflet@1.7.1/dist/leaflet.js"></script>
                <style>
                    html, body { margin:0; height:100%; }
                    #map { width:100%; height:100%; }
                </style>
            </head>
            <body>
                <div id="map"></div>
                <script>
                    const apiKey = "$apiKey";
                    const start = [$startLat, $startLon];
                    const end = [$destLat, $destLon];
                    const map = L.map('map').setView(start, 13);

                    // Tambahkan peta dasar dari Geoapify
                    L.tileLayer('https://maps.geoapify.com/v1/tile/osm-carto/{z}/{x}/{y}.png?apiKey=' + apiKey, {
                        attribution: '© OpenStreetMap contributors, © Geoapify'
                    }).addTo(map);

                    // Marker awal dan akhir
                    L.marker(start).addTo(map).bindPopup("Lokasi Anda").openPopup();
                    L.marker(end).addTo(map).bindPopup("Tujuan (RS)");

                    // Ambil data rute dari Geoapify Routing API
                    const url = `https://api.geoapify.com/v1/routing?waypoints=${'$'}{start[0]},${'$'}{start[1]}|${'$'}{end[0]},${'$'}{end[1]}&mode=drive&apiKey=${'$'}{apiKey}`;

                    fetch(url)
                        .then(response => response.json())
                        .then(result => {
                            const coords = result.features[0].geometry.coordinates[0];
                            const latlngs = coords.map(c => [c[1], c[0]]);
                            L.polyline(latlngs, {color: 'blue', weight: 5}).addTo(map);
                            map.fitBounds(L.polyline(latlngs).getBounds());
                        })
                        .catch(err => console.error(err));
                </script>
            </body>
            </html>
        """.trimIndent()

        webView.loadDataWithBaseURL(null, htmlContent, "text/html", "UTF-8", null)
    }
}
