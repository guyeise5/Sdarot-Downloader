
import sdarot.client as client
import sdarot.show as show

SHOW_ID = 110

def main():
    c = client.Client()
    s = show.Show(SHOW_ID, c)
    print(s.get_user_agent())
    c.cookie = s.get_cookie()
    s.download(c)

if __name__ == '__main__':
    main()
