import sys
import re
import time
import json
import requests

base_url = "https://sdarot.rocks"
show_url = "https://sdarot.rocks/watch/5555"

SLEEP_BETWEEN_REQS = 0.5
SLEEP_AFTER_TOKENS = 30

def download_episode(ep):
    with requests.get(ep['video_url'], stream=True) as r:
        r.raise_for_status()
        with open(ep['filename'], 'wb') as f:
            for chunk in r.iter_content(chunk_size=8192): 
                if chunk:
                    f.write(chunk)

print("Getting episode URLs")
response = requests.get(show_url, headers={
    'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/84.0.4147.105 Safari/537.36',
    'Origin': base_url,
})
content = response.content
cookie = response.headers['Set-Cookie'].split(';')[0]
episodes = []

for episode in re.findall('(/watch/(\d+)-.*?/season/(\d+?)/episode/(\d+?))"', content.decode('ISO-8859-1')):
    episodes.append({
        'url': base_url + episode[0],
        'SID': episode[1],
        'season': episode[2],
        'episode': episode[3]
    })

for ep in episodes:
    print("Prewatch SID[{}] Season[{}] Episode[{}]".format(ep['SID'], ep['season'], ep['episode']))
    response = requests.post("https://sdarot.rocks/ajax/watch", {
        'preWatch': 'true',
        'SID': ep['SID'],
        'season': ep['season'],
        'ep': ep['episode']
    }, headers={
        'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/84.0.4147.105 Safari/537.36',
        'Referer': ep['url'],
        'X-Requested-With': 'XMLHttpRequest',
        'Cookie': cookie
    })

    ep['token1'] = response.content
    print("Got token1 {}".format(ep['token1']))
    time.sleep(SLEEP_BETWEEN_REQS)

print("Sleeping {}s".format(SLEEP_AFTER_TOKENS))
time.sleep(SLEEP_AFTER_TOKENS)

for ep in episodes:
    try:
        content = requests.post("https://sdarot.rocks/ajax/watch", {
            'watch': 'true',
            'token': ep['token1'],
            'serie': ep['SID'],
            'season': ep['season'],
            'episode': ep['episode'],
            'auth': 'false',
            'type': 'episode'
        }, headers={
            'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/84.0.4147.105 Safari/537.36',
            'Referer': ep['url'],
            'X-Requested-With': 'XMLHttpRequest',
            'Cookie': cookie
        }).content
        time.sleep(SLEEP_BETWEEN_REQS)
        print(f"{content.decode('UTF-8')}")
        video_data = json.loads(content)
        print("Got token2 {}".format(video_data['watch']['480']))

        video_url = 'https://{}/w/episode/480/{}.mp4?token={}&time={}&uid='.format(
            video_data['url'],
            video_data['VID'],
            video_data['watch']['480'],
            video_data['time'])

        print(video_url)
        
        ep['filename'] = '{}_S{:0>2}_E{:0>3}.mp4'.format(
            ep['SID'],
            ep['season'],
            ep['episode']
        )

        ep['video_url'] = video_url
    except IndexError:
        print("Error... skipping token2.")


for ep in episodes:
    print("Downloading {}".format(ep['filename']))
    download_episode(ep)
    print("Downloaded {}".format(ep['filename']))
