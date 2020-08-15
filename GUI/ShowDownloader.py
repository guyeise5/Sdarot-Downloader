
from GUI.show import ShowIdGui, ShowGui
from sdarot.client import Client
from sdarot.show import Show
from GUI.tools import ErrorWindow

class ShowDownloader:
    """
    This Class is a ShowDownloader
    every instance is able to create GUI for downloading a show
    """

    def __init__(self, show_id=None):
        self._show_gui = None
        self._show_id_gui = None
        if show_id is None:
            self._show_id = None
            self._show_id_gui = ShowIdGui(self.start_show_gui)
            self._show_id_gui.startwindow()
        else:
            assert isinstance(show_id, int)
            if show_id <= 0:
                raise Exception("The show id need to be above 0")
            self._show_id = show_id
            self.start_show_gui()

    def start_show_gui(self):
        if self._show_id is None:
            show_id = self._show_id_gui.show_id_box
            if show_id == -1:
                ErrorWindow("Please enter a valid show id").startwindow()
                return
        c = Client()
        s = Show(show_id, c)
        if self._show_id_gui is not None:
            self._show_id_gui.destroy()
        self._show_gui = ShowGui(s, c)
        self._show_gui.startwindow()