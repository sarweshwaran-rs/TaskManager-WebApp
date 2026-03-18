document.addEventListener('DOMContentLoaded', () => {
    const WEBSOCKET_URL = "ws://localhost:8080/ws";

    const statusText = document.getElementById('status-text');
    const connectionStatus = document.getElementById('connection-status');

    // DOM Elements
    const cpuName = document.getElementById('cpu-name');
    const cpuLoadBar = document.getElementById('cpu-load-bar');
    const cpuLoadText = document.getElementById('cpu-load-text');
    const cpuTemp = document.getElementById('cpu-temp');
    const cpuFreq = document.getElementById('cpu-freq');
    const cpuCoreLoads = document.getElementById('cpu-core-loads');
    
    const memUsedBar = document.getElementById('mem-used-bar');
    const memUsedText = document.getElementById('mem-used-text');
    const swapUsedBar = document.getElementById('swap-used-bar');
    const swapUsedText = document.getElementById('swap-used-text');

    const processesTableBody = document.querySelector('#processes-table tbody');
    const osInfo = document.getElementById('os-info');
    const systemUptime = document.getElementById('system-uptime');

    const diskInfoContainer = document.getElementById('disk-info-container');
    const networkInfoContainer = document.getElementById('network-info-container');
    const gpuInfoContainer = document.getElementById('gpu-info-container');

    const client = Stomp.client(WEBSOCKET_URL);
    client.debug = null; // Disable STOMP debug logs in console

    // Helper to format bytes
    const formatBytes = (bytes, decimals = 2) => {
        if (!+bytes) return '0 Bytes';
        const k = 1024;
        const dm = decimals < 0 ? 0 : decimals;
        const sizes = ['Bytes', 'KB', 'MB', 'GB', 'TB'];
        const i = Math.floor(Math.log(bytes) / Math.log(k));
        return `${parseFloat((bytes / Math.pow(k, i)).toFixed(dm))} ${sizes[i]}`;
    };

    const updateCpuInfo = (cpu) => {
        cpuName.textContent = cpu.name;
        
        const systemLoad = parseFloat(cpu.systemLoad);
        cpuLoadBar.style.width = `${systemLoad}%`;
        cpuLoadText.textContent = `${systemLoad.toFixed(2)}%`;

        cpuTemp.textContent = cpu.Temperature;
        cpuFreq.textContent = cpu.frequency;

        cpuCoreLoads.innerHTML = '';
        cpu.perProcessorLoad.forEach((load, index) => {
            const loadValue = parseFloat(load);
            const coreElement = document.createElement('div');
            coreElement.className = 'core-load';
            coreElement.innerHTML = `
                <div class="progress-bar-container" style="height: 8px;">
                    <div class="progress-bar" style="width: ${loadValue}%;"></div>
                </div>
                <span>Core ${index}: ${loadValue.toFixed(1)}%</span>
            `;
            cpuCoreLoads.appendChild(coreElement);
        });

        systemUptime.innerHTML = `<strong>Uptime:</strong> ${cpu.Uptime}`;
    };

    const updateMemoryInfo = (memory) => {
        const total = parseFloat(memory.total);
        const used = parseFloat(memory.used);
        const memPercent = total > 0 ? (used / total) * 100 : 0;
        memUsedBar.style.width = `${memPercent}%`;
        memUsedText.textContent = `${memory.used} / ${memory.total}`;

        const swapTotal = parseFloat(memory.swapTotal);
        const swapUsed = parseFloat(memory.swapUsed);
        if (swapTotal > 0) {
            const swapPercent = (swapUsed / swapTotal) * 100;
            swapUsedBar.style.width = `${swapPercent}%`;
            swapUsedText.textContent = `${memory.swapUsed} / ${memory.swapTotal}`;
        } else {
            swapUsedBar.style.width = '0%';
            swapUsedText.textContent = 'N/A';
        }
    };

    const updateProcesses = (processes) => {
        processesTableBody.innerHTML = '';
        processes.forEach(p => {
            const row = document.createElement('tr');
            row.innerHTML = `
                <td>${p.pid}</td>
                <td>${p.name}</td>
                <td>${p.cpuLoad.toFixed(2)}</td>
                <td>${formatBytes(p.memory)}</td>
            `;
            processesTableBody.appendChild(row);
        });
    };

    const updateOsInfo = (os) => {
        osInfo.innerHTML = `
            <strong>OS:</strong> ${os.family} ${os.version}<br>
            <strong>Manufacturer:</strong> ${os.Manufacturer}
        `;
    };

    const updateDiskInfo = (disks) => {
        diskInfoContainer.innerHTML = '<h2>Disks</h2>';
        disks.forEach(disk => {
            const diskElement = document.createElement('div');
            diskElement.className = 'info-item';
            diskElement.innerHTML = `
                <h3>${disk.Model} (${disk['Disk Size']})</h3>
                <div class="info-grid">
                    <div><strong>Read:</strong> ${disk['Read Speed']}</div>
                    <div><strong>Write:</strong> ${disk['Write Speed']}</div>
                </div>
                <div class="small-text">${disk.Name}</div>
            `;
            diskInfoContainer.appendChild(diskElement);
        });
    };

    const updateNetworkInfo = (networks) => {
        networkInfoContainer.innerHTML = '<h2>Network</h2>';
        networks.forEach(net => {
            const netElement = document.createElement('div');
            netElement.className = 'info-item';
            netElement.innerHTML = `
                <h3>${net['Display Name']} (${net.Type})</h3>
                <div class="info-grid">
                    <div><strong>IPv4:</strong> ${net['IP V4 Address']}</div>
                    <div><strong>Upload:</strong> ${net['Upload Speed']}</div>
                    <div><strong>Download:</strong> ${net['Download Speed']}</div>
                </div>
                <div class="small-text">${net['MAC Address']}</div>
            `;
            networkInfoContainer.appendChild(netElement);
        });
    };

    const updateGpuInfo = (gpus) => {
        gpuInfoContainer.innerHTML = '<h2>Graphics</h2>';
        gpus.forEach(gpu => {
            const gpuElement = document.createElement('div');
            gpuElement.className = 'info-item';
            gpuElement.innerHTML = `
                <h3>${gpu['Device name']}</h3>
                <div class="info-grid">
                    <div><strong>Vendor:</strong> ${gpu.vendor}</div>
                    <div><strong>VRAM:</strong> ${gpu.memory}</div>
                    <div><strong>Version:</strong> ${gpu['version Info']}</div>
                </div>
            `;
            gpuInfoContainer.appendChild(gpuElement);
        });
    };

    const onConnect = () => {
        console.log("STOMP connection established!");
        statusText.textContent = 'Connected';
        connectionStatus.classList.add('connected');
        connectionStatus.classList.remove('error');

        client.subscribe("/api/metrics", (message) => {
            const data = JSON.parse(message.body);
            
            if (data.cpu) updateCpuInfo(data.cpu);
            if (data.memory) updateMemoryInfo(data.memory);
            if (data.processes) updateProcesses(data.processes);
            if (data.os) updateOsInfo(data.os);
            if (data.disk) updateDiskInfo(data.disk);
            if (data.network) updateNetworkInfo(data.network);
            if (data.gpu) updateGpuInfo(data.gpu);
        });
    };

    const onError = (error) => {
        console.error("STOMP Error: ", error);
        statusText.textContent = 'Connection Error';
        connectionStatus.classList.add('error');
        connectionStatus.classList.remove('connected');
    };

    client.connect({}, onConnect, onError);
});
