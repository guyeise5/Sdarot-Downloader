from tkinter import *
from sdarot import show
from sdarot import client
# pip install pillow
from PIL import Image, ImageTk
from operator import attrgetter


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
        c = client.Client()
        s = show.Show(show_id, c)
        if self._show_id_gui is not None:
            self._show_id_gui.destroy()
        self._show_gui = ShowGui(s, c)
        self._show_gui.startwindow()


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


class ShowIdGui(Gui):
    """
    This class is for the Show id gui - a gui for getting show id from user and running a function accordingly
    """

    def __init__(self, button_command):
        # creating basic window and centering it
        super(ShowIdGui, self).__init__("Sdarot downloader", 90, 500)
        self.center()

        # frame for the text box and my text
        frame = Frame(self.window)
        frame.pack()
        Label(
            frame,
            text="Enter the show id:",
            font=("Helvetica", 18)
        ).pack(side=LEFT)
        self._show_id_box = Text(frame, height=1, width=20)
        self._show_id_box.pack(side=LEFT)

        # a frame for the button
        bottom_frame = Frame(self.window)
        bottom_frame.pack()
        self._button = Button(
            bottom_frame,
            text="OK",
            command=button_command
        )
        self._button.pack(padx=10, pady=10)

    @property
    def show_id_box(self):
        try:
            show_id = int(self._show_id_box.get("1.0", "end"))
            if show_id > 0:
                return show_id
        except Exception as e:
            print(e)
        return -1


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


class ShowGui(Gui):
    """
    This class is for the main show GUI - from which the user download
    """

    ALL_OPTION = "ALL"

    def __init__(self, serie, client):
        # The sdarot object stuff
        self._show = serie
        self._client = client
        self._client.cookie = self._show.get_cookie()

        # creating the window in the center - with some basic setting
        super(ShowGui, self).__init__(f"Show {serie.id} Downloader", 600, 900)
        self.center()
        self.window.config(bg="skyblue")
        self.window.resizable(False, False)

        # dividing the window into two frames - the left bigger from the right

        left_frame = Frame(self.window, width=400, height=400, bg='grey')
        left_frame.pack(side='left', fill='both', padx=10, pady=5, expand=True)

        right_frame = Frame(self.window, width=200, height=400, bg='grey')
        right_frame.pack(side='right', fill='both', padx=10, pady=5, expand=True)

        # creating show image in the right frame
        with Image.open(self._show.get_show_image(self._client, './images')) as img:
            img_size = img.size
            image = img.resize((int(img_size[0] * 1.5), int(img_size[1] * 1.5)), Image.ANTIALIAS)
            image = ImageTk.PhotoImage(image)

        # label for the image
        img_lbl = Label(right_frame, image=image)
        img_lbl.image = image
        img_lbl.pack(fill='both', padx=10, pady=10, expand=True)

        # Left side - creating a frame for the main part
        main_frame = Frame(left_frame, bg='lightgray')
        main_frame.pack(fill='both', padx=10, pady=10, expand=True)

        # The Headline in the left
        Label(
            main_frame,
            text="What would you like to do?",
            font=("Helvetica", 28, "bold"),
            bg='lightgray'
        ).pack(fill='both', padx=10, pady=10)

        # creating a frame for the season choice
        download_by_season_frame = Frame(main_frame, bg='lightgray')
        download_by_season_frame.pack(fill='both', padx=5, pady=5)
        Label(
            download_by_season_frame,
            text="Which season would you like to download?",
            font=("Helvetica", 16),
            bg='lightgray'
        ).pack(side=LEFT)

        # setting option menu for seasons
        season_choices = list(map(attrgetter('number'), self._show.seasons))
        season_choices.append(self.ALL_OPTION)

        # setting the selected option menu variable
        self._season = StringVar()
        self._season.set(self.ALL_OPTION)

        # if it changes - calls the function
        self._season.trace("w", self._season_menu_changed)

        option_menu = OptionMenu(download_by_season_frame, self._season, *season_choices)
        option_menu.pack(side=LEFT, padx=5, pady=5)

        # setting frame for episode selections - at first invisible
        # only when season is not ALL it is visible
        self._episode_option_frame = Frame(main_frame, bg='lightgrey')

        Label(
            self._episode_option_frame,
            text="Which episode would you like to download?",
            font=("Helvetica", 16),
            bg='lightgray'
        ).pack(side=LEFT)

        self._episode = None
        self._episode_option_menu = None

        Button(
            main_frame,
            text=" Download ",
            font=("Helvetica", 22, "bold"),
            command=self.download,
        ).pack(side='bottom', padx=10, pady=10)

    def _season_menu_changed(self, *args):
        if self._season.get() == self.ALL_OPTION:
            # making the episodes frame invisible
            self._episode_option_frame.pack_forget()
        else:
            # getting the available episodes in this season
            episodes = self._show.get_season(int(self._season.get())).episodes
            episode_options = list(map(attrgetter('number'), episodes))
            episode_options.append(self.ALL_OPTION)
            self._episode = StringVar()
            self._episode.set(self.ALL_OPTION)
            if self._episode_option_menu is not None:
                self._episode_option_menu.destroy()
            # creating the new option menu
            self._episode_option_menu = OptionMenu(self._episode_option_frame, self._episode, *episode_options)
            self._episode_option_menu.pack(side=LEFT, padx=5, pady=5)
            # making the episode frame visible
            self._episode_option_frame.pack(fill='both', padx=5, pady=5)  # , expand=True)

    def download(self):
        if self._season.get() == self.ALL_OPTION:
            DownloadingGui(self._client, self._show, None, None)
        elif self._episode == self.ALL_OPTION:
            DownloadingGui(self._client, self._show, int(self._season.get()), None)
        else:
            DownloadingGui(self._client, self._show, int(self._season.get()), int(self._episode.get()))


class DownloadingGui(Gui):

    def __init__(self, client, show, season, episode):
        self._client = client
        self._show = show
        super(DownloadingGui, self).__init__("Loading", 60, 600)
        self.center()

        self._lbl = Label(self.window, text="Start Downloading ...", font=("Helvetica", 18, "bold"))
        self._lbl.pack()
        self.window.after(200, self.download, *[season, episode])
        self.startwindow()

    def download(self, season, episode):
        if season is None:
            for s in self._show.seasons:
                for e in s.episodes:
                    self._lbl.configure(text=f"Downloading season {s.number} episode {e.number} ...")
                    self._lbl.update()
                    e.download(self._client)
        elif episode is None:
            for e in self._show.get_season(season).episodes:
                self._lbl.configure(text=f"Downloading season {season} episode {e.number} ...")
                self._lbl.update()
                e.download(self._client)
        else:
            self._lbl.configure(text=f"Downloading season {season} episode {episode} ...")
            self._lbl.update()
            (self._show.get_season(season).get_episode(episode)).download(self._client)
        self.destroy()