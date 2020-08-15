import requests
import re
import sdarot.season as season
from pathlib import Path

BASE_URL = "https://sdarot.rocks"
DECODE_PAGE = 'ISO-8859-1'

class Show:

    # This is shared by all instances of sdarot_show
    # The pattern for season in show page
    _season_pattern = '(/watch/(\d+)-.*?/season/(\d+?))"'
    # The base path for the show photo
    _base_image_path = "https://static.sdarot.rocks/series"
    # show photo file ending
    _image_ending = ".jpg"

    def __init__(self, id, sdarot_client):
        self._id = id
        self._url = f"{BASE_URL}/watch/{id}"
        print("getting the show page")
        self._page = requests.get(
            self._url,
            headers={
                'User-Agent': sdarot_client.user_agent,
                'Origin': BASE_URL,
            },
        )
        # TODO: make sure we didn't get error from request - if do raise exception

        self._find_seasons(sdarot_client)

    def get_cookie(self):
        return self._page.headers['Set-Cookie'].split(';')[0]

    @property
    def seasons(self):
        return self._seasons

    def get_season(self, season_number):
        for season in self._seasons:
            if season.number == season_number:
                return season
        return -1

    @property
    def id(self):
        return self._id

    def _find_seasons(self, sdarot_client):
        self._seasons = []
        for season_info in re.findall(self._season_pattern, self._page.content.decode(DECODE_PAGE)):
            self._seasons.append(
                season.Season(
                    self._id,
                    int(season_info[2]),
                    season_info[0],
                    sdarot_client
                )
            )
        print(f"The show has {len(self._seasons)} seasons")

    def download_episodes(self, sdarot_client, season, episodes):
        for s in self._seasons:
            if s.number == season:
                s.download_episodes(sdarot_client, episodes)

    def download_seasons(self, sdarot_client, seasons):
        for s in self._seasons:
            if s.number in seasons:
                print(f"downloading season {s.number}")
                s.download(sdarot_client)

    def download(self, sdarot_client):
        for s in self._seasons:
            print(f"downloading season {s.number}")
            s.download(sdarot_client)

    def get_show_image(self, sdarot_client, image_folder):
        if not isinstance(image_folder, Path):
            image_folder = Path(image_folder)

        filename = f"{self._id}{self._image_ending}"
        full_path_to_file = image_folder.joinpath(filename)

        if not full_path_to_file.exists():

            # making sure the folder exists
            image_folder.mkdir(parents=True, exist_ok=True)

            # getting the image from the sdarot website
            image_data = requests.get(
                f"{self._base_image_path}/{filename}",
                headers={
                    'User-Agent': sdarot_client.user_agent,
                    'Referer': self._url,
                },
            )
            # writing the image to the file
            with full_path_to_file.open('wb') as fd:
                fd.write(image_data.content)

        return str(full_path_to_file)

    def delete_show_image(self, image_path):
        if not isinstance(image_path, Path):
            image_path = Path(image_path)
        image_path.unlink(missing_ok=True)
