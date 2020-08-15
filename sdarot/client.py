
class Client:
    """
    This class is for the client info for sdarot website,
    it contains some user specific needed headers for requests to sdarot website
    """

    X_REQUESTED_WITH = "XMLHttpRequest"

    def __init__(self, user_agent):
        assert isinstance(user_agent, str)
        self._user_agent = user_agent
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
