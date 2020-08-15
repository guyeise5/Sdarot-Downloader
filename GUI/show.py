
from tkinter import *
# pip install pillow
from PIL import Image, ImageTk
from GUI.tools import Gui


IMAGES_PATH = "./images"


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
        super(ShowGui, self).__init__(f"Show {serie.id} Downloader", 700, 950)
        self.center()
        self.window.config(bg="skyblue")
        self.window.resizable(False, False)

        # dividing the window into two frames - the left bigger from the right

        left_frame = Frame(self.window, width=400, height=400, bg='grey')
        left_frame.pack(side='left', fill='both', padx=10, pady=5, expand=True)

        right_frame = Frame(self.window, width=200, height=400, bg='grey')
        right_frame.pack(side='right', fill='both', padx=10, pady=5, expand=True)

        # creating show image in the right frame
        with Image.open(self._show.get_show_image(self._client, f'{IMAGES_PATH}/shows')) as img:
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

        download_all_show = Frame(main_frame, bg='lightgray')
        download_all_show.pack(fill='both', padx=5, pady=5, expand=True)

        # creating a button by image
        with Image.open(f'{IMAGES_PATH}/buttons/DownloadShow.png') as img:
            image = ImageTk.PhotoImage(img)

        download_show_button = Button(
            download_all_show,
            image=image,
            command=self.download_show,
            borderwidth=0
        )
        download_show_button.image = image
        download_show_button.pack(expand=True)

        download_by_season = Frame(main_frame, bg='lightgray')
        download_by_season.pack(fill='both', padx=5, pady=5, expand=True)

        # creating a button by image
        with Image.open(f'{IMAGES_PATH}/buttons/DownloadbySeason.png') as img:
            image = ImageTk.PhotoImage(img)

        download_season_button = Button(
            download_by_season,
            image=image,
            command=self.by_season_gui,
            borderwidth=0
        )
        download_season_button.image = image
        download_season_button.pack(expand=True)

        download_by_episode = Frame(main_frame, bg='lightgray')
        download_by_episode.pack(fill='both', padx=5, pady=5, expand=True)

        # creating a button by image
        with Image.open(f'{IMAGES_PATH}/buttons/DownloadbyEpisode.png') as img:
            image = ImageTk.PhotoImage(img)

        download_episode_button = Button(
            download_by_episode,
            image=image,
            command=self.by_episode_gui,
            borderwidth=0
        )
        download_episode_button.image = image
        download_episode_button.pack(expand=True)

    def download_show(self):
        # TODO: gui for the downloading that shows status - from log that will be written by the sdarot modules
        self._show.download(self._client)

    def by_season_gui(self):
        pass

    def by_episode_gui(self):
        pass