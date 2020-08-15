
import sdarot.client as client
import sdarot.show as show

USER_AGENT = 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/84.0.4147.105 ' \
             'Safari/537.36 '
SHOW_ID = 110

def main():
    c = client.Client(USER_AGENT)
    s = show.Show(SHOW_ID, c)
    c.cookie = s.get_cookie()
    s.download(c)

if __name__ == '__main__':
    main()
