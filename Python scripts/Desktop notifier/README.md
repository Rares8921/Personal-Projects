# Desktop Notification Script

A Python script that sends desktop notifications using the `plyer` library. The script is designed to send a notification with a specified title and message, along with customizable application name and icon.

## Code summary

Firstly, the necessary library `plyer` is imported, specifically the `notification` module. The script defines a function `send_notification` that takes two arguments: `title` and `message`. This function uses the `notify` method from `plyer.notification` to display a desktop notification with the given title and message. Additional parameters such as `app_name`, `app_icon`, and `timeout` are specified within the function.

The script then includes a main block that sets the `title` and `message` for the notification and calls the `send_notification` function to display the notification.

## Complexity and efficiency

The `send_notification` function is straightforward and operates in constant time, O(1), as it simply calls the `notify` method with predefined arguments. The `notify` method from the `plyer` library handles the actual process of creating and displaying the notification, which may involve interfacing with the operating system's notification service.

The space complexity is also minimal, O(1), as the function only requires storage for the input arguments and the parameters passed to the `notify` method. The script's efficiency is highly dependent on the underlying implementation of the `plyer.notification` module and the operating system's notification service.