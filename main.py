
import sdarot.client as client
import sdarot.show as show
import argparse

def parse_args():
    """Parse program arguments."""
    parser = argparse.ArgumentParser()
    parser.add_argument(
        '-s',
        '--sid',
        '--show-id',
        required=True,
        type=int,
        help='The show id for the show you want to download',
    )
    args = parser.parse_args()
    return args

def main():
    parameters = parse_args()
    c = client.Client()
    s = show.Show(parameters.sid, c)
    c.cookie = s.get_cookie()
    s.download(c)

if __name__ == '__main__':
    main()
