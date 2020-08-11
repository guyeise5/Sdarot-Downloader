import random

class Client:
    """
    This class is for the client info for sdarot website,
    it contains some user specific needed headers for requests to sdarot website
    """
    # Those variables are the same for all instances of class
    # random user agent
    USER_AGENTS = [
        'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/84.0.4147.105 Safari/537.36 ',
        'Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:76.0) Gecko/20100101 Firefox/76.0',
    ]
    X_REQUESTED_WITH = "XMLHttpRequest"

    def __init__(self):
        self._user_agent = random.choice(self.USER_AGENTS)
        self._cookie = None

    @property
    def cookie(self):
        if self._cookie is None:
            raise Exception("The cookie was not defined!!")
        return self._cookie

    @cookie.setter
    def cookie(self, cookie):
        assert isinstance(cookie, str)
        self._cookie = cookie

    @property
    def user_agent(self):
        return self._user_agent
