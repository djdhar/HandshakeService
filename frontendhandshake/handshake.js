
    // Create EventSource to listen to the stream
    const url = "http://127.0.0.1:8999/v1/stream_admin_handshake";
    let eventSource;
    const urlLive = "http://127.0.0.1:8999/v1/stream_live_handshake";
    let eventSourceLive;

    document.getElementById("handshakeForm").addEventListener("submit", async function(event) {
        event.preventDefault();

        const name = document.getElementById("name").value;
        const email = document.getElementById("email").value;
        const messageElement = document.getElementById("message");

        try {
            const response = await fetch("http://127.0.0.1:8999/v1/create_handshake", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify({ name, email })
            });

            if (response.status === 200) {
                messageElement.textContent = `${name} has successfully sent a handshake request.`;
                messageElement.style.color = "green";
            } else {
                messageElement.textContent = "Error occurred while sending a handshake request.";
                messageElement.style.color = "red";
            }
        } catch (error) {
            messageElement.textContent = "Error occurred while sending a handshake request.";
            messageElement.style.color = "red";
        }

        messageElement.style.display = "block";

        // Hide the message after 2 seconds
        setTimeout(() => {
            messageElement.style.display = "none";
        }, 2000);
    });

    document.getElementById("startStream").addEventListener("click", async() => {
            const chatWindow = document.getElementById("chatWindow");
            eventSource = new EventSource(url);
            console.log("event source initiated")
            // Append new messages to the chat window
            eventSource.onmessage = (event) => {
                const message = document.createElement("div");
                message.textContent = event.data; // Add streamed message
                chatWindow.appendChild(message);
                chatWindow.scrollTop = chatWindow.scrollHeight; // Auto-scroll to the bottom
            };

            // Handle errors
            eventSource.onerror = () => {
                const errorMessage = document.createElement("div");
                errorMessage.textContent = "Error occurred. Stopping stream.";
                chatWindow.appendChild(errorMessage);
                eventSource.close();
                document.getElementById("stopStream").disabled = true;
            };

            document.getElementById("startStream").disabled = true;
            document.getElementById("stopStream").disabled = false;
        });

        document.getElementById("stopStream").addEventListener("click", () => {
            if (eventSource) {
                eventSource.close();
                chatWindow.innerHTML = ''; // Clear messages
            }
            document.getElementById("startStream").disabled = false;
            document.getElementById("stopStream").disabled = true;
        });

        document.getElementById("startStreamLive").addEventListener("click", () => {
            const chatWindow = document.getElementById("chatWindowLive");
            eventSourceLive = new EventSource(urlLive);
            // Append new messages to the chat window
            eventSourceLive.onmessage = (event) => {
                const message = document.createElement("div");
                message.textContent = event.data; // Add streamed message
                chatWindowLive.appendChild(message);
                chatWindowLive.scrollTop = chatWindowLive.scrollHeight; // Auto-scroll to the bottom
            };

            // Handle errors
            eventSourceLive.onerror = () => {
                const errorMessage = document.createElement("div");
                errorMessage.textContent = "Error occurred. Stopping stream.";
                chatWindowLive.appendChild(errorMessage);
                eventSourceLive.close();
                document.getElementById("stopStreamLive").disabled = true;
            };

            document.getElementById("startStreamLive").disabled = true;
            document.getElementById("stopStreamLive").disabled = false;
        });

        document.getElementById("stopStreamLive").addEventListener("click", () => {
            if (eventSourceLive) {
                eventSourceLive.close();
                chatWindowLive.innerHTML = ''; // Clear messages
            }
            document.getElementById("startStreamLive").disabled = false;
            document.getElementById("stopStreamLive").disabled = true;
        });
