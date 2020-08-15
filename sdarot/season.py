
import requests
import re
import time
import sdarot.episode as episode

BASE_URL = "https://sdarot.rocks"
DECODE_PAGE = 'ISO-8859-1'
TIME_BETWEEN_EPISODES = 0.5

class Season:

    # This is shared by all instances of sdarot_season
    # The pattern for episode in season page
    _episode_pattern = '(/watch/(\d+)-.*?/season/(\d+?)/episode/(\d+?))"'

    def __init__(self, show_id, season_number, season_url, sdarot_client):
        self._show_id = show_id
        self._number = season_number
        self._url = f"{BASE_URL}{season_url}"
        self._page = requests.get(
            self._url,
            headers={
                'User-Agent': sdarot_client.user_agent,
                'Origin': BASE_URL,
            },
        )
        self._find_episodes()
        # TODO: make sure we got what we expected from request

    def _find_episodes(self):
        self._episodes = []
        for episode_info in re.findall(self._episode_pattern, self._page.content.decode(DECODE_PAGE)):
            self._episodes.append(
                episode.Episode(
                    self._show_id,
                    self._number,
                    episode_info[3],
                    episode_info[0]
                )
            )
    @property
    def number(self):
        return self._number

    def download(self, sdarot_client):
        for episode in self._episodes:
            episode.download(sdarot_client)
            time.sleep(TIME_BETWEEN_EPISODES)
