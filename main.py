import re
import time
import json
import requests

# The url for sdarot website
BASE_URL = "https://sdarot.rocks"
# The url for the watch page
WATCH_URL = f"{BASE_URL}/ajax/watch"
# The url for the show you want to download - the url is usually goes by id
SHOW_URL = f"{BASE_URL}/watch/5411"
# The version of your chrome browser
USER_AGENT = 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/84.0.4147.105 ' \
             'Safari/537.36 '
X_REQUESTED_WITH = 'XMLHttpRequest'
DECODE_PAGE = 'ISO-8859-1'

# We don't won't multiple request at the same time
SLEEP_BETWEEN_REQS = 0.5
# The sdarot website asks to wait 30 seconds before we can download
SLEEP_AFTER_TOKENS = 30


def pre_watch_episode(
    episode,
    cookies,
    sleep_for_token=30,
    user_agent=USER_AGENT,
    x_requested_with=X_REQUESTED_WITH,
):
    """
    This Function is doing the pre watch steps needed by the website
    :param episode: The episode we want to pre-watch info - should include:
    - 'SID' (show id)
    - 'season'
    - 'episode'
    - 'url' (the url for the episode pre-watch)
    :param cookies: The cookies to send in the pre-watch request
    :param sleep_for_token: the time to sleep for the token to be valid
    :param user_agent: the browser info to send in the header
    :param x_requested_with: the request type - need to send it for authentication reasons/
    :return: the token of approval for the pre-watch
    """
    print("Prewatch SID[{}] Season[{}] Episode[{}]".format(
        episode['SID'],
        episode['season'],
        episode['episode']
    ))
    token = requests.post(
        WATCH_URL,
        {
            'preWatch': 'true',
            'SID': episode['SID'],
            'season': episode['season'],
            'ep': episode['episode']
        },
        headers={
            'User-Agent': user_agent,
            'Referer': episode['url'],
            'X-Requested-With': x_requested_with,
            'Cookie': cookies
        }
    ).content

    # TODO: make sure we got what we expected from request

    print("Got pre-watch token {}".format(token))
    print("Sleeping {}s for the token to be valid".format(sleep_for_token))
    time.sleep(sleep_for_token)
    return token


def request_video_info(
    episode,
    cookies,
    user_agent=USER_AGENT,
    x_requested_with=X_REQUESTED_WITH,
):
    """
    This function is getting the episode video info from the website
    :param episode: The episode info - should include:
    - 'SID' (show id)
    - 'season'
    - 'episode'
    - 'url' (the url for the episode pre-watch)
    - 'pre-watch-token' (the token that approves the pre-watch)
    :param cookies: The cookies to send in the request
    :param user_agent: The browser info to send in the header
    :param x_requested_with: The request type - need to send it for authentication reasons/
    :return:
    """
    video_data = json.loads(requests.post(
        WATCH_URL,
        {
            'watch': 'true',
            'token': episode['pre-watch-token'],
            'serie': episode['SID'],
            'season': episode['season'],
            'episode': episode['episode'],
            # 'auth': 'false',
            'type': 'episode'
        },
        headers={
            'user-agent': user_agent,
            'referer': episode['url'],
            'x-requested-with': x_requested_with,
            'cookie': cookies
        }
    ).content)

    # TODO: make sure we got what we expected

    print("Got video token {}".format(video_data['watch']['480']))
    return video_data


def download_episode(
    episode,
    cookies,
    user_agent=USER_AGENT,
    chunk_size=8192
):
    # TODO: do this better
    with requests.get(
            episode['video_url'],
            headers={
                'user-agent': user_agent,
                'referer': episode['url'],
                'cookie': cookies
            },
            stream=True
    ) as r:
        r.raise_for_status()
        with open(episode['filename'], 'wb') as f:
            for chunk in r.iter_content(chunk_size):
                if chunk:
                    f.write(chunk)


def find_seasons(content):
    """
    This function finds the seasons on the host
    :param content: the show page content
    :return: the seasons and their info
    """
    # Getting the seasons that are in website from the page given
    seasons = []
    # TODO: think of a better way to save pattern
    for season in re.findall('(/watch/(\d+)-.*?/season/(\d+?))"', content):
        seasons.append({
            'url': BASE_URL + season[0],
            'SID': season[1],  # not sure this is needed but for future references
            'number': season[2],
        })
    return seasons


def find_episodes(
    season,
    user_agent=USER_AGENT,
    origin=BASE_URL,
    decode_page=DECODE_PAGE
):
    """
    This function finds all the episodes of a season in the website
    :param season: The season we want the episodes of, should include:
    - number: the season number
    - url: the url for the season
    :param user_agent: The browser info to send in the header
    :param origin: The base website
    :param decode_page: the decode code
    :return: The episodes info in the season
    """
    print(f"Getting The season {season['number']} page - so we can see which episodes exists")
    show_page = requests.get(
        season['url'],
        headers={
            'User-Agent': user_agent,
            'Origin': origin,
        },
    ).content.decode(decode_page)

    # TODO: think of a better way to save pattern
    episodes = []
    # re.findall allows to find the regex we need in the content
    for episode in re.findall('(/watch/(\d+)-.*?/season/(\d+?)/episode/(\d+?))"', show_page):
        episodes.append({
            'url': origin + episode[0],
            'SID': episode[1],
            'season': episode[2],
            'episode': episode[3]
        })
    return episodes


def main():

    # TODO: argparse to give the show id (or multiple show ids), which seasons, episodes and such
    # TODO: also add debug mode

    # The first time we are sending request to the sdarot website - for the show page,
    # it returns a lot of info - we want the Cookie for future requests
    # and the list of episode of our show
    print("Getting The show page - so we can see the info we need")
    show_page = requests.get(
        SHOW_URL,
        headers={
            'User-Agent': USER_AGENT,
            'Origin': BASE_URL,
        },
    )
    content = show_page.content.decode(DECODE_PAGE)
    # Getting the cookie we need for authentication
    cookie = show_page.headers['Set-Cookie'].split(';')[0]

    # finding the seasons exist in website
    seasons = find_seasons(content)
    print(f"{len(seasons)} seasons exists")
    # finding all the episodes of each season
    episodes = []
    for season in seasons:
        episodes += find_episodes(season)
        time.sleep(SLEEP_BETWEEN_REQS)

    print(f"overall {len(episodes)} episodes will be downloaded")

    # Going over all the episodes
    # we need to get pre watch token - a valid one
    # get the video url info to know from where to request the videos - using the valid pre-watch-token
    # then getting the videos from the url we got using the token we got
    for episode in episodes:
        # Getting the pre watch token
        episode['pre-watch-token'] = pre_watch_episode(
            episode,
            cookie,
        )

        # Getting the video info - for the valid url
        video_data = request_video_info(episode, cookie)

        # Creating the url from the data we got
        # TODO: think of a prettier way to do it - maybe using jinja ?
        episode['video_url'] = 'https://{}/w/episode/{}/480/{}.mp4?token={}&time={}&uid='.format(
            video_data['url'],
            episode['SID'],
            video_data['VID'],
            video_data['watch']['480'],
            video_data['time']
        )

        # Creating the file name
        # TODO: need to make prettier name - with the show name and not id
        episode['filename'] = '{}_S{:0>2}_E{:0>3}.mp4'.format(
            episode['SID'],
            episode['season'],
            episode['episode']
        )

        # We don't want to make a lot of requests it the same time
        time.sleep(SLEEP_BETWEEN_REQS)

        print("Downloading {}".format(episode['filename']))
        download_episode(episode, cookie)
        print("Downloaded {}".format(episode['filename']))


if __name__ == '__main__':
    main()
