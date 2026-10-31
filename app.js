let hlsInstance = null;

function setStatus(msg, type = "info") {
    const el = document.getElementById("statusMessage");
    el.textContent = msg;
    el.className = `status-msg ${type}`;
}

function showOverlay(text) {
    const overlay = document.getElementById("playerOverlay");
    const label = document.getElementById("spinnerText");
    label.textContent = text;
    overlay.classList.remove("hidden");
}

function hideOverlay() {
    const overlay = document.getElementById("playerOverlay");
    overlay.classList.add("hidden");
}

function loadSample(url) {
    document.getElementById("streamInput").value = url;
    handleStreamClick();
}

async function handleStreamClick() {
    const input = document.getElementById("streamInput").value.trim();
    if (!input) {
        setStatus("Please enter or paste a valid link.", "error");
        return;
    }

    setStatus("");
    showOverlay("Resolving stream URL...");

    try {
        let streamUrl = input;

        // If a web URL like https://www.diskwala.com/app/<id> was entered, parse the ID
        if (input.includes("/app/")) {
            const id = input.split("/app/")[1].split("?")[0].split("/")[0];
            setStatus(`Identified Media ID: ${id}. Resolving stream...`, "info");
            
            // Query the backend endpoint discovered during APK analysis
            try {
                const res = await fetch(`https://api.diskwala.com/api/v2/android/media/${id}`, {
                    headers: { "Accept": "application/json" }
                });
                if (res.ok) {
                    const json = await res.json();
                    if (json && json.data && json.data.streamUrl) {
                        streamUrl = json.data.streamUrl;
                    }
                }
            } catch (err) {
                console.warn("Direct API resolve notice:", err);
            }
        }

        playStream(streamUrl);
    } catch (e) {
        hideOverlay();
        setStatus(`Error preparing stream: ${e.message}`, "error");
    }
}

function playStream(url) {
    const video = document.getElementById("videoPlayer");

    // Clean up previous HLS instance if active
    if (hlsInstance) {
        hlsInstance.destroy();
        hlsInstance = null;
    }

    const isHls = url.includes(".m3u8");

    if (isHls && Hls.isSupported()) {
        hlsInstance = new Hls({
            enableWorker: true,
            lowLatencyMode: true
        });

        hlsInstance.loadSource(url);
        hlsInstance.attachMedia(video);

        hlsInstance.on(Hls.Events.MANIFEST_PARSED, () => {
            hideOverlay();
            setStatus("Streaming HLS content...", "info");
            video.play().catch(e => console.log("Auto-play blocked:", e));
        });

        hlsInstance.on(Hls.Events.ERROR, (event, data) => {
            if (data.fatal) {
                hideOverlay();
                setStatus(`HLS playback error: ${data.type} (${data.details})`, "error");
            }
        });
    } else {
        // Native browser player (MP4, WebM, or Safari native HLS)
        video.src = url;
        video.onloadeddata = () => {
            hideOverlay();
            setStatus("Playing media...", "info");
            video.play().catch(e => console.log("Auto-play blocked:", e));
        };
        video.onerror = () => {
            hideOverlay();
            setStatus("Failed to load video stream. Check if the link is accessible.", "error");
        };
    }
}
