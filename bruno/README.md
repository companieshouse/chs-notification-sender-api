# CHS Notification Sender API - Bruno Collection

This is a Bruno HTTP collection for testing chs-notification-sender-api.

## Usage

### Using Bruno UI
1. Open Bruno and load this collection
2. Choose an environment: local, cidev, or phoenix
3. Send requests through the UI

### Using Bruno CLI 


```bash
# Install bruno
brew install bruno

# Install the bruno cli
npm install -g @usebruno/cli
```

From the collection directory:
```bash
# Run a specific request
bru run ./requests/healthcheck.bru --env local

# Run all requests
bru run ./requests --env cidev
```

Each request contains the required headers for authentication, including ERIC identity headers needed for Companies House API access.
