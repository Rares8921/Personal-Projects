# Desktop Notifier

<div align="center">

![Python](https://img.shields.io/badge/Python-3.7+-blue?style=for-the-badge&logo=python)
![Plyer](https://img.shields.io/badge/Plyer-Notifications-orange?style=for-the-badge)

**Cross-platform system notifications from Python. Send alerts, reminders, and custom messages to your desktop.**

[What It Does](#what-it-does) • [Tech Stack](#tech-stack) • [Getting Started](#getting-started)

</div>

---

## What It Does

This is a Python-based notification system that sends native desktop alerts using the Plyer library. It provides a simple API for triggering system notifications with custom titles, messages, icons, and durations.

**Features:**
- Send native OS notifications (Windows/macOS/Linux)
- Custom notification titles and messages
- Custom app icon support
- Configurable timeout duration
- Non-blocking notifications (returns immediately)

**Use cases:**
- Remind yourself of tasks/events
- Alert on script completion (long-running tasks)
- System monitoring alerts
- Timer/alarm applications
- Scheduled notifications

**Current implementation:**
Sends a test notification with title "Desktop Notifier" and message "This is a test notification" for 3 seconds.

---

## Tech Stack

**Language:** Python 3.7+  
**Library:** Plyer (cross-platform notification wrapper)  
**Platform Support:** Windows 10 toast notifications, macOS Notification Center, Linux notify-send

### Architecture

```
Python Application
         ↓
send_notification() Function
         ↓
Plyer Notification API
         ↓
OS-Specific Backend
(Windows: win10toast)
(macOS: NSUserNotification)
(Linux: notify-send)
         ↓
Native Desktop Notification
```

**Implementation Details:**
- Plyer abstracts OS differences (write once, run anywhere)
- Notification parameters: title, message, app_name, app_icon, timeout
- Icon path must be absolute (currently hardcoded)
- Timeout in seconds (3s default)
- Returns immediately (asynchronous by nature)

---

## Project Structure

```
Desktop notifier/
├── main.py              # Notification sender function
├── clipboard.ico        # Custom notification icon
├── Notification.png     # Screenshot of notification
└── README.md
```

**Code structure:**
- Lines 1-2: Import Plyer notification module
- Lines 4-6: `send_notification()` wrapper function
- Lines 8-11: Test execution (if __name__ == "__main__")
- Line 14: Reference to Plyer test suite

---

## Getting Started

**Requirements:**
- Python 3.7+
- Plyer library

**Installation:**

```bash
pip install plyer
```

**Run it:**

```bash
python main.py
```

A desktop notification will appear in the system notification area for 3 seconds.

**Usage in your code:**

```python
from plyer import notification

notification.notify(
    title='Task Complete',
    message='Your script has finished running.',
    app_name='My App',
    timeout=10
)
```

**Notes:**
- Update `app_icon` path to your icon file location
- On Linux, ensure `notify-send` is installed (libnotify)
- On macOS, notification permissions may be required
- Timeout may not work on all platforms (OS-dependent)

---

## What's Next

**Possible enhancements:**
- Add command-line arguments for custom notifications
- Integrate with schedule module for timed notifications
- Read messages from file or API
- Add notification sound support
- Create GUI for scheduling reminders
- Monitor system events and alert on thresholds
- Build a notification server (Flask + webhooks)

---

## License

**Proprietary - All Rights Reserved**

This code is the intellectual property of the author. No copying, modification, or distribution is permitted without explicit written consent.
