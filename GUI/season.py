
from GUI.tools import Gui

class DownloadBySeasonGui(Gui):
    
    def __init__(self, show):
        self._show = show
        super(DownloadBySeasonGui, self).__init__(f"show {self._show.id} downloader by season")

        
    def download(self):
        # TODO: pretty gui for loading time
        self._show.get_season(self._season).download()
