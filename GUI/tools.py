from tkinter import *
# pip install pillow
from PIL import Image, ImageTk


class Gui:
    """
    This is a basic GUI class - creating a window with a title,
    This is mostly for functions which multiple guis may need - like center a window
    """

    def __init__(self, title, height, width):
        self.window = Tk()
        self._width = width
        self._height = height
        self.window.title(title)
        self.window.geometry(f"{self._width}x{self._height}")

    def center(self):
        screen_width = self.window.winfo_screenwidth()
        screen_height = self.window.winfo_screenheight()
        x = int((screen_width / 2) - (self._width / 2))
        y = int((screen_height / 2) - (self._height / 2))
        self.window.geometry(f"{self._width}x{self._height}+{x}+{y}")

    def startwindow(self):
        self.window.mainloop()

    def destroy(self):
        self.window.destroy()



class ErrorWindow(Gui):
    """
    This class is for error window - a small basic window with error message
    """

    def __init__(self, message):
        super(ErrorWindow, self).__init__("Error", 60, 400)
        self.center()
        Label(
            self.window,
            text=message,
            font=("Helvetica", 10)
        ).pack()

