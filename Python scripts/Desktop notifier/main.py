from plyer import notification

#def notify(self, title='', message='', app_name='', app_icon='',
# timeout=10, ticker='', toast=False, hints={}):
def send_notification(title, message):
    notification.notify(title = title, message = message, app_name='Ceva nebunie', app_icon='C:\\Users\\dumra\\PycharmProjects\\desktopNewsNotifier\\clipboard.ico', timeout = 3)

if __name__ == "__main__":
    title = "Desktop Notifier"
    message = "This is a test notification."

    send_notification(title, message)


# https://github.com/kivy/plyer/blob/master/plyer/tests/test_notification.py