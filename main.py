
import sdarot.client as client
import sdarot.show as show
import argparse

def parse_args():
    """Parse program arguments."""
    parser = argparse.ArgumentParser()
    parser.add_argument(
        '-s',
        '--show-id',
        required=True,
        type=int,
        help='The show id for the show you want to download',
    )
    parser.add_argument(
        '-S',
        '--seasons',
        nargs='*',
        type=int,
        help='The seasons of the show you want to download',
    )
    args = parser.parse_args()
    return args

def main():
    parameters = parse_args()
    c = client.Client()
    s = show.Show(parameters.show_id, c)
    c.cookie = s.get_cookie()
    if parameters.seasons:
        print("downloading selected seasons only")
        s.download_seasons(c, parameters.seasons)
    else:
        print("downloading all show")
        s.download(c)

if __name__ == '__main__':
    main()
