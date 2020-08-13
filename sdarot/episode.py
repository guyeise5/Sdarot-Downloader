import requests
import time
import json
import sdarot.client as client
BASE_URL = "https://sdarot.rocks"

class Episode:

    _SLEEP_FOR_PRE_WATCH = 30
    _WATCH_URL = f"{BASE_URL}/ajax/watch"

    def __init__(self, show_id, season_number, episode_number, episode_url):
        self._show_id = show_id
        self._season_id = season_number
        self._number = episode_number
        self._url = f"{BASE_URL}{episode_url}"

    @property
    def number(self):
        return self._number

    def _pre_watch(self, sdarot_client):
        print(f"pre watch episode {self._number}")
        token = requests.post(
            self._WATCH_URL,
            {
                'preWatch': 'true',
                'SID': self._show_id,
                'season': self._season_id,
                'ep': self._number
            },
            headers={
                'User-Agent': sdarot_client.user_agent,
                'Referer': self._url,
                'X-Requested-With': sdarot_client.X_REQUESTED_WITH,
                'Cookie': sdarot_client.cookie
            }
        ).content
        # TODO: make sure we got what we expected from request

        time.sleep(self._SLEEP_FOR_PRE_WATCH)
        return token

    def _get_video_info(self, sdarot_client, pre_watch_token):
        print(f"getting video info on episode {self._number}")
        return json.loads(
            requests.post(
                self._WATCH_URL,
                {
                    'watch': 'true',
                    'token': pre_watch_token,
                    'serie': self._show_id,
                    'season': self._season_id,
                    'episode': self._number,
                    # 'auth': 'false',
                    'type': 'episode'
                },
                headers={
                    'user-agent': sdarot_client.user_agent,
                    'referer': self._url,
                    'x-requested-with': sdarot_client.X_REQUESTED_WITH,
                    'cookie': sdarot_client.cookie
                }
            ).content
        )
        # TODO: make sure we got what we expected

    def download(self, sdarot_client, chunk_size=8192):
        video_info = self._get_video_info(sdarot_client, self._pre_watch(sdarot_client))
        video_url = f"https://{video_info['url']}/w/episode/{self._show_id}/480/{video_info['VID']}.mp4"
        filename = f'{self._show_id}_S{self._season_id}_E{self._number}.mp4'
        print(f"downloading {filename}")
        with requests.get(
            video_url,
            {
                'token': video_info['watch']['480'],
                'time': video_info['time'],
                'uid': ''
            },
            headers={
                'user-agent': sdarot_client.user_agent,
                'Referer': self._url,
                'cookie': sdarot_client.cookie
            },
            stream=True
        ) as response:
            response.raise_for_status()
            with open(filename, 'wb') as f:
                for chunk in response.iter_content(chunk_size=chunk_size):
                    if chunk:
                        f.write(chunk)
        # TODO: error handling